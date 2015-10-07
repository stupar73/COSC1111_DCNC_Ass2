package Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client
{
    private Socket clientSocket;
    private String hostname;
    private int port;

    public Client(String hostname, int port)
    {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Run a client.
     *
     * @param client
     *            client to be run
     * @throws IOException
     *             if an I/O error occurs when creating the socket (See
     *             {@link #connect() connect})
     * @throws UnknownHostException
     *             if the IP address of the host could not be determined (See
     *             {@link #connect() connect})
     * @throws InterruptedException
     *             if any thread has interrupted the current thread. The
     *             interrupted status of the current thread is cleared when this
     *             exception is thrown (See {@link java.lang.Thread#sleep()
     *             Thread.sleep})
     */
    public static void run(Client client)
            throws UnknownHostException, IOException, InterruptedException
    {
        BufferedReader userIn = new BufferedReader(
                new InputStreamReader(System.in));

        // Attempt to connect to server
        client.connect();

        Boolean terminateSignal = false;
        while (!terminateSignal)
        {
            System.out.println("----------------------------------------");
            // Get data from user
            int[] data = null;
            Boolean readSuccessful = false;
            while (!readSuccessful)
            {
                readSuccessful = true;

                // Allow error to print first
                Thread.sleep(1);

                System.out.print("Enter data: ");
                String inputStr = userIn.readLine();

                if (inputStr.equalsIgnoreCase("EXIT") || inputStr.isEmpty())
                {
                    // Send empty array to signal server to exit
                    client.sendMessage(new int[0]);
                    System.out.println("Server terminated.");
                    terminateSignal = true;
                    break;
                }

                inputStr = inputStr.replaceAll(" ", "");
                int length = inputStr.length();

                data = new int[length];
                for (int i = 0; i < length; i++)
                {
                    int arrayPos = length - 1 - i;
                    char thisChar = inputStr.charAt(i);
                    // Check if input contains only a sequence of 1s and 0s
                    if (thisChar != '1' && thisChar != '0')
                    {
                        System.err.println(
                                "Error! Input data must be a binary "
                                        + "string");
                        readSuccessful = false;
                        break;
                    }
                    data[arrayPos] = Character.getNumericValue(thisChar);
                }
            }

            if (terminateSignal)
            {
                break;
            }

            HammingCode.printMessage(data);

            int[] message = HammingCode.encode(data);
            HammingCode.printHeaderedMessage(message);

            System.out.println();

            // Sending correct code first
            System.out.println("1. Correct message");
            System.out.print("Sending:  ");
            HammingCode.printMessage(message);
            client.sendMessage(message);
            System.out.println("Response: " + client.receiveMessage());
            System.out.println();

            // Sending message with one-bit error
            System.out.println("2. 1 bit flipped");
            message[(int) (Math.random() * message.length)] ^= 1;
            System.out.print("Sending:  ");
            HammingCode.printMessage(message);
            client.sendMessage(message);
            System.out.println("Response: " + client.receiveMessage());
        }
    }

    /**
     * Connects to server specified by {@code hostname} and {@code port} for
     * this Client
     *
     * @throws UnknownHostException
     *             if the IP address of the host could not be determined (See
     *             {@link java.net.Socket#Socket(java.lang.String, int) Socket})
     * @throws IOException
     *             if an I/O error occurs when creating the socket (See
     *             {@link java.net.Socket#Socket(java.lang.String, int) Socket})
     */
    public void connect() throws UnknownHostException, IOException
    {
        System.out.print("Attempting to connect to server at " + hostname + ":"
                + port + "... ");
        clientSocket = new Socket(hostname, port);
        System.out.println("Connection established");
    }

    /**
     * Sends {@code message} to the server in this Client's {@code clientSocket}
     *
     * @param message
     *            message to be sent
     * @throws IOException
     *             if an I/O error occurs while writing stream header (See
     *             {@link java.io.ObjectOutputStream#ObjectOutputStream(java.io.OutputStream)
     *             ObjectOutputStream})
     * @throws IOException
     *             any exception thrown by the underlying OutputStream (See
     *             {@link java.io.ObjectOutputStream#writeObject(Object)
     *             ObjectOutputStream.writeObject})
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
     *             if an I/O error occurs (See
     *             {@link java.io.BufferedReader#readLine()
     *             BufferedReader.readLine})
     */
    public String receiveMessage() throws IOException
    {
        BufferedReader serverIn = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

        return serverIn.readLine();
    }
}
