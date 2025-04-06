package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.HostingTable;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;
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
import java.util.List;
import java.util.stream.Collectors;

public class ConfirmOrderController {

    @FXML
    private TextField visaField;


    @FXML
    private ComboBox<String> AvailableTimeBox;

    @FXML
    private Button ConfirmOrderButton;

    @FXML
    private TextField addressField, idField, nameField, phoneField;

    @FXML
    private Button backToMainPageButton, backToOrderTablesButton, branchDetailsButton, viewMapButton;

    @FXML
    private Label branchNameLabel, guestCountLabel, notesLabel, preferredTimeLabel, sittingTypeLabel;


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

        OrderData order = OrderData.getInstance();

        preferredTimeLabel.setText(order.getPreferredTime());
        sittingTypeLabel.setText(order.getSittingType());
        guestCountLabel.setText(String.valueOf(order.getGuestCount()));
        notesLabel.setText(order.getGeneralNote());

        // Prepare reservation to get available tables
        Reservation tempReservation = new Reservation();
        tempReservation.setRestaurantId(Client.getClientAttributes().getRestaurantInterest());
        System.out.println(Client.getClientAttributes().getRestaurantInterest());
        tempReservation.setDate(order.getDate());
        tempReservation.setTimeSlot(order.getPreferredTime());

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

    @FXML
    void handleConfirmReservation() {
        OrderData order = OrderData.getInstance();

        Reservation reservation = new Reservation();
        reservation.setName(nameField.getText());
        reservation.setIdNumber(idField.getText());
        reservation.setPhoneNumber(phoneField.getText());
        reservation.setAddress(addressField.getText());
        reservation.setTimeSlot(order.getPreferredTime());
        reservation.setSittingType(order.getSittingType());
        reservation.setTotalGuests(order.getGuestCount());
        reservation.setOrderingTime(LocalDateTime.now());
        reservation.setVisa("");
        reservation.setPayed(false);
        reservation.setRestaurantId(Client.getClientAttributes().getRestaurantInterest());
        reservation.setDate(order.getDate());



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
        LocalDateTime end = start.plusMinutes(90);  // next 1.5 hours

        List<String> timeSlots = new ArrayList<>();

        for (LocalDateTime slot = start; !slot.isAfter(end.minusMinutes(30)); slot = slot.plusMinutes(30)) {
            LocalDateTime slotStart = slot;

            List<HostingTable> suitableTables = availableTables.stream()
                    .filter(table -> table.isInside() == sittingType.equalsIgnoreCase("Inside"))
                    .filter(table -> isTableAvailable(table, slotStart))
                    .collect(Collectors.toList());

            System.out.println("Time slot " + slotStart.toLocalTime() + ": " + suitableTables.size() + " suitable tables");

            List<List<HostingTable>> combinations = new ArrayList<>();
            findCombinations(suitableTables, 0, new ArrayList<>(), 0, requiredSeats, combinations);

            if (!combinations.isEmpty()) {
                timeSlots.add(sittingType + " - " + slotStart.toLocalTime().toString());
            }
        }

        System.out.println("Final available slots: " + timeSlots);

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
        LocalDateTime slotEnd = slotStart.plusMinutes(90);
        List<LocalDateTime> reserved = table.getReservedTimes();
        if (reserved == null) return true;

        for (LocalDateTime reservedStart : reserved) {
            LocalDateTime reservedEnd = reservedStart.plusMinutes(90);
            if (slotStart.isBefore(reservedEnd) && reservedStart.isBefore(slotEnd)) {
                return false;
            }
        }
        return true;
    }

    private void findCombinations(List<HostingTable> tables, int index, List<HostingTable> currentCombo,
                                  int currentSeats, int requiredSeats, List<List<HostingTable>> validCombos) {
        if (currentSeats >= requiredSeats) {
            validCombos.add(new ArrayList<>(currentCombo));
            return;
        }

        for (int i = index; i < tables.size(); i++) {
            HostingTable table = tables.get(i);
            currentCombo.add(table);
            findCombinations(tables, i + 1, currentCombo, currentSeats + table.getSeatsNumber(), requiredSeats, validCombos);
            currentCombo.remove(currentCombo.size() - 1);
        }
    }
}
