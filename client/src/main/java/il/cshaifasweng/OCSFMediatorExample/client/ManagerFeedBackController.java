package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.BranchManager;
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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        restaurantColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRestaurant() != null) {
                return new ReadOnlyStringWrapper(cellData.getValue().getRestaurant().getName());
            } else {
                return new ReadOnlyStringWrapper("");
            }
        });


        submittedAtColumn.setCellValueFactory(cellData -> {
            LocalDateTime time = cellData.getValue().getSubmittedAt();
            String formatted = (time != null) ? time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
            return new ReadOnlyStringWrapper(formatted);
        });

        Client.getClient().sendToServer(BranchManager.requestFeedbackMsg());
    }

    @Subscribe
    public void handleFeedbackList(Object msg) {
        if (!(msg instanceof List<?>)) return;

        List<?> list = (List<?>) msg;
        if (list.isEmpty() || !(list.get(0) instanceof Feedback)) return;

        List<Feedback> allFeedbacks = (List<Feedback>) list;

        // === filter ===
        List<Feedback> filtered;
        if (userAtt != null && userAtt.getPermissionLevel() == 4) {
            // Admin (permission 4) sees everything
            filtered = allFeedbacks;
        } else {
            // Non-admins see only feedbacks from their restaurant
            int userRestaurantId = (userAtt != null) ? userAtt.getRestaurantId() : -1;

            filtered = allFeedbacks.stream()
                    .filter(f -> f != null && f.getRestaurant() != null)
                    // Prefer matching by restaurant ID (safer than names)
                    .filter(f -> {
                        try {
                            // If your Restaurant entity has getId():
                            return f.getRestaurant().getId() == userRestaurantId;
                        } catch (NoSuchMethodError | NullPointerException e) {
                            // Fallback: if you *don’t* have getId(), map names by ID:
                            String expectedName =
                                    switch (userRestaurantId) {
                                        case 1 -> "Haifa";
                                        case 2 -> "Tel-Aviv";
                                        case 3 -> "Nahariya";
                                        default -> null;
                                    };
                            return expectedName != null &&
                                    expectedName.equalsIgnoreCase(f.getRestaurant().getName());
                        }
                    })
                    .toList();
        }

        Platform.runLater(() -> {
            feedbackList.setAll(filtered);
        });
    }



    @FXML
    void goBack(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        App.switchScreen("Management Page");
    }

}
