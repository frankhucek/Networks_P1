package tcp_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private final PrintWriter myData_OUT;
    private       PrintWriter theirData_OUT; 
    
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
        
        this.myData_OUT = new PrintWriter(new DataOutputStream(myListeningSocket.getOutputStream()));
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
            myListeningSocket.close();
        } catch (IOException ex)
        {
            System.out.println("ERROR sending data");
        }
    }
    
    private void initializeUsername() throws IOException
    {
        // FIRST MESSAGE -> send connection confirmation to client
        myData_OUT.print("CONNECTED!");
        myData_OUT.flush();
        System.out.println("SERVER: connected to client");
        
        while (this.myUsername == null)
        {
            myData_OUT.println("Please enter a username");
            myData_OUT.flush();
            String name = myData_IN.readLine();            /// REFACTOR SO THIS ISNT IN INFINITE LOOP
            if(name != null && !userList.containsKey(name))
            {
                myUsername = name;
                userList.put(myUsername, this);
                myData_OUT.println("set");
            }
        }
        
        System.out.println("SERVER: Username set to " + myUsername);
        
    }
}
