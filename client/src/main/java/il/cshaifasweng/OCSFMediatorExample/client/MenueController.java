package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;


public class MenueController {

    @FXML
    private Text IP;

    @FXML
    private Text Port;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private Button startGame;

    @FXML
    private void startGame()throws IOException {
        SimpleClient client = SimpleClient.getClient(ip.getText(), Integer.parseInt(port.getText()));
        client.openConnection();
        App.setRoot("primary");

    }

}
