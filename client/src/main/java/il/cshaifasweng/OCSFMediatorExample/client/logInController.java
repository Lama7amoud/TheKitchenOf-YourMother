package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

public class logInController {

    @FXML
    private Button connectButton;

    @FXML
    private Label connectingMessageLabel;

    @FXML
    private Button customerButton;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    void connectButtonClicked(ActionEvent event) {
        Platform.runLater(() -> {
            connectButton.setDisable(true);
            customerButton.setDisable(true);
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();

            if(username.isEmpty() || password.isEmpty()){
                connectingMessageLabel.setStyle("-fx-text-fill: red;");
                connectingMessageLabel.setText("The username or password field is empty");
                connectButton.setDisable(false);
                customerButton.setDisable(false);
            }
            else {
                try {
                    Client.getClient().sendToServer("logIn: " + username + ";" + password);
                } catch (Exception e) {
                    connectingMessageLabel.setText("Connection failed, try again");
                }
            }
        });
    }

    @FXML
    void customerButtonClicked(ActionEvent event) {
        Platform.runLater(() -> {
            customerButton.setDisable(true);
            connectButton.setDisable(true);
            userAtt.setUsername("Customer");
            EventBus.getDefault().unregister(this);
            App.switchScreen("Main Page");
            connectButton.setDisable(false);
            customerButton.setDisable(false);

        });
    }

    @Subscribe
    public void userDetails(String msg){
        if(msg.startsWith("Authorized user request:")){
            Platform.runLater(() -> {
                int indexOfColon = msg.indexOf(":");
                String response = msg.substring(indexOfColon + 2);
                System.out.println(response);
                connectingMessageLabel.setText(response);
                if (!response.equals("Login successful")) {
                    connectingMessageLabel.setStyle("-fx-text-fill: red;");

                } else {
                    connectingMessageLabel.setStyle("-fx-text-fill: green;");
                    EventBus.getDefault().unregister(this);
                    App.switchScreen("Main Page");
                }
                connectButton.setDisable(false);
                customerButton.setDisable(false);
            });
        }
    }

    @FXML
    void initialize(){
        EventBus.getDefault().register(this);
    }
}
