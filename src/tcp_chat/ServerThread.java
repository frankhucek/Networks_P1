package tcp_chat;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author frank
 */
public class ServerThread extends Thread
{
    private final Socket connSocket;
    
    private ConcurrentHashMap<String, Socket> userSockets;
    
    public ServerThread(Socket connSocket, ConcurrentHashMap<String, Socket> userSockets)
    {
        this.connSocket = connSocket;
        this.userSockets = userSockets;
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
        
    }
}
