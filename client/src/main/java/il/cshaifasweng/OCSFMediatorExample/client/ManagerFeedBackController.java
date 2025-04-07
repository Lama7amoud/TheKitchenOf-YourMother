package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Feedback;
import il.cshaifasweng.OCSFMediatorExample.entities.PriceConfirmation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;

public class ManagerFeedBackController {
    @FXML
    private TextField feedback0;

    @FXML
    private TextField feedback1;

    @FXML
    private TextField feedback2;
    @FXML
    private TextField feedback3;
    @FXML
    private TextField feedback4;
    @FXML
    private TextField feedback5;
    @FXML
    private TextField feedback6;

    Client client = Client.getClient();

    @FXML
    void initialize() throws IOException {

        EventBus.getDefault().register(this);
        try {
            Client.getClient().sendToServer("Get Manager feedback");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Feedback> feedbacks;
    @Subscribe
    public void ExternalIntervention(Object msg) {
        Platform.runLater(() -> {
            try {
                if (msg instanceof List) {
                    feedbacks = (List<Feedback>) msg;
                    fillTextFields(feedbacks);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void fillTextFields(List<Feedback> confirmations) {
        int size = confirmations.size();

        if (size >= 1) {
            feedback0.setText(confirmations.get(0).getMessage());
        }

        if (size >= 2) {
            feedback1.setText(confirmations.get(1).getMessage());
        }

        if(size >= 3) {
            feedback2.setText(confirmations.get(2).getMessage());

        }
        if(size >= 4) {
            feedback3.setText(confirmations.get(3).getMessage());

        }
        if(size >= 5) {
            feedback4.setText(confirmations.get(4).getMessage());

        }
        if(size >= 6) {
            feedback5.setText(confirmations.get(5).getMessage());

        }


        reorder();
    }

    public void reorder()
    {
        if (!feedback0.getText().trim().isEmpty()) {
            feedback0.setVisible(true);
        }
        else{
            feedback0.setVisible(false);

        }
        if (!feedback1.getText().trim().isEmpty()) {
           feedback1.setVisible(true);
        }
        else{
            feedback1.setVisible(false);
        }
        if (!feedback2.getText().trim().isEmpty()) {
            feedback2.setVisible(true);
        }
        else {
            feedback2.setVisible(false);
        }
        if (!feedback3.getText().trim().isEmpty()) {
            feedback3.setVisible(true);
        }
        else {
            feedback3.setVisible(false);
        }
        if (!feedback4.getText().trim().isEmpty()) {
            feedback4.setVisible(true);
        }
        else {
            feedback4.setVisible(false);
        }
        if (!feedback5.getText().trim().isEmpty()) {
            feedback5.setVisible(true);
        }
        else {
            feedback5.setVisible(false);
        }
    }

    @FXML
    void back_func(ActionEvent event) {
        String page = "Management Page";
        App.switchScreen(page);
    }

}
