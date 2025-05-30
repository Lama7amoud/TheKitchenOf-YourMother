package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class TakeAwayOrReservationController {

    @FXML
    private TextField IDtextField;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button branchDetailsButton;

    @FXML
    void initialize() {
        branchDetailsButton.setVisible(false);

    }

    public void goToOrderTables(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("OrderTablesPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void goToMenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("MenuPage.fxml")); // Update path if needed
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlToLoad));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else {
            System.err.println("No matching FXML found for button ID: " + buttonId);
        }
    }


    @FXML
    public void handleCancelReservation(ActionEvent event) {
        String id = IDtextField.getText().trim(); // Or wherever you store the reservation ID
        if (id.matches("\\d{9}")) {
            Client.getClient().sendToServer("cancel_reservation;" + id);
        } else {
            // Show validation alert
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid ID number.");
            alert.showAndWait();
        }
    }

    @FXML
    public void goToTakeAwayPage(ActionEvent event) {
        App.switchScreen("TakeAway Page");
    }

}
