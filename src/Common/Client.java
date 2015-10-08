package Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
     * Run client as a sender.
     *
     * @throws InterruptedException
     *             see {@link java.lang.Thread#sleep() Thread.sleep}
     * @throws IOException
     *             see {@link java.io.BufferedReader#readLine
     *             BufferedReader.readLine}, {@link #sendMessage(Object)
     *             sendMessage}, {@link #receiveMessage() receiveMesage}
     * @throws ClassNotFoundException
     *             see {@link #receiveMessage() receiveMessage}
     */
    public void runAsSender()
            throws InterruptedException, IOException, ClassNotFoundException
    {
        BufferedReader userIn = new BufferedReader(
                new InputStreamReader(System.in));

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
                    sendMessage(new int[0]);
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
                        System.err.println("Error! Input data must be a binary "
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

            System.out.println();

            int[] message = HammingCode.encode(data);
            HammingCode.printHeaderedMessage(message);

            System.out.println();

            // Sending correct code first
            System.out.println("1. Correct message");
            System.out.print("Sending:  ");
            HammingCode.printMessage(message);
            sendMessage(message);
            System.out.println("Response: " + receiveMessage());
            System.out.println();

            // Sending message with one-bit error
            System.out.println("2. 1 bit flipped");
            message[(int) (Math.random() * message.length)] ^= 1;
            System.out.print("Sending:  ");
            HammingCode.printMessage(message);
            sendMessage(message);
            System.out.println("Response: " + receiveMessage());
        }
    }

    /**
     * Run client as a receiver.
     *
     * @throws IOException
     *             see {@link #receiveMessage() receiveMesage},
     *             {@link #sendMessage(Object) sendMessage}
     * @throws ClassNotFoundException
     *             see {@link #receiveMessage() receiveMessage}
     */
    public void runAsReceiver() throws IOException, ClassNotFoundException
    {
        Boolean terminateSignal = false;
        while (!terminateSignal)
        {
            System.out.println("----------------------------------------");
            System.out.println("Listening to server...");
            System.out.println();

            // Receive message from server
            int[] message = (int[]) receiveMessage();

            if (message.length == 0)
            {
                System.out.println("Connection closed by sender.");
                terminateSignal = true;
                break;
            }

            System.out.print("Received:  ");
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

            int[] data = HammingCode.removeParity(message);
            System.out.print("Data:      ");
            HammingCode.printMessage(data);

            sendMessage("ACK");
        }
    }

    /**
     * Connects to server specified by {@code hostname} and {@code port} for
     * this Client
     *
     * @throws UnknownHostException
     *             see {@link java.net.Socket#Socket(java.lang.String, int)
     *             Socket}
     * @throws IOException
     *             see {@link java.net.Socket#Socket(java.lang.String, int)
     *             Socket}
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
     *             see
     *             {@link java.io.ObjectOutputStream#ObjectOutputStream(java.io.OutputStream)
     *             ObjectOutputStream}, {@link java.net.Socket#getOutputStream()
     *             Socket.getOutputStream},
     *             {@link java.io.ObjectOutputStream#writeObject(Object)
     *             ObjectOutputStream.writeObject})
     */
    public void sendMessage(Object message) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(
                clientSocket.getOutputStream());

        out.writeObject(message);
    }

    /**
     * Reads incoming message from server in {@code clientSocket} and returns it
     *
     * @return Message read from server
     * @throws IOException
     *             see
     *             {@link java.io.ObjectInputStream#ObjectInputStream(java.io.InputStream)
     *             ObjectInputStream}, {@link java.net.Socket#getInputStream()
     *             Socket.getInputStream},
     *             {@link java.io.ObjectInputStream#readObject()
     *             ObjectInputStream.readObject}
     * @throws ClassNotFoundException
     *             see {@link java.io.ObjectInputStream#readObject()
     *             ObjectInputStream.readObject})
     */
    public Object receiveMessage() throws IOException, ClassNotFoundException
    {
        ObjectInputStream serverIn = new ObjectInputStream(
                clientSocket.getInputStream());

        return serverIn.readObject();
    }
}
