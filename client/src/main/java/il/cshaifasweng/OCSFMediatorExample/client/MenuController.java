package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;


public class MenuController {

    @FXML
    private Button editMeal1;

    @FXML
    private Button editMeal2;

    @FXML
    private Button editMeal3;

    @FXML
    private Button editMeal4;

    @FXML
    private Button editMeal5;

    @FXML
    private Button editMeal6;

    @FXML
    private Button editMeal7;

    @FXML
    private GridPane menuGrid;


    @FXML
    private Button savebtn;

    @FXML
    private TextField textField00;

    @FXML
    private TextField textField01;

    @FXML
    private TextField textField02;

    @FXML
    private TextField textField03;

    @FXML
    private TextField textField10;

    @FXML
    private TextField textField11;

    @FXML
    private TextField textField12;

    @FXML
    private TextField textField13;

    @FXML
    private TextField textField20;

    @FXML
    private TextField textField21;

    @FXML
    private TextField textField22;

    @FXML
    private TextField textField23;

    @FXML
    private TextField textField30;

    @FXML
    private TextField textField31;

    @FXML
    private TextField textField32;

    @FXML
    private TextField textField33;

    @FXML
    private TextField textField40;

    @FXML
    private TextField textField41;

    @FXML
    private TextField textField42;

    @FXML
    private TextField textField43;

    @FXML
    private TextField textField50;

    @FXML
    private TextField textField51;

    @FXML
    private TextField textField52;

    @FXML
    private TextField textField53;

    @FXML
    private TextField textField60;

    @FXML
    private TextField textField61;

    @FXML
    private TextField textField62;

    @FXML
    private TextField textField63;

    @FXML
    private TextField textField70;

    @FXML
    private TextField textField71;

    @FXML
    private TextField textField72;

    @FXML
    private TextField textField73;

    @FXML
     void initialize() throws IOException {
        EventBus.getDefault().register(this);
        Client client = Client.getClient();
        client.sendToServer("Request menu");
    }
   private List<Meal> Menu ;

