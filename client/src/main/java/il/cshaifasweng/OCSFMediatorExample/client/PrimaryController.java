package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;

public class PrimaryController {

    int numOfImages = 3;
    Image[] images;

    @FXML
    private ComboBox<String> ChooseRestaurantBox;

    @FXML
    private Text ComboChoiceRequestLabel;

    @FXML
    private Button PersonalAreaButton;

    @FXML
    private Button feedbackButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button logOutButton;

    @FXML
    private Button menuButton;

    @FXML
    private Button orderButton;

    @FXML
    private Text RestaurantDetailsInstructionLabel;

    void updateResInterest(){
        Platform.runLater(() -> {
            String selectedRestaurant = ChooseRestaurantBox.getValue();
            if(selectedRestaurant != null){
                userAtt.setRestaurantInterest((short) switch (selectedRestaurant) {
                            case "Haifa Branch" -> 1;
                            case "Tel-Aviv Branch" -> 2;
                            case "Nahariya Branch" -> 3;
                            default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                        }
                );
            }
        });
    }

    @FXML
    void switchPage(ActionEvent event) {
        Platform.runLater(() -> {
            String page = "";
            Button sourceButton = (Button) event.getSource();

            // Check which button triggered the event
            if (sourceButton == menuButton && ChooseRestaurantBox.getValue().equals("Haifa Branch")) {
                MenuController.setRestaurantInterest(1);
                page = "Menu Page";

            }
            else  if (sourceButton == menuButton && ChooseRestaurantBox.getValue().equals("Tel-Aviv Branch")) {
                MenuController.setRestaurantInterest(2);
                page = "Menu Page";
            }
            else  if (sourceButton == menuButton && ChooseRestaurantBox.getValue().equals("Nahariya Branch")) {
                MenuController.setRestaurantInterest(3);
                page = "Menu Page";
            }

            else if (sourceButton == PersonalAreaButton) {
                page = "Personal Area Page";
            }

            else if(sourceButton == feedbackButton&& ChooseRestaurantBox.getValue().equals("Haifa Branch")){
                MenuController.setRestaurantInterest(1);
                page = "Feedback Page";
            }
            else if(sourceButton == feedbackButton&& ChooseRestaurantBox.getValue().equals("Tel-Aviv Branch")){
                MenuController.setRestaurantInterest(2);
                page = "Feedback Page";
            }
            else if(sourceButton == feedbackButton&& ChooseRestaurantBox.getValue().equals("Nahariya Branch")){
                MenuController.setRestaurantInterest(3);
                page = "Feedback Page";
            }

            else if(sourceButton == orderButton){
                page = "Order Tables Page";
            }
            if(userAtt.getUsername().equals("Customer") || userAtt.getPermissionLevel() == 4){
                String selectedRestaurant = ChooseRestaurantBox.getValue();
                if(selectedRestaurant != null){
                    userAtt.setRestaurantInterest((short) switch (selectedRestaurant) {
                                case "Haifa Branch" -> 1;
                                case "Tel-Aviv Branch" -> 2;
                                case "Nahariya Branch" -> 3;
                                default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                            }
                    );
                }
            }
            updateResInterest();
            App.switchScreen(page);
        });
    }

    @FXML
    void toBranchPage(MouseEvent event){
        Platform.runLater(() -> {
            if(userAtt.getUsername().equals("Customer") || userAtt.getPermissionLevel() == 4){
                String selectedRestaurant = ChooseRestaurantBox.getValue();
                if(selectedRestaurant != null){
                    userAtt.setRestaurantInterest((short) switch (selectedRestaurant) {
                                case "Haifa Branch" -> 1;
                                case "Tel-Aviv Branch" -> 2;
                                case "Nahariya Branch" -> 3;
                                default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                            }
                    );
                }
            }
            updateResInterest();
            App.switchScreen("Branch Page");
        });
    }

    @FXML
    private void logOut(ActionEvent event) {
        Platform.runLater(() -> {
            Client client = Client.getClient();
            String username = getClientUsername();
            if (username != null && !username.equals("Customer")) {
                client.sendToServer("log out;" + username);
            }
            resetClientAttributes();
            App.switchScreen("Log In Page");
        });
    }

    @FXML
    void comboBoxChoice(){
        Platform.runLater(() -> {
            String selectedRestaurant = ChooseRestaurantBox.getValue();

            if (selectedRestaurant != null) {
                ComboChoiceRequestLabel.setVisible(false); // making the label that tells the user to choose a restaurant to invisible
                RestaurantDetailsInstructionLabel.setVisible(true);
                orderButton.setDisable(false);
                feedbackButton.setDisable(false);
                menuButton.setDisable(false);
                imageView.setVisible(true);

                switch (selectedRestaurant) {
                    case "Haifa Branch":
                        imageView.setImage(images[0]);
                        break;
                    case "Tel-Aviv Branch":
                        imageView.setImage(images[1]);
                        break;
                    case "Nahariya Branch":
                        imageView.setImage(images[2]);
                        break;
                }
            }
        });
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            int user_permission = userAtt.getPermissionLevel();
            feedbackButton.setVisible((user_permission == 0) || (user_permission == 4));

            RestaurantDetailsInstructionLabel.setVisible(false);
            orderButton.setDisable(true);
            feedbackButton.setDisable(true);
            menuButton.setDisable(true);
            imageView.setVisible(false);

            try {
                images = new Image[numOfImages];

                for (int i = 0; i < numOfImages; i++) {
                    images[i] = new Image(String.valueOf(PrimaryController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Maps/" + i + ".jpg")));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ObservableList<String> restaurantList = FXCollections.observableArrayList(
                    "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch"
            );

            ChooseRestaurantBox.setItems(restaurantList);

            if (userAtt.getPermissionLevel() == 0) {
                logOutButton.setVisible(true);
                logOutButton.setText("Back");
                PersonalAreaButton.setVisible(false);
            } else {
                logOutButton.setVisible(true);
                logOutButton.setText("Log Out");
                PersonalAreaButton.setVisible(true);
            }
        });

    }
}
