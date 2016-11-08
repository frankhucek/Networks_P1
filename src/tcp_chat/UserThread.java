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
    private final Socket myListeningSocket;
    private Socket talkingToSocket;
    
    private ConcurrentHashMap<String, UserThread> userList; 
    
    private User USER1, USER2;
    
    public UserThread(Socket connSocket, 
            ConcurrentHashMap<String, UserThread> userSockets) throws IOException
    {
        this.myListeningSocket = connSocket;  
        this.talkingToSocket = null;
        
        this.userList = userSockets;
        
        this.USER1 = new User(connSocket);
        this.USER2 = null; // until connected to second user
    }
    public UserThread(Socket connSocket, 
            ConcurrentHashMap<String, UserThread> userSockets, 
            User existingUser) throws IOException
    {
        this(connSocket,userSockets);
        this.USER1 = existingUser;
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
                handleInput(USER1);
                handleInput(USER2);
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
        if(userList.containsKey(inputName))
            return false;
        else
        {    
            userList.put(inputName, this);
            USER1.setUsername(inputName);
            return true;
        } 
    }
    ////////////////// ABOVE IS SERVER - CLIENT INITIALIZATION /////////////////
     
    
    private void handleInput(User user) throws IOException
    {
        if(user != null)
        {
            String userIN = user.getmyData_IN().readLine();
            
            if(isServerCommand(userIN))
            {
                handleServerCommand(user, userIN);
            }
            else // relay messages
            {
                
            }
        }
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
            String command = input.substring(0, 2);
            String commandData = input.substring(3);
            switch (command)
            {
                case "-c": // chat
                    if(isChatting()) // if is in chat and want to connect with other
                    {
                        // move other user to its own thread
                    }
                    this.userChatSetup(user, commandData);
                    
                    break;
                    
                default:
                    // some default case if not an actual command
                    break;
            }
                    
        }
        
        handleInput(user); // can therefore call as many commands as possible
    }
    
    private void userChatSetup(User user, String userToConnect)
    {
        
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


/*

    logic stuff.

                input = USER1.myData_IN.readLine(); // server awaits input
                
                // check for control message
                
                if(!isChatting) // Listening
                {
                    // send input to Listening method
                }
                else // isChatting
                {
                    // send input to USER 
                    // REMEMBER WHEN CONNECTING TO USER, PULL USER TO THIS THREAD, DO NOT PUSH (logic stuff)
                        // - check if they're chatting before pulling them here
                        // when reading from other user, will have to check them for control messages
                }
                
                /*
                I.READ LINE FIRST???
                
                II. either "Listening" or talking to another user based on flag isChatting - determines where data written to
                III. both options process control messages the same
                
                ^^^ Implemented above
                /
                break;*/