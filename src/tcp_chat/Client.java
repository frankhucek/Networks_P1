package tcp_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
    
    private BufferedReader myData_IN;
    private PrintWriter myData_OUT;
    
    public Client(String address, int port)
    {
        this.scanner = new Scanner(System.in);
        this.myUsername = null;
        createSocket(address, port);
    }
    
    private void createSocket(String address, int port)
    {
        try
        {
            this.socket = new Socket (address, port);
            System.out.println("Created socket to server");
            myData_IN = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            myData_OUT = new PrintWriter(new DataOutputStream(this.socket.getOutputStream()));
            initializeUsername();
            // connected to address and port and server has username
        }
        catch (IOException E)
        {
            System.out.println("Failed to create socket stuff.");
            System.exit(1);
        }
    }
    
    /**
     * Send username to server
     */
    private void initializeUsername() throws IOException
    {      
        System.out.println(myData_IN.readLine());
        String fromServer = "";
        while(!fromServer.equals("set"))
        {
            String name = scanner.nextLine();
            System.out.println(name + "repeat");
            myData_OUT.println(name);
            System.out.println((fromServer = myData_IN.readLine()));
        }
        System.out.println("Username set to");
    }
    
    public String readSocket()
    {
        try
        {
            return myData_IN.readLine();
        } 
        catch (IOException ex)
        {
            return "CLIENT COULDN'T READ FROM SOCKET";
        }
    }
    
    public static void main(String[] args)
    {
        Client c = new Client("127.0.0.1", 1884);
        System.out.println(c.readSocket());
    }
}
