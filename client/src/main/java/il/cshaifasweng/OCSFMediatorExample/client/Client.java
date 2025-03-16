
package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.List;

public class Client extends AbstractClient {

    private static Client client = null;
    public static String host;
    public static int port;
    static AuthorizedUser userAtt;

    public Client(String host, int port) {
        super(host, port);
        this.host = host;
        this.port = port;
    }


    public static Client getClient() {
        if (client == null) {
            client = new Client(host, port);
        }
        if (userAtt == null) {
            userAtt = getClientAttributes();
        }
        return client;
    }

    public static void resetClientAttributes(){
        userAtt.resetAttributes();
    }

    public static AuthorizedUser getClientAttributes(){
        if (userAtt == null)
        {
            return userAtt = new AuthorizedUser();
        }
        return userAtt;
    }

    public static String getClientUsername(){
        if (userAtt.getUsername() != null)
        {
            return userAtt.getUsername();
        }
        return null;
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if(msg instanceof String) {
            System.out.println(msg);
            if(msg.equals("client added successfully")){
                App.switchScreen("Log In Page");
            }
        } else if (msg instanceof Warning) {
            // Handle warning message
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
            return;
        } else if (msg instanceof List) {
            List<Meal> menu = (List<Meal>) msg;
            EventBus.getDefault().post(menu);
        }
        else if (msg instanceof AuthorizedUser) {
            userAtt.copyUser((AuthorizedUser) msg);
            if((userAtt.getMessageToServer().equals("Login successful"))){
                System.out.println("response: " + userAtt.getMessageToServer());
                String response = "Authorized user request:" + userAtt.getMessageToServer();
                EventBus.getDefault().post(response);
            }
        }

    }


}
