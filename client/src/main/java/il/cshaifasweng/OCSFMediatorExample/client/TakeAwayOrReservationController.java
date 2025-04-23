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
                fxmlToLoad = "PrimaryPage.fxml"; // Replace with actual main page FXML name
                break;
            case "viewMapButton":
                fxmlToLoad = "MapPage.fxml"; // Replace with actual map page
                break;
            case "branchDetailsButton":
                fxmlToLoad = "BranchDetailsPage.fxml"; // Replace with actual details page
                break;
        }

        if (fxmlToLoad != null) {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlToLoad));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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

}
