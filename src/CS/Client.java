package CS;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        DataOutputStream out = new DataOutputStream(
                client.getOutputStream());

        // TODO Send message to server
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

        try
        {
            client.connect();

            // Message should be 11 bits (7 bits data, 4 bits parity)
            System.out.print("Enter data: ");

            String dataStr = userIn.readLine();
            dataStr = dataStr.replaceAll(" ", "");

            int[] data = new int[dataStr.length()];
            for(int i = 0; i < dataStr.length(); i++)
            {
                data[i] = Character.getNumericValue(dataStr.charAt(i));
            }

            int[] message = HammingCode.generateHammingCode(data);

            // TODO Send message to server
        }
        catch(UnknownHostException e)
        {
            System.err.println("Failed! Host unknown");
        }
        catch(IOException e)
        {
            System.err.println("Failed! Server may be down");
        }
    }
}
