package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import il.cshaifasweng.OCSFMediatorExample.entities.PriceConfirmation;
import il.cshaifasweng.OCSFMediatorExample.entities.Discounts;
import java.io.IOException;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;


public class DiscountConfirmationController {

    @FXML
    private Button backButton;

    @FXML
    private Button backToMainButton;

    @FXML
    private TextField discount1;
    @FXML
    private TextField discount2;
    @FXML
    private TextField discount3;
    @FXML
    private TextField discount4;
    @FXML
    private TextField discount5;
    @FXML
    private TextField discount6;
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
    private Label label1;

    @FXML
    private Label label2;
    @FXML
    private Label label3;
    @FXML
    private Label label4;
    @FXML
    private Label label5;
    @FXML
    private Label label6;


    @FXML
    void confirm1_func(ActionEvent event) {
        double discount = Double.parseDouble(discount1.getText());
        int id = Integer.parseInt(id1.getText());
        String message = String.format("Confirm Discount \"%s\" \"%s\"", discount, id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount2.getText());
        int id = Integer.parseInt(id2.getText());
        String message = String.format("Confirm Discount \"%s\" \"%s\"", discount, id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount3.getText());
        int id = Integer.parseInt(id3.getText());
        String message = String.format("Confirm Discount \"%s\" \"%s\"", discount, id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount4.getText());
        int id = Integer.parseInt(id4.getText());
        String message = String.format("Confirm Discount \"%s\" \"%s\"", discount, id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount5.getText());
        int id = Integer.parseInt(id5.getText());
        String message = String.format("Confirm Discount \"%s\" \"%s\"", discount, id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount6.getText());
        int id = Integer.parseInt(id6.getText());
        String message = String.format("Confirm Discount \"%s\" \"%s\"", discount, id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount1.getText());
        int id = Integer.parseInt(id1.getText());
        String message = String.format("Reject Discount \"%s\" \"%s\"", discount,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount2.getText());
        int id = Integer.parseInt(id2.getText());
        String message = String.format("Reject Discount \"%s\" \"%s\"", discount,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount3.getText());
        int id = Integer.parseInt(id3.getText());
        String message = String.format("Reject Discount \"%s\" \"%s\"", discount,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount4.getText());
        int id = Integer.parseInt(id4.getText());
        String message = String.format("Reject Discount \"%s\" \"%s\"", discount,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount5.getText());
        int id = Integer.parseInt(id5.getText());
        String message = String.format("Reject Discount \"%s\" \"%s\"", discount,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
        double discount = Double.parseDouble(discount6.getText());
        int id = Integer.parseInt(id6.getText());
        String message = String.format("Reject Discount \"%s\" \"%s\"", discount,id);

        try {
            Client.getClient().sendToServer(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

        discount1.clear();
        discount2.clear();
        discount3.clear();
        discount4.clear();
        discount5.clear();
        discount6.clear();

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
            Client.getClient().sendToServer("Get Discount Confirmations");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




    public void reorder()
    {
        if (!discount1.getText().trim().isEmpty()) {
            discount1.setVisible(true);
            confirm1Button.setVisible(true);
            reject1Button.setVisible(true);
            label1.setVisible(true);
        }
        else{
            discount1.setVisible(false);
            confirm1Button.setVisible(false);
            reject1Button.setVisible(false);
            label1.setVisible(false);
        }
        if (!discount2.getText().trim().isEmpty()) {
            discount2.setVisible(true);
            confirm2Button.setVisible(true);
            reject2Button.setVisible(true);
            label2.setVisible(true);
        }
        else{
            discount2.setVisible(false);
            confirm2Button.setVisible(false);
            reject2Button.setVisible(false);
            label2.setVisible(false);
        }
        if (!discount3.getText().trim().isEmpty()) {
            discount3.setVisible(true);
            confirm3Button.setVisible(true);
            reject3Button.setVisible(true);
            label3.setVisible(true);
        }
        else {
            discount3.setVisible(false);
            confirm3Button.setVisible(false);
            reject3Button.setVisible(false);
            label3.setVisible(false);
        }
        if (!discount4.getText().trim().isEmpty()) {
            discount4.setVisible(true);
            confirm4Button.setVisible(true);
            reject4Button.setVisible(true);
            label4.setVisible(true);
        }
        else {
            discount4.setVisible(false);
            confirm4Button.setVisible(false);
            reject4Button.setVisible(false);
            label4.setVisible(false);
        }
        if (!discount5.getText().trim().isEmpty()) {
            discount5.setVisible(true);
            confirm5Button.setVisible(true);
            reject5Button.setVisible(true);
            label5.setVisible(true);
        }
        else {
            discount5.setVisible(false);
            confirm5Button.setVisible(false);
            reject5Button.setVisible(false);
            label5.setVisible(false);
        }
        if (!discount6.getText().trim().isEmpty()) {
            discount6.setVisible(true);
            confirm6Button.setVisible(true);
            reject6Button.setVisible(true);
            label6.setVisible(true);
        }
        else {
            discount6.setVisible(false);
            confirm6Button.setVisible(false);
            reject6Button.setVisible(false);
            label6.setVisible(false);
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
    public void fillTextFields(List<il.cshaifasweng.OCSFMediatorExample.entities.Discounts> Discounts) {
        int size = Discounts.size();

        if (size >= 1) {

            discount1.setText(String.valueOf(Discounts.get(0).getDiscount()));
            id1.setText(String.valueOf(Discounts.get(0).getId()));
        }

        if (size >= 2) {
            discount2.setText(String.valueOf(Discounts.get(1).getDiscount()));
            id2.setText(String.valueOf(Discounts.get(1).getId()));
        }

        if(size >= 3) {
            discount3.setText(String.valueOf(Discounts.get(2).getDiscount()));
            id3.setText(String.valueOf(Discounts.get(2).getId()));
        }
        if(size >= 4) {
            discount4.setText(String.valueOf(Discounts.get(3).getDiscount()));
            id4.setText(String.valueOf(Discounts.get(3).getId()));

        }
        if(size >= 5) {
            discount5.setText(String.valueOf(Discounts.get(4).getDiscount()));
            id5.setText(String.valueOf(Discounts.get(4).getId()));
        }
        if(size >= 6) {
            discount6.setText(String.valueOf(Discounts.get(5).getDiscount()));
            id6.setText(String.valueOf(Discounts.get(5).getId()));
        }

        reorder();
    }




    private List<Discounts> Discounts ;


    @Subscribe
    public void ExternalIntervention(Object msg) {
        Platform.runLater(() -> {
            try {
                if (msg instanceof List) {
                    Discounts = (List<Discounts>) msg;
                    fillTextFields(Discounts);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }




}


