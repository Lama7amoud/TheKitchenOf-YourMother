package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.PriceConfirmation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class PriceConfirmationController {

    @FXML
    private TableView<PriceConfirmation> priceTable;

    @FXML
    private TableColumn<PriceConfirmation, Integer> idColumn;

    @FXML
    private TableColumn<PriceConfirmation, String> mealNameColumn;

    @FXML
    private TableColumn<PriceConfirmation, Double> oldPriceColumn;

    @FXML
    private TableColumn<PriceConfirmation, Double> newPriceColumn;

    @FXML
    private TableColumn<PriceConfirmation, Void> actionColumn;

    @FXML
    private Button backToMainButton;

    @FXML
    private Button backButton;

    private ObservableList<PriceConfirmation> priceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws IOException {
        EventBus.getDefault().register(this);
        priceTable.setItems(priceList);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setVisible(false); // hide

        mealNameColumn.setCellValueFactory(new PropertyValueFactory<>("mealName"));
        oldPriceColumn.setCellValueFactory(new PropertyValueFactory<>("oldPrice"));
        newPriceColumn.setCellValueFactory(new PropertyValueFactory<>("newPrice"));

        addActionButtonsToTable();

        Client.getClient().sendToServer("Get Price Confirmations");

    }

    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PriceConfirmation, Void> call(final TableColumn<PriceConfirmation, Void> param) {
                return new TableCell<>() {
                    private final Button confirmButton = new Button("✔");
                    private final Button rejectButton = new Button("✖");

                    {
                        confirmButton.setOnAction((ActionEvent event) -> {
                            PriceConfirmation item = getTableView().getItems().get(getIndex());
                            String msg = String.format("Confirm Price \"%s\" \"%s\"\"%s\"",
                                    item.getMealName(), item.getNewPrice(), item.getId());
                            try {
                                Client.getClient().sendToServer(msg);
                                priceList.remove(item);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        rejectButton.setOnAction((ActionEvent event) -> {
                            PriceConfirmation item = getTableView().getItems().get(getIndex());
                            String msg = String.format("Reject Price \"%s\" \"%s\"\"%s\"",
                                    item.getMealName(), item.getNewPrice(), item.getId());
                            try {
                                Client.getClient().sendToServer(msg);
                                priceList.remove(item);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        confirmButton.setStyle("-fx-background-color: lightgreen; -fx-text-fill: black;");
                        rejectButton.setStyle("-fx-background-color: lightcoral; -fx-text-fill: black;");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(10, confirmButton, rejectButton);
                            setGraphic(box);
                        }
                    }
                };
            }
        });
    }

    @Subscribe
    public void ExternalIntervention(Object msg) {
        Platform.runLater(() -> {

            if (msg instanceof List<?>) {
                List<?> list = (List<?>) msg;
                if (!list.isEmpty() && list.get(0) instanceof PriceConfirmation) {
                    List<PriceConfirmation> confirmations = (List<PriceConfirmation>) list;
                    priceList.setAll(confirmations);
                }
            }

        });
    }

    @FXML
    void back_func(ActionEvent event) {
        App.switchScreen("Management Page");
    }

    @FXML
    void back_to_main_func(ActionEvent event) {
        App.switchScreen("Main Page");
    }
}
