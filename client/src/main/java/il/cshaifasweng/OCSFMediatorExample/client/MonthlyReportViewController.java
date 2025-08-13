package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DailyReport;
import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.userAtt;

public class MonthlyReportViewController {

    @FXML
    private Button backButtun;

    @FXML
    private BarChart<String, Number> complaintsHistogram;

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
    private TableView<Reservation> TableView;

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
            TableView.getColumns().clear();
            complaintsHistogram.getData().clear();
            String message = wantedRestaurant + ";" + monthLabel.getText() + ";" + yearLabel.getText();
            Client client = Client.getClient();
            client.sendToServer("request_reports;" + message);
        });

    }

    @Subscribe
    public void reportsReceived(List<DailyReport> reportsList) {

        Platform.runLater(() -> {
            complaintsHistogram.getData().clear(); // reset chart

            if (reportsList == null || reportsList.isEmpty()) {
                errorLabel.setText("No reports found for the selected filters.");
                totalCustomersLabel.setText("0");
                deliveryOrdersLabel.setText("0");
                TableView.getItems().clear(); // clear table items
                return;
            }

            errorLabel.setText("");

            // Show totals from all reports
            long totalCustomers = reportsList.stream().mapToLong(DailyReport::getTotalCustomers).sum();
            long totalDeliveries = reportsList.stream().mapToLong(DailyReport::getDeliveryOrders).sum();

            totalCustomersLabel.setText(String.valueOf(totalCustomers));
            deliveryOrdersLabel.setText(String.valueOf(totalDeliveries));

            // Complaints histogram
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.setName("Complaints Histogram");

            Map<Integer, Integer> complaintsMap = new HashMap<>();
            for (DailyReport report : reportsList) {
                int day = report.getGeneratedTime().getDayOfMonth();
                complaintsMap.put(day, report.getComplaintsCount());
            }

            LocalDate firstDate = reportsList.get(0).getGeneratedTime().toLocalDate();
            int daysInMonth = firstDate.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                int complaints = complaintsMap.getOrDefault(day, 0);
                series1.getData().add(new XYChart.Data<>(String.valueOf(day), complaints));
            }
            complaintsHistogram.getData().add(series1);

            // Gather all reservations
            List<Reservation> allReservations = reportsList.stream()
                    .flatMap(report -> report.getReservationsList().stream())
                    .collect(Collectors.toList());

            // Only create columns once
            if (TableView.getColumns().isEmpty()) {

                // Name
                TableColumn<Reservation, String> nameCol = new TableColumn<>("Name");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                // Phone
                TableColumn<Reservation, String> phoneCol = new TableColumn<>("Phone");
                phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

                // Reservation Time
                TableColumn<Reservation, LocalDateTime> timeCol = new TableColumn<>("Reservation Time");
                timeCol.setCellValueFactory(new PropertyValueFactory<>("reservationTime"));

                // Guests
                TableColumn<Reservation, Integer> guestsCol = new TableColumn<>("Guests");
                guestsCol.setCellValueFactory(new PropertyValueFactory<>("totalGuests"));

                // Restaurant Name
                TableColumn<Reservation, String> restaurantCol = new TableColumn<>("Restaurant");
                restaurantCol.setCellValueFactory(cellData -> {
                    Reservation res = cellData.getValue();
                    if (res.getRestaurant() != null) {
                        int id = res.getRestaurant().getId();
                        String name;
                        switch (id) {
                            case 1: name = "Haifa-Mom Kitchen"; break;
                            case 2: name = "Tel-Aviv-Mom Kitchen"; break;
                            case 3: name = "Nahariya-Mom Kitchen"; break;
                            default: name = "Unknown";
                        }
                        return new SimpleStringProperty(name);
                    }
                    return new SimpleStringProperty("Unknown");
                });

                // Sitting Type
                TableColumn<Reservation, String> sittingTypeCol = new TableColumn<>("Sitting Type");
                sittingTypeCol.setCellValueFactory(new PropertyValueFactory<>("sittingType"));

                // Address
                TableColumn<Reservation, String> addressCol = new TableColumn<>("Address");
                addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

                // Payed Method (Visa or Cash)
                TableColumn<Reservation, String> payedMethodCol = new TableColumn<>("Payed Method");
                payedMethodCol.setCellValueFactory(cellData -> {
                    Reservation res = cellData.getValue();
                    String method = (res.getVisa() == null || res.getVisa().isEmpty()) ? "Cash" : "Visa";
                    return new SimpleStringProperty(method);
                });

                TableView.getColumns().addAll(
                        nameCol, phoneCol, timeCol, guestsCol,
                        restaurantCol, sittingTypeCol, addressCol, payedMethodCol
                );
            }

            // Clear items first to prevent duplicates
            TableView.getItems().clear();
            TableView.getItems().setAll(allReservations);
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
