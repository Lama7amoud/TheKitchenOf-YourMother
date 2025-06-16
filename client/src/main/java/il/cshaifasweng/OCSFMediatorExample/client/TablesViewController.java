
package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.HostingTable;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.Point;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class TablesViewController {

    int numOfImages = 3;
    Image[] images;
    int[] reservedTables;

    @FXML
    private ComboBox<String> RestaurantCombo;

    @FXML
    private ComboBox<String> timePicker;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button TableButton1;

    @FXML
    private Button TableButton10;

    @FXML
    private Button TableButton11;

    @FXML
    private Button TableButton12;

    @FXML
    private Button TableButton13;

    @FXML
    private Button TableButton14;

    @FXML
    private Button TableButton2;

    @FXML
    private Button TableButton3;

    @FXML
    private Button TableButton4;

    @FXML
    private Button TableButton5;

    @FXML
    private Button TableButton6;

    @FXML
    private Button TableButton7;

    @FXML
    private Button TableButton8;

    @FXML
    private Button TableButton9;

    @FXML
    private ImageView imageView;

    private Button[] tableButtons;
    private Restaurant restaurantToMap;
    private Set<DayOfWeek> blockedDays = new HashSet<>();
    private Map<String, String[]> reservationMap = new HashMap<>();



    private static final Point[] HAIFA_TABLES = {
            new Point(38, 56), new Point(106, 56), new Point(36, 139), new Point(110, 139),
            new Point(35, 226), new Point(108, 227), new Point(249, 233), new Point(304, 233),
            new Point(23, 322), new Point(70, 325), new Point(126, 323), new Point(235, 325),
            new Point(291, 325)
    };

    private static final Point[] TEL_AVIV_TABLES = {
            new Point(116, 40), new Point(115, 133), new Point(276, 167), new Point(274, 242),
            new Point(121, 315), new Point(230, 314), new Point(29, 60), new Point(35, 312)
    };

    private static final Point[] NAHARIYA_TABLES = {
            new Point(20, 37), new Point(77, 36), new Point(20, 139), new Point(75, 139),
            new Point(239, 162), new Point(305, 164), new Point(20, 240), new Point(75, 240),
            new Point(236, 252), new Point(298, 253), new Point(20, 338), new Point(73, 339),
            new Point(241, 339), new Point(301, 337)
    };

    public static Point[] getTableCoordinates(int restaurantId) {
        return switch (restaurantId) {
            case 2 -> TEL_AVIV_TABLES;
            case 3 -> NAHARIYA_TABLES;
            default -> HAIFA_TABLES; // for case 1 and for other cases because it's the default for the manager
        };
    }

    public void updateReservedTables(String tableNum) {
        // Convert tableNum to an integer
        int tableToModify = Integer.parseInt(tableNum);

        if (reservedTables == null) {
            reservedTables = new int[0]; // Initialize to an empty array if null
        }
        // Check if the number exists in the array
        boolean exists = Arrays.stream(reservedTables).anyMatch(num -> num == tableToModify);

        if (exists) {
            // If exists, remove it
            reservedTables = Arrays.stream(reservedTables)
                    .filter(num -> num != tableToModify)
                    .toArray();
        } else {
            // If not exists, add it
            int[] newReservedTables = Arrays.copyOf(reservedTables, reservedTables.length + 1);
            newReservedTables[reservedTables.length] = tableToModify;
            reservedTables = newReservedTables;
        }

        // Sort the array
        Arrays.sort(reservedTables);
    }


    @FXML
    void backButton(ActionEvent event){
        EventBus.getDefault().unregister(this);
        App.switchScreen("Order Tables Page");
    }

    @FXML
    void comboBoxChoice(){
        Platform.runLater(() -> {

            String selectedRestaurant = RestaurantCombo.getValue();
            if (selectedRestaurant != null) {

                int j = 0;
                int restaurantId = 0;

                switch (selectedRestaurant) {
                    case "Haifa Branch":
                        imageView.setImage(images[0]);
                        j = 13;
                        restaurantId = 1;
                        break;
                    case "Tel-Aviv Branch":
                        imageView.setImage(images[1]);
                        j = 8;
                        restaurantId = 2;
                        break;
                    case "Nahariya Branch":
                        imageView.setImage(images[2]);
                        j = 14;
                        restaurantId = 3;
                        break;
                }
                for (int i = 0; i < j; i++){
                    tableButtons[i].setVisible(true);
                }
                for (int i = j; i < 14; i++){
                    tableButtons[i].setVisible(false);
                }
                positionTables(restaurantId);
                datePicker.setValue(null);
                timePicker.setValue(null);
                userAtt.setRestaurantInterest((short)restaurantId);
                Client.getClient().sendToServer("Get branch details;" + userAtt.getRestaurantInterest());
            }
        });
    }

    private void positionTables(int restaurantId) {
        Point[] selectedTables = getTableCoordinates(restaurantId);

        for (int i = 0; i < selectedTables.length && i < tableButtons.length; i++) {
            if (tableButtons[i] != null) {
                tableButtons[i].setLayoutX(selectedTables[i].x);
                tableButtons[i].setLayoutY(selectedTables[i].y);
            }
        }
    }

    private void fillPreferredTimeBox(Restaurant restaurant) {
        ObservableList<String> availableTimes = FXCollections.observableArrayList();

        double open = restaurant.getOpeningTime();
        double close = restaurant.getClosingTime();

        LocalDate selectedDate = datePicker.getValue();
        LocalTime now = LocalTime.now();

        // Convert open/close to LocalTime
        LocalTime openingTime = LocalTime.of((int) open, (int) ((open - (int) open) * 60));
        int closingHour = (int) close;
        int closingMinute = (int) ((close - closingHour) * 60);
        if (closingHour >= 24) {
            closingHour = 23;
            closingMinute = 59;
        }
        LocalTime closingTime = LocalTime.of(closingHour, closingMinute);
        LocalTime latestStartTime = closingTime.minusMinutes(90);  // 90 mins = 1.5 hours

        // Determine startTime
        LocalTime startTime;
        if (selectedDate.equals(LocalDate.now())) {
            // Round to next 15-minute slot
            int minute = now.getMinute();
            int nextQuarter = ((minute + 14) / 15) * 15;
            if (nextQuarter == 60) {
                now = now.plusHours(1).withMinute(0);
            } else {
                now = now.withMinute(nextQuarter).withSecond(0).withNano(0);
            }

            // Ensure not before opening time
            startTime = now.isBefore(openingTime) ? openingTime : now;
        } else {
            // For future dates, start at opening time
            startTime = openingTime;
        }

        // Generate slots from startTime to latestStartTime (inclusive)
        for (LocalTime time = startTime; !time.isAfter(latestStartTime); time = time.plusMinutes(15)) {
            availableTimes.add(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        timePicker.setItems(availableTimes);
        timePicker.setVisibleRowCount(Math.min(availableTimes.size(), 10));
    }

    // Handle restaurant object from server
    @Subscribe
    public void handleRestaurantLoaded(Restaurant restaurant) {
        Platform.runLater(() -> {
            this.restaurantToMap = restaurant;

            OrderData.getInstance().setSelectedRestaurant(restaurant);

            // Set restaurant globally for next page
            Client.getClientAttributes().setRestaurant(restaurant);

            setUpHolidayFilter(restaurant.getHolidays());
        });
    }

    private void setUpHolidayFilter(String holidaysString) {
        // Convert comma-separated day names (e.g., "SUNDAY,MONDAY") into DayOfWeek enum
        blockedDays = Arrays.stream(holidaysString.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());

        datePicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                boolean isHoliday = blockedDays.contains(date.getDayOfWeek());
                boolean isPast = date.isBefore(LocalDate.now());

                if (!empty && (isHoliday || isPast)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #FFAAAA;");
                }
            }
        });
    }

    @FXML
    void checkTimeAvailability(ActionEvent event){
        resetTablesStatus();
        String time = timePicker.getValue();
        if (time == null || time.isEmpty()){
            return;
        }
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timePicker.getValue();
        String selectedRestaurant = RestaurantCombo.getValue();

        int restaurantId = switch (selectedRestaurant) {
            case "Haifa Branch" -> 1;
            case "Tel-Aviv Branch" -> 2;
            case "Nahariya Branch" -> 3;
            default -> 0;
        };

        String message = String.format("CheckTablesAvailability;%d;%s;%s", restaurantId,
                selectedDate.toString(), selectedTime);
        Client.getClient().sendToServer(message);
    }

    @Subscribe
    public void handleReservationList(List<String[]> reservedTable) {
        if (reservedTable == null || reservedTable.isEmpty()) {
            return;
        }

        String[] firstRow = reservedTable.get(0);
        if (firstRow.length != 4 || !Character.isDigit(firstRow[0].charAt(0))) {
            return;
        }

        // Reset UI state
        List<Button> allTableButtons = resetTablesStatus();

        // Clear previous reservation data
        reservationMap.clear();

        // Process each reservation
        for (String[] row : reservedTable) {
            String tableId = row[0];
            String time = row[1];
            String name = row[2];
            String guests = row[3];

            // Save reservation info for the table
            reservationMap.put(tableId, new String[]{name, time, guests});

            // Find the corresponding button and mark it as reserved
            for (Button button : allTableButtons) {
                if (button.getId().equals("TableButton" + tableId)) {
                    button.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white;");
                    break;
                }
            }
        }
    }


    List<Button> resetTablesStatus(){
        List<Button> allTableButtons = List.of(
                TableButton1, TableButton2, TableButton3, TableButton4, TableButton5,
                TableButton6, TableButton7, TableButton8, TableButton9, TableButton10,
                TableButton11, TableButton12, TableButton13, TableButton14
        );

        for (Button tableButton : allTableButtons) {
            tableButton.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white;");
        }

        return allTableButtons;
    }

    @FXML
    void tableDetailsWindowPop(MouseEvent event) {
        Button sourceButton = (Button) event.getSource();
        String tableId = sourceButton.getText().replace("Table ", "");

        String[] reservation = reservationMap.get(tableId);
        if (reservation == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tableDetailsWindow.fxml"));
            Parent root = loader.load();

            // Get controller of the popup window
            TableDetailsWindowController controller = loader.getController();

            // Pass details to popup controller
            controller.setDetails(tableId, reservation[0], reservation[1], reservation[2]);

            Stage popupStage = new Stage();
            popupStage.setTitle("Table Details");
            popupStage.setScene(new Scene(root));
            Bounds bounds = datePicker.localToScreen(datePicker.getBoundsInLocal());
            double popupX = bounds.getMinX();
            double popupY = bounds.getMinY();
            popupStage.setX(popupX-20);
            popupStage.setY(popupY-20);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        Platform.runLater(() -> {
            int employee_permission = userAtt.getPermissionLevel();
            ObservableList<String> restaurantList = FXCollections.observableArrayList(
                    "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch"
            );
            // For manager and service
            int restaurantId;
            if ((employee_permission == 1 || employee_permission == 3)) {
                RestaurantCombo.setValue(restaurantList.get(userAtt.getRestaurantId()-1));
                RestaurantCombo.setDisable(true);
                restaurantId = userAtt.getRestaurantId();
            } else {
                RestaurantCombo.setDisable(false);
                restaurantId = userAtt.getRestaurantInterest();
                RestaurantCombo.setItems(restaurantList);
                RestaurantCombo.setValue(restaurantList.get(userAtt.getRestaurantInterest()-1));
            }

            tableButtons = new Button[14];

            tableButtons[0] = TableButton1;
            tableButtons[1] = TableButton2;
            tableButtons[2] = TableButton3;
            tableButtons[3] = TableButton4;
            tableButtons[4] = TableButton5;
            tableButtons[5] = TableButton6;
            tableButtons[6] = TableButton7;
            tableButtons[7] = TableButton8;
            tableButtons[8] = TableButton9;
            tableButtons[9] = TableButton10;
            tableButtons[10] = TableButton11;
            tableButtons[11] = TableButton12;
            tableButtons[12] = TableButton13;
            tableButtons[13] = TableButton14;

            positionTables(restaurantId); // Set the appropriate tables positions

            switch (restaurantId) {
                case 2:
                    TableButton9.setVisible(false);
                    TableButton10.setVisible(false);
                    TableButton11.setVisible(false);
                    TableButton12.setVisible(false);
                    TableButton13.setVisible(false);
                    TableButton14.setVisible(false);
                    break;
                case 1:
                    TableButton14.setVisible(false);
                    break;
            }
            try {
                images = new Image[numOfImages];

                for (int i = 0; i < numOfImages; i++) {
                    images[i] = new Image(String.valueOf(TablesViewController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Maps/" + i + ".jpg")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (employee_permission == 4){
                imageView.setImage(images[0]);
            }
            else {
                imageView.setImage(images[restaurantId - 1]);
            }


            datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
                if (newDate != null) {
                    timePicker.setValue(null);
                    if (restaurantToMap != null && !blockedDays.contains(newDate.getDayOfWeek())) {
                        fillPreferredTimeBox(restaurantToMap);
                    }
                }
            });

            Client.getClient().sendToServer("Get branch details;" + userAtt.getRestaurantInterest());
        });
    }

}
