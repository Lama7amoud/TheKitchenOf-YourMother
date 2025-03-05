package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

public class PrimaryController {


    @FXML
    private ComboBox<String> ChooseRestaurantBox;

    @FXML
    private Button PersonalAreaButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Button menuButton;

	@FXML
	private void showMenu(ActionEvent event){
		Platform.runLater(() -> {
            try {
                App.setRoot("menu");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void toPersonalArea(ActionEvent event){
        Platform.runLater(() -> {
            try {
                App.setRoot("personalAreaPage");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void logOut(ActionEvent event){
        Platform.runLater(() -> {
            try {
                Client client = Client.getClient();
                client.sendToServer("log out;" + getClientUsername());
                resetClientAttributes();
                App.setRoot("logIn");
                App.setRoot("logIn");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void initialize(){
        Platform.runLater(() -> {
            ObservableList<String> restaurantList = FXCollections.observableArrayList(
                    "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch"
            );

            ChooseRestaurantBox.setItems(restaurantList);
            ChooseRestaurantBox.setStyle("-fx-text-fill: white;");

            if(userAtt.getPermissionLevel() == 0){
                logOutButton.setVisible(false);
                PersonalAreaButton.setVisible(false);
            }
            else {
                logOutButton.setVisible(true);
                PersonalAreaButton.setVisible(true);
            }
        });

    }
}


