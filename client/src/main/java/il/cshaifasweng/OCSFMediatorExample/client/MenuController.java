package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

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
    void initialize() {
      // EventBus.getDefault().register(this);
        //editMeal1.setDisable(true);



    }

    @FXML
    void edit1(ActionEvent event) {
     editMeal2.setDisable(true);
     editMeal3.setDisable(true);
     editMeal4.setDisable(true);
     editMeal5.setDisable(true);
     editMeal6.setDisable(true);
     editMeal7.setDisable(true);
     savebtn.setVisible(true);
     textField13.setEditable(true);
     editMeal1.setDisable(true);

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
        editMeal2.setDisable(true);
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
        editMeal3.setDisable(true);
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
        editMeal4.setDisable(true);
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
        editMeal5.setDisable(true);
    }

    @FXML
    void edit6(ActionEvent event) {
        editMeal1.setDisable(true);
        editMeal2.setDisable(true);
        editMeal3.setDisable(true);
        editMeal4.setDisable(true);
        editMeal5.setDisable(true);
        editMeal7.setDisable(true);
        savebtn.setVisible(true);
        textField63.setEditable(true);
        editMeal6.setDisable(true);
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
        editMeal7.setDisable(true);
    }

    @FXML
    void saveFunc(ActionEvent event) {

    }

}
