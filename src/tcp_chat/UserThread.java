package tcp_chat;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author frank
 */
public class UserThread extends Thread
{
    private final Socket myListeningSocket;
    private Socket talkingToSocket;
    
    public String myUsername;
    public boolean isChatting;
    
    private ConcurrentHashMap<String, UserThread> userList;
    
    public UserThread(Socket connSocket, ConcurrentHashMap<String, UserThread> userSockets)
    {
        this.myListeningSocket = connSocket;  
        this.talkingToSocket = null;
        
        this.userList = userSockets;
        this.myUsername = null;
        this.isChatting = false;
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
        initializeUsername();
    }
    
    private static void initializeUsername()
    {
        
    }
}
