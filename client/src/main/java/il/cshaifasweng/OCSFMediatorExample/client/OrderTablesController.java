package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;


import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

public class OrderTablesController {


    @FXML
    private Button ConfirmOrderButton;

    @FXML
    private Button backButton;

    @FXML
    private Button branchDetailsButton;

    @FXML
    private Label branchNameLabel;

    @FXML
    private Button viewMapButton;

    @FXML
    void switchPage(ActionEvent event) {
        Platform.runLater(() -> {
            String page = "";
            Button sourceButton = (Button) event.getSource();

            // Check which button triggered the event
            if (sourceButton == backButton) {
                page = "Main Page";
            } else if (sourceButton == viewMapButton) {
                page = "Tables Page";
            }
            else if(sourceButton == branchDetailsButton) {
                page = "Branch Page";
            }

            //EventBus.getDefault().unregister(this);
            App.switchScreen(page);
        });
    }

    @FXML
    void initialize(){
        Platform.runLater(() -> {
            int employee_permission = userAtt.getPermissionLevel();

            // For customer
            if (employee_permission < 1) {
                viewMapButton.setVisible(false);
            } else {
                viewMapButton.setVisible(true);
            }

            //EventBus.getDefault().register(this);
            Client clientInstance = Client.getClient();
            try {
                //clientInstance.sendToServer("Get tables info for branch;" + userAtt.getRestaurant());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
