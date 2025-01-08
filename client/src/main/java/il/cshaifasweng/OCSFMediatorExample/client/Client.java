
package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

public class Client extends AbstractClient {

    private static Client client = null;
    private String host;
    private int port;
    private String symbol;

    public Client(String host, int port) {
        super(host, port);
        this.host = host;
        this.port = port;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public static Client getClient(String host, int port) {
        if (client == null) {
            client = new Client(host, port);
        }
        return client;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof Warning) {
            // Handle warning message
           // EventBus.getDefault().post(new WarningEvent((Warning) msg));
            return;
        }
       // if (msg.toString().equals()) {
           // EventBus.getDefault().post();
       // }
       // if (msg.toString().equals()) {
            //EventBus.getDefault().post();
      //  }
       // if (msg.toString().equals()) {
           // EventBus.getDefault().post();
       // }
        //if (msg.toString().equals()) {
           // EventBus.getDefault().post();
        //}
    }

}
