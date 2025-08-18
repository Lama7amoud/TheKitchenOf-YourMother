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

    @FXML
    private TextField addressField, nameField, phoneField, idField;

    @FXML
    private ComboBox<String> favTimeComboBox;

    @FXML
    private Button viewMapButton, branchDetailsButton, backToMainPageButton, backToOrderTablesButton, ContinueButton;

    @FXML
    private Label fullNameLabel, numberLabel, iDLabel, addressLabel, favTimeLabel;

    private boolean waitingForIdCheck = false;
    private boolean idAlreadyUsed = false;

    @FXML
    private TextField emailField;

    @FXML
    private Label emailErrorLabel;


    private LocalTime convertDoubleToLocalTime(double time) {
        int hours = (int) time;
        int minutes = (int) Math.round((time - hours) * 100);
        return LocalTime.of(hours, minutes);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            //EventBus.getDefault().register(this);
            LocalTime now = LocalTime.now().withSecond(0).withNano(0);

            Restaurant res = Client.getClientAttributes().getRestaurantInterestEntity();
            // Current time rounded up to next 15 minutes
            int minutes = ((now.getMinute() + 14) / 15) * 15; // round up
            LocalTime firstSlot = now.withMinute(0).plusMinutes(minutes);

            // Get restaurant opening/closing times
            LocalTime opening = convertDoubleToLocalTime(res.getOpeningTime());
            LocalTime closing = convertDoubleToLocalTime(res.getClosingTime());

            // Make sure the first slot is not before opening
            if (firstSlot.isBefore(opening)) {
                firstSlot = opening;
            }

            // Add 15-min interval slots until closing time
            LocalTime slot = firstSlot;
            while (!slot.isAfter(closing)) {
                favTimeComboBox.getItems().add(slot.toString());
                slot = slot.plusMinutes(15);
            }


            // Initially hide all error labels
            hideAllErrorLabels();

            // Set buttons that should not be visible
            viewMapButton.setVisible(false);
            branchDetailsButton.setVisible(false);

            // Display the selected restaurant from userAtt (if available)
            short restaurantInterest = userAtt.getRestaurantInterest();
            String restaurantName = switch (restaurantInterest) {
                case 1 -> "Haifa Branch";
                case 2 -> "Tel-Aviv Branch";
                case 3 -> "Nahariya Branch";
                default -> "Unknown";
            };
        });
    }

    // Hide all error labels
    private void hideAllErrorLabels() {
        fullNameLabel.setVisible(false);
        numberLabel.setVisible(false);
        iDLabel.setVisible(false);
        addressLabel.setVisible(false);
        favTimeLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
    }

    // Show specific error message
    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    // Full name validation: At least 2 words and no digits
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

    // Phone number validation: Must start with '05' and have 10 digits
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

    // ID validation: Exactly 9 digits
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

    // Address validation: Non-empty
    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            showError(addressLabel, "Address is required.");
            return false;
        }
        return true;
    }

    // Favorite time validation: Must be selected
    private boolean validateFavoriteTime(String favoriteTime) {
        if (favoriteTime == null) {
            showError(favTimeLabel, "Please select a favorite time.");
            return false;
        }
        return true;
    }

    @FXML
    void handleContinue(ActionEvent event) {
        hideAllErrorLabels(); // Hide all errors at the start

        boolean isValid = true;
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String id = idField.getText().trim();
        String address = addressField.getText().trim();
        String favoriteTime = favTimeComboBox.getValue();



        // Perform all validations and update the flag

        if (!validateFavoriteTime(favoriteTime)) isValid = false;
        if (!validateFullName(name)) isValid = false;
        if (!validatePhoneNumber(phone)) isValid = false;
        if (!validateID(id)) {
            isValid = false;
        }/* else {
            waitingForIdCheck = true;
            Client.getClient().sendToServer("check_reservation_id;" + id);
        }*/
        if (!validateAddress(address)) isValid = false;


        if (isValid /*&& !waitingForIdCheck*/) {
            continueIfValid(name, phone, id, address, favoriteTime);
        }

        // Only proceed if all fields are valid
/*
        if (isValid) {
            OrderData.getInstance().setFullName(name);
            OrderData.getInstance().setPhoneNumber(phone);
            OrderData.getInstance().setIdNumber(id);
            OrderData.getInstance().setAddress(address);
            OrderData.getInstance().setPreferredTime(favoriteTime.toString());

            // Switch to the next page
            App.switchScreen("Menu Page");
*/
         else {
            System.out.println("Validation failed: Please fix the highlighted errors.");
        }
    }

    private void continueIfValid(String name, String phone, String id, String address, String favoriteTime) {
        OrderData.getInstance().setFullName(name);
        OrderData.getInstance().setPhoneNumber(phone);
        OrderData.getInstance().setIdNumber(id);
        OrderData.getInstance().setAddress(address);
        OrderData.getInstance().setPreferredTime(favoriteTime);
        OrderData.getInstance().setDate(LocalDate.now()); // or the selected date
        OrderData.getInstance().setEmail(emailField.getText().trim());


        int restaurantId = userAtt.getRestaurantInterest();
        Client.getClient().sendToServer("Get branch details;" + restaurantId);
        // EventBus.getDefault().unregister(this);
        App.switchScreen("Menu Page");
    }

/*    @Subscribe
    public void handleRestaurantResponse(Object msg) {
        if (msg instanceof Restaurant restaurant) {
            Platform.runLater(() -> {
                userAtt.setRestaurant(restaurant);  //  Set it in userAtt
                System.out.println("Restaurant set in userAtt: " + restaurant.getId());
                App.switchScreen("Menu Page");      //  Now switch to menu
            });
        }
    }*/



   /* @Subscribe
    public void handleIdCheck(IdCheckEvent event) {
        Platform.runLater(() -> {
            waitingForIdCheck = false;
            idAlreadyUsed = event.doesExist();

            if (idAlreadyUsed) {
                showError(iDLabel, "This ID is already used.");
            } else {
                // Re-validate other fields in case of UI delay
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String id = idField.getText().trim();
                String address = addressField.getText().trim();
                String favoriteTime = favTimeComboBox.getValue();

                boolean stillValid = validateFullName(name) && validatePhoneNumber(phone)
                        && validateID(id) && validateAddress(address) && validateFavoriteTime(favoriteTime);

                if (stillValid) {
                    continueIfValid(name, phone, id, address, favoriteTime);
                }
            }
        });
    }*/


    @FXML
    void switchPage(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String page = switch (sourceButton.getId()) {
            case "backToOrderTablesButton" -> "TakeAwayOrReservation Page";
            case "backToMainPageButton" -> "Main Page";
            default -> "";
        };
        if (!page.isEmpty()) {
            // EventBus.getDefault().unregister(this);
            App.switchScreen(page);
        }
    }
}
