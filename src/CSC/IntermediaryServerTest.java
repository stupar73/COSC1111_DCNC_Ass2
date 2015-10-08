package CSC;

import java.net.Socket;
import Common.HammingCode;
import Common.Server;

public class IntermediaryServerTest
{
    public static final int PORT = 9091;

    public static void main(String[] args)
    {
        try
        {
            Server server = new Server(PORT);

            // Start server
            server.start();

            /*
             * Wait for two clients to connect, first client will be sending a
             * message to the second client
             */
            Socket sendingClient = server.waitForClient("sending client");
            server.sendMessage(sendingClient, "SENDER");
            Socket receivingClient = server.waitForClient("receiving client");
            server.sendMessage(receivingClient, "RECEIVER");

            Boolean terminateSignal = false;
            while (!terminateSignal)
            {
                System.out.println("----------------------------------------");
                System.out.println("Listening to sending client...");
                System.out.println();

                // Receive message from sending client
                int[] message = (int[]) server.receiveMessage(sendingClient);

                if (message.length == 0)
                {
                    System.out.println("Terminating by request.");
                    // Inform receiving client of connection termination request
                    server.sendMessage(receivingClient, new int[0]);
                    terminateSignal = true;
                    break;
                }

                System.out.print("Received: ");
                HammingCode.printMessage(message);
                System.out.print("          Sending on to receiving client...");
                server.sendMessage(receivingClient, message);
                System.out.println(" Done!");

                String receiverResponse = (String) server
                        .receiveMessage(receivingClient);
                if (!receiverResponse.equals("ACK"))
                {
                    throw new Exception("Unable to pass message on to receiving"
                            + " client");
                }
                System.out.println("Response: " + receiverResponse);

                server.sendMessage(sendingClient, "ACK");
            }

        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
