package Common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static final int PORT = 9090;

    private ServerSocket server;
    private int port;

    public Server(int port)
    {
        this.port = port;
    }

    /**
     * TODO
     *
     * @throws IOException
     */
    public void start() throws IOException
    {
        System.out.print("Starting server on port " + port + "... ");
        server = new ServerSocket(PORT);
        System.out.println("Done!");
    }

    /**
     * TODO
     *
     * @return
     * @throws IOException
     */
    public Socket waitForClient() throws IOException
    {
        // Block until client connects
        System.out.print("Waiting for client... ");
        Socket client = server.accept();
        System.out.println("Client connected!");

        return client;
    }

    /**
     * TODO
     *
     * @param client
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public int[] receiveMessage(Socket client)
            throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());

        int[] message = (int[]) in.readObject();

        return message;
    }

    /**
     * TODO
     *
     * @param client
     * @param message
     * @throws IOException
     */
    public void respondToClient(Socket client, String message)
            throws IOException
    {
        OutputStreamWriter out = new OutputStreamWriter(
                client.getOutputStream());

        out.write(message + "\r\n");
        out.flush();
    }
}
