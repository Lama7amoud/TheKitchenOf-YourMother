package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.userAtt;

public class TakeAwayController implements Initializable {

    @FXML private TextField addressField, nameField, phoneField, idField;
    @FXML private ComboBox<String> favTimeComboBox;
    @FXML private Button viewMapButton, branchDetailsButton, backToMainPageButton, backToOrderTablesButton, ContinueButton;
    @FXML private Label fullNameLabel, numberLabel, iDLabel, addressLabel, favTimeLabel;
    private boolean waitingForIdCheck = false;
    private boolean idAlreadyUsed = false;
    @FXML private TextField emailField;
    @FXML private Label emailErrorLabel;
    @FXML private Label branchNameLabel;

    private LocalTime convertDoubleToLocalTime(double time) {
        int hours = (int) time;
        int minutes = (int) Math.round((time - hours) * 100);
        return LocalTime.of(hours, minutes);
    }

    private String nameForInterest() {
        short id = Client.getClientAttributes().getRestaurantInterest();
        return switch (id) {
            case 1 -> "Haifa Branch";
            case 2 -> "Tel-Aviv Branch";
            case 3 -> "Nahariya Branch";
            default -> "";
        };
    }

    private void setBranchLabel(Restaurant r) {
        Platform.runLater(() -> {
            if (branchNameLabel != null) {
                branchNameLabel.setText(
                        (r != null && r.getName() != null && !r.getName().isEmpty())
                                ? r.getName()
                                : nameForInterest()
                );
            }
        });
    }

    @Subscribe
    public void restaurantEntityReceived(Restaurant res) {
        if (res == null) return;
        setBranchLabel(res);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            LocalTime now = LocalTime.now().withSecond(0).withNano(0);

            Restaurant res = Client.getClientAttributes().getRestaurantInterestEntity();
            setBranchLabel(res);
            int minutes = ((now.getMinute() / 30) + 2) * 30; // +2 to skip the next slot
            LocalTime firstSlot = now.withMinute(0).plusMinutes(minutes);

            LocalTime opening = convertDoubleToLocalTime(res.getOpeningTime());
            LocalTime closing = convertDoubleToLocalTime(res.getClosingTime());

            if (firstSlot.isBefore(opening)) {
                firstSlot = opening;
            }

            LocalTime slot = firstSlot;
            while (!slot.isAfter(closing)) {
                favTimeComboBox.getItems().add(slot.toString());
                slot = slot.plusMinutes(30);
            }

            hideAllErrorLabels();
            viewMapButton.setVisible(false);
            branchDetailsButton.setVisible(false);
        });
    }

    private void hideAllErrorLabels() {
        fullNameLabel.setVisible(false);
        numberLabel.setVisible(false);
        iDLabel.setVisible(false);
        addressLabel.setVisible(false);
        favTimeLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private boolean validateFullName(String name) {
        if (name.isEmpty()) {
            showError(fullNameLabel, "Full name is required.");
            return false;
        }
        if (name.split("\\s+").length < 2 || name.matches(".*\\d.*")) {
            showError(fullNameLabel, "Invalid Full Name (min 2 words, no digits).");
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(String phone) {
        if (phone.isEmpty()) {
            showError(numberLabel, "Phone number is required.");
            return false;
        }
        if (!phone.matches("^05\\d{8}$")) {
            showError(numberLabel, "Invalid Phone Number (05 + 8 digits).");
            return false;
        }
        return true;
    }

    private boolean validateID(String id) {
        if (id.isEmpty()) {
            showError(iDLabel, "ID is required.");
            return false;
        }
        if (!id.matches("^\\d{9}$")) {
            showError(iDLabel, "Invalid ID (must contain 9 digits).");
            return false;
        }
        return true;
    }

    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            showError(addressLabel, "Address is required.");
            return false;
        }
        return true;
    }

    private boolean validateFavoriteTime(String favoriteTime) {
        if (favoriteTime == null) {
            showError(favTimeLabel, "Please select a favorite time.");
            return false;
        }
        return true;
    }

    @FXML
    void handleContinue(ActionEvent event) {
        hideAllErrorLabels();

        boolean isValid = true;
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String id = idField.getText().trim();
        String address = addressField.getText().trim();
        String favoriteTime = favTimeComboBox.getValue();

        if (!validateFavoriteTime(favoriteTime)) isValid = false;
        if (!validateFullName(name)) isValid = false;
        if (!validatePhoneNumber(phone)) isValid = false;
        if (!validateID(id)) isValid = false;
        if (!validateAddress(address)) isValid = false;

        if (isValid) {
            continueIfValid(name, phone, id, address, favoriteTime);
        } else {
            System.out.println("Validation failed: Please fix the highlighted errors.");
        }
    }

    private void continueIfValid(String name, String phone, String id, String address, String favoriteTime) {
        OrderData od = OrderData.getInstance();
        od.setFullName(name);
        od.setPhoneNumber(phone);
        od.setIdNumber(id);
        od.setAddress(address);
        od.setPreferredTime(favoriteTime);
        od.setPickupTime(LocalTime.parse(favoriteTime));   // <-- CRITICAL
        od.setDate(LocalDate.now());                       // or a chosen date
        od.setEmail(emailField.getText().trim());

        int restaurantId = userAtt.getRestaurantInterest();
        Client.getClient().sendToServer("Get branch details;" + restaurantId);
        App.switchScreen("Menu Page");
    }

    @FXML
    void switchPage(ActionEvent event) {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        Button sourceButton = (Button) event.getSource();
        String page = switch (sourceButton.getId()) {
            case "backToOrderTablesButton" -> "TakeAwayOrReservation Page";
            case "backToMainPageButton" -> "Main Page";
            default -> "";
        };
        if (!page.isEmpty()) {
            App.switchScreen(page);
        }
    }
}
