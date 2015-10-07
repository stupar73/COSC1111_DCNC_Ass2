package CSCS;

import Common.Client;
import Common.Server;

public class ClientServerTest
{
    public static final String HOSTNAME = "localhost";
    public static final int PORT = 9091;

    public static void main(String[] args)
    {
        try
        {
            // Try to connect to a server as a client
            Client client = new Client(HOSTNAME, PORT);
            Client.run(client);
        }
        catch (Exception e)
        {
            // No server found, start one and act as server
            System.out.println("Server not found!");
            Server server = new Server(PORT);

            try
            {
                Server.run(server);
            }
            catch (Exception e1)
            {
                System.err.println(e1.getMessage());
                return;
            }
        }
    }

}
