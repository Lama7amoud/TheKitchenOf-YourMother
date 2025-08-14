package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import static il.cshaifasweng.OCSFMediatorExample.client.SessionManager.getUserId;

public class feedbackpageController implements Initializable {

    private String feedbackText;
    private String complaintText;

    @FXML
    private Button backButton;

    @FXML
    private TextArea complaintTextArea;

    @FXML
    private TextArea feedbackTextArea;

    @FXML
    private Button submitComplaintButton;

    @FXML
    private Button submitFeedbackButton;

    @FXML
    private ComboBox<Integer> ratingComboBox;

    @FXML
    private Label messsageLabel;


    @FXML
    void submitComplaintOnAction(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            messsageLabel.setText("");
            complaintText = complaintTextArea.getText();
            if (complaintText == null || complaintText.isEmpty()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("Complaint text field is empty");
                return;
            }
            Client.getClient().sendToServer("complaint;" + complaintText + ";" + Client.getClientAttributes().getRestaurantInterest());
            complaintTextArea.clear();
        });
    }

    @Subscribe
    public void insertconfirmation(String msg){
        if(!(msg.startsWith("complaint") || msg.startsWith("feedback"))){
            return;
        }
        Platform.runLater(() -> {
            if (msg.equals("complaint inserted successfully") || msg.equals("feedback inserted successfully")) {
                messsageLabel.setStyle("-fx-text-fill: green;");
            } else if (msg.equals("complaint has not inserted") || msg.equals("feedback has not inserted")) {
                messsageLabel.setStyle("-fx-text-fill: red;");

            }
            messsageLabel.setText(msg);
        });
    }


    @FXML
    void submitFeedbackOnAction(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            messsageLabel.setText("");
            feedbackText = feedbackTextArea.getText();
            if (feedbackText == null || feedbackText.isEmpty()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("Feedback text field is empty");
                return;
            }
            int rating = ratingComboBox.getValue();
            Client.getClient().sendToServer("feedback;" + feedbackText + ";" + rating + ";" + Client.getClientAttributes().getRestaurantInterest());
            feedbackTextArea.clear();
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EventBus.getDefault().register(this);
        Platform.runLater(() -> {
            ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
            ratingComboBox.setValue(5); // Default value
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