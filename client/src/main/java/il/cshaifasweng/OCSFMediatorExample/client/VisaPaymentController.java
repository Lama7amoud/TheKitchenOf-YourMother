package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    private MenuController menuController;

    public void setParentController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML
    public void initialize() {
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



  @FXML
  private void handlePaymentSubmit() {
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

      // Process payment (mock)
      boolean paymentSuccess = processPayment(cardNumber, expirationDate, cvv, cardHolderName);
      if (paymentSuccess) {
          showAlert(Alert.AlertType.INFORMATION, "Payment Success", "Your payment was successful.");

          try {
              // Save card number into OrderData
              OrderData.getInstance().setVisa(cardNumber);
              OrderData.getInstance().setExpirationMonth(Integer.parseInt(selectedMonth));
              OrderData.getInstance().setExpirationYear(Integer.parseInt(selectedYear));
              OrderData.getInstance().setCvv(cvv);

              // Close popup window before proceeding
              Stage stage = (Stage) submitButton.getScene().getWindow();
              if (stage != null) {
                  stage.close();
              }

              // Callback to MenuController to complete the order
              if (menuController != null) {
                  menuController.finalizeOrderAndReservation("Visa");
              } else {
                  System.err.println("MenuController is null — cannot finalize order.");
              }

          } catch (Exception e) {
              e.printStackTrace();
              showAlert(Alert.AlertType.WARNING, "Server Error", "Payment succeeded, but order confirmation failed.");
          }

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