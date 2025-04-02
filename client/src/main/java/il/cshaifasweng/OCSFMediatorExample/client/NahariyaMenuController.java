package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NahariyaMenuController {


    @FXML
    private Button editMeal1;

    @FXML
    private GridPane menuGrid;


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
    private TextField textField24;

    @FXML
    private TextField textField30;

    @FXML
    private TextField textField31;

    @FXML
    private TextField textField32;

    @FXML
    private TextField textField33;

    @FXML
    private TextField textField34;

    @FXML
    private TextField textField40;

    @FXML
    private TextField textField41;

    @FXML
    private TextField textField42;

    @FXML
    private TextField textField43;

    @FXML
    private TextField textField44;

    @FXML
    private TextField textField50;

    @FXML
    private TextField textField51;

    @FXML
    private TextField textField52;

    @FXML
    private TextField textField53;

    @FXML
    private TextField textField54;

    @FXML
    private TextField textField60;

    @FXML
    private TextField textField61;

    @FXML
    private TextField textField62;

    @FXML
    private TextField textField63;

    @FXML
    private TextField textField64;

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
    private GridPane menuGrid1;


    @FXML
    private TextField textField001;


    @FXML
    private TextField textField011;


    @FXML
    private TextField textField021;


    @FXML
    private TextField textField031;

    @FXML
    private TextField textField04;

    @FXML
    private TextField textField041;


    @FXML
    private TextField textField101;

    @FXML
    private TextField textField10112;

    @FXML
    private TextField textField101121;


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
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        Client client = Client.getClient();
        client.sendToServer("Request Nahariya menu");

    }



    private List<Meal> Menu ;
    private List<Image> images ;

    @Subscribe
    public void ExternalIntervention(Object msg) {
        Platform.runLater(() -> {
            try {
                if (msg instanceof List) {
                    Menu = (List<Meal>) msg;
                    menuOrder(Menu);
                    imagesOrder(Menu);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
    private List<Meal> special3List(List<Meal>Menu) {
        crt2=0;
        List<Meal> special3List = new ArrayList<>();
        for (Meal meal : Menu) {
            if (meal.getMealCategory().equals("special3")) {
                special3List.add(meal);
                crt2++;
            }

        }
        return special3List;
    }

    private void menuOrder(List<Meal>Menu) {
        Platform.runLater(() -> {
            List<Meal> sharedList = sharedList(Menu);
            List<Meal> special3List = special3List(Menu);
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
                    textField101.setText(special3List.get(0).getMealName());
                    textField111.setText(special3List.get(0).getMealDescription());
                    textField121.setText(special3List.get(0).getMealPreferences());
                    textField131.setText(String.valueOf(special3List.get(0).getMealPrice()));
                }
                if (i==1) {
                    textField201.setText(special3List.get(1).getMealName());
                    textField211.setText(special3List.get(1).getMealDescription());
                    textField221.setText(special3List.get(1).getMealPreferences());
                    textField231.setText(String.valueOf(special3List.get(1).getMealPrice()));
                }
                if (i==2) {
                    textField301.setText(special3List.get(2).getMealName());
                    textField311.setText(special3List.get(2).getMealDescription());
                    textField321.setText(special3List.get(2).getMealPreferences());
                    textField331.setText(String.valueOf(special3List.get(2).getMealPrice()));

                }
                if (i==3) {
                    textField401.setText(special3List.get(3).getMealName());
                    textField411.setText(special3List.get(3).getMealDescription());
                    textField421.setText(special3List.get(3).getMealPreferences());
                    textField431.setText(String.valueOf(special3List.get(3).getMealPrice()));
                }
                if (i==4) {
                    textField501.setText(special3List.get(4).getMealName());
                    textField511.setText(special3List.get(4).getMealDescription());
                    textField521.setText(special3List.get(4).getMealPreferences());
                    textField531.setText(String.valueOf(special3List.get(4).getMealPrice()));
                }

            }
        });

    }

    private void imagesOrder(List<Meal> Menu) {

        Platform.runLater(() -> {
            List<Meal> sharedList = sharedList(Menu);
            List<Meal> special3List = special3List(Menu);
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
                    String imagePath1 = special3List.get(0).getImagePath();
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
                    String imagePath2 = special3List.get(1).getImagePath();
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
                    String imagePath3 = special3List.get(2).getImagePath();
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
                    String imagePath4 = special3List.get(3).getImagePath();
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
                    String imagePath5 = special3List.get(4).getImagePath();
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
    void edit1(ActionEvent event) {
        Platform.runLater(() -> {
            textField13.setEditable(true);
            textField13.setStyle("-fx-background-color: #D3D3D3 ;");
            editMeal1.setDisable(true);
            mealName = textField10.getText();
            flag = 1;
        });
    }





}
