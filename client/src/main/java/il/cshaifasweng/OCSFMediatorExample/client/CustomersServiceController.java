package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.Discounts;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class CustomersServiceController {

    @FXML
    private TableView<Complaint> complaintTable;

    @FXML
    private TableColumn<Complaint, String> complaintColumn;

    @FXML
    private TableColumn<Complaint, String> nameColumn;

    @FXML
    private TableColumn<Complaint, String> restaurantColumn;

    @FXML
    private TableColumn<Complaint, String> submittedAtColumn;

    @FXML
    private TableColumn<Complaint, String> emailColumn;

    @FXML private TableColumn<Complaint, String> statusColumn;

    @FXML private TableColumn<Complaint, Void>   actionColumn;

    @FXML private TableColumn<Complaint, String> responseColumn;
    @FXML private TableColumn<Complaint, String> refundColumn; // <- change from Double to String in FXML & controller



    private final java.util.Map<Integer, String> responseDrafts = new java.util.HashMap<>();
    private final java.util.Map<Integer, String> refundDrafts   = new java.util.HashMap<>();


    @FXML
    private Button backButton;

    private ObservableList<Complaint> complaintlist = FXCollections.observableArrayList();



    @FXML
    public void initialize() throws IOException {
        EventBus.getDefault().register(this);
        complaintTable.setItems(complaintlist);

        complaintTable.setEditable(true); // allow per-cell editing

        complaintColumn.setCellValueFactory(new PropertyValueFactory<>("complaint"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Show readable restaurant name, not the Restaurant object
        restaurantColumn.setCellValueFactory(cd -> {
            var r = cd.getValue().getRestaurant();
            String name = (r != null && r.getName() != null && !r.getName().isBlank())
                    ? r.getName()
                    : (r != null && r.getLocation() != null ? r.getLocation() : "");
            return new ReadOnlyStringWrapper(name);
        });

        submittedAtColumn.setCellValueFactory(cellData -> {
            LocalDateTime time = cellData.getValue().getSubmittedAt();
            String formatted = (time != null)
                    ? time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    : "";
            return new ReadOnlyStringWrapper(formatted);
        });

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // RESPONSE column: show TextField always, write to responseDrafts only
        responseColumn.setCellValueFactory(cd -> {
            String v = cd.getValue().getResponse();
            return new ReadOnlyStringWrapper(v == null ? "" : v);
        });
        responseColumn.setCellFactory(col -> new TableCell<Complaint, String>() {
            private final TextField tf = new TextField();
            {
                tf.textProperty().addListener((obs, oldV, newV) -> {
                    Complaint row = getTableView().getItems().get(getIndex());
                    if (row != null) responseDrafts.put(row.getId(), newV);
                });
                setGraphic(tf);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Complaint row = getTableView().getItems().get(getIndex());
                    String draft = responseDrafts.get(row.getId());
                    tf.setText(draft != null ? draft : (row.getResponse() == null ? "" : row.getResponse()));
                    setGraphic(tf);
                }
            }
        });

        refundColumn.setCellValueFactory(cd -> {
            Double v = cd.getValue().getRefund();
            return new ReadOnlyStringWrapper(v == null ? "" : String.valueOf(v));
        });
        refundColumn.setCellFactory(col -> new TableCell<Complaint, String>() {
            private final TextField tf = new TextField();
            {
                tf.textProperty().addListener((obs, oldV, newV) -> {
                    Complaint row = getTableView().getItems().get(getIndex());
                    if (row != null) refundDrafts.put(row.getId(), newV);
                });
                setGraphic(tf);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Complaint row = getTableView().getItems().get(getIndex());
                    String draft = refundDrafts.get(row.getId());
                    String base = String.valueOf(row.getRefund());
                    tf.setText(draft != null ? draft : base);
                    setGraphic(tf);
                }
            }
        });


        actionColumn.setCellFactory(col -> new TableCell<Complaint, Void>() {
            private final Button btn = new Button("Send");
            {
                btn.setOnAction(e -> {
                    Complaint c = getTableView().getItems().get(getIndex());

                    // Use drafts if present; else fall back to model values
                    String respRaw = responseDrafts.getOrDefault(c.getId(),
                            c.getResponse() == null ? "" : c.getResponse());
                    String refundRaw = refundDrafts.getOrDefault(c.getId(), String.valueOf(c.getRefund()));


                    if (respRaw.isEmpty()) {
                        new Alert(Alert.AlertType.ERROR, "You have to write response.").showAndWait();
                        return;
                    }

                    final double refundVal;
                    try {
                        refundVal = refundRaw.isBlank() ? 0.0 : Double.parseDouble(refundRaw.trim());
                    } catch (NumberFormatException ex) {
                        new Alert(Alert.AlertType.ERROR, "Refund must be a valid number.").showAndWait();
                        return;
                    }
                    if (refundVal < 0) {
                        new Alert(Alert.AlertType.ERROR, "Refund must be zero or positive.").showAndWait();
                        return;
                    }

                    // Send to server (don’t mutate the row yet; wait for server push/update)
                    Client.getClient().sendToServer(
                            String.format("UpdateComplaint;%d;%s;%.2f", c.getId(), respRaw.trim(), refundVal)
                    );

                    // Optionally clear drafts
                    responseDrafts.remove(c.getId());
                    refundDrafts.remove(c.getId());
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setText(null);
            }
        });


        Client.getClient().sendToServer("Get complaints");
    }


    @Subscribe
    public void handleComplaintList(Object msg) {
        Platform.runLater(() -> {
            try {
                if (msg instanceof List) {
                    List<?> list = (List<?>) msg;
                    if (!list.isEmpty() && list.get(0) instanceof Complaint) {
                        List<Complaint> complaints = (List<Complaint>) list;

                        // Filter: keep only complaints with status == false
                        List<Complaint> filtered = complaints.stream()
                                .filter(c -> !c.getStatus())
                                .toList();

                        complaintlist.setAll(filtered);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    @FXML
    void goBack(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        App.switchScreen("Main Page");
    }

}
