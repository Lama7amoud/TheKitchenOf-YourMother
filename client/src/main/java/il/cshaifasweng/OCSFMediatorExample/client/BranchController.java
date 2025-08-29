package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

public class BranchController {

    int numOfImages = 2; // For restaurant manager
    int j; // The current image
    Image[] images;


    @FXML
    private Button NextImageButton;

    @FXML
    private Button PrevImageButton;

    @FXML
    private Label activityHoursLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Button backButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Label locationLabel;

    @FXML
    private Label phoneNumberLabel;


    @FXML
    void switchPage(ActionEvent event) {
        Platform.runLater(() -> {
            String page = "";
            Button sourceButton = (Button) event.getSource();

            // Check which button triggered the event
            if (sourceButton == backButton) {
                page = "Main Page";
            }

            EventBus.getDefault().unregister(this);
            App.switchScreen(page);
        });
    }

    @FXML
    void ToRightImage(){
        Platform.runLater(() -> {
            j++;
            imageView.setImage(images[j]);

            if (j == numOfImages - 1) {
                NextImageButton.setDisable(true);
            }

            if (j > 0) {
                PrevImageButton.setDisable(false);
            }
        });
    }

    @FXML
    void ToLeftImage(){
        Platform.runLater(() -> {
            j--;
            imageView.setImage(images[j]);

            if (j == 0) {
                PrevImageButton.setDisable(true);
            }

            if (j < numOfImages - 1) {
                NextImageButton.setDisable(false);
            }
        });
    }

    @Subscribe
    public void restaurantDetailsReceiver(Restaurant restaurant){
        Platform.runLater(() -> {
            addressLabel.setText("Address: " + restaurant.getAddress());
            phoneNumberLabel.setText("Phone Number: " + restaurant.getPhoneNumber());
            locationLabel.setText("location: " + restaurant.getLocation());
            activityHoursLabel.setText("All Days Except: " + restaurant.getHolidays() + " From " + (int)restaurant.getOpeningTime() + ":00\nUntil " + (int)restaurant.getClosingTime() + ":00"); // In the next line in GUI (long string)
        });
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            EventBus.getDefault().register(this);
            User user = Client.getClientAttributes();
            String locationPrefix = "";

            switch (user.getRestaurantInterest()) {
                case 1:
                    locationPrefix = "Haifa";
                    break;
                case 2:
                    locationPrefix = "Tel-Aviv";
                    break;
                case 3:
                    locationPrefix = "Nahariya";
                    break;
            }
            System.out.println(user.getRestaurantInterest());
            j = 0;
            try {
                images = new Image[numOfImages];

                for (int i = 0; i < numOfImages; i++) {
                    images[i] = new Image(String.valueOf(BranchController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/" + locationPrefix + "_Branch_Images/" + i + ".jpg")));
                }
                imageView.setImage(images[j]);
                PrevImageButton.setDisable(true);

                Client clientInstance = Client.getClient();
                clientInstance.sendToServer("Get branch details;" + userAtt.getRestaurantInterest());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
