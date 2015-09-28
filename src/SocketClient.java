import java.io.*;
import java.net.*;
import java.util.*;

public class SocketClient
{
    private String hostname;
    private int port;
    Socket socketClient;

    public SocketClient(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() throws UnknownHostException, IOException
    {
        System.out.println("Connecting to " + hostname + ":" + port + "...");
        socketClient = new Socket(hostname, port);
        System.out.println("Connection established");
    }

    public void sendMessage(int[] message) throws IOException
    {
        DataOutputStream out = new DataOutputStream(
                socketClient.getOutputStream());

        // TODO Send message to server
    }

    public String receiveMessage() throws IOException
    {
        BufferedReader serverIn = new BufferedReader(
                new InputStreamReader(socketClient.getInputStream()));

        return serverIn.readLine();
    }

    public static void main(String[] args)
    {
        SocketClient client = new SocketClient("localhost", 9090);
        Scanner userIn = new Scanner(System.in);

        // Message should be 11 bits (7 bits data, 4 bits parity)
        System.out.print("Enter data: ");

        String dataStr = userIn.next();
        dataStr = dataStr.replaceAll(" ", "");

        int[] data = new int[dataStr.length()];
        for(int i = 0; i < dataStr.length(); i++)
        {
            data[i] = Character.getNumericValue(dataStr.charAt(i));
        }

        int[] message = HammingCode.generateHammingCode(data);

        // TODO Send message to server

        userIn.close();
    }
}
