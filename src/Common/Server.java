package Common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private ServerSocket server;
    private int port;

    public Server(int port)
    {
        this.port = port;
    }

    /**
     * Run a server.
     *
     * @param server
     *            server object to run
     * @throws IOException
     *             see {@link #start() start}, {@link #waitForClient(String)
     *             waitForClient}, {@link #receiveMessage(Socket)
     *             receiveMessage}, {@link #sendMessage(Socket, Object)
     *             sendMessage}
     * @throws ClassNotFoundException
     *             see {@link #receiveMessage(Socket) receiveMessage}
     */
    public static void run(Server server)
            throws IOException, ClassNotFoundException
    {
        // Start server
        server.start();

        // Wait for client to connect
        Socket client = server.waitForClient("client");

        Boolean terminateSignal = false;
        while (!terminateSignal)
        {
            System.out.println("----------------------------------------");
            System.out.println("Listening to client...");
            System.out.println();

            // Receive message from client
            int[] message = (int[]) server.receiveMessage(client);

            if (message.length == 0)
            {
                System.out.println("Terminating by request.");
                terminateSignal = true;
                break;
            }

            System.out.print("Client:    ");
            HammingCode.printMessage(message);

            // Check message was received with no error
            int errorLocation = HammingCode.checkHammingCode(message);
            if (errorLocation < 0)
            {
                System.out.println("           No error detected!");
            }
            else
            {
                System.out.println("           Error detected at bit "
                        + "position " + (errorLocation + 1) + "...");
                message[errorLocation] ^= 1;
                System.out.print("Corrected: ");
                HammingCode.printMessage(message);
            }

            int[] data = HammingCode.removeParityBits(message);
            System.out.print("Data:      ");
            HammingCode.printMessage(data);

            server.sendMessage(client, "ACK");
        }
    }

    /**
     * Start a server on this {@code Server}'s {@code port}.
     *
     * @throws IOException
     *             see {@link java.net.ServerSocket#ServerSocket(int)
     *             ServerSocket}
     */
    public void start() throws IOException
    {
        System.out.print("Starting server on port " + port + "... ");
        server = new ServerSocket(port);
        System.out.println("Done!");
    }

    /**
     * Wait for a client to connect this this {@code server}.
     *
     * @param clientType
     *            type of client expected (e.g. "client",
     *            "sending client", "receiving client", etc.)
     * @return {@code client Socket}
     * @throws IOException
     *             see {@link java.net.ServerSocket#accept()
     *             ServerSocket.accept}
     */
    public Socket waitForClient(String clientType) throws IOException
    {
        // Block until client connects
        System.out.print("Waiting for " + clientType + "... ");
        Socket client = server.accept();
        System.out.println("Client connected!");

        return client;
    }

    /**
     * Receive message from {@code client}
     *
     * @param client
     *            client socket to get message from
     * @return message received from {@code client}
     * @throws IOException
     *             see
     *             {@link java.io.ObjectInputStream#ObjectInputStream(java.io.InputStream)
     *             ObjectInputStream}, {@link java.net.Socket#getInputStream()
     *             Socket.getInputStream},
     *             {@link java.io.ObjectInputStream#readObject()
     *             ObjectInputStream.readObject}
     * @throws ClassNotFoundException
     *             see {@link java.io.ObjectInputStream#readObject() readObject}
     */
    public Object receiveMessage(Socket client)
            throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());

        return in.readObject();
    }

    /**
     * Send {@code message} to {@code client}
     *
     * @param client
     *            client socket to send message to
     * @param message
     *            message to be sent
     * @throws IOException
     *             see
     *             {@link java.io.ObjectOutputStream#ObjectOutputStream(java.io.OutputStream)
     *             ObjectOutputStream}, {@link java.net.Socket#getOutputStream()
     *             Socket.getOutputStream},
     *             {@link java.io.ObjectOutputStream#writeObject(Object)
     *             ObjectOutputStream.writeObject},
     *             {@link java.io.ObjectOutputStream#flush()
     *             ObjectOutputStream.flush}
     */
    public void sendMessage(Socket client, Object message)
            throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(
                client.getOutputStream());

        out.writeObject(message);
        out.flush();
    }
}
