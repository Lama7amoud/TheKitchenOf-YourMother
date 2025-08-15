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

    @FXML
    private TableColumn<Complaint, String> statusColumn;
    @FXML private TableColumn<Complaint, String> responseColumn;
    @FXML private TableColumn<Complaint, Double> refundColumn;
    @FXML private TableColumn<Complaint, Void>   actionColumn;

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

        responseColumn.setCellValueFactory(new PropertyValueFactory<>("response"));
        responseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        responseColumn.setOnEditCommit(ev -> {
            Complaint row = ev.getRowValue();
            row.setResponse(ev.getNewValue());
        });

        refundColumn.setCellValueFactory(new PropertyValueFactory<>("refund"));
        refundColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        refundColumn.setOnEditCommit(ev -> {
            Complaint row = ev.getRowValue();
            row.setRefund(ev.getNewValue());
        });

        actionColumn.setCellFactory(col -> new TableCell<Complaint, Void>() {
            private final Button btn = new Button("Send");
            {
                btn.setOnAction(e -> {
                    Complaint c = getTableView().getItems().get(getIndex());

                    Double refund = (c.getRefund() != 0) ? c.getRefund() : 0.0;
                    String resp = (c.getResponse() != null) ? c.getResponse().trim() : "";

                    // Format: UpdateComplaint;<id>;<response>;<refund>
                    Client.getClient().sendToServer(
                            String.format("UpdateComplaint;%d;%s;%.2f",
                                    c.getId(), resp, refund)
                    );

                    Alert ok = new Alert(Alert.AlertType.INFORMATION,
                            "Complaint #" + c.getId() + " updated in the database.");
                    ok.showAndWait();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
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
        App.switchScreen("Main Page");
    }

}
