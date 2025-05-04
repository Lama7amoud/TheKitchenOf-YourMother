package il.cshaifasweng.OCSFMediatorExample.client;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalTime;
import java.net.URL;
import java.util.ResourceBundle;

    public class TakeAwayController implements Initializable {

        @FXML
        private TextField addressField;

        @FXML
        private Button backToMainPageButton;

        @FXML
        private Button backToOrderTablesButton;

        @FXML
        private Button branchDetailsButton;

        @FXML
        private Label branchNameLabel;

        // Change ComboBox type to LocalTime
        @FXML
        private ComboBox<LocalTime> favTimeComboBox;

        @FXML
        private Label guestCountLabel;

        @FXML
        private TextField idField;

        @FXML
        private TextField nameField;

        @FXML
        private Label notesLabel;

        @FXML
        private Button payWithVisaButton;

        @FXML
        private TextField phoneField;

        @FXML
        private Label preferredTimeLabel;

        @FXML
        private Label sittingTypeLabel;

        @FXML
        private Button takeAwayButton;

        @FXML
        private Button viewMapButton;

        @FXML
        void handleBackToMainPage(ActionEvent event) {
            // Handle action
        }

        @FXML
        void handleBackToOrderTables(ActionEvent event) {
            // Handle action
        }

        @FXML
        void switchPage(ActionEvent event) {
            // Handle action
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            LocalTime now = LocalTime.now();
            // Add LocalTime values to the ComboBox
            favTimeComboBox.getItems().addAll(now, now.plusMinutes(30), now.plusMinutes(60));
        }
    }