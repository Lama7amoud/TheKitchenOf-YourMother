package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.MessageEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TakeAwayOrReservationController {


    @FXML
    private Label takeAwayInfoLabel;
    @FXML
    private Button takeAwayButton;
    @FXML
    private Button eatWithUsButton;
    @FXML private TextField reservationIdField;


    @FXML private Button cancelButton;   // Cancel Reservation
    @FXML private Button cancelButton1;  // Cancel Order
    @FXML
    private TextField IDtextField;

    @FXML
    private Button branchDetailsButton;
    @FXML private Label branchNameLabel;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private boolean isActive = false;


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
                                : nameForInterest()  // fallback until entity arrives
                );
            }
        });
    }


    @FXML
    void initialize() {
        Platform.runLater(() -> {

            cancelButton.setDisable(true);
            cancelButton1.setDisable(true);
            Restaurant restaurant = OrderData.getInstance().getSelectedRestaurant();
            branchNameLabel.setText(restaurant != null ? restaurant.getName() : "");
            takeAwayInfoLabel.setVisible(false);
            takeAwayButton.setDisable(true); // disable till we check the times

            User user = Client.getClientAttributes();
            if (user.getPermissionLevel() != 0) {
                takeAwayButton.setVisible(false);
                eatWithUsButton.setText("Host Customers");
            } else {
                takeAwayButton.setVisible(true);
                eatWithUsButton.setText("Eat With Us!");
            }

            Client.getClient().sendToServer("Check open times for res;" + user.getRestaurantInterest());


            setBranchLabel(null);

            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }

            //added by mohammad
            IDtextField.textProperty().addListener((a,b,c) -> updateCancelButtons());
            reservationIdField.textProperty().addListener((a,b,c) -> updateCancelButtons());
            updateCancelButtons();

            branchDetailsButton.setVisible(false);
            isActive = true;
        });
    }

    //added by mohammad
    private void updateCancelButtons() {
        String idNumber = (IDtextField.getText() == null) ? "" : IDtextField.getText().trim();
        String resIdStr = (reservationIdField.getText() == null) ? "" : reservationIdField.getText().trim();

        boolean idOk  = idNumber.matches("\\d{9}");
        boolean ridOk = resIdStr.matches("\\d+");

        // default: both off
        cancelButton.setDisable(true);
        cancelButton1.setDisable(true);

        // ONLY when both are valid, ask the server exactly for that pair
        if (idOk && ridOk) {
            Client.getClient().sendToServer("check_id_type_exact:" + idNumber + ":" + resIdStr);
        }
    }





    // Convenience method to show a simple information alert
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Subscribe
    public void restaurantEntityReceived(Restaurant res) {
        if (res == null) {
            System.out.println("Restaurant is null");
            return;
        }
        Client.getClientAttributes().setRestaurantInterestEntity(res);
        boolean isOpen = isRestaurantOpenNow(res);
        setBranchLabel(res);
        Platform.runLater(() -> {
            if (isOpen) {
                takeAwayButton.setDisable(false);
                takeAwayInfoLabel.setVisible(false);
            } else {
                takeAwayButton.setDisable(true);
                takeAwayInfoLabel.setVisible(true);
            }
        });
    }

    private boolean isRestaurantOpenNow(Restaurant res) {
        // Check holidays first
        if (res.getHolidays() != null && !res.getHolidays().isEmpty()) {
            Set<DayOfWeek> holidays = Arrays.stream(res.getHolidays().split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(DayOfWeek::valueOf)
                    .collect(Collectors.toSet());

            DayOfWeek today = LocalDate.now().getDayOfWeek();
            if (holidays.contains(today)) {
                return false; // Closed due to holiday
            }
        }

        LocalTime now = LocalTime.now();
        LocalTime opening = convertDoubleToLocalTime(res.getOpeningTime());
        LocalTime closing = convertDoubleToLocalTime(res.getClosingTime());

        // Handle normal case (open and close on same day)
        if (closing.isAfter(opening)) {
            return !now.isBefore(opening) && !now.isAfter(closing);
        }
        // Handle overnight case (e.g., open 22:00, close 02:00 next day)
        else {
            return !now.isBefore(opening) || !now.isAfter(closing);
        }
    }

    private LocalTime convertDoubleToLocalTime(double time) {
        int hours = (int) time;
        int minutes = (int) Math.round((time - hours) * 100);
        return LocalTime.of(hours, minutes);
    }

    @Subscribe
    public void handleIdTypeResponse(MessageEvent evt) {
        String msg = evt.getMessage();
        if (!msg.startsWith("id_type:")) return;

        String type = msg.split(":")[1].trim();

        String idNumber = (IDtextField.getText() == null) ? "" : IDtextField.getText().trim();
        String resIdStr = (reservationIdField.getText() == null) ? "" : reservationIdField.getText().trim();
        boolean idOk  = idNumber.matches("\\d{9}");
        boolean ridOk = resIdStr.matches("\\d+");

        Platform.runLater(() -> {
            if (!idOk || !ridOk) {
                cancelButton.setDisable(true);
                cancelButton1.setDisable(true);
                return;
            }
            if ("reservation".equals(type)) {
                cancelButton.setDisable(false);
                cancelButton1.setDisable(true);
            } else if ("takeaway".equals(type)) {
                cancelButton1.setDisable(false);
                cancelButton.setDisable(true);
            } else {
                cancelButton.setDisable(true);
                cancelButton1.setDisable(true);
            }
        });
    }


/*
    @Subscribe
    public void handleIdTypeResponse(MessageEvent evt) {
        String msg = evt.getMessage();
        if (!msg.startsWith("id_type:")) return;

        String type = msg.split(":")[1].trim();


        Platform.runLater(() -> {
            switch (type) {
                case "reservation":
                    cancelButton.setDisable(false);     // enable Cancel Reservation
                    cancelButton1.setDisable(true);
                    break;
                case "takeaway":
                    cancelButton1.setDisable(false);    // enable Cancel Order
                    cancelButton.setDisable(true);
                    break;
                default: // not_found or unrecognized
                    cancelButton.setDisable(true);
                    cancelButton1.setDisable(true);
            }
        });
    }
*/


    // Convenience method to show an error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

/*
    @FXML
    public void handleCancelReservation(ActionEvent event) {
        System.out.println("[TakeAwayOrReservationController] Cancel Reservation clicked, ID = " + IDtextField.getText());
        String id = IDtextField.getText().trim();
        if (id.matches("\\d{9}")) {
            try {
                System.out.println("[TakeAwayOrReservationController] Sending to server: cancel_reservation;" + id);
                Client.getClient().sendToServer("cancel_reservation;" + id);
            } catch (Exception e) {
                e.printStackTrace();
                showError("Failed to send reservation‐cancellation request to server.");
            }
        } else {
            showError("Invalid ID number. Please enter a 9‐digit ID.");
        }
    }


    @FXML
    public void handleCancelOrder(ActionEvent event) {
        String id = IDtextField.getText().trim();
        if (!id.matches("\\d{9}")) {
            showError("Invalid ID number. Please enter a 9‐digit ID.");
            return;
        }
        try {
            System.out.println("[TakeAwayOrReservationController] Sending to server: cancel_order;" + id);
            Client.getClient().sendToServer("cancel_order;" + id);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to send order‐cancellation request to server.");
        }
    }*/



    //mohammad update for cancelling + reservation number
    @FXML
    public void handleCancelReservation(ActionEvent event) {
        String idNumber = IDtextField.getText().trim();
        String resIdStr = reservationIdField.getText().trim();

        if (!idNumber.matches("\\d{9}")) {
            showError("Invalid ID number. Please enter 9 digits.");
            return;
        }
        if (!resIdStr.matches("\\d+")) {
            showError("Invalid Order Number. Please enter numbers only.");
            return;
        }

        // send BOTH idNumber and reservationId
        try {
            Client.getClient().sendToServer("cancel_reservation_exact;" + idNumber + ";" + resIdStr);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not send cancellation request.");
        }
    }

    @FXML
    public void handleCancelOrder(ActionEvent event) {
        String idNumber = IDtextField.getText().trim();
        String resIdStr = reservationIdField.getText().trim();

        if (!idNumber.matches("\\d{9}")) {
            showError("Invalid ID number. Please enter 9 digits.");
            return;
        }
        if (!resIdStr.matches("\\d+")) {
            showError("Invalid Order Number. Please enter numbers only.");
            return;
        }

        // send BOTH idNumber and reservationId
        try {
            Client.getClient().sendToServer("cancel_order_exact;" + idNumber + ";" + resIdStr);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not send cancellation request.");
        }
    }



    // ─────────(2) Listen for "confirm_order_cancellation;<fee>;<idNumber>"
    @Subscribe
    public void handleOrderCancellationPrompt(MessageEvent evt) {
        if (!isActive) return;
        String payload = evt.getMessage();
        // e.g. payload = "confirm_order_cancellation;50;123456789"
        if (!payload.startsWith("confirm_order_cancellation;")) {
            return;
        }

        String[] parts = payload.split(";");
        int fee = Integer.parseInt(parts[1]);
        String customerId = parts[2];

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Order Cancellation Confirmation");
            alert.setHeaderText("A cancellation fee of " + fee + "₪ will be charged for your order.");
            alert.setContentText("Are you sure you wish to cancel your order?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                new Thread(() -> {
                    Client.getClient().sendToServer("process_order_cancellation;" + customerId + ";" + fee);
                }).start();
            }
            // If user clicks CANCEL or closes dialog, do nothing.
        });
    }

    // ─────────(3) Listen for final “order” responses (Visa path) ─────────
    @Subscribe
    public void handleOrderFinalResponse(MessageEvent evt) {
        if (!isActive) return;
        String payload = evt.getMessage();

        if (payload.startsWith("order_cancellation_success;")) {
            // e.g. "order_cancellation_success;50"
            String[] parts = payload.split(";");
            int fee = Integer.parseInt(parts[1]);

            Platform.runLater(() -> {
                showAlert("Your order was cancelled successfully.\nYou have been charged " + fee + "₪.");
                cancelButton1.setDisable(true);

                // go home
                try {
                    EventBus.getDefault().unregister(this);
                } catch (Exception ignore) {}
                isActive = false;
                App.switchScreen("Main Page");
            });

        } else if (payload.equals("order_cancellation_failed;visa_error")) {
            Platform.runLater(() -> {
                showError("Order cancellation failed: Could not charge your Visa card.");
            });

        } else if (payload.equals("no_order_found")) {
            Platform.runLater(() -> {
                showError("No active order found for this ID.");
            });
        }

        // Anything else is ignored here.
    }

    // ─────────(4) Listen for “confirm_cancellation;<fee>;<idNumber>” (reservation flow) ─────────
    @Subscribe
    public void handleCancellationPrompt(MessageEvent evt) {
        if (!isActive) return;
        String payload = evt.getMessage();
        System.out.println("[TakeAwayOrReservationController] handleCancellationPrompt got: " + payload);
        if (!payload.startsWith("confirm_cancellation;")) {
            return;
        }

        String[] parts = payload.split(";");
        int fee = Integer.parseInt(parts[1]);
        String customerId = parts[2];

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Reservation Cancellation Confirmation");
            alert.setHeaderText("A cancellation fee of " + fee + "₪ will be charged.");
            alert.setContentText("Are you sure you want to cancel your reservation?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                new Thread(() -> {
                    Client.getClient().sendToServer("process_cancellation;" + customerId + ";" + fee);
                }).start();
            }
            // If user clicks CANCEL or closes dialog, do nothing.
        });
    }

    // ─────────(5) Listen for final “reservation” responses (Visa path) ─────────
    @Subscribe
    public void handleReservationFinalResponse(MessageEvent evt) {
        if (!isActive) return;
        String payload = evt.getMessage();
        System.out.println("[TakeAwayOrReservationController] handleReservationFinalResponse got: " + payload);

        if (payload.startsWith("cancellation_success;")) {
            // e.g. "cancellation_success;50"
            String[] parts = payload.split(";");
            int fee = Integer.parseInt(parts[1]);

            Platform.runLater(() -> {
                showAlert("Reservation cancelled successfully.\nYou have been charged " + fee + "₪.");
            });

        } else if (payload.equals("cancellation_failed;visa_error")) {
            Platform.runLater(() -> {
                showError("Cancellation failed: Could not charge your Visa card.");
            });

        } else if (payload.equals("no_reservation_found")) {
            Platform.runLater(() -> {
                showError("No active reservation found for this ID.");
            });
        }
    }

    // ─── (6) Listen for the “take‐away order saved” message ─────────
   @Subscribe
    public void handleOrderSaved(MessageEvent evt) {
       if (!isActive) return;
        String payload = evt.getMessage();
       if (!payload.equals("order_saved_successfully")) {
           return;
       }

       // Only show one pop‐up for take‐away‐order‐saved
       Platform.runLater(() -> {
           showAlert("Your take‐away order was placed successfully!");
        });
    }

    @Subscribe
    public void handleOrderCancellationDebt(MessageEvent evt) {
        if (!isActive) return;
        String payload = evt.getMessage();
        if (!payload.startsWith("order_cancellation_debt;")) {
            return;
        }
        // payload = "order_cancellation_debt;<amount>;<idNumber>"
        String[] parts = payload.split(";");
        int totalDue = Integer.parseInt(parts[1]);
        String customerId = parts[2];

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Cancellation (Debt)");
            alert.setHeaderText("Your order has been canceled.");
            alert.setContentText(
                    "Since you paid by cash, you now owe ₪" + totalDue +
                            " under ID: " + customerId
            );
            alert.showAndWait();
            try { EventBus.getDefault().unregister(this); } catch (Exception ignore) {}
            isActive = false;
            App.switchScreen("Main Page");

        });
    }

    @FXML
    public void goToOrderTables(ActionEvent event) throws IOException {
        isActive = false;
        EventBus.getDefault().unregister(this);
        root = FXMLLoader.load(getClass().getResource("OrderTablesPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void goToMenu(ActionEvent event) throws IOException {
        isActive = false;
        EventBus.getDefault().unregister(this);
        root = FXMLLoader.load(getClass().getResource("MenuPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchPage(ActionEvent event) throws IOException {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();
        String fxmlToLoad = null;

        switch (buttonId) {
            case "backButton":
                fxmlToLoad = "primary.fxml";
                break;
            case "viewMapButton":
                fxmlToLoad = "MapPage.fxml";
                break;
            case "branchDetailsButton":
                fxmlToLoad = "BranchDetailsPage.fxml";
                break;
        }
        if (fxmlToLoad != null) {
            EventBus.getDefault().unregister(this);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlToLoad));
            Parent root2 = loader.load();
            Stage stage2 = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage2.setScene(new Scene(root2));
            stage2.show();
        } else {
            System.err.println("No matching FXML found for button ID: " + buttonId);
        }
    }

    @FXML
    public void goToTakeAwayPage(ActionEvent event) {
        isActive = false;
        EventBus.getDefault().unregister(this);
        App.switchScreen("TakeAway Page");

    }

}
