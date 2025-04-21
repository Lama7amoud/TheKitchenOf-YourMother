package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.HostingTable;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConfirmOrderController {



    private static final int RESERVATION_DURATION_MINUTES = 60;
    private boolean idAlreadyUsed = false;
    private String[] currentFormData;
    @FXML
    private ComboBox<String> AvailableTimeBox;

    @FXML
    private Button ConfirmOrderButton;

    @FXML
    private TextField addressField, idField, nameField, phoneField;

    @FXML
    private Button backToMainPageButton, backToOrderTablesButton, branchDetailsButton, viewMapButton;

    @FXML
    private Label guestCountLabel, notesLabel, preferredTimeLabel, sittingTypeLabel;

    @FXML
    private  Label phoneLabel,nameLabel,idLabel,addressLabel;

    private List<HostingTable> lastReceivedTables = new ArrayList<>();

    @FXML
    private void handleBackToMainPage(ActionEvent event) {
        Platform.runLater(() -> App.switchScreen("Main Page"));
    }

    @FXML
    private void handleBackToOrderTables(ActionEvent event) {
        Platform.runLater(() -> App.switchScreen("Order Tables Page"));
    }

    @FXML
    private void switchPage(ActionEvent event) {
        Platform.runLater(() -> {
            Button source = (Button) event.getSource();
            String page = switch (source.getId()) {
                case "backButton" -> "Main Page";
                case "viewMapButton" -> "Tables Page";
                case "branchDetailsButton" -> "Branch Page";
                default -> null;
            };
            if (page != null) App.switchScreen(page);
        });
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        // Hide error labels when page loads
        nameLabel.setVisible(false);
        phoneLabel.setVisible(false);
        idLabel.setVisible(false);
        addressLabel.setVisible(false);

        OrderData order = OrderData.getInstance();

        preferredTimeLabel.setText(order.getPreferredTime());
        sittingTypeLabel.setText(order.getSittingType());
        guestCountLabel.setText(String.valueOf(order.getGuestCount()));
        notesLabel.setText(order.getGeneralNote());
        Reservation tempReservation = new Reservation();
        tempReservation.setRestaurant(Client.getClientAttributes().getRestaurant());

        // This reservation is just for availability checking. Use reservationTime:
        String raw = order.getPreferredTime(); // e.g., "Inside - 20:30"
        String timePart = raw.contains(" - ") ? raw.split(" - ")[1].trim() : raw;
        LocalDateTime reservationTime = LocalDateTime.of(order.getDate(), LocalTime.parse(timePart));
        tempReservation.setReservationTime(reservationTime);
        tempReservation.setSittingType(order.getSittingType());

        Client.getClient().sendToServer(tempReservation);


        AvailableTimeBox.setOnAction(e -> {
            String selected = AvailableTimeBox.getValue();
            if (selected != null && selected.contains(" - ")) {
                String[] parts = selected.split(" - ");
                OrderData.getInstance().setSittingType(parts[0].trim());
                OrderData.getInstance().setPreferredTime(parts[1].trim());
            }
        });
    }

    private List<HostingTable> findTablesForTime(String preferredTime) {
        LocalDateTime time = LocalDateTime.of(OrderData.getInstance().getDate(), LocalTime.parse(preferredTime));
        String sittingType = OrderData.getInstance().getSittingType();
        int requiredSeats = OrderData.getInstance().getGuestCount();

        List<HostingTable> suitable = lastReceivedTables.stream()
                .filter(t -> t.isInside() == sittingType.equalsIgnoreCase("Inside"))
                .filter(t -> isTableAvailable(t, time))
                .collect(Collectors.toList());

        List<List<HostingTable>> combinations = new ArrayList<>();
        findOptimalCombinations(suitable, 0, new ArrayList<>(), 0, requiredSeats, combinations);

        // Return the optimal one (fewest tables used)
        return combinations.stream()
                .min(Comparator.comparingInt(List::size)) // Smallest number of tables
                .orElse(new ArrayList<>());
    }



    @FXML
    void handleConfirmReservation() {
        boolean isValid = true;

        // Hide all labels initially
        nameLabel.setVisible(false);
        phoneLabel.setVisible(false);
        idLabel.setVisible(false);
        addressLabel.setVisible(false);

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String id = idField.getText().trim();
        String address = addressField.getText().trim();

        // Name must have at least 2 parts and no digits
        if (name.split("\\s+").length < 2 || name.matches(".*\\d.*")) {
            nameLabel.setVisible(true);
            isValid = false;
        }

        // Phone must be exactly 10 digits and start with "05"
        if (!phone.matches("^05\\d{8}$")) {
            phoneLabel.setVisible(true);
            isValid = false;
        }

        // ID must be exactly 9 digits
        if (!id.matches("^\\d{9}$")) {
            idLabel.setVisible(true);
            isValid = false;
        }

        // Address must not be empty
        if (address.isEmpty()) {
            addressLabel.setVisible(true);
            isValid = false;
        }

        if (!isValid) {
            return; // Don't proceed if any field is invalid
        }

        currentFormData = new String[]{name, phone, id, address};
        Client.getClient().sendToServer("check_reservation_id;" + id);
        // Proceed with reservation only if all inputs are valid
        OrderData order = OrderData.getInstance();


        String raw = order.getPreferredTime(); // Example: "Outside - 14:30"
        String timePart = raw.contains(" - ") ? raw.split(" - ")[1].trim() : raw;
        LocalTime preferredTime = LocalTime.parse(timePart);
        LocalDateTime reservationDateTime = LocalDateTime.of(order.getDate(), preferredTime);

        Reservation reservation = new Reservation();
        reservation.setName(name);
        reservation.setIdNumber(id);
        reservation.setPhoneNumber(phone);
        reservation.setAddress(address);
        reservation.setSittingType(order.getSittingType());
        reservation.setTotalGuests(order.getGuestCount());
        reservation.setReservationTime(reservationDateTime);
        reservation.setVisa("");
        reservation.setPayed(false);

        reservation.setRestaurant(Client.getClientAttributes().getRestaurant());
        List<HostingTable> chosenTables = findTablesForTime(order.getPreferredTime());
        reservation.setReservedTables(chosenTables);
    }

    @Subscribe
    public void onIdCheckResponse(IdCheckEvent event) {
        Platform.runLater(() -> {
            idAlreadyUsed = event.doesExist();

            if (idAlreadyUsed) {
                idLabel.setText("This ID already has a reservation.");
                idLabel.setVisible(true);
                idLabel.setManaged(true);
            } else {
                idLabel.setVisible(false);
                idLabel.setManaged(false);
                proceedWithReservation();
            }
        });
    }



    private void proceedWithReservation() {
        if (currentFormData == null) return;
        String name = currentFormData[0];
        String phone = currentFormData[1];
        String id = currentFormData[2];
        String address = currentFormData[3];

        OrderData order = OrderData.getInstance();
        String raw = order.getPreferredTime();
        String timePart = raw.contains(" - ") ? raw.split(" - ")[1].trim() : raw;
        LocalTime preferredTime = LocalTime.parse(timePart);
        LocalDateTime reservationDateTime = LocalDateTime.of(order.getDate(), preferredTime);

        Reservation reservation = new Reservation();
        reservation.setName(name);
        reservation.setIdNumber(id);
        reservation.setPhoneNumber(phone);
        reservation.setAddress(address);
        reservation.setSittingType(order.getSittingType());
        reservation.setTotalGuests(order.getGuestCount());
        reservation.setReservationTime(reservationDateTime);
        reservation.setVisa("");
        reservation.setPayed(false);
        reservation.setRestaurant(Client.getClientAttributes().getRestaurant());

        List<HostingTable> chosenTables = findTablesForTime(order.getPreferredTime());
        reservation.setReservedTables(chosenTables);

        Client.getClient().sendToServer("save_reservation");
        Client.getClient().sendToServer(reservation);
    }

    @Subscribe
    public void checkingList(List<HostingTable> availableTables) {
        System.out.println("Received " + availableTables.size() + " available tables from server.");

        OrderData order = OrderData.getInstance();
        int requiredSeats = order.getGuestCount();
        String sittingType = order.getSittingType();
        LocalDate date = order.getDate();
        LocalTime preferredTime = LocalTime.parse(order.getPreferredTime());
        LocalDateTime start = LocalDateTime.of(date, preferredTime);

        Restaurant restaurant = Client.getClientAttributes().getRestaurant();
        if (restaurant == null) {
            restaurant = OrderData.getInstance().getSelectedRestaurant();
        }

        // Get restaurant closing time
        int closingHour = (int) restaurant.getClosingTime();
        int closingMinutes = (int) ((restaurant.getClosingTime() - closingHour) * 60);
        LocalTime restaurantClosing = LocalTime.of(closingHour, closingMinutes);

        // Calculate time bounds
        LocalDateTime latestAllowedEnd = LocalDateTime.of(date, restaurantClosing).minusMinutes(RESERVATION_DURATION_MINUTES);
        LocalDateTime maxSlotEnd = start.plusMinutes(RESERVATION_DURATION_MINUTES);
        LocalDateTime end = maxSlotEnd.isBefore(latestAllowedEnd) ? maxSlotEnd : latestAllowedEnd;

        List<String> timeSlots = new ArrayList<>();

        for (LocalDateTime slot = start; !slot.isAfter(end); slot = slot.plusMinutes(15)) {
            LocalDateTime currentSlot = slot;
            List<HostingTable> suitableTables = availableTables.stream()
                    .filter(table -> table.isInside() == sittingType.equalsIgnoreCase("Inside"))
                    .filter(table -> isTableAvailable(table, currentSlot))
                    .collect(Collectors.toList());

            List<List<HostingTable>> combinations = new ArrayList<>();
            findOptimalCombinations(suitableTables, 0,
                    new ArrayList<>(), 0, requiredSeats, combinations);

            if (!combinations.isEmpty()) {
                String formattedTime = currentSlot.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                timeSlots.add(sittingType + " - " + formattedTime);
            }
        }

        System.out.println("Final available slots: " + timeSlots);
        this.lastReceivedTables = availableTables;

        Platform.runLater(() -> {
            AvailableTimeBox.getItems().clear();
            if (!timeSlots.isEmpty()) {
                AvailableTimeBox.getItems().addAll(timeSlots);
                AvailableTimeBox.getSelectionModel().selectFirst();
                AvailableTimeBox.setDisable(false);
            } else {
                AvailableTimeBox.setPromptText("No available times");
                AvailableTimeBox.setDisable(true);
            }
        });
    }


    private boolean isTableAvailable(HostingTable table, LocalDateTime slotStart) {
        LocalDateTime slotEnd = slotStart.plusMinutes(RESERVATION_DURATION_MINUTES);
        List<LocalDateTime> reserved = table.getReservedTimes();
        if (reserved == null) return true;

        for (LocalDateTime reservedStart : reserved) {
            LocalDateTime reservedEnd = reservedStart.plusMinutes(RESERVATION_DURATION_MINUTES);
            if (slotStart.isBefore(reservedEnd) && reservedStart.isBefore(slotEnd)) {
                return false;
            }
        }
        return true;
    }

    private void findOptimalCombinations(List<HostingTable> tables, int index,
                                         List<HostingTable> currentCombo, int currentSeats,
                                         int requiredSeats, List<List<HostingTable>> results) {
        if (currentSeats >= requiredSeats) {
            results.add(new ArrayList<>(currentCombo));  // Found a valid combo
            return;
        }

        if (index >= tables.size()) return;

        // Include current table
        HostingTable current = tables.get(index);
        currentCombo.add(current);
        findOptimalCombinations(tables, index + 1, currentCombo, currentSeats + current.getSeatsNumber(), requiredSeats, results);

        // Exclude current table
        currentCombo.remove(currentCombo.size() - 1);
        findOptimalCombinations(tables, index + 1, currentCombo, currentSeats, requiredSeats, results);
    }

}