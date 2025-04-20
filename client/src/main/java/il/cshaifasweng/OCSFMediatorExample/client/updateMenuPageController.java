package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;

import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class updateMenuPageController {

    @FXML
    private Label helloTitleLabel;

    @FXML
    private Label managerTypeLabel;

    @FXML
    private Button addMealButton;

    @FXML
    private TextField addMealName;

    @FXML
    private TextField mealPreferences;

    @FXML
    private TextField mealDescription;

    @FXML
    private TextField mealCategory;

    @FXML
    private TextField mealPrice;

    @FXML
    private TextField imagePath;



    @FXML
    private TextField oldCategory;

    @FXML
    private TextField newCategory;






    @FXML
    private Button removeMealButton;

    @FXML
    private TextField removeMealName;

    @FXML
    private Button changeButton;

    @FXML
    private TextField changeCategoryMealName;




    String[] restaurantTitles = {"Haifa", "Tel-Aviv", "Nahariya"};

    @FXML
    void initialize(){
        Platform.runLater(() -> {
            AuthorizedUser user = Client.getClientAttributes();
            helloTitleLabel.setText("Hello " + user.getFirstname());
            managerTypeLabel.setText("Dietitian");
            //pageTitleLabel.setText("Dietitian Page");
            //fromChooseBox.getItems().addAll("shared meal", "special1", "special2", "special3");
            //toChooseBox.getItems().addAll("shared meal", "special1", "special2", "special3");

        });
    }
    @FXML
    void backFunc(ActionEvent event) {
        String page = "Personal Area Page";
        App.switchScreen(page);

    }
    @FXML
    void get_haifa_menu_func(ActionEvent event) {
        String page = "Haifa Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);

    }

    @FXML
    void get_telaviv_menu_func(ActionEvent event) {
        String page = "TelAviv Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);

    }


    @FXML
    void get_nahariya_menu_func(ActionEvent event) {
        String page = "Nahariya Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);
    }

    @FXML
    void add_meal_func(ActionEvent event) {

        String name = addMealName.getText();
        String description = mealDescription.getText();
        String preferences = mealPreferences.getText();
        double price = Double.parseDouble(mealPrice.getText());
        String inputImagePath = imagePath.getText().trim(); // full path from user

        Client client = Client.getClient();

        String category = "";
        if (mealCategory.getText().equals("Haifa")) {
            category = "special1";
        } else if (mealCategory.getText().equals("Tel-Aviv")) {
            category = "special2";
        } else if (mealCategory.getText().equals("Nahariya")) {
            category = "special3";
        } else if (mealCategory.getText().equals("All")) {
            category = "shared meal";
        }

        // Path to meals folder inside your project
        File destinationDir = new File("C:\\Users\\lamah\\IdeaProjects\\TheKitchenOf-YourMotherJDED\\client\\src\\main\\resources\\il\\cshaifasweng\\OCSFMediatorExample\\client\\meals");
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        File sourceFile = new File(inputImagePath);
        if (!sourceFile.exists()) {
            System.out.println("❌ Image file does not exist at: " + inputImagePath);
            return;
        }

        // Only use the file name, not full path
        String imageFileName = sourceFile.getName();
        File destinationFile = new File(destinationDir, imageFileName);

        try {
            // Copy the image to your project meals folder
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Image copied to: " + destinationFile.getAbsolutePath());
            imageFileName = "/il/cshaifasweng/OCSFMediatorExample/client/meals/" + imageFileName;

            // Send the filename only (the path in the project will be resolved later)
            String messageToSend = String.format("Add Meal \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", name, description, preferences, price, imageFileName, category
            );
            client.sendToServer(messageToSend);
            System.out.println("✅ Sent to server: " + messageToSend);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Failed to copy image.");
        }
    }


    @FXML
    void remove_meal_func(ActionEvent event) {
        String mealName = removeMealName.getText();
        try {
            String messageToSend = String.format("Remove Meal \"%s\"", mealName);
            Client.getClient().sendToServer(messageToSend);
            System.out.println("Sent to server: " + messageToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void change_category_func(ActionEvent event) {
        String from = "";
        String to = "";
        String old = oldCategory.getText();
        String new1 = newCategory.getText();
        if(old.equals("Haifa")) {
            from = "special1";
        }
        else if(old.equals("Tel-Aviv")) {
            from = "special2";
        }
        else if(old.equals("Nahariya")) {
            from = "special3";
        }
        else if (old.equals("All"))
        {
            from = "shared meal";
        }

        if(new1.equals("Haifa")) {
            to = "special1";
        }
        else if(new1.equals("Tel-Aviv")) {
            to = "special2";
        }
        else if(new1.equals("Nahariya")) {
            to = "special3";
        }
        else if(new1.equals("All"))
        {
            to = "shared meal";
        }


        String Name = changeCategoryMealName.getText();

        try {
            String messageToSend = String.format("Change Category Meal \"%s\" \"%s\" \"%s\"", Name,from,to);
            Client.getClient().sendToServer(messageToSend);
            System.out.println("Sent to server: " + messageToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
