package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class PersonalAreaPageController {

    int numOfImages = 3; // For restaurant manager
    int j; // The current image
    Image[] images;
    String[] restaurantTitles = {"Haifa", "Tel-Aviv", "Nahariya"}; // Array of restaurant titles corresponding to the restaurant IDs


    @FXML
    private Label ageLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label firstnameLabel;

    @FXML
    private Label IDNumLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private Label jobPositionLabel;

    @FXML
    private Label lastnameLabel;

    @FXML
    private Button leftImageButton;

    @FXML
    private Button advancedPageButton;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label restaurantIDLabel;

    @FXML
    private Label restaurantMapTitle;

    @FXML
    private Button rightImageButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    void ToRightImage(){
        j++;
        restaurantMapTitle.setText(restaurantTitles[j] + " restaurant Map:");
        imageView.setImage(images[j]);

        if(j == numOfImages - 1){
            rightImageButton.setDisable(true);
        }

        if(j > 0){
            leftImageButton.setDisable(false);
        }
    }

    @FXML
    void ToLeftImage(){
        j--;
        restaurantMapTitle.setText(restaurantTitles[j] + " restaurant Map:");
        imageView.setImage(images[j]);

        if(j == 0){
            leftImageButton.setDisable(true);
        }

        if(j < numOfImages - 1){
            rightImageButton.setDisable(false);
        }
    }

    @FXML
    void switchPage(ActionEvent event) {
        Platform.runLater(() -> {
            String page = "";

            // Check which button triggered the event
            if (event.getSource() == advancedPageButton) {
                page = "Management Page";
            } else if (event.getSource() == backButton) {
                page = "Main Page";
            }

            App.switchScreen(page);
        });
    }

    @FXML
    void initialize(){
        Platform.runLater(() -> {
            AuthorizedUser user = Client.getClientAttributes();
            j = 0;
            try {
                // if not the restaurant manager
                if(user.getPermissionLevel() < 4){
                    rightImageButton.setVisible(false);
                    leftImageButton.setVisible(false);
                    numOfImages = 1;
                    images = new Image[numOfImages];
                    j = (user.getRestaurantId()-1);
                    images[0] = new Image(String.valueOf(PrimaryController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Images/" + j + ".jpg")));
                    restaurantMapTitle.setText(restaurantTitles[j] + " restaurant Map:");
                    imageView.setImage(images[0]);
                }
                else {
                    rightImageButton.setVisible(true);
                    leftImageButton.setVisible(true);
                    leftImageButton.setDisable(true);

                    images = new Image[numOfImages];

                    for (int i = 0; i < numOfImages; i++) {
                        images[i] = new Image(String.valueOf(PrimaryController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Images/" + i + ".jpg")));
                    }
                    restaurantMapTitle.setText(restaurantTitles[j] + " restaurant Map:");
                    imageView.setImage(images[j]);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if(user != null && user.getPermissionLevel() == 1 ){
                advancedPageButton.setVisible(false);
            }
            else {
                advancedPageButton.setVisible(true);
            }

            welcomeLabel.setText("Hello " + user.getFirstname());
            usernameLabel.setText("Username: " + user.getUsername());
            passwordLabel.setText("Password: " + user.getPassword());
            firstnameLabel.setText("Firstname: " + user.getFirstname());
            lastnameLabel.setText("Lastname: " + user.getLastname());
            IDNumLabel.setText("IDNum: " + user.getIDNum());
            ageLabel.setText("Age: " + user.getAge());
            restaurantIDLabel.setText("Restaurant ID: " + user.getRestaurantId());
            short userPermissionLevel = user.getPermissionLevel();
            jobPositionLabel.setText("Job Position: " + switch (userPermissionLevel) {
                case 1 -> "Employee";
                case 2 -> "Service";
                case 3 -> "Branch Manager";
                case 4 -> "Restaurant Manager";
                case 5 -> "Dietitian";
                default -> "Unknown";
            });
            if(userPermissionLevel == 2){
                advancedPageButton.setText("Customer Service Page");
            }
            else if(userPermissionLevel == 3 || userPermissionLevel == 4){
                advancedPageButton.setText("Manager Page");
            }
            else if(userPermissionLevel == 5){
                advancedPageButton.setText("Dietitian Page");
            }
        });
    }
}
