package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MonthlyReport;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MonthlyReportViewController {

    @FXML private Label restaurantNameLabel;
    @FXML private Label generatedTimeLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label deliveryOrdersLabel;
    @FXML private BarChart<String, Number> complaintsHistogram;

    @FXML
    private void initialize() {
        EventBus.getDefault().register(this);
    }

    /**
     * Trigger fetching the monthly report from the server
     */
    @FXML
    void viewReport(ActionEvent event) {
        try {
            Client.getClient().sendToServer("REQUEST_MONTHLY_REPORTS");
        } catch (Exception e) {
            showError("Error sending request: " + e.getMessage());
        }
    }

    /**
     * Handle incoming MonthlyReport object from the server
     */
    @Subscribe
    public void onMonthlyReportReceived(MonthlyReport report) {
        Platform.runLater(() -> {
            restaurantNameLabel.setText(report.getRestaurantName());
            generatedTimeLabel.setText(report.getGeneratedTime().toString());
            totalCustomersLabel.setText(String.valueOf(report.getTotalCustomers()));
            deliveryOrdersLabel.setText(String.valueOf(report.getDeliveryOrders()));

            // Fill bar chart with complaint data
            complaintsHistogram.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Complaints");
            report.getComplaintsData().forEach((category, count) ->
                    series.getData().add(new XYChart.Data<>(category, count))
            );
            complaintsHistogram.getData().add(series);
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
