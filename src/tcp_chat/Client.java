package tcp_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author frank
 */
public class Client
{
    private Socket socket;
    private final Scanner scanner;
    private String myUsername;
    
    public Client(String address, int port)
    {
        createSocket(address, port);
        this.scanner = new Scanner(System.in);
    }
    
    private void createSocket(String address, int port)
    {
        try
        {
            this.socket = new Socket (address, port);
            initializeConnection();
            // connected to address and port and server has username
        }
        catch (IOException E)
        {
            System.out.println("Failed to create socket stuff.");
        }
    }
    
    /**
     * Send username to server
     */
    private void initializeConnection() throws IOException
    {
        PrintWriter output = new PrintWriter(this.socket.getOutputStream(),true);
        BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        
        while(true)
        {
            System.out.println("Please enter a username on the server");
            //String name = scanner.
        }
    }
}
