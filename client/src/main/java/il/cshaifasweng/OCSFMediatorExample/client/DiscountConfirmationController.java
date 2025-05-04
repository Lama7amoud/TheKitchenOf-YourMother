package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Discounts;
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

public class DiscountConfirmationController {

    @FXML
    private TableView<Discounts> discountTable;

    @FXML
    private TableColumn<Discounts, Integer> idColumn;

    @FXML
    private TableColumn<Discounts, String> categoryColumn;

    @FXML
    private TableColumn<Discounts, Double> percentageColumn;

    @FXML
    private TableColumn<Discounts, Void> actionColumn;

    @FXML
    private Button backToMainButton;

    @FXML
    private Button backButton;

    private ObservableList<Discounts> discountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws IOException {
        EventBus.getDefault().register(this);
        discountTable.setItems(discountList);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setVisible(false);

        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String category, boolean empty) {
                super.updateItem(category, empty);
                if (empty || category == null) {
                    setText(null);
                } else {
                    switch (category) {
                        case "special1" -> setText("Specials of Haifa");
                        case "special2" -> setText("Specials of Tel-Aviv");
                        case "special3" -> setText("Specials of Nahariya");
                        default -> setText(category);
                    }
                }
            }
        });

        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));

        addActionButtonsToTable();

        Client.getClient().sendToServer("Get Discount Confirmations");
    }

    private void addActionButtonsToTable() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Discounts, Void> call(final TableColumn<Discounts, Void> param) {
                return new TableCell<>() {
                    private final Button confirmButton = new Button("✔"); // or use "V"
                    private final Button rejectButton = new Button("✖");  // or use "X"

                    {
                        confirmButton.setOnAction((ActionEvent event) -> {
                            Discounts discount = getTableView().getItems().get(getIndex());
                            String message = String.format("Confirm Discount \"%s\" \"%s\" \"%s\"",
                                    discount.getDiscount(), discount.getId(), discount.getCategory());
                            Client.getClient().sendToServer(message);
                            discountList.remove(discount);
                        });

                        rejectButton.setOnAction((ActionEvent event) -> {
                            Discounts discount = getTableView().getItems().get(getIndex());
                            String message = String.format("Reject Discount \"%s\" \"%s\"",
                                    discount.getDiscount(), discount.getId());
                            Client.getClient().sendToServer(message);
                            discountList.remove(discount);
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
            try {
                if (msg instanceof List) {
                    List<?> list = (List<?>) msg;
                    if (!list.isEmpty() && list.get(0) instanceof Discounts) {
                        List<Discounts> discounts = (List<Discounts>) list;
                        discountList.setAll(discounts);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
