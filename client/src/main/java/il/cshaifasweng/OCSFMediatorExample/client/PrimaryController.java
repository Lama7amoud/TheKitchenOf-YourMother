package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;


import java.io.File;
import java.io.IOException;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

public class PrimaryController {

    int numOfImages = 3;

    @FXML
    private ComboBox<String> ChooseRestaurantBox;

    @FXML
    private Button PersonalAreaButton;

    @FXML
    private Button logOutButton;

    @FXML
    private Button menuButton;

    @FXML
    private void showMenu(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("menu");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void toPersonalArea(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("personalAreaPage");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    private void logOut(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                Client client = Client.getClient();
                String username = getClientUsername();

                if (username != null && !username.equals("Customer")) {
                    client.sendToServer("log out;" + getClientUsername());
                }

                resetClientAttributes();
                App.setRoot("logIn");
                App.setRoot("logIn");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            try {
                images = new Image[numOfImages];
                for (int i = 0; i < numOfImages; i++) {
                    File file = new File("C:\\Users\\User\\IdeaProjects\\JFXApps\\src\\main\\resources\\images\\" + i + ".jpg");
                    images[i] = new Image(file.toURI().toString());
                }
                imageView.setImage(images[j]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ObservableList<String> restaurantList = FXCollections.observableArrayList(
                    "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch"
            );

            ChooseRestaurantBox.setItems(restaurantList);
            ChooseRestaurantBox.setStyle("-fx-text-fill: white;");

            if (userAtt.getPermissionLevel() == 0) {
                logOutButton.setVisible(true);
                logOutButton.setText("Back");
                PersonalAreaButton.setVisible(false);
            } else {
                logOutButton.setVisible(true);
                logOutButton.setText("Log Out");
                PersonalAreaButton.setVisible(true);
            }
        });

    }
}


