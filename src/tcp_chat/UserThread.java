package tcp_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    
    public String myUsername;
    public boolean isChatting;
    
    private ConcurrentHashMap<String, UserThread> userList;
    
    /*
    IN to THIS UserThread
    OUT to Client(s)
    */
    private final BufferedReader myData_IN;
    private       BufferedReader theirData_IN;
    private final DataOutputStream myData_OUT;
    private       DataOutputStream theirData_OUT; 
    
    public UserThread(Socket connSocket, 
            ConcurrentHashMap<String, UserThread> userSockets) throws IOException
    {
        this.myListeningSocket = connSocket;  
        this.talkingToSocket = null;
        
        this.userList = userSockets;
        this.myUsername = "";
        this.isChatting = false;
        
        this.myData_IN = new BufferedReader (
                         new InputStreamReader (
                                 myListeningSocket.getInputStream()));
        
        this.myData_OUT = new DataOutputStream(myListeningSocket.getOutputStream());
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
                input = myData_IN.readLine(); // server awaits input
                
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
                */
                break;
            }
            myListeningSocket.close();
        } catch (IOException ex)
        {
            System.out.println("ERROR sending data");
        }
    }
    
    /*
    read initial input to get user/state
    typical case:   put this user in hashmap with connsocket,
                    request user, 
                    find user in hashmap,
                    retrieve that user's connection socket from hashmap,
                    this thread listens from one end and writes that to the other end (possible issue with concurrent read/writes to sockets)
                    
    */
    
    private void initializeUsername() throws IOException
    {
        System.out.println("SERVER: connected to client");
        boolean set;
        do
        {
            String input = myData_IN.readLine(); // FIRST MESSAGE RECEIVED
            
            set = setUsername(input);
            
            if(!set)
                myData_OUT.write(UserThread.formatOutput("Invalid Username. Please enter a new one."));
            else
                myData_OUT.write(UserThread.formatOutput("set"));
        } while (!set);
        
        System.out.println("SERVER: Client username set to " + myUsername);
    }
    
    private boolean setUsername(String inputName)
    {
        if(userList.containsKey(inputName))
            return false;
        else
        {    
            userList.put(inputName, this);
            myUsername = inputName;
            return true;
        } 
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
