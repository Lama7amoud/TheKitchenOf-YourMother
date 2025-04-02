package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Button;


public class ManagerController {

    @FXML
    private Label helloTitleLabel;

    @FXML
    private Label managerTypeLabel;

    @FXML
    private Label pageTitleLabel;

    @FXML
    private Button managerConfirmButton;

    @FXML
    private Button managerFeedbackButton;

    @FXML
    private Button makeOrderButton;

    @FXML
    private Button organizationButton;




    String[] restaurantTitles = {"Haifa", "Tel-Aviv", "Nahariya"};

    @FXML
    void initialize(){
        Platform.runLater(() -> {
            AuthorizedUser user = Client.getClientAttributes();
            helloTitleLabel.setText("Hello " + user.getFirstname());
            switch (user.getPermissionLevel()){
                case(2):
                    managerTypeLabel.setText("Customer Service");
                    pageTitleLabel.setText("Service Page");
                    break;

                case(3):
                    managerTypeLabel.setText(restaurantTitles[user.getRestaurantInterest()-1] + " Branch Manger");
                    pageTitleLabel.setText("Manager Page");
                    break;

                case(4):
                    managerTypeLabel.setText("Restaurant Manager");
                    pageTitleLabel.setText("Manager Page");
                    break;

                case(5):
                    managerTypeLabel.setText("Dietitian");
                    pageTitleLabel.setText("Dietitian Page");
            }
        });
    }
    @FXML
    void backFunc(ActionEvent event) {
        String page = "Personal Area Page";
        App.switchScreen(page);

    }
    @FXML
    void manager_confirm_price_func(ActionEvent event) {
        //String page = "Confirmation Price Page";
        String page = "Personal Area Page";
        App.switchScreen(page);

    }

    @FXML
    void manager_feedback_func(ActionEvent event) {
        //String page = "Manager Feedback Page";
        String page = "Personal Area Page";
        App.switchScreen(page);

    }


    @FXML
    void make_order_func(ActionEvent event) {
        //String page = "Make Order Page";
        String page = "Personal Area Page";
        App.switchScreen(page);

    }


    @FXML
    void organization_func(ActionEvent event) {
        //String page = "Organization Page";
        String page = "Personal Area Page";
        App.switchScreen(page);

    }



}
