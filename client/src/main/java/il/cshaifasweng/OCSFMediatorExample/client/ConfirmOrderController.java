package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
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
    private TextField addressField, idField, nameField, phoneField,visaTextField;

    @FXML
    private Button backToMainPageButton, backToOrderTablesButton, branchDetailsButton, viewMapButton, ConfirmOrderButton;

    @FXML
    private Label guestCountLabel, notesLabel, preferredTimeLabel, sittingTypeLabel,visaLabel;

    @FXML
    private  Label phoneLabel,nameLabel,idLabel,addressLabel,errorTimeLabel;

    private List<HostingTable> lastReceivedTables = new ArrayList<>();
    private boolean reservationAlreadySent = false;

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
        visaLabel.setVisible(false);


        OrderData order = OrderData.getInstance();

        preferredTimeLabel.setText(order.getPreferredTime());
        sittingTypeLabel.setText(order.getSittingType());
        guestCountLabel.setText(String.valueOf(order.getGuestCount()));
        notesLabel.setText(order.getGeneralNote());

        Reservation tempReservation = new Reservation();
        tempReservation.setRestaurant(Client.getClientAttributes().getRestaurant());

        // This reservation is just for availability checking
        String raw = order.getPreferredTime(); // e.g., "Inside - 20:30"
        String timePart = raw.contains(" - ") ? raw.split(" - ")[1].trim() : raw;
        LocalDateTime reservationTime = LocalDateTime.of(order.getDate(), LocalTime.parse(timePart));
        tempReservation.setReservationTime(reservationTime);
        tempReservation.setSittingType(order.getSittingType());

        Client.getClient().sendToServer(new ReservationRequest(tempReservation, false));

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

        // Hide all labels
        nameLabel.setVisible(false);
        phoneLabel.setVisible(false);
        idLabel.setVisible(false);
        addressLabel.setVisible(false);
        visaLabel.setVisible(false);

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String id = idField.getText().trim();
        String address = addressField.getText().trim();
        String visa = visaTextField.getText().trim();

        if (name.split("\\s+").length < 2 || name.matches(".*\\d.*")) {
            nameLabel.setVisible(true);
            isValid = false;
        }
        if (!phone.matches("^05\\d{8}$")) {
            phoneLabel.setVisible(true);
            isValid = false;
        }
        if (!id.matches("^\\d{9}$")) {
            idLabel.setText("ID must be exactly 9 digits.");
            idLabel.setVisible(true);
            idLabel.setManaged(true);
            isValid = false;
        }
        if (address.isEmpty()) {
            addressLabel.setVisible(true);
            isValid = false;
        }
        if (!visa.matches("^\\d{16}$")) {  // validation for 16 digits
            visaLabel.setVisible(true);
            isValid = false;
        }


        if (!isValid || reservationAlreadySent) return;
        currentFormData = new String[]{name, phone, id, address, visa};
        Client.getClient().sendToServer("check_reservation_id;" + id);

    }

    @Subscribe
    public void onIdCheckResponse(IdCheckEvent event) {
        Platform.runLater(() -> {
            System.out.println("ID check result: " + event.doesExist());
            if (reservationAlreadySent) return;
            idAlreadyUsed = event.doesExist();
            if (idAlreadyUsed) {
                idLabel.setText("This ID already has a reservation.");
                idLabel.setVisible(true);
                idLabel.setManaged(true);
            } else {
                idLabel.setVisible(false);
                idLabel.setManaged(false);
                reservationAlreadySent = true;
                // Only now – proceed and send both save TakeAway + reservation
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
        String visa = currentFormData[4];

        //check error
        System.out.println("Proceeding to save reservation for ID: " + id);

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
        reservation.setPayed(false);
        reservation.setVisa(visa);  // Set the visa field instead of empty string
        reservation.setReceivingTime(LocalDateTime.now());  // Set current time as receiving time
        reservation.setRestaurant(Client.getClientAttributes().getRestaurant());

        List<HostingTable> chosenTables = findTablesForTime(order.getPreferredTime());
        reservation.setReservedTables(chosenTables);

        ReservationRequest request = new ReservationRequest(reservation, true);
        Client.getClient().sendToServer(request);
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

        // Calculate bounds
        int closingHour = (int) restaurant.getClosingTime();
        int closingMinutes = (int) ((restaurant.getClosingTime() - closingHour) * 60);
        LocalTime restaurantClosing = LocalTime.of(closingHour, closingMinutes);
        LocalDateTime endOfDay = LocalDateTime.of(date, restaurantClosing).minusMinutes(RESERVATION_DURATION_MINUTES);

        this.lastReceivedTables = availableTables;
        List<String> timeSlots = new ArrayList<>();

        // Try first: slots within next hour
        LocalDateTime endOneHour = start.plusMinutes(60);
        List<String> nextHourSlots = findAvailableSlots(start, endOneHour, availableTables, sittingType, requiredSeats);
        timeSlots.addAll(nextHourSlots);

        Platform.runLater(() -> {
            AvailableTimeBox.getItems().clear();
            AvailableTimeBox.setDisable(false);

            if (!timeSlots.isEmpty()) {
                AvailableTimeBox.getItems().addAll(timeSlots);
                AvailableTimeBox.getSelectionModel().selectFirst();
                errorTimeLabel.setVisible(false);
            } else {
                // If no slots in next hour, try to find 5 closest after preferred time today
                List<String> fallbackSlots = findAvailableSlots(start, endOfDay, availableTables, sittingType, requiredSeats);
                if (!fallbackSlots.isEmpty()) {
                    timeSlots.addAll(fallbackSlots.stream().limit(5).collect(Collectors.toList()));
                    AvailableTimeBox.getItems().addAll(timeSlots);
                    AvailableTimeBox.getSelectionModel().selectFirst();
                    errorTimeLabel.setText("No available time in the next hour. Showing closest today:");
                    errorTimeLabel.setVisible(true);
                } else {
                    AvailableTimeBox.setPromptText("No available times");
                    AvailableTimeBox.setDisable(true);
                    errorTimeLabel.setText("There is no available time today, please try another day.");
                    errorTimeLabel.setVisible(true);
                }
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

    private List<String> findAvailableSlots(LocalDateTime start, LocalDateTime end,
                                            List<HostingTable> availableTables,
                                            String sittingType, int requiredSeats) {
        List<String> slots = new ArrayList<>();
        for (LocalDateTime slot = start; !slot.isAfter(end); slot = slot.plusMinutes(15)) {
            final LocalDateTime currentSlot = slot;
            List<HostingTable> suitableTables = availableTables.stream()
                    .filter(table -> table.isInside() == sittingType.equalsIgnoreCase("Inside"))
                    .filter(table -> isTableAvailable(table, currentSlot))
                    .collect(Collectors.toList());

            List<List<HostingTable>> combinations = new ArrayList<>();
            findOptimalCombinations(suitableTables, 0, new ArrayList<>(), 0, requiredSeats, combinations);

            if (!combinations.isEmpty()) {
                String formatted = slot.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                slots.add(sittingType + " - " + formatted);
            }
        }
        return slots;
    }


}