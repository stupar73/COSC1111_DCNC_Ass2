package CS;

import java.net.Socket;
import Common.HammingCode;
import Common.Server;

public class ServerTest
{

    public static void main(String[] args)
    {
        Server server = new Server(Server.PORT);

        try
        {
            // Start server
            server.start();

            // Wait for client to connect
            Socket client = server.waitForClient();
            System.out.println("Listening to client...");

            Boolean terminateSignal = false;
            while (!terminateSignal)
            {
                // Receive message from client
                int[] message = server.receiveMessage(client);

                System.out.println("----------------------------------------");

                if (message.length == 0)
                {
                    System.out.println("Terminating by request.");
                    terminateSignal = true;
                    break;
                }

                System.out.print("Client: ");
                HammingCode.printMessage(message);

                // Check message was received with no error
                int errorLocation = HammingCode.checkHammingCode(message);
                if (errorLocation < 0)
                {
                    System.out.println("No error detected!");
                }
                else
                {
                    System.out.println("Error detected at bit position "
                            + (errorLocation + 1) + "... Corrected!");
                    message[errorLocation] ^= 1;
                }

                int[] data = HammingCode.removeParity(message);
                System.out.print("Data:   ");
                HammingCode.printMessage(data);

                server.respondToClient(client, "ACK");
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return;
        }
    }

}
