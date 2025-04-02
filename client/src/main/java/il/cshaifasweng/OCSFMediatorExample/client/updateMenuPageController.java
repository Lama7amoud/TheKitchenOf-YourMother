package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Button;


public class updateMenuPageController {

    @FXML
    private Label helloTitleLabel;

    @FXML
    private Label managerTypeLabel;

    @FXML
    private Label pageTitleLabel;

    @FXML
    private Button addMealButton;

    @FXML
    private Button removeMealButton;





    String[] restaurantTitles = {"Haifa", "Tel-Aviv", "Nahariya"};

    @FXML
    void initialize(){
        Platform.runLater(() -> {
            AuthorizedUser user = Client.getClientAttributes();
            helloTitleLabel.setText("Hello " + user.getFirstname());
            managerTypeLabel.setText("Dietitian");
            pageTitleLabel.setText("Dietitian Page");

        });
    }
    @FXML
    void backFunc(ActionEvent event) {
        String page = "Personal Area Page";
        App.switchScreen(page);

    }
    @FXML
    void get_haifa_menu_func(ActionEvent event) {
        String page = "Haifa Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);

    }

    @FXML
    void get_telaviv_menu_func(ActionEvent event) {
        String page = "Tel-Aviv Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);

    }


    @FXML
    void get_nahariya_menu_func(ActionEvent event) {
        String page = "Nahariya Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);
    }

    @FXML
    void add_meal_func(ActionEvent event) {
        //String page = "Add Meal Page";
        String page = "Personal Area Page";
        App.switchScreen(page);
    }

    @FXML
    void remove_meal_func(ActionEvent event) {
        //String page = "Remove Meal Page";
        String page = "Personal Area Page";
        App.switchScreen(page);
    }



}
