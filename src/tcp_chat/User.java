package tcp_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Class used to help with encapsulation of 
 * username, socket, reader, and writer
 * @author frank
 */
public class User
{
    private Socket userSocket;
    private DataOutputStream myData_OUT;
    private BufferedReader myData_IN;
    private String username;
    
    public User(Socket userSocket) throws IOException
    {
        this.userSocket = userSocket;
        this.myData_OUT = new DataOutputStream(this.userSocket.getOutputStream());
        this.myData_IN = new BufferedReader(new InputStreamReader(
                                                userSocket.getInputStream()));
    }
    
    public User(String username, Socket userSocket) throws IOException
    {
        this(userSocket);
        this.username = username;
    }

    /**
     * @return the userSocket
     */
    public Socket getUserSocket()
    {
        return userSocket;
    }

    /**
     * @param userSocket the userSocket to set
     */
    public void setUserSocket(Socket userSocket)
    {
        this.userSocket = userSocket;
    }

    /**
     * @return the myData_OUT
     */
    public DataOutputStream getmyData_OUT()
    {
        return myData_OUT;
    }

    /**
     * @param myData_OUT the myData_OUT to set
     */
    public void setmyData_OUT(DataOutputStream myData_OUT)
    {
        this.myData_OUT = myData_OUT;
    }

    /**
     * @return the myData_IN
     */
    public BufferedReader getmyData_IN()
    {
        return myData_IN;
    }

    /**
     * @param myData_IN the myData_IN to set
     */
    public void setmyData_IN(BufferedReader myData_IN)
    {
        this.myData_IN = myData_IN;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    
}
