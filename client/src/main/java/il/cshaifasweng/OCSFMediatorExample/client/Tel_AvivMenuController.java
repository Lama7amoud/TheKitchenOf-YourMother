package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.userAtt;


public class Tel_AvivMenuController {


    @FXML
    private TextField textField10;

    @FXML
    private TextField textField11;

    @FXML
    private TextField textField12;

    @FXML
    private TextField textField13;

    @FXML
    private ImageView imageView1;


    @FXML
    private ImageView imageView2;

    @FXML
    private ImageView imageView3;

    @FXML
    private ImageView imageView4;

    @FXML
    private ImageView imageView5;

    @FXML
    private ImageView imageView6;

    @FXML
    private ImageView imageView7;

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
    private ImageView imageView11;

    @FXML
    private ImageView imageView21;

    @FXML
    private ImageView imageView31;

    @FXML
    private ImageView imageView41;

    @FXML
    private ImageView imageView51;

    @FXML
    private TextField textField101;

    @FXML
    private TextField textField111;


    @FXML
    private TextField textField121;


    @FXML
    private TextField textField131;


    @FXML
    private TextField textField201;


    @FXML
    private TextField textField211;


    @FXML
    private TextField textField221;


    @FXML
    private TextField textField231;


    @FXML
    private TextField textField301;


    @FXML
    private TextField textField311;


    @FXML
    private TextField textField321;


    @FXML
    private TextField textField331;


    @FXML
    private TextField textField401;


    @FXML
    private TextField textField411;


    @FXML
    private TextField textField421;


    @FXML
    private TextField textField431;

    @FXML
    private TextField textField501;


    @FXML
    private TextField textField511;


    @FXML
    private TextField textField521;


    @FXML
    private TextField textField531;

    @FXML
    private Button savebtn;

    @FXML
    private Button editPrice1;

    @FXML
    private Button editPrice2;

    @FXML
    private Button editPrice3;

    @FXML
    private Button editPrice4;

    @FXML
    private Button editPrice5;

    @FXML
    private Button editPrice6;

    @FXML
    private Button editPrice7;

    @FXML
    private Button editPrice11;

    @FXML
    private Button editPrice22;

    @FXML
    private Button editPrice33;

    @FXML
    private Button editPrice44;

    @FXML
    private Button editPrice55;

    @FXML
    private TextField discount;

    @FXML
    private Label errorLabel;

    @FXML
    private Button editIngredients1;

    @FXML
    private Button editIngredients2;

    @FXML
    private Button editIngredients3;

    @FXML
    private Button editIngredients4;

    @FXML
    private Button editIngredients5;

    @FXML
    private Button editIngredients6;

    @FXML
    private Button editIngredients7;

    @FXML
    private Button editIngredients11;

    @FXML
    private Button editIngredients22;

    @FXML
    private Button editIngredients33;

    @FXML
    private Button editIngredients44;

    @FXML
    private Button editIngredients55;

    @FXML
    private Button save2btn;



