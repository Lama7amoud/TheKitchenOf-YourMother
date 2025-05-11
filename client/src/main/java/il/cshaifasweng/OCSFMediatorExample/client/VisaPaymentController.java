package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class VisaPaymentController {

    @FXML
    private TextField cardNumberField;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private TextField cvvField;

    @FXML
    private TextField cardHolderField;

    @FXML
    private Button submitButton;

    private Reservation currentReservation;  // Hold the Reservation object

  /*  @FXML
    public void initialize() {
        this.currentReservation = OrderData.getReservation(); // Load it on init
        ObservableList<String> months = FXCollections.observableArrayList();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        monthComboBox.setItems(months);
        int currentYear = LocalDate.now().getYear();
        ObservableList<String> years = FXCollections.observableArrayList();
        for (int y = currentYear; y <= 2040; y++) {
            years.add(String.valueOf(y));
        }
        yearComboBox.setItems(years);
    }
*/
    @FXML
    private void handlePaymentSubmit() {
        // Optional: safety check in case reservation was never passed
        if (currentReservation == null) {
            showAlert(Alert.AlertType.ERROR, "Reservation Error", "Reservation details are missing.");
            return;
        }

        String cardNumber = cardNumberField.getText().trim();
        String selectedMonth = monthComboBox.getValue();
        String selectedYear = yearComboBox.getValue();
        String cvv = cvvField.getText().trim();
        String cardHolderName = cardHolderField.getText().trim();

        // Validate expiration date
        if (selectedMonth == null || selectedYear == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Expiration Date", "Please select both month and year.");
            return;
        }

        LocalDate expirationDate;
        try {
            expirationDate = LocalDate.of(
                    Integer.parseInt(selectedYear),
                    Integer.parseInt(selectedMonth),
                    1
            );
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Expiration Date", "Could not parse selected expiration date.");
            return;
        }

        if (expirationDate.isBefore(LocalDate.now().withDayOfMonth(1))) {
            showAlert(Alert.AlertType.ERROR, "Invalid Expiration Date", "Please choose a future expiration date.");
            return;
        }

        // Validate card number
        if (!cardNumber.matches("\\d{16}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Card Number", "Card number must be 16 digits.");
            return;
        }

        // Validate CVV
        if (!cvv.matches("\\d{3}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid CVV", "CVV must be 3 digits.");
            return;
        }

        // Validate card holder name
        if (cardHolderName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Name", "Card holder name is required.");
            return;
        }

        // Process payment (fake)
        boolean paymentSuccess = processPayment(cardNumber, expirationDate, cvv, cardHolderName);

        if (paymentSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Payment Success", "Your payment was successful.");

            try {
                String customerId = currentReservation.getIdNumber();
                String message = "payment-confirmed:" + customerId;

                System.out.println("Sending to server: " + message);
                Client client = Client.getClient();
                System.out.println("Client is connected: " + client.isConnected());

                client.sendToServer(message);
                System.out.println("Message sent successfully!");

            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.WARNING, "Server Error", "Payment succeeded, but server was not notified.");
            }

            // Optional: clear shared data
/*            OrderData.clear();*/

        } else {
            showAlert(Alert.AlertType.ERROR, "Payment Failed", "There was an issue processing your payment.");
        }
    }

    private boolean processPayment(String cardNumber, LocalDate expirationDate, String cvv, String cardHolderName) {
        // Placeholder logic for real payment processing
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}