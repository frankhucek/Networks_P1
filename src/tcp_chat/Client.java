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
    private static final String MESSAGE_DELINEATER1 = "Enter";
    private static final String MESSAGE_DELINEATER2 = "Escape";
    
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
            
            boolean keepRunning = true;
            String dataIn;
            while(keepRunning)
            {
                System.out.print("$ ");
                dataIn = scanner.nextLine();
                keepRunning = handleInput(dataIn);
            }
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
    
    private boolean handleInput(String data) throws IOException
    {
        boolean keepRunning = true;
        if(data.contains(MESSAGE_DELINEATER1))
            data = data.replace(MESSAGE_DELINEATER1, "");
        else if(data.contains(MESSAGE_DELINEATER2))
            data = data.replace(MESSAGE_DELINEATER2, "");
            
        
        if(data.contains("-c"))
        {
            myData_OUT.write(formatOutput(data));
            String serverResponse = myData_IN.readLine();
            System.out.println(serverResponse);
            if(serverResponse.contains("SWITCHED"))
            {
                myData_OUT.write(formatOutput("Connected to " + myUsername));
                System.out.println(myData_IN.readLine());
            }
        }
        else if(data.contains("-q"))
        {
            myData_OUT.write(formatOutput(data));
            this.socket.close();
            
            keepRunning = false;
        }
        else
        {
            myData_OUT.write(formatOutput(data));
            System.out.println(myData_IN.readLine());
        }
        
        return keepRunning;
    }
    
    /**
     * SERVER COMMANDS DELINEATED BY '-'
     * ALL server commands of the form "-x input"
     * where x is the option
     * To chat with another user: -c UserToChatWith
     * @param input
     * @return 
     */
    private static boolean isServerCommand(String input)
    {
        return (input != null && input.startsWith("-"));
    }
        
    public static byte[] formatOutput(String outputText)
    {
        return (outputText + "\n").getBytes();
    }
    
    public static void main(String[] args)
    {
        Client c = new Client("127.0.0.1", 1884);
    }
}
