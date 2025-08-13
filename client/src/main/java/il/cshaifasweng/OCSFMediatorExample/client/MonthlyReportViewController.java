package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DailyReport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.userAtt;

public class MonthlyReportViewController {

    @FXML
    private Button backButtun;

    @FXML
    private BarChart<String, String> complaintsHistogram;

    @FXML
    private Label deliveryOrdersLabel;

    @FXML
    private ComboBox<String> restaurantNameBox;

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private TextField yearLabel;

    @FXML
    private TextField monthLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> graphOrTableBox;

    @FXML
    void back(ActionEvent event){
        EventBus.getDefault().unregister(this);
        Platform.runLater(() -> {
            App.switchScreen("Management Page");
        });
    }

    @FXML
    void checkInputs(ActionEvent event) {
        String monthText = monthLabel.getText();
        String yearText = yearLabel.getText();
        String restaurant = restaurantNameBox.getValue();

        Platform.runLater(() -> {
            // Check restaurant selection
            if (restaurant == null || restaurant.trim().isEmpty()) {
                errorLabel.setText("Choose a restaurant");
                return;
            }

            // Check month
            if (monthText == null || monthText.trim().isEmpty()) {
                errorLabel.setText("Choose month");
                return;
            } else {
                try {
                    int month = Integer.parseInt(monthText);
                    if (month < 1 || month > 12) {
                        errorLabel.setText("Month must be between 1 and 12");
                        return;
                    }
                } catch (NumberFormatException e) {
                    errorLabel.setText("Month must be a number");
                    return;
                }
            }

            // Check year
            if (yearText == null || yearText.trim().isEmpty()) {
                errorLabel.setText("Choose year");
                return;
            } else {
                try {
                    int year = Integer.parseInt(yearText);
                    int currentYear = java.time.Year.now().getValue();
                    if (year < 2025|| year > currentYear) {
                        errorLabel.setText("Year must be between 2025 and " + currentYear);
                        return;
                    }
                } catch (NumberFormatException e) {
                    errorLabel.setText("Year must be a number");
                    return;
                }
            }

            errorLabel.setText("");
            String wantedRestaurant;
            switch(restaurantNameBox.getValue()){
                case("Haifa Branch"): wantedRestaurant = "Haifa-Mom Kitchen";
                break;
                case("Tel-Aviv Branch"): wantedRestaurant = "Tel-Aviv-Mom Kitchen";
                break;
                case ("Nahariya Branch"): wantedRestaurant = "Nahariya-Mom Kitchen";
                break;
                default: wantedRestaurant = "";
            }
            errorLabel.setText("");
            totalCustomersLabel.setText("0");
            deliveryOrdersLabel.setText("0");
            complaintsHistogram.getData().clear();
            String message = wantedRestaurant + ";" + monthLabel.getText() + ";" + yearLabel.getText();
            Client client = Client.getClient();
            client.sendToServer("request_reports;" + message);
        });

    }

    @Subscribe
    public void reportsReceived(List<DailyReport> reportsList) {
        System.out.println("reports received");

        Platform.runLater(() -> {
            if (reportsList == null || reportsList.isEmpty()) {
                errorLabel.setText("No reports found for the selected filters.");
                totalCustomersLabel.setText("0");
                deliveryOrdersLabel.setText("0");
                complaintsHistogram.getData().clear();
                return;
            }

            errorLabel.setText("");

            // Take the first report (or aggregated one)
            DailyReport report = reportsList.get(0);

            totalCustomersLabel.setText(String.valueOf(report.getTotalCustomers()));
            deliveryOrdersLabel.setText(String.valueOf(report.getDeliveryOrders()));

        });
    }



    @FXML
    void initialize(){
        EventBus.getDefault().register(this);
        errorLabel.setText("");
        Platform.runLater(() -> {
            ObservableList<String> restaurantList = FXCollections.observableArrayList(
                    "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch", "All"
            );
            restaurantNameBox.setItems(restaurantList);
            ObservableList<String> graphOrTableList = FXCollections.observableArrayList(
                    "Complaints Histogram", "Reservations", "deliveries"
            );
            graphOrTableBox.setItems(graphOrTableList);
            int employee_permission = userAtt.getPermissionLevel();
            if((employee_permission != 4)){
                restaurantNameBox.setValue(Client.getClientAttributes().getRestaurant().getName());
                restaurantNameBox.setDisable(true);
            } else {
                restaurantNameBox.setDisable(false);
            }
        });
    }

}
