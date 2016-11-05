package tcp_chat;

import java.net.ServerSocket;
import java.util.Hashtable;

/**
 *
 * @author frank
 */
public class Server
{
    /*
    
    */
    private Hashtable<String, ServerSocket> userSockets;
    
    public Server()
    {
        this.userSockets = new Hashtable<>();
    }

}
