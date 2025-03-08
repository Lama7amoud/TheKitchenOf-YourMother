package il.cshaifasweng.OCSFMediatorExample.client;


import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ManagerController {

    @FXML
    private Label helloTitleLabel;

    @FXML
    private Label managerTypeLabel;

    @FXML
    private Label pageTitleLabel;


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
                    managerTypeLabel.setText(restaurantTitles[user.getRestaurantId()-1] + " Branch Manger");
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
}
