package il.cshaifasweng.OCSFMediatorExample.client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TableDetailsWindowController {

    @FXML
    private Label ReservedByLabel;

    @FXML
    private Label guestsNumLabel;

    @FXML
    private Label reservedTimeLabel;

    @FXML
    private Label tableIDLabel;

    @FXML
    private AnchorPane rootPane;

    public void setDetails(String tableId, String reservedBy, String reservedTime, String guestsNum) {
        tableIDLabel.setText("Table " + tableId + " Details");
        ReservedByLabel.setText("Reserved By: " + reservedBy);
        reservedTimeLabel.setText("Reserved Time: " + reservedTime);
        guestsNumLabel.setText("Guests Num: " + guestsNum);
    }

    public void initialize() {
        rootPane.setOnMouseMoved(event -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();
            // Remove the handler (optional if stage is already closed)
            rootPane.setOnMouseMoved(null);
        });
    }

}
