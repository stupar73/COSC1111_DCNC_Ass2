package CS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import Common.HammingCode;

public class Client
{
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9090;

    private Socket clientSocket;
    private String hostname;
    private int port;

    public Client(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Connects to server specified by the value of {@code hostname} and
     * {@code port} for this Client
     *
     * @throws UnknownHostException
     * @throws IOException
     */
    public void connect() throws UnknownHostException, IOException
    {
        System.out.print("Attempting to connect to " + hostname + ":" + port +
                "... ");
        clientSocket = new Socket(hostname, port);
        System.out.println("Connection established");
    }

    /**
     * Sends {@code message} to the server in {@code clientSocket}
     *
     * @param message
     *            Message to be sent
     * @throws IOException
     */
    public void sendMessage(int[] message) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(
                clientSocket.getOutputStream());

        // Send int array as object
        out.writeObject(message);
    }

    /**
     * Reads incoming message from server in {@code clientSocket} and returns it
     *
     * @return Message read from server
     * @throws IOException
     */
    public String receiveMessage() throws IOException
    {
        BufferedReader serverIn = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

        return serverIn.readLine();
    }

    public static void main(String[] args)
    {
        try
        {
            Client client = new Client(HOSTNAME, PORT);
            BufferedReader userIn = new BufferedReader(
                    new InputStreamReader(System.in));

            // Attempt to connect to server
            client.connect();

            // Get data from user and encode using Hamming code
            System.out.print("Enter data: ");
            String dataStr = userIn.readLine();
            dataStr = dataStr.replaceAll(" ", "");

            int[] data = new int[dataStr.length()];
            for (int i = 0; i < dataStr.length(); i++)
            {
                data[i] = Character.getNumericValue(dataStr.charAt(i));
            }

            int[] message = HammingCode.encode(data);

            client.sendMessage(message);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return;
        }
    }
}
