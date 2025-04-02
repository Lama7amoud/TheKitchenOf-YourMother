package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class FeedbackController {


    @FXML
    void back_func(ActionEvent event) {
        String page = "Main Page";
        App.switchScreen(page);
    }
}
