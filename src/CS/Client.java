package CS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import Common.HammingCode;

public class Client
{
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9090;

    private Socket client;
    private String hostname;
    private int port;

    public Client(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws UnknownHostException, IOException
    {
        System.out.print("Attempting to connect to " + hostname + ":" + port +
                "... ");
        client = new Socket(hostname, port);
        System.out.println("Connection established");
    }

    public void sendMessage(int[] message) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(
                client.getOutputStream());

        // Send int array as object
        out.writeObject(message);
    }

    public String receiveMessage() throws IOException
    {
        BufferedReader serverIn = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        return serverIn.readLine();
    }

    public static void main(String[] args)
    {
        Client client = new Client(HOSTNAME, PORT);
        BufferedReader userIn = new BufferedReader(
                new InputStreamReader(System.in));

        // Attempt to connect to server
        try
        {
            client.connect();
        }
        catch(Exception e)
        {
            System.err.println("Failed! Unable to connect to server");
            return;
        }

        // Get data from user and encode using Hamming code
        String dataStr = null;
        try
        {
            System.out.print("Enter data: ");
            dataStr = userIn.readLine();
            dataStr = dataStr.replaceAll(" ", "");
        }
        catch(IOException e)
        {
            // Should never happen?
            e.printStackTrace();
        }

        int[] data = new int[dataStr.length()];
        for(int i = 0; i < dataStr.length(); i++)
        {
            data[i] = Character.getNumericValue(dataStr.charAt(i));
        }

        int[] message = HammingCode.generateHammingCode(data);

        try
        {
            client.sendMessage(message);
        }
        catch(IOException e)
        {
            // Should never happen?
            e.printStackTrace();
        }
    }
}
