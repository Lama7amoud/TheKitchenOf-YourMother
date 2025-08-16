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

public class feedbackpageController implements Initializable {

    private String feedbackText;

    @FXML
    private Button backButton;


    @FXML private TextField idText;
    @FXML private TextField nameText;

    @FXML
    private TextArea feedbackTextArea;


    @FXML
    private Button submitFeedbackButton;

    @FXML
    private ComboBox<Integer> ratingComboBox;

    @FXML
    private Label messsageLabel;




    @Subscribe
    public void insertconfirmation(String msg){
        if(!(msg.startsWith("user") || msg.startsWith("feedback"))){
            return;
        }
        Platform.runLater(() -> {
            if (msg.equals("feedback inserted successfully")) {
                messsageLabel.setStyle("-fx-text-fill: green;");
                feedbackTextArea.clear();
                idText.clear();
                nameText.clear();
                ratingComboBox.setValue(null);
            } else if (msg.equals("feedback has not inserted")) {
                messsageLabel.setStyle("-fx-text-fill: red;");
            }
            else if(msg.equals("user not exist"))
            {
                messsageLabel.setStyle("-fx-text-fill: red;");
                idText.clear();
                nameText.clear();
            }
            messsageLabel.setText(msg);
        });
    }


    @FXML
    void submitFeedbackOnAction(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            messsageLabel.setText("");

            // name required
            String name = (nameText.getText() == null) ? "" : nameText.getText().trim();
            String idNumber = (idText.getText() == null) ? "" : idText.getText().trim();
            Integer rating = ratingComboBox.getValue();
            String feedbackText = feedbackTextArea.getText();

            if (name.isEmpty()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("Please enter your name");
                return;
            }

            else if (idNumber.isEmpty() || !idNumber.matches("\\d+")) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("Please enter your ID");
                return;
            }

            else if (rating == null) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("Please choose a rating");
                return;
            }
            else if (feedbackText == null || feedbackText.isBlank()) {
                messsageLabel.setStyle("-fx-text-fill: red;");
                messsageLabel.setText("Feedback text field is empty");
                return;
            }

            int restaurantId = Client.getClientAttributes().getRestaurantInterest();

            String msg = String.format("feedback;%s;%d;%d;%s;%s",
                    feedbackText.replace(";", ","), // avoid breaking the protocol if ';' typed
                    rating,
                    restaurantId,
                    idNumber,
                    name.replace(";", ",")
            );
            Client.getClient().sendToServer(msg);


        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EventBus.getDefault().register(this);
        Platform.runLater(() -> {
            ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
            ratingComboBox.setValue(null);
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