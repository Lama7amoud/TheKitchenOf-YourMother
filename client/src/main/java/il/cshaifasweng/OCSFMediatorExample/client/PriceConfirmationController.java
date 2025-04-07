package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import il.cshaifasweng.OCSFMediatorExample.entities.PriceConfirmation;
import java.io.IOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;


public class PriceConfirmationController {

    @FXML
    private Button backButton;

    @FXML
    private Button backToMainButton;

    @FXML
    private TextField mealName1;
    @FXML
    private TextField oldPrice1;
    @FXML
    private TextField newPrice1;

    @FXML
    private TextField mealName2;
    @FXML
    private TextField oldPrice2;
    @FXML
    private TextField newPrice2;

    @FXML
    private TextField mealName3;
    @FXML
    private TextField oldPrice3;
    @FXML
    private TextField newPrice3;

    @FXML
    private TextField mealName4;
    @FXML
    private TextField oldPrice4;
    @FXML
    private TextField newPrice4;

    @FXML
    private TextField mealName5;
    @FXML
    private TextField oldPrice5;
    @FXML
    private TextField newPrice5;

    @FXML
    private TextField mealName6;
    @FXML
    private TextField oldPrice6;
    @FXML
    private TextField newPrice6;

    @FXML
    private Button confirm1Button;

    @FXML
    private Button confirm2Button;

    @FXML
    private Button confirm3Button;

    @FXML
    private Button confirm4Button;

    @FXML
    private Button confirm5Button;

    @FXML
    private Button confirm6Button;
    @FXML
    private Button reject1Button;

    @FXML
    private Button reject2Button;

    @FXML
    private Button reject3Button;

    @FXML
    private Button reject4Button;

    @FXML
    private Button reject5Button;

    @FXML
    private Button reject6Button;

    @FXML
    private TextField id1;
    @FXML
    private TextField id2;
    @FXML
    private TextField id3;
    @FXML
    private TextField id4;
    @FXML
    private TextField id5;
    @FXML
    private TextField id6;


