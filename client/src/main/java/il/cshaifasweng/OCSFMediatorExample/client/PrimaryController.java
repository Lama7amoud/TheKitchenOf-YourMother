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
    private Button complaintButton;

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


    void updateResInterest() {
        Platform.runLater(() -> {
            String selectedRestaurant = ChooseRestaurantBox.getValue();
            if (selectedRestaurant != null) {
                short restaurantId = switch (selectedRestaurant) {
                    case "Haifa Branch" -> 1;
                    case "Tel-Aviv Branch" -> 2;
                    case "Nahariya Branch" -> 3;
                    default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                };
                userAtt.setRestaurantInterest(restaurantId);
                System.out.println("Saved restaurant in userAtt: " + restaurantId);
            }
        });
    }


    @FXML
    void switchPage(ActionEvent event) {
        Platform.runLater(() -> {
            String page = "";
            Button sourceButton = (Button) event.getSource();
            String selectedRestaurant = ChooseRestaurantBox.getValue();

            // Check which button triggered the event
            if (sourceButton == menuButton) {
                if (selectedRestaurant != null) {
                    int restaurantId = switch (selectedRestaurant) {
                        case "Haifa Branch" -> 1;
                        case "Tel-Aviv Branch" -> 2;
                        case "Nahariya Branch" -> 3;
                        default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                    };
                    userAtt.setRestaurantInterest((short) restaurantId); // Save the restaurant in userAtt
                    MenuController.setRestaurantInterest(restaurantId); // Set in MenuController for compatibility
                    page = "Menu Page";
                }
            } else if (sourceButton == PersonalAreaButton) {
                page = "Personal Area Page";
            } else if (sourceButton == feedbackButton) {
                if (selectedRestaurant != null) {
                    int restaurantId = switch (selectedRestaurant) {
                        case "Haifa Branch" -> 1;
                        case "Tel-Aviv Branch" -> 2;
                        case "Nahariya Branch" -> 3;
                        default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                    };
                    userAtt.setRestaurantInterest((short) restaurantId); // Save the restaurant in userAtt
                    page = "Feedback Page";
                }
            } else if (sourceButton == complaintButton) {
                if (selectedRestaurant != null) {
                    int restaurantId = switch (selectedRestaurant) {
                        case "Haifa Branch" -> 1;
                        case "Tel-Aviv Branch" -> 2;
                        case "Nahariya Branch" -> 3;
                        default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                    };
                    userAtt.setRestaurantInterest((short) restaurantId);
                    page = "Complaint Page";
                }

            } else if (sourceButton == orderButton) {
                page = "TakeAwayOrReservation Page";
            }


            if (userAtt.getUsername().equals("Customer") || userAtt.getPermissionLevel() == 4) {
                if (selectedRestaurant != null) {
                    int restaurantId = switch (selectedRestaurant) {
                        case "Haifa Branch" -> 1;
                        case "Tel-Aviv Branch" -> 2;
                        case "Nahariya Branch" -> 3;
                        default -> throw new IllegalArgumentException("Unknown restaurant: " + selectedRestaurant);
                    };
                    userAtt.setRestaurantInterest((short) restaurantId); // Save the restaurant in userAtt
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
    void comboBoxChoice() {
        Platform.runLater(() -> {
            String selectedRestaurant = ChooseRestaurantBox.getValue();

            if (selectedRestaurant != null) {
                ComboChoiceRequestLabel.setVisible(false);
                RestaurantDetailsInstructionLabel.setVisible(true);
                orderButton.setDisable(false);
                feedbackButton.setDisable(false);
                complaintButton.setDisable(false);
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
            feedbackButton.setVisible((user_permission == 0));
            complaintButton.setVisible(true);
            RestaurantDetailsInstructionLabel.setVisible(false);
            orderButton.setDisable(true);
            feedbackButton.setDisable(true);

            complaintButton.setDisable(true);

            menuButton.setDisable(true);
            imageView.setVisible(false);

            try {
                images = new Image[numOfImages];
                for (int i = 0; i < numOfImages; i++) {
                    images[i] = new Image(String.valueOf(PrimaryController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Maps/" + i + ".jpg")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ObservableList<String> restaurantList = FXCollections.observableArrayList(
                    "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch"
            );

            ChooseRestaurantBox.setItems(restaurantList);
            int employee_permission = userAtt.getPermissionLevel();
            if((employee_permission == 1 || employee_permission == 3)){
                ChooseRestaurantBox.setValue(restaurantList.get(userAtt.getRestaurantId()-1));
                ChooseRestaurantBox.setDisable(true);
            }

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
