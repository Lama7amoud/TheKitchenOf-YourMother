package il.cshaifasweng.OCSFMediatorExample.client;

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
    void connect(ActionEvent event) throws IOException {
        Client client = Client.getClient();
        try {
            client.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        App.setRoot("primary");
    }

}

