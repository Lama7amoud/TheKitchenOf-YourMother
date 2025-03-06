package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class PersonalAreaPageController {

    @FXML
    private Label ageLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label firstnameLabel;

    @FXML
    private Label genderLabel;

    @FXML
    private Label jobPositionLabel;

    @FXML
    private Label lastnameLabel;

    @FXML
    private Button managerPageButton;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label restaurantIDLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    void switchPage(ActionEvent event){
        String page;
        managerPageButton.setOnAction(event -> (page = "Manager Page"));

        App.switchScreen("Manager Page");
    }

    @FXML
    void backToMainPage(ActionEvent event){
        try {
            App.setRoot("Primary");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void initialize(){
        AuthorizedUser user = Client.getClientAttributes();
        if(user != null && user.getPermissionLevel() < 2){
            managerPageButton.setVisible(false);
        }
        else {
            managerPageButton.setVisible(true);
        }

        welcomeLabel.setText("Welcome " + user.getFirstname());
        usernameLabel.setText("Username: " + user.getUsername());
        passwordLabel.setText("Password: " + user.getPassword());
        firstnameLabel.setText("Firstname: " + user.getFirstname());
        lastnameLabel.setText("Lastname: " + user.getLastname());
        genderLabel.setText("Gender: " + user.getGender());
        ageLabel.setText("Age: " + user.getAge());
        restaurantIDLabel.setText("Restaurant ID: " + user.getRestaurantId());
        jobPositionLabel.setText("Job Position: " + switch (user.getPermissionLevel()) {
            case 1 -> "Employee";
            case 2 -> "Branch Manager";
            case 3 -> "Restaurant Manager";
            default -> "Unknown";
        });
    }
}
