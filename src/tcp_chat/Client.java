package tcp_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private DataOutputStream myData_OUT;
    
    public Client(String address, int port)
    {
        this.scanner = new Scanner(System.in);
        this.myUsername = null;
        
        initializeClient(address, port);
    }
    
    private void initializeClient(String address, int port)
    {
        try
        {
            this.socket = new Socket (address, port);
            
            System.out.println("Connected to " + address + " on port " + port);
            
            myData_IN = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            myData_OUT = new DataOutputStream(this.socket.getOutputStream());
            
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
        boolean set = false;
        String response;
        do
        {
            System.out.print("Username: ");
            myUsername = scanner.nextLine();
                
                // FIRST MESSAGE IN CONNECTION
            myData_OUT.write(Client.formatOutput(myUsername)); // 
            
            response = myData_IN.readLine();
            System.out.println(response);

        }while(!"set".equals(response));
        System.out.println("Username set on server to " + myUsername);
    }
    
    public static void main(String[] args)
    {
        Client c = new Client("127.0.0.1", 1884);
    }
    
    public static byte[] formatOutput(String outputText)
    {
        return (outputText + "\n").getBytes();
    }
}
