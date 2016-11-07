package tcp_chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author frank
 */
public class Server
{
    /*
        <User, ConnectionSocket>
    */
    private ConcurrentHashMap<String, Socket> userSockets;
    private ServerSocket welcomeSocket;
    private final int PORT_NUMBER;
    
    public Server(int PORT)
    {
        this.userSockets = new ConcurrentHashMap<>();
        PORT_NUMBER = PORT;
    }
    
    public void runChatServer()
    {
        try
        {
            this.welcomeSocket = new ServerSocket(PORT_NUMBER);
            
            while(true)
            {
                Socket connSocket = welcomeSocket.accept();
                // pass userSockets by reference for all threads to use
                new ServerThread(connSocket, this.userSockets).start();
                // could pass "this" to ServerThread and make userSockets visible
                // should work to pass hashmap bc same instance
            }
        } 
        catch (IOException ex)
        {
            System.out.println("IOException creating sockets.");
        }
    }

    public static void main(String[] args)
    {
        Server server = new Server(1884);
        server.runChatServer();
    }
}
