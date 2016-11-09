package tcp_chat;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Each client gets its own user thread 
 * unless it is talking to another client in its user thread
 * @author frank
 */
public class UserThread extends Thread
{
    private ConcurrentHashMap<User, UserThread> userList; 
    
    private User USER1, USER2;
    
    public UserThread(Socket connSocket, 
            ConcurrentHashMap<User, UserThread> userSockets) throws IOException
    {
        this.userList = userSockets;
        
        this.USER1 = new User(connSocket);
        this.USER2 = null; // until connected to second user
    }
    
    public UserThread(User existingUser,
            ConcurrentHashMap<User, UserThread> userSockets) throws IOException
    {
        this.userList = userSockets;
        this.USER1 = existingUser;
        this.USER2 = null;
    }
    

    @Override
    public void run()
    {
        try
        {
            initializeUsername();
            
            String input;
            
            while(true)
            {
                handleInput(USER1); // alternate whos turn it is to for input
                handleInput(USER2); // strict 1 for 1 message sending
            }
        } catch (IOException ex)
        {
            System.out.println("ERROR sending data");
        }
    }
    
    private void initializeUsername() throws IOException
    {
        if(USER1.getUsername() == null)
        {
            System.out.println("SERVER: connected to client");
            boolean set;
            do
            {
                String input = USER1.getmyData_IN().readLine(); // FIRST MESSAGE RECEIVED
            
                set = setUsername(input);
            
                if(!set)
                    USER1.getmyData_OUT().write(UserThread.formatOutput("Invalid Username. Please enter a new one."));
                else
                    USER1.getmyData_OUT().write(UserThread.formatOutput("set"));
            } while (!set);
        
            System.out.println("SERVER: Client username set to " + USER1.getUsername());
        }
    }
    
    private boolean setUsername(String inputName)
    {
        User user = searchUsersFromName(inputName);
        if(user != null)
            return false;
        else
        {    
            userList.put(USER1, this);
            USER1.setUsername(inputName);
            return true;
        } 
    }
    ////////////////// ABOVE IS SERVER - CLIENT INITIALIZATION /////////////////
     
    
    private void handleInput(User user) throws IOException
    {
        if(user != null)
        {
            System.out.println("Handling input from " + user.getUsername());
            String userIN = user.getmyData_IN().readLine();
            
            if(isServerCommand(userIN))
            {
                handleServerCommand(user, userIN);
            }
            else // relay messages
            {
                handleChatMessage(user, userIN);
            }
        }
    }
    
    private void handleChatMessage(User user, String messageToSend) throws IOException
    {
        User userToSendTo = user.equals(USER1) ? USER2 : USER1;
        
        if(userToSendTo != null)         
            userToSendTo.getmyData_OUT().write(formatOutput(messageToSend));
        else
            user.getmyData_OUT().write(formatOutput(messageToSend));
    }
    
    /**
     * SERVER COMMANDS DELINEATED BY '-'
     * ALL server commands of the form "-x input"
     * where x is the option
     * To chat with another user: -c UserToChatWith
     * @param input
     * @return 
     */
    private static boolean isServerCommand(String input)
    {
        return (input != null && input.startsWith("-"));
    }
    
    /**
     * 
     * @param input 
     */
    private void handleServerCommand(User user, String input) throws IOException
    {
        
        if(input != null)
        {    
            System.out.println("SERVER received command " + input);
            String command = input.substring(0, 2);
            switch (command)
            {
                case "-c": // chat
                    String commandData = input.substring(3);
                    
                    if(isChatting()) // if is in chat and want to connect with other
                    {
                        // move other user to its own thread
                        moveCurrentChattingUser(user);
                    }
                    this.userChatSetup(user, commandData);
                    break;
                    
                case "-q":
                    closeUserConnection(user);
                    break;
                    
                default:
                    user.getmyData_OUT().write(formatOutput("invalid command"));
                    break;
            }
                    
        }
        
        handleInput(user); // can therefore call as many commands as possible
    }
    
    /**
     * If there is a user currently chatting with the current user,
     * move him/her to their own thread to make room for requested user here
     * @param currentUser
     * @throws IOException 
     */
    private void moveCurrentChattingUser(User currentUser) throws IOException
    {
        User userToMove;
        if(!currentUser.equals(USER1)) // user2 sending this command
            userToMove = USER1;
        else
            userToMove = USER2;
        
        UserThread newUserThread = new UserThread(userToMove, this.userList);
        
        this.userList.replace(userToMove, newUserThread);
        
        newUserThread.start();
        
        currentUser.getmyData_OUT().write(formatOutput("SERVER: CHAT SWITCHED"));
        userToMove.getmyData_OUT().write(formatOutput(
                "SERVER: CHAT DISCONNECTED"));
        
        USER1 = currentUser;
        USER2 = null;
    }
    
    
    /**
     * 
     * @param user
     * @param userToConnect 
     */
    private void userChatSetup(User user, String usernameToConnect) throws IOException
    {
        if(usernameToConnect.equalsIgnoreCase("LISTENER"))
            return;
        
        User userToConnect = searchUsersFromName(usernameToConnect);
                
        if(userToConnect != null
            || usernameToConnect.equals(user.getUsername()))                       
        {
            UserThread userToConnectThread = userList.get(userToConnect);
            
            if(!userToConnectThread.isChatting())
            {
                USER2 = userToConnect;
                
                userToConnectThread.stop();
                
                userList.replace(USER2, this);
                user.getmyData_OUT().write(formatOutput("SWITCHED chats"));
            }
            else
                user.getmyData_OUT().write(
                        formatOutput("Failed to connect to client. " 
                                + usernameToConnect + " is busy."));
        }
        else
            user.getmyData_OUT().write(formatOutput("Offline client"));
    }

    private User searchUsersFromName(String name)
    {
        return userList.searchKeys(1, usr -> 
                usr.getUsername().equals(name) ? usr : null);
    }
    
    private void closeUserConnection(User user) throws IOException
    {
        if(isChatting())
        {
            User other = user.equals(USER1) ? USER2 : USER1;
            other.getmyData_OUT().write(formatOutput(
                    "SERVER: The person you were chatting with has disconnected."));
            new UserThread(other, this.userList).start();
        }
        
        user.getUserSocket().close();
        userList.remove(user);
        this.stop();
    }
    
    /**
     * Determines whether user 1 is chatting with user 2 or no one.
     * @return 
     */
    public synchronized boolean isChatting()
    {
        return USER2 != null;
    }
        
    /**
     * Format output to all sockets on a DataOutputStream
     * @param outputText
     * @return 
     */
    public static byte[] formatOutput(String outputText)
    {
        return (outputText + "\n").getBytes();
    }
}