package CS;

import Common.Server;

public class ServerTest
{
    public static final int PORT = 9090;

    public static void main(String[] args)
    {
        Server server = new Server(PORT);

        try
        {
            Server.run(server);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            return;
        }
    }
}
