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
        this.myUsername = null;
        this.isChatting = false;
        
        this.myData_IN = new BufferedReader (
                         new InputStreamReader (
                                 myListeningSocket.getInputStream()));
        
        this.myData_OUT = new DataOutputStream(myListeningSocket.getOutputStream());
    }

    
    /*
    read initial input to get user/state
    typical case:   put this user in hashmap with connsocket,
                    request user, 
                    find user in hashmap,
                    retrieve that user's connection socket from hashmap,
                    this thread listens from one end and writes that to the other end (possible issue with concurrent read/writes to sockets)
                    
    */
    
    @Override
    public void run()
    {
        try
        {
            initializeUsername();
        } catch (IOException ex)
        {
            System.out.println("ERROR sending data");
        }
    }
    
    private void initializeUsername() throws IOException
    {
        myData_OUT.write("CONNECTED!".getBytes());
        System.out.println("SERVER: connected to client");
        
        myListeningSocket.close();
    }
}
