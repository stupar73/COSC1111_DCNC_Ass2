package CS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import Common.HammingCode;

public class Server
{
    private static final int PORT = 9090;

    private ServerSocket server;
    private int port;

    public Server(int port)
    {
        this.port = port;
    }

    public void start() throws IOException
    {
        System.out.print("Starting server on port " + port + "... ");
        server = new ServerSocket(PORT);
        System.out.println("Done!");
    }

    public Socket waitForClient() throws IOException
    {
        // Block until client connects
        System.out.print("Waiting for client... ");
        Socket client = server.accept();
        System.out.println("Client connected!");

        return client;
    }

    public int[] receiveMessage(Socket client)
            throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());

        int[] message = (int[]) in.readObject();

        return message;
    }

    public static void main(String[] args)
    {
        Server server = new Server(PORT);

        // Start server
        try
        {
            server.start();
        }
        catch(IOException e)
        {
            System.err.println("Unable to start server!");
            return;
        }

        // Wait for client to connect
        Socket client;
        try
        {
            client = server.waitForClient();
        }
        catch(IOException e)
        {
            System.err.println("Error waiting for client!");
            return;
        }

        // Receive message from client
        int[] message = null;
        try
        {
            message = server.receiveMessage(client);
            System.out.print("Client: ");
            HammingCode.printMessage(message);
        }
        catch(ClassNotFoundException | IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Check message was received with no error
        int errorLocation = HammingCode.checkHammingCode(message);
        if(errorLocation < 0)
        {
            System.out.println("No error detected!");
            // TODO Remove parity bits and print data
        }
        else
        {
            System.out.println("Error detected at bit position "
                    + (errorLocation + 1) + ", correcting...");
            message[errorLocation] ^= 1;
            System.out.print("Data: ");
            // TODO Remove parity bits and print data
        }
    }
}
