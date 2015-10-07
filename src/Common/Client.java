package Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
    public static final String HOSTNAME = "localhost";
    public static final int PORT = 9090;

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
     *            message to be sent
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
}
