package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

public class PrimaryController {

    int numOfImages = 3;
    Image[] images;

    @FXML
    private ComboBox<String> ChooseRestaurantBox;

    @FXML
    private Text ComboChoiceRequestLabel;

    @FXML
    private Button PersonalAreaButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button logOutButton;

    @FXML
    private Button menuButton;

    @FXML
    void switchPage(ActionEvent event) {
        Platform.runLater(() -> {
            String page = "";

            // Check which button triggered the event
            if (event.getSource() == menuButton) {
                page = "Menu Page";
            } else if (event.getSource() == PersonalAreaButton) {
                page = "Personal Area Page";
            }

            App.switchScreen(page);
        });
    }

    @FXML
    private void logOut(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                Client client = Client.getClient();
                String username = getClientUsername();

                if (username != null && !username.equals("Costumer")) {
                    client.sendToServer("log out;" + getClientUsername());
                }

                resetClientAttributes();
                App.switchScreen("Log In Page");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void comboBoxChoice(){
        Platform.runLater(() -> {
        String selectedRestaurant = ChooseRestaurantBox.getValue();

        if (selectedRestaurant != null) {
            ComboChoiceRequestLabel.setVisible(false);
            switch (selectedRestaurant) {
                case "Haifa Branch":
                    imageView.setImage(images[0]);
                    break;
                case "Tel-Aviv Branch":
                    imageView.setImage(images[1]);
                    break;
                case "Nahariya Branch":
                    imageView.setImage(images[2]);
                    break;
            }
        }
        });
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            try {
                images = new Image[numOfImages];

                for (int i = 0; i < numOfImages; i++) {
                    images[i] = new Image(String.valueOf(PrimaryController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Images/" + i + ".jpg")));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ObservableList<String> restaurantList = FXCollections.observableArrayList(
                    "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch"
            );

            ChooseRestaurantBox.setItems(restaurantList);

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


