package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import il.cshaifasweng.OCSFMediatorExample.entities.MonthlyReport;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReportPageController implements Initializable {


    @FXML
    private VBox reportContainer;

    @FXML
    private BarChart<String, Number> complaintsBarChart;

    @FXML
    private Label accessDeniedLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int permissionLevel = SessionManager.getPermissionLevel();

        if (permissionLevel != 3 && permissionLevel != 4) {
            // Hide chart and show access denied message
            complaintsBarChart.setVisible(false);
            accessDeniedLabel.setText("Access Denied: You do not have permission to view this page.");
            accessDeniedLabel.setVisible(true);

        }

    }


    @FXML
    void getReport(ActionEvent event) {
        Client.requestMonthlyReports();
    }


    @FXML
    void backButton(ActionEvent event) {
        Platform.runLater(() -> {
            App.switchScreen("Main Page");
        });
    }

}



