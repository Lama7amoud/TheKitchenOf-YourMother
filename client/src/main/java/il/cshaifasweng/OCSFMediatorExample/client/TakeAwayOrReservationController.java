package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.MessageEvent;
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

import java.io.IOException;
import java.util.Optional;

public class TakeAwayOrReservationController {




    @FXML private Button cancelButton;   // Cancel Reservation
    @FXML private Button cancelButton1;  // Cancel Order
    @FXML
    private TextField IDtextField;

    @FXML
    private Button branchDetailsButton;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private boolean isActive = false;

    @FXML
    void initialize() {
        cancelButton.setDisable(true);
        cancelButton1.setDisable(true);

        IDtextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d{9}")) {
                Client.getClient().sendToServer("check_id_type:" + newValue + ":" + Client.getClientAttributes().getRestaurantInterest()); // changed ; to :
            } else {
                cancelButton.setDisable(true);
                cancelButton1.setDisable(true);
            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        branchDetailsButton.setVisible(false);
        isActive = true;
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


    // Convenience method to show an error alert
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * This method is bound to your FXML “Cancel Reservation” button.
     * It simply sends “cancel_reservation;<id>” to the server.
     */
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

    /**
     * New “Cancel Order” button handler.
     * It sends “cancel_order;<id>” to the server.
     */
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
