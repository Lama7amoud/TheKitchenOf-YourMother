package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class SecondaryController {

    @FXML
    private Text IP;

    @FXML
    private Text Port;

    @FXML
    private Button connectBTN;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    void connect(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                // Updating the instance variables
                Client.port = Integer.parseInt(port.getText());
                Client.host = ip.getText();
                // Create client
                Client currentClient = Client.getClient();
                currentClient.openConnection();
                Client.getClient().sendToServer("add client");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }
}

