package il.cshaifasweng.OCSFMediatorExample.server;
import java.io.IOException;

public class App 
{
    private static SimpleServer server;


    public static void main( String[] args ) throws IOException
    {
        DataManager.main(args);
        server = new SimpleServer(3000 );
        server.listen();
        System.out.println("The server is listening");
    }
}