    @FXML
    void initialize() throws IOException {

        EventBus.getDefault().register(this);
        Client client = Client.getClient();
        try {
            client.sendToServer("Request Tel-Aviv menu");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    private List<Meal> Menu ;

    @Subscribe
    public void ExternalIntervention(Object msg) {
        Platform.runLater(() -> {
            try {
                if (msg instanceof List) {
                    Menu = (List<Meal>) msg;
                    menuOrder(Menu);
                    imagesOrder(Menu);
                    isDietitian();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void isDietitian()
    {
        Platform.runLater(() -> {
            AuthorizedUser user = Client.getClientAttributes();
            if (user != null && user.getPermissionLevel() == 5) {
                if (!textField10.getText().trim().isEmpty()) {
                    editPrice1.setVisible(true);
                    editIngredients1.setVisible(true);
                }
                if (!textField20.getText().trim().isEmpty()) {
                    editPrice2.setVisible(true);
                    editIngredients2.setVisible(true);
                }
                if (!textField30.getText().trim().isEmpty()) {
                    editPrice3.setVisible(true);
                    editIngredients3.setVisible(true);

                }
                if (!textField40.getText().trim().isEmpty()) {
                    editPrice4.setVisible(true);
                    editIngredients4.setVisible(true);
                }
                if (!textField50.getText().trim().isEmpty()) {
                    editPrice5.setVisible(true);
                    editIngredients5.setVisible(true);

                }
                if (!textField60.getText().trim().isEmpty()) {
                    editPrice6.setVisible(true);
                    editIngredients6.setVisible(true);
                }
                if (!textField70.getText().trim().isEmpty()) {
                    editPrice7.setVisible(true);
                    editIngredients7.setVisible(true);
                }
                if (!textField101.getText().trim().isEmpty()) {
                    editPrice11.setVisible(true);
                    editIngredients11.setVisible(true);
                }
                if (!textField201.getText().trim().isEmpty()) {
                    editPrice22.setVisible(true);
                    editIngredients22.setVisible(true);
                }
                if (!textField301.getText().trim().isEmpty()) {
                    editPrice33.setVisible(true);
                    editIngredients33.setVisible(true);
                }
                if (!textField401.getText().trim().isEmpty()) {
                    editPrice44.setVisible(true);
                    editIngredients44.setVisible(true);
                }
                if (!textField501.getText().trim().isEmpty()) {
                    editPrice55.setVisible(true);
                    editIngredients55.setVisible(true);
                }


            }
        });
    }
    int crt1;
    private List<Meal> sharedList(List<Meal>Menu) {
        crt1=0;
        List<Meal> sharedList = new ArrayList<>();
        for (Meal meal : Menu) {
            if (meal.getMealCategory().equals("shared meal")) {
                sharedList.add(meal);
                crt1++;
            }
        }
        return sharedList;
    }

    int crt2;
    private List<Meal> special2List(List<Meal>Menu) {
        crt2=0;
        List<Meal> special2List = new ArrayList<>();
        for (Meal meal : Menu) {
            if (meal.getMealCategory().equals("special2")) {
                special2List.add(meal);
                crt2++;
            }

        }
        return special2List;
    }

    private void menuOrder(List<Meal>Menu) {
        Platform.runLater(() -> {
            List<Meal> sharedList = sharedList(Menu);
            List<Meal> special2List = special2List(Menu);
            int i;

            for( i=0 ; i<crt1 ; i++) {

                if(i==0) {
                    textField10.setText(sharedList.get(0).getMealName());
                    textField11.setText(sharedList.get(0).getMealDescription());
                    textField12.setText(sharedList.get(0).getMealPreferences());
                    textField13.setText(String.valueOf(sharedList.get(0).getMealPrice()));
                }
                if (i==1) {
                    textField20.setText(sharedList.get(1).getMealName());
                    textField21.setText(sharedList.get(1).getMealDescription());
                    textField22.setText(sharedList.get(1).getMealPreferences());
                    textField23.setText(String.valueOf(sharedList.get(1).getMealPrice()));

                }
                if (i==2) {
                    textField30.setText(sharedList.get(2).getMealName());
                    textField31.setText(sharedList.get(2).getMealDescription());
                    textField32.setText(sharedList.get(2).getMealPreferences());
                    textField33.setText(String.valueOf(sharedList.get(2).getMealPrice()));

                }
                if (i==3) {
                    textField40.setText(sharedList.get(3).getMealName());
                    textField41.setText(sharedList.get(3).getMealDescription());
                    textField42.setText(sharedList.get(3).getMealPreferences());
                    textField43.setText(String.valueOf(sharedList.get(3).getMealPrice()));

                }

                if (i==4) {
                    textField50.setText(sharedList.get(4).getMealName());
                    textField51.setText(sharedList.get(4).getMealDescription());
                    textField52.setText(sharedList.get(4).getMealPreferences());
                    textField53.setText(String.valueOf(sharedList.get(4).getMealPrice()));
                }
                if (i==5) {
                    textField60.setText(sharedList.get(5).getMealName());
                    textField61.setText(sharedList.get(5).getMealDescription());
                    textField62.setText(sharedList.get(5).getMealPreferences());
                    textField63.setText(String.valueOf(sharedList.get(5).getMealPrice()));
                }
                if (i==6) {
                    textField70.setText(sharedList.get(6).getMealName());
                    textField71.setText(sharedList.get(6).getMealDescription());
                    textField72.setText(sharedList.get(6).getMealPreferences());
                    textField73.setText(String.valueOf(sharedList.get(6).getMealPrice()));
                }

            }
            for (i=0 ; i<crt2 ; i++) {
                if (i==0) {
                    textField101.setText(special2List.get(0).getMealName());
                    textField111.setText(special2List.get(0).getMealDescription());
                    textField121.setText(special2List.get(0).getMealPreferences());
                    textField131.setText(String.valueOf(special2List.get(0).getMealPrice()));
                }
                if (i==1) {
                    textField201.setText(special2List.get(1).getMealName());
                    textField211.setText(special2List.get(1).getMealDescription());
                    textField221.setText(special2List.get(1).getMealPreferences());
                    textField231.setText(String.valueOf(special2List.get(1).getMealPrice()));
                }
                if (i==2) {
                    textField301.setText(special2List.get(2).getMealName());
                    textField311.setText(special2List.get(2).getMealDescription());
                    textField321.setText(special2List.get(2).getMealPreferences());
                    textField331.setText(String.valueOf(special2List.get(2).getMealPrice()));

                }
                if (i==3) {
                    textField401.setText(special2List.get(3).getMealName());
                    textField411.setText(special2List.get(3).getMealDescription());
                    textField421.setText(special2List.get(3).getMealPreferences());
                    textField431.setText(String.valueOf(special2List.get(3).getMealPrice()));
                }
                if (i==4) {
                    textField501.setText(special2List.get(4).getMealName());
                    textField511.setText(special2List.get(4).getMealDescription());
                    textField521.setText(special2List.get(4).getMealPreferences());
                    textField531.setText(String.valueOf(special2List.get(4).getMealPrice()));
                }

            }
        });

    }

    private void imagesOrder(List<Meal> Menu) {

        Platform.runLater(() -> {
            List<Meal> sharedList = sharedList(Menu);
            List<Meal> special2List = special2List(Menu);
            int i;
            for( i=0 ; i<crt1 ; i++) {
                if(i==0) {
                    String imagePath1 = sharedList.get(0).getImagePath();
                    try {
                        Image image1 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath1)));
                        imageView1.setImage(image1);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==1) {
                    String imagePath2 = sharedList.get(1).getImagePath();
                    try {
                        Image image2 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath2)));
                        imageView2.setImage(image2);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==2) {
                    String imagePath3 = sharedList.get(2).getImagePath();
                    try {
                        Image image3 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath3)));
                        imageView3.setImage(image3);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }

                }
                if (i==3) {
                    String imagePath4 = sharedList.get(3).getImagePath();
                    try {
                        Image image4 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath4)));
                        imageView4.setImage(image4);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==4) {
                    String imagePath5 = sharedList.get(4).getImagePath();
                    try {
                        Image image5 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath5)));
                        imageView5.setImage(image5);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==5) {
                    String imagePath6 = sharedList.get(5).getImagePath();
                    try {
                        Image image6 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath6)));
                        imageView6.setImage(image6);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==6) {
                    String imagePath7 = sharedList.get(6).getImagePath();
                    try {
                        Image image7 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath7)));
                        imageView7.setImage(image7);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
            }

            for (i=0 ; i<crt2 ; i++) {
                if(i==0) {
                    String imagePath1 = special2List.get(0).getImagePath();
                    try {
                        Image image1 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath1)));
                        imageView11.setImage(image1);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==1) {
                    String imagePath2 = special2List.get(1).getImagePath();
                    try {
                        Image image2 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath2)));
                        imageView21.setImage(image2);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==2) {
                    String imagePath3 = special2List.get(2).getImagePath();
                    try {
                        Image image3 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath3)));
                        imageView31.setImage(image3);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==3) {
                    String imagePath4 = special2List.get(3).getImagePath();
                    try {
                        Image image4 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath4)));
                        imageView41.setImage(image4);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
                if (i==4) {
                    String imagePath5 = special2List.get(4).getImagePath();
                    try {
                        Image image5 = new Image(String.valueOf(PrimaryController.class.getResource(imagePath5)));
                        imageView51.setImage(image5);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("Failed to load image at index: 0");
                    }
                }
            }


        });

    }


    private String mealName ;
    private int flag=0 ;
    private String price;


    @FXML
    void editPrice1(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            editPrice11.setVisible(false);
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            savebtn.setVisible(true);
            textField13.setEditable(true);
            textField13.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editPrice1.setVisible(false);
            mealName = textField10.getText();
            flag = 1;
        });
    }

    @FXML
    void editPrice2(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice1.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            editPrice11.setVisible(false);
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            savebtn.setVisible(true);
            textField23.setEditable(true);
            textField23.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice2.setVisible(false);
            mealName = textField20.getText();
            flag = 2;
        });
    }

    @FXML
    void editPrice3(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            editPrice11.setVisible(false);
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            savebtn.setVisible(true);
            textField33.setEditable(true);
            textField33.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice3.setVisible(false);
            mealName = textField30.getText();
            flag = 3;
        });
    }

    @FXML
    void editPrice4(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            editPrice11.setVisible(false);
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            savebtn.setVisible(true);
            textField43.setEditable(true);
            textField43.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice4.setVisible(false);
            mealName = textField40.getText();
            flag = 4;
        });
    }

    @FXML
    void editPrice5(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            editPrice11.setVisible(false);
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            savebtn.setVisible(true);
            textField53.setEditable(true);
            textField53.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice5.setVisible(false);
            mealName = textField50.getText();
            flag = 5;

        });
    }

    @FXML
    void editPrice6(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice7.setVisible(false);
            editPrice11.setVisible(false);
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            savebtn.setVisible(true);
            textField63.setEditable(true);
            textField63.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice6.setVisible(false);
            mealName = textField60.getText();
            flag = 6;
        });
    }

    @FXML
    void editPrice7(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice11.setVisible(false);
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            savebtn.setVisible(true);
            textField73.setEditable(true);
            textField73.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice7.setVisible(false);
            mealName = textField70.getText();
            flag = 7;
        });
    }

    @FXML
    void editPrice11(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);

            savebtn.setVisible(true);
            textField131.setEditable(true);
            textField131.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editPrice11.setVisible(false);
            mealName = textField101.getText();
            flag = 8;
        });
    }

    @FXML
    void editPrice22(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice11.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            savebtn.setVisible(true);
            textField231.setEditable(true);
            textField231.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice22.setVisible(false);
            mealName = textField201.getText();
            flag = 9;
        });
    }

    @FXML
    void editPrice33(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice22.setVisible(false);
            editPrice11.setVisible(false);
            editPrice44.setVisible(false);
            editPrice55.setVisible(false);
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            savebtn.setVisible(true);
            textField331.setEditable(true);
            textField331.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice33.setVisible(false);
            mealName = textField301.getText();
            flag = 10;
        });
    }

    @FXML
    void editPrice44(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice11.setVisible(false);
            editPrice55.setVisible(false);
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            savebtn.setVisible(true);
            textField431.setEditable(true);
            textField431.setStyle("-fx-background-color: orange ;-fx-border-color: blue");
            editPrice44.setVisible(false);
            mealName = textField401.getText();
            flag = 11;
        });
    }

    @FXML
    void editPrice55(ActionEvent event) {
        Platform.runLater(() -> {
            editPrice22.setVisible(false);
            editPrice33.setVisible(false);
            editPrice44.setVisible(false);
            editPrice11.setVisible(false);
            editPrice1.setVisible(false);
            editPrice2.setVisible(false);
            editPrice3.setVisible(false);
            editPrice4.setVisible(false);
            editPrice5.setVisible(false);
            editPrice6.setVisible(false);
            editPrice7.setVisible(false);
            savebtn.setVisible(true);
            textField531.setEditable(true);
            textField531.setStyle("-fx-background-color: orange ; -fx-border-color: blue");
            editPrice55.setVisible(false);
            mealName = textField501.getText();
            flag = 12;

        });
    }

    @FXML
    void saveFunc(ActionEvent event) {
        Platform.runLater(() -> {

            if (flag == 1) {
                price = textField13.getText();
                textField13.setStyle("-fx-background-color: orange ; -fx-border-color: orange");
            }
            if (flag == 2) {
                price = textField23.getText();
                textField23.setStyle("-fx-background-color: orange ; -fx-border-color: orange");


            }
            if (flag == 3) {
                price = textField33.getText();
                textField33.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 4) {
                price = textField43.getText();
                textField43.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 5) {
                price = textField53.getText();
                textField53.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 6) {
                price = textField63.getText();
                textField63.setStyle("-fx-background-color: orange ; -fx-border-color: orange");
            }
            if (flag == 7) {
                price = textField73.getText();
                textField73.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 8) {
                price = textField131.getText();
                textField131.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 9) {
                price = textField231.getText();
                textField231.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 10) {
                price = textField331.getText();
                textField331.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 11) {
                price = textField431.getText();
                textField431.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag == 12) {
                price = textField531.getText();
                textField531.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (price.equals("")) {
                price = "0";
            }
            try {
                Client.getClient().sendToServer("Update price " + "\"" + mealName + "\" " + "\"" + price + "\"");
                System.out.println(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
            textField131.setEditable(false);
            textField231.setEditable(false);
            textField331.setEditable(false);
            textField431.setEditable(false);
            textField531.setEditable(false);
            textField13.setEditable(false);
            textField23.setEditable(false);
            textField33.setEditable(false);
            textField43.setEditable(false);
            textField53.setEditable(false);
            textField63.setEditable(false);
            textField73.setEditable(false);

            savebtn.setVisible(false);
            isDietitian();
        });
    }



    @FXML
    void backFunc(ActionEvent event) {

        String page = "Main Page";
        App.switchScreen(page);

    }
    @FXML
    void makeDiscount(ActionEvent event) {
        Platform.runLater(() -> {
            String input = discount.getText().trim();

            try {
                double percentage = Double.parseDouble(input);
                if (percentage < 0 || percentage > 100) {
                    errorLabel.setVisible(true);
                    errorLabel.setText("Enter value between 0 and 100 , Try Again !");
                }
                else {

                    for (Meal meal : Menu) {
                        double originalPrice = meal.getMealPrice();
                        double newPrice = originalPrice * (1 - percentage / 100);
                        meal.setMealPrice(Math.round(newPrice * 100.0) / 100.0);
                    }

                    menuOrder(Menu);
                    errorLabel.setVisible(false);
                    System.out.println("Discount applied to all meals.");
                }

            } catch (NumberFormatException e) {
                errorLabel.setVisible(true);
                errorLabel.setText("Enter a valid number");
            }
        });
    }
    private String mealName2 ;
    private int flag2=0 ;
    private String ingredients;

    @FXML
    void editIngredients1(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField11.setEditable(true);
            textField11.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients1.setVisible(false);
            mealName2 = textField10.getText();
            flag2 = 1;
        });
    }
    @FXML
    void editIngredients2(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients1.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField21.setEditable(true);
            textField21.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients2.setVisible(false);
            mealName2 = textField20.getText();
            flag2 = 2;
        });
    }
    @FXML
    void editIngredients3(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients2.setVisible(false);
            editIngredients1.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField31.setEditable(true);
            textField31.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients3.setVisible(false);
            mealName2 = textField30.getText();
            flag2 = 3;
        });
    }
    @FXML
    void editIngredients4(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients1.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField41.setEditable(true);
            textField41.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients4.setVisible(false);
            mealName2 = textField40.getText();
            flag2 = 4;
        });
    }
    @FXML
    void editIngredients5(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients1.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField51.setEditable(true);
            textField51.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients5.setVisible(false);
            mealName2 = textField50.getText();
            flag2 = 5;
        });
    }
    @FXML
    void editIngredients6(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients1.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField61.setEditable(true);
            textField61.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients6.setVisible(false);
            mealName2 = textField60.getText();
            flag2 = 6;
        });
    }
    @FXML
    void editIngredients7(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients1.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField71.setEditable(true);
            textField71.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients7.setVisible(false);
            mealName2 = textField70.getText();
            flag2 = 7;
        });
    }
    @FXML
    void editIngredients11(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients1.setVisible(false);
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField111.setEditable(true);
            textField111.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients11.setVisible(false);
            mealName2 = textField101.getText();
            flag2 = 8;
        });
    }
    @FXML
    void editIngredients22(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients1.setVisible(false);
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField211.setEditable(true);
            textField211.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients22.setVisible(false);
            mealName2 = textField201.getText();
            flag2 = 9;
        });
    }
    @FXML
    void editIngredients33(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients1.setVisible(false);
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients44.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField311.setEditable(true);
            textField311.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients33.setVisible(false);
            mealName2 = textField301.getText();
            flag2 = 10;
        });
    }
    @FXML
    void editIngredients44(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients1.setVisible(false);
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients55.setVisible(false);
            save2btn.setVisible(true);
            textField411.setEditable(true);
            textField411.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients44.setVisible(false);
            mealName2 = textField401.getText();
            flag2 = 11;
        });
    }
    @FXML
    void editIngredients55(ActionEvent event) {
        Platform.runLater(() -> {
            editIngredients1.setVisible(false);
            editIngredients2.setVisible(false);
            editIngredients3.setVisible(false);
            editIngredients4.setVisible(false);
            editIngredients5.setVisible(false);
            editIngredients6.setVisible(false);
            editIngredients7.setVisible(false);
            editIngredients22.setVisible(false);
            editIngredients11.setVisible(false);
            editIngredients33.setVisible(false);
            editIngredients44.setVisible(false);
            save2btn.setVisible(true);
            textField511.setEditable(true);
            textField511.setStyle("-fx-background-color: orange;-fx-border-color: blue");
            editIngredients55.setVisible(false);
            mealName2 = textField501.getText();
            flag2 = 12;
        });
    }

    @FXML
    void save2Func(ActionEvent event) {
        Platform.runLater(() -> {

            if (flag2 == 1) {
                ingredients = textField11.getText();
                textField11.setStyle("-fx-background-color: orange ; -fx-border-color: orange");
            }
            if (flag2 == 2) {
                ingredients = textField21.getText();
                textField21.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 3) {
                ingredients = textField31.getText();
                textField31.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 4) {
                ingredients = textField41.getText();
                textField41.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 5) {
                ingredients = textField51.getText();
                textField51.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 6) {
                ingredients = textField61.getText();
                textField61.setStyle("-fx-background-color: orange ; -fx-border-color: orange");
            }
            if (flag2 == 7) {
                ingredients = textField71.getText();
                textField71.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 8) {
                ingredients = textField111.getText();
                textField111.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 9) {
                ingredients = textField211.getText();
                textField211.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 10) {
                ingredients = textField311.getText();
                textField311.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 11) {
                ingredients = textField411.getText();
                textField411.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (flag2 == 12) {
                ingredients = textField511.getText();
                textField511.setStyle("-fx-background-color: orange ; -fx-border-color: orange");

            }
            if (ingredients.equals("")) {
                ingredients = " ";
            }
            try {
                Client.getClient().sendToServer("Update Ingredient " + "\"" + mealName2 + "\" " + "\"" + ingredients + "\"");
            } catch (Exception e) {
                e.printStackTrace();
            }
            textField111.setEditable(false);
            textField211.setEditable(false);
            textField311.setEditable(false);
            textField411.setEditable(false);
            textField511.setEditable(false);
            textField11.setEditable(false);
            textField21.setEditable(false);
            textField31.setEditable(false);
            textField41.setEditable(false);
            textField51.setEditable(false);
            textField61.setEditable(false);
            textField71.setEditable(false);

            save2btn.setVisible(false);
            isDietitian();
        });
    }
}
