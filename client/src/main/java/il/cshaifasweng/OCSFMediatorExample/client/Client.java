
package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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

    @Override
    public void sendToServer(Object msg) {
        try {
            super.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace(); // or show a dialog
        }
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
        } else if (msg instanceof List) {
            List<Meal> menu = (List<Meal>) msg;
            EventBus.getDefault().post(menu);
        }
        else if (msg instanceof AuthorizedUser) {
            userAtt.copyUser((AuthorizedUser) msg);
            System.out.println("response: " + userAtt.getMessageToServer());
            String response = "Authorized user request:" + userAtt.getMessageToServer();
            EventBus.getDefault().post(response);
        }
        else if (msg instanceof Restaurant) {
            System.out.println("Restaurant " + ((Restaurant) msg).getId() + " has received to client side");
            EventBus.getDefault().post(msg);
        } else if (msg instanceof Reservation) {
            EventBus.getDefault().post(msg);
        }
        else if (msg instanceof String) {
            String strMsg = (String) msg;
            if (strMsg.equals("Reservation failed: ID already used.")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Reservation Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("A reservation already exists for this ID.");
                    alert.showAndWait();
                });
            }
        else if (msg.equals("Reservation saved successfully")) {
            Platform.runLater(() -> {
                // Alert success and redirect to Main Page
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Your reservation has been saved successfully.");
                alert.showAndWait();
                App.switchScreen("Main Page");
            });
        }
    }
}

}
