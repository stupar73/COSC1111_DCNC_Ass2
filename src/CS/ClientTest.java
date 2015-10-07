package CS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import Common.Client;
import Common.HammingCode;

public class ClientTest
{

    public static void main(String[] args)
    {
        try
        {
            Client client = new Client(Client.HOSTNAME, Client.PORT);
            BufferedReader userIn = new BufferedReader(
                    new InputStreamReader(System.in));

            // Attempt to connect to server
            client.connect();

            Boolean terminateSignal = false;
            connectionLoop: while (!terminateSignal)
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

                    if (inputStr.equals("EXIT"))
                    {
                        // Send empty array to signal server to exit
                        client.sendMessage(new int[0]);
                        System.out.println("Server terminated.");
                        terminateSignal = true;
                        break connectionLoop;
                    }

                    inputStr = inputStr.replaceAll(" ", "");
                    int length = inputStr.length();

                    data = new int[inputStr.length()];
                    for (int i = length - 1; i >= 0; i--)
                    {
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
                        data[i] = Character.getNumericValue(thisChar);
                    }
                }

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
                System.out.print("Sending: ");
                HammingCode.printMessage(message);
                client.sendMessage(message);
                System.out.println("Response: " + client.receiveMessage());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return;
        }
    }

}
