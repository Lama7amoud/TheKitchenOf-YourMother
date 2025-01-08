
package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.util.List;


public class Client extends AbstractClient {

    private static Client client = null;
    public static String host;
    public static int port;

    public Client(String host, int port) {
        super(host, port);
        this.host = host;
        this.port = port;
    }


    public static Client getClient() {
        if (client == null) {
            client = new Client(host, port);
        }
        return client;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof Warning) {
            // Handle warning message
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
            return;
        }
        if (msg instanceof List) {
            List<Meal> Menu = (List<Meal>) msg;
            EventBus.getDefault().post(Menu);
        }

    }

}
