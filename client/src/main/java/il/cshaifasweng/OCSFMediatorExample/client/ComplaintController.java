package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import static il.cshaifasweng.OCSFMediatorExample.client.SessionManager.getUserId;

public class ComplaintController implements Initializable {


    private String complaintText;
    private String id;
    private String name;
    private String email;

    @FXML
    private Button backButton;

    @FXML
    private TextArea complaintTextArea;

    @FXML
    private TextField idText;

    @FXML
    private TextField nameText;

    @FXML
    private TextField emailText;



    @FXML
    private Button submitComplaintButton;


    @FXML
    private Label messsageLabel;


    @FXML
    void submitComplaintOnAction(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            messsageLabel.setText("");
            complaintText = complaintTextArea.getText();
            id = idText.getText();
            name = nameText.getText();
            email = emailText.getText();

            if (id == null || id.isEmpty()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("id text field is empty");
                return;
            } else if (name == null || name.isEmpty()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("name text field is empty");
                return;
            }
            else if (email == null || email.isEmpty()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("email text field is empty");
                return;
            }
            else if (complaintText == null || complaintText.isEmpty()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("Complaint text field is empty");
                return;
            }
            Client.getClient().sendToServer("complaint;" + complaintText + ";" + Client.getClientAttributes().getRestaurantInterest() + ";"+id + ";" + name+ ";" + email);

        });
    }

    @Subscribe
    public void insertconfirmation(String msg){
        if(!(msg.startsWith("complaint") || msg.startsWith("user"))){
            return;
        }
        Platform.runLater(() -> {
            if (msg.equals("complaint inserted successfully"))
            {
                messsageLabel.setStyle("-fx-text-fill: green;");
                complaintTextArea.clear();
                idText.clear();
                nameText.clear();
                emailText.clear();

            } else if (msg.equals("complaint has not inserted")){
                messsageLabel.setStyle("-fx-text-fill: red;");
                complaintTextArea.clear();

            } else if (msg.equals("user not exist")) {
                messsageLabel.setStyle("-fx-text-fill: red;");
            }
            messsageLabel.setText(msg);
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EventBus.getDefault().register(this);
        Platform.runLater(() -> {
            messsageLabel.setText("");
            messsageLabel.setVisible(true);
        });
    }


    @FXML
    void back_func(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            EventBus.getDefault().unregister(this);
            App.switchScreen("Main Page");
        });
    }

}