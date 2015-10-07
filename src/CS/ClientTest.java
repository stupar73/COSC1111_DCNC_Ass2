package CS;

import Common.Client;

public class ClientTest
{
    public static final String HOSTNAME = "localhost";
    public static final int PORT = 9090;

    public static void main(String[] args)
    {
        Client client = new Client(HOSTNAME, PORT);

        try
        {
            Client.run(client);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return;
        }
    }
}
