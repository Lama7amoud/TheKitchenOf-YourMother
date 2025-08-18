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
import javafx.scene.chart.NumberAxis;
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

    private List<DailyReport> currentReportList;
    private TableColumn<Reservation, String> nameCol;
    private TableColumn<Reservation, String> phoneCol;
    private TableColumn<Reservation, LocalDateTime> timeCol;
    private TableColumn<Reservation, Integer> guestsCol;
    private TableColumn<Reservation, String> restaurantCol;
    private TableColumn<Reservation, String> sittingTypeCol;
    private TableColumn<Reservation, String> addressCol;
    private TableColumn<Reservation, String> payedMethodCol;
    private String message = null;

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

            errorLabel.setStyle("-fx-text-fill: red;");
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

            if(Client.getClientAttributes().getPermissionLevel() != 4){ // can reach this page the managers (branches or restaurant)
                switch(Client.getClientAttributes().getRestaurantInterest()){
                    case(1): wantedRestaurant = "Haifa-Mom Kitchen";
                        break;
                    case(2): wantedRestaurant = "Tel-Aviv-Mom Kitchen";
                        break;
                    case (3): wantedRestaurant = "Nahariya-Mom Kitchen";
                        break;
                    default: wantedRestaurant = "";
                }
            } else{
                switch(restaurantNameBox.getValue()){
                    case("Haifa Branch"): wantedRestaurant = "Haifa-Mom Kitchen";
                        break;
                    case("Tel-Aviv Branch"): wantedRestaurant = "Tel-Aviv-Mom Kitchen";
                        break;
                    case ("Nahariya Branch"): wantedRestaurant = "Nahariya-Mom Kitchen";
                        break;
                    case ("All") : wantedRestaurant = "All";
                        break;
                    default: wantedRestaurant = "";
                }

            }
            System.out.println("my restaurant is: " + wantedRestaurant);
            errorLabel.setText("");
            graphOrTableBox.setValue("None");
            totalCustomersLabel.setText("0");
            deliveryOrdersLabel.setText("0");
            TableView.getColumns().clear();
            complaintsHistogram.getData().clear();
            currentReportList = null;
            message = wantedRestaurant + ";" + monthLabel.getText() + ";" + yearLabel.getText();
            Client client = Client.getClient();
            client.sendToServer("request_reports;" + message);
        });

    }

    @Subscribe
    public void reportsUpdated(String msg){
        if((!(msg.equals("Monthly report updated"))) || (message == null)){
            return;
        }
        Client client = Client.getClient();
        client.sendToServer("request_reports;" + message);
    }

    @Subscribe
    public void reportsReceived(List<DailyReport> reportsList) {
        if (reportsList == null) {
            return;
        }
        if (!reportsList.isEmpty()) {
            Object first = reportsList.get(0);

            if (!(first instanceof DailyReport)) {
                return;
            }
        }
        currentReportList = reportsList;

        Platform.runLater(() -> {
            complaintsHistogram.getData().clear(); // reset chart

            if (reportsList == null || reportsList.isEmpty()) {
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setText("No reports found for the selected filters.");
                totalCustomersLabel.setText("0");
                deliveryOrdersLabel.setText("0");
                TableView.getItems().clear(); // clear table items
                return;
            }

            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Live data displayed");

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
            NumberAxis yAxis = (NumberAxis) complaintsHistogram.getYAxis();
            yAxis.setAutoRanging(false);   // disable auto-scaling
            yAxis.setLowerBound(0);

            // find the max complaints count from your reports
            int maxComplaints = complaintsMap.values().stream().max(Integer::compareTo).orElse(0);

            // round up a little so chart looks nice
            int upperBound = Math.max(5, maxComplaints + 1);

            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(1);          // step 1
            yAxis.setMinorTickCount(0);    // no minor ticks

            listChoice(null);
        });
    }


    @FXML
    void listChoice(ActionEvent event) {
        if (currentReportList == null || currentReportList.isEmpty()) return;

        String choice = graphOrTableBox.getValue(); // get selected value
        if (choice == null || choice.equals("None")) {
            TableView.getItems().clear();
            return;
        }

        Platform.runLater(() -> {

            // Gather reservations filtered by isTakeAway
            List<Reservation> filteredReservations = currentReportList.stream()
                    .flatMap(report -> report.getReservationsList().stream())
                    .filter(res -> {
                        if (choice.equals("Reservations")) return !res.isTakeAway();
                        else if (choice.equals("deliveries")) return res.isTakeAway();
                        else return true;
                    })
                    .collect(Collectors.toList());

            // Create columns once
            if (TableView.getColumns().isEmpty()) {
                // Name
                nameCol = new TableColumn<>("Name");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

                // Phone
                phoneCol = new TableColumn<>("Phone");
                phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

                // Reservation Time
                timeCol = new TableColumn<>("Reservation Time");
                timeCol.setCellValueFactory(new PropertyValueFactory<>("reservationTime"));

                // Guests
                guestsCol = new TableColumn<>("Guests");
                guestsCol.setCellValueFactory(new PropertyValueFactory<>("totalGuests"));

                // Restaurant Name
                restaurantCol = new TableColumn<>("Restaurant");
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
                sittingTypeCol = new TableColumn<>("Sitting Type");
                sittingTypeCol.setCellValueFactory(new PropertyValueFactory<>("sittingType"));

                // Address
                addressCol = new TableColumn<>("Address");
                addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

                // Payed Method
                payedMethodCol = new TableColumn<>("Payed Method");
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

            // Show/hide columns depending on selection
            boolean isDeliveries = choice.equals("deliveries");
            guestsCol.setVisible(!isDeliveries);
            sittingTypeCol.setVisible(!isDeliveries);

            // Update table items
            TableView.getItems().clear();
            TableView.getItems().setAll(filteredReservations);
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
                    "None" , "Reservations", "deliveries"
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
