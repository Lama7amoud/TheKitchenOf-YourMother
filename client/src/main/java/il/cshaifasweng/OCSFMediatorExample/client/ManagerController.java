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
    private Button managerDiscountButton;




    String[] restaurantTitles = {"Haifa", "Tel-Aviv", "Nahariya"};

    @FXML
    void initialize(){
        Platform.runLater(() -> {
            AuthorizedUser user = Client.getClientAttributes();
            helloTitleLabel.setText("Hello " + user.getFirstname());
            switch (user.getPermissionLevel()){

                case(3):
                    managerTypeLabel.setText(restaurantTitles[user.getRestaurantId()-1] + " Branch Manger");
                    pageTitleLabel.setText("Manager Page");
                    break;

                case(4):
                    managerTypeLabel.setText("Restaurant Manager");
                    pageTitleLabel.setText("Manager Page");
                    managerConfirmButton.setVisible(true);
                    managerDiscountButton.setVisible(true);
                    break;

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
        String page = "Price Confirmation Page";
        App.switchScreen(page);
    }
    @FXML
    void manager_feedback_func(ActionEvent event) {
        //String page = "Manager Feedback Page";
        String page = "Personal Area Page";
        App.switchScreen(page);
    }

    @FXML
    void manager_confirm_discount_func(ActionEvent event) {
        String page = "Discount Confirmation Page";
        App.switchScreen(page);

    }

}
