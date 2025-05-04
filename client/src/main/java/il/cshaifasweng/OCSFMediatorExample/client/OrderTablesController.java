    package il.cshaifasweng.OCSFMediatorExample.client;

    import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;
    import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
    import javafx.application.Platform;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import org.greenrobot.eventbus.EventBus;
    import org.greenrobot.eventbus.Subscribe;
    import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
    import javafx.beans.value.ChangeListener;
    import javafx.beans.value.ObservableValue;

    import java.time.DayOfWeek;
    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.time.format.DateTimeFormatter;
    import java.util.*;
    import java.util.stream.Collectors;

    import org.greenrobot.eventbus.Subscribe;

    import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

    public class OrderTablesController {

        @FXML private ComboBox<String> PrefferedTimeBox;
        @FXML private Button ConfirmOrderButton;
        @FXML private Button backButton;
        @FXML private Button branchDetailsButton;
        @FXML private Label branchNameLabel;
        @FXML private Button viewMapButton;
        @FXML private DatePicker datePicker;
        @FXML private Button insideButton;
        @FXML private Button outsideButton;
        @FXML private TextField guestCountField;
        @FXML private TextField generalNoteField;

        private String selectedTime;
        private String sittingType;
        private int guestCount;
        private String generalNote;


        private Restaurant selectedRestaurant;
        private Set<DayOfWeek> blockedDays = new HashSet<>();

        @FXML
        void switchPage(ActionEvent event) {
            Platform.runLater(() -> {
                Button source = (Button) event.getSource();
                String page = switch (source.getId()) {
                    case "backButton" -> "Main Page";
                    case "viewMapButton" -> {
                        if (selectedRestaurant != null) {
                            userAtt.setRestaurantInterest((short) selectedRestaurant.getId());
                        }
                        yield "Tables Page"; // ✅ Match the name in App.java switchScreen map
                    }
                    case "branchDetailsButton" -> "Branch Page";
                    default -> "";
                };
                App.switchScreen(page);
            });
        }


        @FXML
        void initialize() {
            EventBus.getDefault().register(this);

            // Disable confirm button at start
            ConfirmOrderButton.setDisable(true);

            // Add listeners to check input
            guestCountField.textProperty().addListener((obs, oldVal, newVal) -> checkFormCompletion());
            PrefferedTimeBox.valueProperty().addListener((obs, oldVal, newVal) -> checkFormCompletion());

            Platform.runLater(() -> {
                int permission = userAtt.getPermissionLevel();
                viewMapButton.setVisible(permission >= 1);
                Client.getClient().sendToServer("Get branch details;" + userAtt.getRestaurantInterest());

                datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
                    if (newDate != null && selectedRestaurant != null && !blockedDays.contains(newDate.getDayOfWeek())) {
                        fillPreferredTimeBox(selectedRestaurant);
                    }
                });
            });
        }


        private void checkFormCompletion() {
            Platform.runLater(() -> {
            boolean hasValidGuestCount = false;
            try {
                int guests = Integer.parseInt(guestCountField.getText());
                hasValidGuestCount = guests > 0;
            } catch (NumberFormatException ignored) {}

            boolean hasSelectedTime = PrefferedTimeBox.getValue() != null;
            boolean sittingChosen = insideButton.isDisabled() || outsideButton.isDisabled(); // one is selected if it's disabled

            boolean allValid = hasValidGuestCount && hasSelectedTime && sittingChosen;
            ConfirmOrderButton.setDisable(!allValid);
            });
        }


        @FXML
        void handleConfirmOrderClick(ActionEvent event) {
            Platform.runLater(() -> {
                selectedTime = PrefferedTimeBox.getValue();
                sittingType = insideButton.isDisabled() ? "Inside" : "Outside";  // Disabled = selected
                try {
                    guestCount = Integer.parseInt(guestCountField.getText());
                } catch (NumberFormatException e) {
                    guestCount = 0; // fallback if not valid
                }
                generalNote = generalNoteField.getText();

                // You can save the data to a singleton for later use
                OrderData.getInstance().setDetails(selectedTime, sittingType, guestCount, generalNote);
                OrderData.getInstance().setDate(datePicker.getValue());
                System.out.println("Switching to Confirm Order Page...");
                App.switchScreen("Confirm Order Page");
            });
        }

        @FXML
        void handleInsideClick(ActionEvent event) {
            insideButton.setDisable(true);
            insideButton.setStyle("-fx-background-color: #ffa500AA; -fx-font-style: italic; -fx-font-weight: bold; -fx-background-radius: 5em;");

            outsideButton.setDisable(false);
            outsideButton.setStyle("-fx-background-color: #ffa500; -fx-font-style: italic; -fx-font-weight: bold; -fx-background-radius: 5em;");
            checkFormCompletion();

            OrderData.getInstance().setSittingType("Inside");

        }


        @FXML
        void handleOutsideClick(ActionEvent event) {
            outsideButton.setDisable(true);
            outsideButton.setStyle("-fx-background-color: #ffa500AA; -fx-font-style: italic; -fx-font-weight: bold; -fx-background-radius: 5em;");

            insideButton.setDisable(false);
            insideButton.setStyle("-fx-background-color: #ffa500; -fx-font-style: italic; -fx-font-weight: bold; -fx-background-radius: 5em;");
            checkFormCompletion();

            OrderData.getInstance().setSittingType("Outside");

        }

        // Handle restaurant object from server
        @Subscribe
        public void handleRestaurantLoaded(Restaurant restaurant) {
            Platform.runLater(() -> {
                this.selectedRestaurant = restaurant;

                branchNameLabel.setText(restaurant.getName());

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

            PrefferedTimeBox.setItems(availableTimes);
            PrefferedTimeBox.setVisibleRowCount(Math.min(availableTimes.size(), 10));
        }

    }
