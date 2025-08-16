package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Feedback;
import il.cshaifasweng.OCSFMediatorExample.entities.PriceConfirmation;
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
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.userAtt;

public class ManagerFeedBackController {

    @FXML
    private TableView<Feedback> feedbackTable;

    @FXML
    private TableColumn<Feedback, String> messageColumn;

    @FXML
    private TableColumn<Feedback, String> idColumn;
    @FXML
    private TableColumn<Feedback, String> nameColumn;

    @FXML
    private TableColumn<Feedback, Integer> ratingColumn;

    @FXML
    private TableColumn<Feedback, String> restaurantColumn;

    @FXML
    private TableColumn<Feedback, String> submittedAtColumn;

    @FXML
    private Button backButton;

    private ObservableList<Feedback> feedbackList = FXCollections.observableArrayList();

    @FXML
    public void initialize() throws IOException {

        EventBus.getDefault().register(this);
        feedbackTable.setItems(feedbackList);

        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("user id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        restaurantColumn.setCellValueFactory(new PropertyValueFactory<>("restaurant"));

        submittedAtColumn.setCellValueFactory(cellData -> {
            LocalDateTime time = cellData.getValue().getSubmittedAt();
            String formatted = (time != null) ? time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
            return new ReadOnlyStringWrapper(formatted);
        });

        Client.getClient().sendToServer("Get Manager feedback");
    }

    @Subscribe
    public void handleFeedbackList(Object msg) {
        if (msg instanceof List<?>) {
            List<?> list = (List<?>) msg;
            if (!list.isEmpty() && list.get(0) instanceof Feedback) {
                List<Feedback> allFeedbacks = (List<Feedback>) list;

                List<Feedback> filtered;

                if (userAtt.getPermissionLevel()==4)
                {
                    filtered = allFeedbacks;
                }
                else {
                    int restaurantId = userAtt.getRestaurantId();
                    System.out.println(restaurantId);
                    if (restaurantId == 1) {
                        filtered = allFeedbacks.stream()
                                .filter(f -> f.getRestaurant().getName().equalsIgnoreCase("Haifa"))
                                .collect(Collectors.toList());
                    } else if (restaurantId == 2) {
                        filtered = allFeedbacks.stream()
                                .filter(f -> f.getRestaurant().getName().equalsIgnoreCase("Tel-Aviv"))
                                .collect(Collectors.toList());
                    } else if (restaurantId == 3) {
                        filtered = allFeedbacks.stream()
                                .filter(f -> f.getRestaurant().getName().equalsIgnoreCase("Nahariya"))
                                .collect(Collectors.toList());
                    } else {
                        filtered = allFeedbacks;
                    }
                }

                Platform.runLater(() -> feedbackList.setAll(filtered));
            }
        }
    }


    @FXML
    void goBack(ActionEvent event) {
        App.switchScreen("Management Page");
    }

}
