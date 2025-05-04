package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MonthlyReport;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.Label;

public class MonthlyReportViewController {

    @FXML
    private Label restaurantNameLabel;

    @FXML
    private Label generatedTimeLabel;

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private Label deliveryOrdersLabel;

    @FXML
    private BarChart<String, Number> complaintsHistogram;

    private MonthlyReport report;

    public void setReport(MonthlyReport report) {
        this.report = report;
        updateUI();
    }

    private void updateUI() {
        if (report == null) return;

        restaurantNameLabel.setText(report.getRestaurant().getName());
        generatedTimeLabel.setText(report.getTimestamp().toString());

        totalCustomersLabel.setText(String.valueOf(report.getCustomersServed()));
        deliveryOrdersLabel.setText(String.valueOf(report.getTakeawayOrders()));  // assuming takeaway = delivery

        // Complaints bar chart
        complaintsHistogram.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Complaints");

        series.getData().add(new XYChart.Data<>("Total Complaints", report.getComplaintCount()));

        complaintsHistogram.getData().add(series);
    }
}
