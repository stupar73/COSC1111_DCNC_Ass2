package CSC;

import Common.Client;

public class SenderReceiverClientTest
{
    public static final String HOSTNAME = "localhost";
    public static final int PORT = 9091;

    public static void main(String[] args)
    {
        try
        {
            Client client = new Client(HOSTNAME, PORT);

            // Attempt to connect to server
            client.connect();

            String clientType = (String) client.receiveMessage();
            if (clientType.equals("SENDER"))
            {
                System.out.println();
                System.out.println("Connected as sender");
                client.runAsSender();
            }
            else if (clientType.equals("RECEIVER"))
            {
                System.out.println();
                System.out.println("Connected as receiver");
                client.runAsReceiver();
            }
            else
            {
                throw new Exception("Unknown reponse from server");
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
