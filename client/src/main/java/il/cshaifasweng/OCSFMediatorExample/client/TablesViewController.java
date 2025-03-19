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
import java.awt.Point;
import java.util.Arrays;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class TablesViewController {

    int numOfImages = 3;
    Image[] images;
    int[] reservedTables;

    @FXML
    private Pane DetailsPane;

    @FXML
    private TextField GuestsNumTextField;

    @FXML
    private TextField NameTextField;

    @FXML
    private TextField NoteTextField;

    @FXML
    private ComboBox<String> RestaurantCombo;

    @FXML
    private Button TableButton1;

    @FXML
    private Button TableButton10;

    @FXML
    private Button TableButton11;

    @FXML
    private Button TableButton12;

    @FXML
    private Button TableButton13;

    @FXML
    private Button TableButton14;

    @FXML
    private Button TableButton2;

    @FXML
    private Button TableButton3;

    @FXML
    private Button TableButton4;

    @FXML
    private Button TableButton5;

    @FXML
    private Button TableButton6;

    @FXML
    private Button TableButton7;

    @FXML
    private Button TableButton8;

    @FXML
    private Button TableButton9;

    @FXML
    private TextField TableNumTextField;

    @FXML
    private ImageView imageView;

    private Button[] tableButtons;

    private static final Point[] HAIFA_TABLES = {
            new Point(38, 56), new Point(106, 56), new Point(36, 139), new Point(110, 139),
            new Point(35, 226), new Point(108, 227), new Point(249, 233), new Point(304, 233),
            new Point(23, 322), new Point(70, 325), new Point(126, 323), new Point(235, 325),
            new Point(291, 325)
    };

    private static final Point[] TEL_AVIV_TABLES = {
            new Point(116, 40), new Point(115, 133), new Point(276, 167), new Point(274, 242),
            new Point(121, 315), new Point(230, 314), new Point(29, 60), new Point(35, 312)
    };

    private static final Point[] NAHARIYA_TABLES = {
            new Point(20, 37), new Point(77, 36), new Point(20, 139), new Point(75, 139),
            new Point(239, 162), new Point(305, 164), new Point(20, 240), new Point(75, 240),
            new Point(236, 252), new Point(298, 253), new Point(20, 338), new Point(73, 339),
            new Point(241, 339), new Point(301, 337)
    };

    public static Point[] getTableCoordinates(int restaurantId) {
        return switch (restaurantId) {
            case 2 -> TEL_AVIV_TABLES;
            case 3 -> NAHARIYA_TABLES;
            default -> HAIFA_TABLES; // for case 1 and for other cases because it's the default for the manager
        };
    }

    public void updateReservedTables(String tableNum) {
        // Convert tableNum to an integer
        int tableToModify = Integer.parseInt(tableNum);

        if (reservedTables == null) {
            reservedTables = new int[0]; // Initialize to an empty array if null
        }
        // Check if the number exists in the array
        boolean exists = Arrays.stream(reservedTables).anyMatch(num -> num == tableToModify);

        if (exists) {
            // If exists, remove it
            reservedTables = Arrays.stream(reservedTables)
                    .filter(num -> num != tableToModify)
                    .toArray();
        } else {
            // If not exists, add it
            int[] newReservedTables = Arrays.copyOf(reservedTables, reservedTables.length + 1);
            newReservedTables[reservedTables.length] = tableToModify;
            reservedTables = newReservedTables;
        }

        // Sort the array
        Arrays.sort(reservedTables);
    }


    @FXML
    void backButton(ActionEvent event){
        App.switchScreen("Order Tables Page");
    }

    @FXML
    void comboBoxChoice(){
        Platform.runLater(() -> {
            resetInfo();

            String selectedRestaurant = RestaurantCombo.getValue();
            if (selectedRestaurant != null) {

                int j = 0;
                int restaurantId = 0;

                switch (selectedRestaurant) {
                    case "Haifa Branch":
                        imageView.setImage(images[0]);
                        j = 13;
                        restaurantId = 1;
                        break;
                    case "Tel-Aviv Branch":
                        imageView.setImage(images[1]);
                        j = 8;
                        restaurantId = 2;
                        break;
                    case "Nahariya Branch":
                        imageView.setImage(images[2]);
                        j = 14;
                        restaurantId = 3;
                        break;
                }
                for (int i = 0; i < j; i++){
                    tableButtons[i].setVisible(true);
                }
                for (int i = j; i < 14; i++){
                    tableButtons[i].setVisible(false);
                }
                positionTables(restaurantId);

            }
        });
    }

    private void positionTables(int restaurantId) {
        Point[] selectedTables = getTableCoordinates(restaurantId);

        for (int i = 0; i < selectedTables.length && i < tableButtons.length; i++) {
            if (tableButtons[i] != null) {
                tableButtons[i].setLayoutX(selectedTables[i].x);
                tableButtons[i].setLayoutY(selectedTables[i].y);
            }
        }
    }


    @FXML
    void selectTable(ActionEvent event){
        Platform.runLater(() -> {
            Button sourceButton = (Button) event.getSource();
            String buttonId = sourceButton.getId(); // Get the fx:id of the clicked button

            // Extract the number part from the fx:id
            String tableNum = buttonId.replace("TableButton", "");

            DetailsPane.setDisable(false);
            String currentStyle = sourceButton.getStyle();
            if (currentStyle.contains("green")) {
                sourceButton.setStyle("-fx-background-color: transparent;");
            } else {
                sourceButton.setStyle("-fx-background-color: green;");
            }
            updateReservedTables(tableNum);
            // Convert array to comma-separated string and update the text field
            String reservedTablesString = Arrays.stream(reservedTables)
                    .mapToObj(String::valueOf) // Convert each int to a String
                    .collect(Collectors.joining(",")); // Join with commas

            TableNumTextField.setText(reservedTablesString);
        });
    }

    @FXML
    void resetInfo(){
        DetailsPane.setDisable(true);

        GuestsNumTextField.setText("");
        NameTextField.setText("");
        NoteTextField.setText("");
        TableNumTextField.setText("");

        for(int i = 0; i < 14; i++){
            tableButtons[i].setStyle("-fx-background-color: transparent;");
        }
    }

    @FXML
    void initialize() {
        Platform.runLater(() -> {
            DetailsPane.setDisable(true);
            int employee_permission = userAtt.getPermissionLevel();
            // For manager and service
            if (!(employee_permission == 4 || employee_permission == 2)) {
                RestaurantCombo.setVisible(false);
            } else {
                RestaurantCombo.setVisible(true);
                ObservableList<String> restaurantList = FXCollections.observableArrayList(
                        "Haifa Branch", "Tel-Aviv Branch", "Nahariya Branch"
                );

                RestaurantCombo.setItems(restaurantList);
                RestaurantCombo.setValue(restaurantList.get(userAtt.getRestaurantInterest()-1));
            }

            tableButtons = new Button[14];

            tableButtons[0] = TableButton1;
            tableButtons[1] = TableButton2;
            tableButtons[2] = TableButton3;
            tableButtons[3] = TableButton4;
            tableButtons[4] = TableButton5;
            tableButtons[5] = TableButton6;
            tableButtons[6] = TableButton7;
            tableButtons[7] = TableButton8;
            tableButtons[8] = TableButton9;
            tableButtons[9] = TableButton10;
            tableButtons[10] = TableButton11;
            tableButtons[11] = TableButton12;
            tableButtons[12] = TableButton13;
            tableButtons[13] = TableButton14;

            int restaurantId = userAtt.getRestaurantInterest();
            positionTables(restaurantId); // Set the appropriate tables positions

            switch (restaurantId) {
                case 2:
                    TableButton9.setVisible(false);
                    TableButton10.setVisible(false);
                    TableButton11.setVisible(false);
                    TableButton12.setVisible(false);
                    TableButton13.setVisible(false);
                    TableButton14.setVisible(false);
                    break;
                case 1:
                    TableButton14.setVisible(false);
                    break;
            }
            try {
                images = new Image[numOfImages];

                for (int i = 0; i < numOfImages; i++) {
                    images[i] = new Image(String.valueOf(PrimaryController.class.getResource("/il/cshaifasweng/OCSFMediatorExample/client/Restaurant_Images/" + i + ".jpg")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (employee_permission == 4){
                imageView.setImage(images[0]);
            }
            else {
                imageView.setImage(images[restaurantId - 1]);
            }

        });
    }
}