    @Subscribe
    public void ExternalIntervention(Object msg) {
        Platform.runLater(() -> {
            try {
                if (msg instanceof List) {
                    Menu = (List<Meal>) msg;
                    menuOrder(Menu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void menuOrder(List<Meal>Menu) {

        textField10.setText(Menu.get(0).getMealName());
        textField11.setText(Menu.get(0).getMealDescription());
        textField12.setText(Menu.get(0).getMealPreferences());
        textField13.setText(String.valueOf(Menu.get(0).getMealPrice()));

        textField20.setText(Menu.get(1).getMealName());
        textField21.setText(Menu.get(1).getMealDescription());
        textField22.setText(Menu.get(1).getMealPreferences());
        textField23.setText(String.valueOf(Menu.get(1).getMealPrice()));

        textField30.setText(Menu.get(2).getMealName());
        textField31.setText(Menu.get(2).getMealDescription());
        textField32.setText(Menu.get(2).getMealPreferences());
        textField33.setText(String.valueOf(Menu.get(2).getMealPrice()));

        textField40.setText(Menu.get(3).getMealName());
        textField41.setText(Menu.get(3).getMealDescription());
        textField42.setText(Menu.get(3).getMealPreferences());
        textField43.setText(String.valueOf(Menu.get(3).getMealPrice()));

        textField50.setText(Menu.get(4).getMealName());
        textField51.setText(Menu.get(4).getMealDescription());
        textField52.setText(Menu.get(4).getMealPreferences());
        textField53.setText(String.valueOf(Menu.get(4).getMealPrice()));

        textField60.setText(Menu.get(5).getMealName());
        textField61.setText(Menu.get(5).getMealDescription());
        textField62.setText(Menu.get(5).getMealPreferences());
        textField63.setText(String.valueOf(Menu.get(5).getMealPrice()));

        textField70.setText(Menu.get(6).getMealName());
        textField71.setText(Menu.get(6).getMealDescription());
        textField72.setText(Menu.get(6).getMealPreferences());
        textField73.setText(String.valueOf(Menu.get(6).getMealPrice()));
    }

    private String mealName ;
    private int flag=0 ;
    private String price;


    @FXML
    void edit1(ActionEvent event) {
     editMeal2.setDisable(true);
     editMeal3.setDisable(true);
     editMeal4.setDisable(true);
     editMeal5.setDisable(true);
     editMeal6.setDisable(true);
     editMeal7.setDisable(true);
     textField13.setEditable(true);
     textField13.setStyle("-fx-background-color: #D3D3D3 ;");
     editMeal1.setDisable(true);
     savebtn.setVisible(true);
     mealName = textField10.getText();
     flag = 1;
    }

    @FXML
    void edit2(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal3.setDisable(true);
        editMeal4.setDisable(true);
        editMeal5.setDisable(true);
        editMeal6.setDisable(true);
        editMeal7.setDisable(true);
        savebtn.setVisible(true);
        textField23.setEditable(true);
        textField23.setStyle("-fx-background-color: #D3D3D3 ;");
        editMeal2.setDisable(true);
        mealName = textField20.getText();
        flag = 2;


    }

    @FXML
    void edit3(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal2.setDisable(true);
        editMeal4.setDisable(true);
        editMeal5.setDisable(true);
        editMeal6.setDisable(true);
        editMeal7.setDisable(true);
        savebtn.setVisible(true);
        textField33.setEditable(true);
        textField33.setStyle("-fx-background-color: #D3D3D3 ;");
        editMeal3.setDisable(true);
        mealName = textField30.getText();
        flag = 3;

    }

    @FXML
    void edit4(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal2.setDisable(true);
        editMeal3.setDisable(true);
        editMeal5.setDisable(true);
        editMeal6.setDisable(true);
        editMeal7.setDisable(true);
        savebtn.setVisible(true);
        textField43.setEditable(true);
        textField43.setStyle("-fx-background-color: #D3D3D3 ;");
        editMeal4.setDisable(true);
        mealName = textField40.getText();
        flag = 4;


    }

    @FXML
    void edit5(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal2.setDisable(true);
        editMeal3.setDisable(true);
        editMeal4.setDisable(true);
        editMeal6.setDisable(true);
        editMeal7.setDisable(true);
        savebtn.setVisible(true);
        textField53.setEditable(true);
        textField53.setStyle("-fx-background-color: #D3D3D3 ;");
        editMeal5.setDisable(true);
        mealName = textField50.getText();
        flag = 5;


    }

    @FXML
    void edit6(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal2.setDisable(true);
        editMeal3.setDisable(true);
        editMeal4.setDisable(true);
        editMeal5.setDisable(true);
        savebtn.setVisible(true);
        editMeal7.setDisable(true);
        textField63.setEditable(true);
        textField63.setStyle("-fx-background-color: #D3D3D3 ;");
        editMeal6.setDisable(true);
        mealName = textField60.getText();
        flag = 6;


    }

    @FXML
    void edit7(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal2.setDisable(true);
        editMeal3.setDisable(true);
        editMeal4.setDisable(true);
        editMeal5.setDisable(true);
        editMeal6.setDisable(true);
        savebtn.setVisible(true);
        textField73.setEditable(true);
        textField73.setStyle("-fx-background-color: #D3D3D3 ;");
        editMeal7.setDisable(true);
        mealName = textField70.getText();
        flag = 7;
    }

    @FXML
    void saveFunc(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal2.setDisable(true);
        editMeal3.setDisable(true);
        editMeal4.setDisable(true);
        editMeal5.setDisable(true);
        editMeal6.setDisable(true);
        editMeal7.setDisable(true);
        if(flag == 1)
        {
            price=textField13.getText();
            textField13.setStyle("-fx-background-color:  white ;");
        }
        if(flag == 2)
        {
            price=textField23.getText();
            textField23.setStyle("-fx-background-color:  white ;");

        }
        if(flag == 3)
        {
            price=textField33.getText();
            textField33.setStyle("-fx-background-color:  white ;");

        }
        if(flag == 4)
        {
            price=textField43.getText();
            textField43.setStyle("-fx-background-color:  white ;");

        }
        if(flag == 5)
        {
            price=textField53.getText();
            textField53.setStyle("-fx-background-color:  white ;");

        }
        if(flag == 6)
        {
            price=textField63.getText();
            textField63.setStyle("-fx-background-color:  white ;");

        }
        if(flag == 7)
        {
            price=textField73.getText();
            textField73.setStyle("-fx-background-color:  white ;");

        }
        if(price.equals("") )
        {
            price="0";
        }
        try {
            Client.getClient().sendToServer("Update price " + "\"" + mealName + "\" " + "\"" + price + "\"");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        editMeal1.setDisable(false);
        editMeal2.setDisable(false);
        editMeal3.setDisable(false);
        editMeal4.setDisable(false);
        editMeal5.setDisable(false);
        editMeal6.setDisable(false);
        editMeal7.setDisable(false);
        savebtn.setVisible(false);



    }
}
