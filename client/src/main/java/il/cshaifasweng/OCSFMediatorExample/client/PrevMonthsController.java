package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchManager;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.MonthlyReport;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PrevMonthsController {

    @FXML
    private Label complaintsCountLabel;

    @FXML
    private Label deliveryOrdersNumLabel;

    @FXML
    private Label reservationsNumLabel;

    @FXML
    private Label totalCustomersNumLabel;

    @FXML
    private ComboBox<Integer> yearCombo;

    @FXML
    private ComboBox<Integer> monthsCombo;

    @FXML
    private Button backButton;

    @FXML
    private Button Confirm;

    @FXML
    void back(ActionEvent event){
        EventBus.getDefault().unregister(this);
        Platform.runLater(() -> {
            App.switchScreen("monthly Reports Page");
        });
    }

    @FXML
    void getReports(ActionEvent event){
        Client client = Client.getClient();
        User user = Client.getClientAttributes();

        int restaurantId;
        if(user.getPermissionLevel() != 4){
            restaurantId = user.getRestaurantId();
        } else {
            restaurantId = 0;
        }

        int month = monthsCombo.getValue();
        int year = yearCombo.getValue();

        client.sendToServer(BranchManager.requestPrevReportsMsg() + restaurantId + ";" + month + ";" + year);
        Platform.runLater(() -> {
            totalCustomersNumLabel.setText("Total Customers:");
            deliveryOrdersNumLabel.setText("Delivery Orders:");
            reservationsNumLabel.setText("Reservations:");
            complaintsCountLabel.setText("Complaints Count:");
        });
    }

    @Subscribe
    public void reportReceived(List<MonthlyReport> reports) {
        if (reports == null) return;

        if (!reports.isEmpty()) {
            Object first = reports.get(0);

            if (!(first instanceof MonthlyReport)) {
                return;
            }
        } else {return;}

        // Helper to get branch name from restaurantId
        Function<Integer, String> getBranchName = id -> switch (id) {
            case 1 -> "Haifa Branch";
            case 2 -> "Tel-Aviv Branch";
            case 3 -> "Nahariya Branch";
            default -> "Branch " + id;
        };

        // Build strings for each label
        String totalCustomersText = reports.stream()
                .map(r -> getBranchName.apply(r.getRestaurant().getId()) + ": " + r.getTotalCustomers())
                .collect(Collectors.joining(" / "));

        String deliveryOrdersText = reports.stream()
                .map(r -> getBranchName.apply(r.getRestaurant().getId()) + ": " + r.getDeliveryOrders())
                .collect(Collectors.joining(" / "));

        String reservationsText = reports.stream()
                .map(r -> getBranchName.apply(r.getRestaurant().getId()) + ": " + r.getReservations())
                .collect(Collectors.joining(" / "));

        String complaintsText = reports.stream()
                .map(r -> getBranchName.apply(r.getRestaurant().getId()) + ": " + r.getComplaintsCount())
                .collect(Collectors.joining(" / "));

        Platform.runLater(() -> {
            totalCustomersNumLabel.setText("Total Customers:        " + totalCustomersText);
            deliveryOrdersNumLabel.setText("Delivery Orders:        " + deliveryOrdersText);
            reservationsNumLabel.setText("Reservations:         " + reservationsText);
            complaintsCountLabel.setText("Complaints Count:         " + complaintsText);
        });
    }


    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        Platform.runLater(() -> {
            // disable months combo until a year is chosen
            monthsCombo.setDisable(true);
            // disable Confirm button until a month is chosen
            Confirm.setDisable(true);

            // get current year and month
            int currentYear = java.time.LocalDate.now().getYear();
            int currentMonth = java.time.LocalDate.now().getMonthValue();

            // populate years from 2025 until current year
            for (int year = 2025; year <= currentYear; year++) {
                yearCombo.getItems().add(year);
            }

            // when user selects a year
            yearCombo.setOnAction(event -> {
                Integer selectedYear = yearCombo.getValue();
                monthsCombo.getItems().clear(); // reset months
                Confirm.setDisable(true); // reset confirm when year changes

                if (selectedYear != null) {
                    // if selected year is current year → allow months up to current month
                    int maxMonth = (selectedYear == currentYear) ? currentMonth-1 : 12;

                    for (int m = 1; m <= maxMonth; m++) {
                        monthsCombo.getItems().add(m);
                    }

                    monthsCombo.setDisable(false); // enable months combo
                } else {
                    monthsCombo.setDisable(true);
                }
            });

            // when user selects a month
            monthsCombo.setOnAction(event -> {
                Integer selectedMonth = monthsCombo.getValue();
                Confirm.setDisable(selectedMonth == null); // enable only if month selected
            });
        });
    }



}