    @FXML
    void confirm1_func(ActionEvent event) {
        String mealName = mealName1.getText();
        double newPrice = Double.parseDouble(newPrice1.getText());
        int id = Integer.parseInt(id1.getText());
        String message = String.format("Confirm Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();

    }

    @FXML
    void confirm2_func(ActionEvent event) {
        String mealName = mealName2.getText();
        double newPrice = Double.parseDouble(newPrice2.getText());
        int id = Integer.parseInt(id2.getText());
        String message = String.format("Confirm Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void confirm3_func(ActionEvent event) {
        String mealName = mealName3.getText();
        double newPrice = Double.parseDouble(newPrice3.getText());
        int id = Integer.parseInt(id3.getText());
        String message = String.format("Confirm Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void confirm4_func(ActionEvent event) {
        String mealName = mealName4.getText();
        double newPrice = Double.parseDouble(newPrice4.getText());
        int id = Integer.parseInt(id4.getText());
        String message = String.format("Confirm Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void confirm5_func(ActionEvent event) {
        String mealName = mealName5.getText();
        double newPrice = Double.parseDouble(newPrice5.getText());
        int id = Integer.parseInt(id5.getText());
        String message = String.format("Confirm Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void confirm6_func(ActionEvent event) {
        String mealName = mealName6.getText();
        double newPrice = Double.parseDouble(newPrice6.getText());
        int id = Integer.parseInt(id6.getText());
        String message = String.format("Confirm Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void reject1_func(ActionEvent event) {
        String mealName = mealName1.getText();
        double newPrice = Double.parseDouble(newPrice1.getText());
        int id = Integer.parseInt(id1.getText());
        String message = String.format("Reject Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void reject2_func(ActionEvent event) {
        String mealName = mealName2.getText();
        double newPrice = Double.parseDouble(newPrice2.getText());
        int id = Integer.parseInt(id2.getText());
        String message = String.format("Reject Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void reject3_func(ActionEvent event) {
        String mealName = mealName3.getText();
        double newPrice = Double.parseDouble(newPrice3.getText());
        int id = Integer.parseInt(id3.getText());
        String message = String.format("Reject Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void reject4_func(ActionEvent event) {
        String mealName = mealName4.getText();
        double newPrice = Double.parseDouble(newPrice4.getText());
        int id = Integer.parseInt(id4.getText());
        String message = String.format("Reject Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void reject5_func(ActionEvent event) {
        String mealName = mealName5.getText();
        double newPrice = Double.parseDouble(newPrice5.getText());
        int id = Integer.parseInt(id5.getText());
        String message = String.format("Reject Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();

        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    void reject6_func(ActionEvent event) {
        String mealName = mealName6.getText();
        double newPrice = Double.parseDouble(newPrice6.getText());
        int id = Integer.parseInt(id6.getText());
        String message = String.format("Reject Price \"%s\" \"%s\"\"%s\"", mealName, newPrice,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mealName1.clear();
        newPrice1.clear();
        oldPrice1.clear();

        mealName2.clear();
        oldPrice2.clear();
        newPrice2.clear();

        mealName3.clear();
        oldPrice3.clear();
        newPrice3.clear();

        mealName4.clear();
        oldPrice4.clear();
        newPrice4.clear();

        mealName5.clear();
        oldPrice5.clear();
        newPrice5.clear();

        mealName6.clear();
        oldPrice6.clear();
        newPrice6.clear();


        id1.clear();
        id2.clear();
        id3.clear();
        id4.clear();
        id5.clear();
        id6.clear();

        reorder();
    }

    @FXML
    private GridPane gridPane;

    @FXML
    void initialize() throws IOException {


        EventBus.getDefault().register(this);
        Client client = Client.getClient();
        try {
            Client.getClient().sendToServer("Get Price Confirmations");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    public void reorder()
    {
        if (!mealName1.getText().trim().isEmpty()) {
            mealName1.setVisible(true);
            oldPrice1.setVisible(true);
            newPrice1.setVisible(true);
            confirm1Button.setVisible(true);
            reject1Button.setVisible(true);
        }
        else{
            mealName1.setVisible(false);
            oldPrice1.setVisible(false);
            newPrice1.setVisible(false);
            confirm1Button.setVisible(false);
            reject1Button.setVisible(false);
        }
        if (!mealName2.getText().trim().isEmpty()) {
            mealName2.setVisible(true);
            oldPrice2.setVisible(true);
            newPrice2.setVisible(true);
            confirm2Button.setVisible(true);
            reject2Button.setVisible(true);
        }
        else{
            mealName2.setVisible(false);
            oldPrice2.setVisible(false);
            newPrice2.setVisible(false);
            confirm2Button.setVisible(false);
            reject2Button.setVisible(false);
        }
        if (!mealName3.getText().trim().isEmpty()) {
            mealName3.setVisible(true);
            oldPrice3.setVisible(true);
            newPrice3.setVisible(true);
            confirm3Button.setVisible(true);
            reject3Button.setVisible(true);
        }
        else {
            mealName3.setVisible(false);
            oldPrice3.setVisible(false);
            newPrice3.setVisible(false);
            confirm3Button.setVisible(false);
            reject3Button.setVisible(false);
        }
        if (!mealName4.getText().trim().isEmpty()) {
            mealName4.setVisible(true);
            oldPrice4.setVisible(true);
            newPrice4.setVisible(true);
            confirm4Button.setVisible(true);
            reject4Button.setVisible(true);
        }
        else {
            mealName4.setVisible(false);
            oldPrice4.setVisible(false);
            newPrice4.setVisible(false);
            confirm4Button.setVisible(false);
            reject4Button.setVisible(false);
        }
        if (!mealName5.getText().trim().isEmpty()) {
            mealName5.setVisible(true);
            oldPrice5.setVisible(true);
            newPrice5.setVisible(true);
            confirm5Button.setVisible(true);
            reject5Button.setVisible(true);
        }
        else {
            mealName5.setVisible(false);
            oldPrice5.setVisible(false);
            newPrice5.setVisible(false);
            confirm5Button.setVisible(false);
            reject5Button.setVisible(false);
        }
        if (!mealName6.getText().trim().isEmpty()) {
            mealName6.setVisible(true);
            oldPrice6.setVisible(true);
            newPrice6.setVisible(true);
            confirm6Button.setVisible(true);
            reject6Button.setVisible(true);
        }
        else {
            mealName6.setVisible(false);
            oldPrice6.setVisible(false);
            newPrice6.setVisible(false);
            confirm6Button.setVisible(false);
            reject6Button.setVisible(false);
        }
    }

    @FXML
    void back_func(ActionEvent event) {
        String page = "Management Page";
        App.switchScreen(page);
    }

    @FXML
    void back_to_main_func(ActionEvent event) {
        String page = "Main Page";
        App.switchScreen(page);
    }


    @FXML
    public void fillTextFields(List<PriceConfirmation> confirmations) {
        int size = confirmations.size();

        if (size >= 1) {
            mealName1.setText(confirmations.get(0).getMealName());
            oldPrice1.setText(String.valueOf(confirmations.get(0).getOldPrice()));
            newPrice1.setText(String.valueOf(confirmations.get(0).getNewPrice()));
            id1.setText(String.valueOf(confirmations.get(0).getId()));
        }

        if (size >= 2) {
            mealName2.setText(confirmations.get(1).getMealName());
            oldPrice2.setText(String.valueOf(confirmations.get(1).getOldPrice()));
            newPrice2.setText(String.valueOf(confirmations.get(1).getNewPrice()));
            id2.setText(String.valueOf(confirmations.get(1).getId()));
        }

        if(size >= 3) {
            mealName3.setText(confirmations.get(2).getMealName());
            oldPrice3.setText(String.valueOf(confirmations.get(2).getOldPrice()));
            newPrice3.setText(String.valueOf(confirmations.get(2).getNewPrice()));
            id3.setText(String.valueOf(confirmations.get(2).getId()));
        }
        if(size >= 4) {
            mealName4.setText(confirmations.get(3).getMealName());
            oldPrice4.setText(String.valueOf(confirmations.get(3).getOldPrice()));
            newPrice4.setText(String.valueOf(confirmations.get(3).getNewPrice()));
            id4.setText(String.valueOf(confirmations.get(3).getId()));
        }
        if(size >= 5) {
            mealName5.setText(confirmations.get(4).getMealName());
            oldPrice5.setText(String.valueOf(confirmations.get(4).getOldPrice()));
            newPrice5.setText(String.valueOf(confirmations.get(4).getNewPrice()));
            id5.setText(String.valueOf(confirmations.get(4).getId()));
        }
        if(size >= 6) {
            mealName6.setText(confirmations.get(5).getMealName());
            oldPrice6.setText(String.valueOf(confirmations.get(5).getOldPrice()));
            newPrice6.setText(String.valueOf(confirmations.get(5).getNewPrice()));
            id6.setText(String.valueOf(confirmations.get(5).getId()));
        }


        reorder();
    }




    private List<PriceConfirmation> PriceConfirmation ;



    @Subscribe
    public void ExternalIntervention(Object msg) {
        Platform.runLater(() -> {
            try {
                if (msg instanceof List) {
                    List list = (List) msg;
                    if (!list.isEmpty() && list.get(0) instanceof PriceConfirmation) {
                        List<PriceConfirmation> confirmations = (List<PriceConfirmation>) list;
                        fillTextFields(confirmations);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }





}


