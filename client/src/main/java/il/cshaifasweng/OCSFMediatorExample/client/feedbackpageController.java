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
    private Label emtpyComplaintMessage;

    @FXML
    private Label emtpyFeedbackMessage;

    @FXML
    private ComboBox<Integer> ratingComboBox;

    @FXML
    void submitComplaintOnAction(ActionEvent event) throws IOException {
        complaintText = complaintTextArea.getText();
        if(complaintText.isEmpty()) {
            emtpyComplaintMessage.setVisible(true);
        }
        sendComplaintToServer();
        complaintTextArea.clear();
        emtpyComplaintMessage.setTextFill(Color.GREEN);
        emtpyComplaintMessage.setText("Complaint sent successfully");
    }




    @FXML
    void submitFeedbackOnAction(ActionEvent event) throws IOException {
        feedbackText = feedbackTextArea.getText();
        if(feedbackText.isEmpty()) {
            emtpyFeedbackMessage.setVisible(true);
        }
        else {
            sendFeedbackToServer();
            feedbackTextArea.clear();
            emtpyFeedbackMessage.setTextFill(Color.GREEN);
            emtpyFeedbackMessage.setText("Feedback sent successfully");
            int rating = ratingComboBox.getValue();
            String msg = String.format("feedback;%d;%s;%d", getUserId(), feedbackText, rating);
            Client.getClient().sendToServer(msg);
        }


    }

    @FXML
    void sendComplaintToServer() throws IOException {
        int userId = 1; // Replace with actual logged-in user ID
        String status = "Pending"; // Default status
        String message = complaintText;

        String formattedMessage = String.format("complaint;%d;%s;%s", userId, message, status);
        Client.getClient().sendToServer(formattedMessage);
    }


    @FXML
    void sendFeedbackToServer() throws IOException {
        int userId = 1; // Replace with actual logged-in user ID
        int rating = 5; // Or let the user choose this from a dropdown/spinner
        String message = feedbackText;

        String formattedMessage = String.format("feedback;%d;%s;%d", userId, message, rating);
        Client.getClient().sendToServer(formattedMessage);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        feedbackTextArea.setWrapText(true);
        complaintTextArea.setWrapText(true);
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingComboBox.setValue(5); // Default value
    }


    @FXML
    void back_func(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            App.switchScreen("Main Page");
        });
    }

}