// Updated TablesViewController based on the latest version provided by the user

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
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
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.Client.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class TablesViewController {

    int numOfImages = 3;
    Image[] images;
    int[] reservedTables;

    @FXML
    private ComboBox<String> TimeBox;

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
            default -> HAIFA_TABLES;
        };
    }
    @FXML
    void backButton(ActionEvent event) {
        App.switchScreen("Main Page");
    }


    @FXML
    void comboBoxChoice(ActionEvent event) {
        String selectedRestaurant = RestaurantCombo.getValue();
        if (selectedRestaurant != null) {
            requestRestaurantDetails(selectedRestaurant);
        }
    }


    private void requestRestaurantDetails(String restaurantName) {
        String message = "Get branch details;" + restaurantName;
        Client.getClient().sendToServer(message);
    }

    private void populateTimeBox(double openingTime, double closingTime) {
        TimeBox.getItems().clear();
        ObservableList<String> times = FXCollections.observableArrayList();
        int startMinutes = (int) (openingTime * 60);
        int endMinutes = (int) (closingTime * 60);  // Ending time is exclusive

        for (int minutes = startMinutes; minutes < endMinutes; minutes += 15) {
            int hours = minutes / 60;
            int mins = minutes % 60;
            String time = String.format("%02d:%02d", hours, mins);
            times.add(time);
        }
        TimeBox.setItems(times);
    }

    /*@FXML
    void comboBoxChoice(ActionEvent event) {
        Platform.runLater(() -> {
            String selectedRestaurant = RestaurantCombo.getValue();
            if (selectedRestaurant != null) {
                int restaurantId = switch (selectedRestaurant) {
                    case "Haifa Branch" -> 1;
                    case "Tel-Aviv Branch" -> 2;
                    case "Nahariya Branch" -> 3;
                    default -> 0;
                };

                // Set the image for the selected restaurant
                if (images != null && restaurantId > 0 && restaurantId <= images.length) {
                    imageView.setImage(images[restaurantId - 1]);
                }

                // Update the table positions based on the selected restaurant
                positionTables(restaurantId);
            }
        });
    }*/
    private void positionTables(int restaurantId) {
        Point[] selectedTables = getTableCoordinates(restaurantId);

        for (int i = 0; i < selectedTables.length && i < tableButtons.length; i++) {
            if (tableButtons[i] != null) {
                tableButtons[i].setLayoutX(selectedTables[i].x);
                tableButtons[i].setLayoutY(selectedTables[i].y);
                tableButtons[i].setVisible(true); // Ensure the buttons are visible
            }
        }

        // Hide any extra buttons that are not used in the selected restaurant
        for (int i = selectedTables.length; i < tableButtons.length; i++) {
            if (tableButtons[i] != null) {
                tableButtons[i].setVisible(false);
            }
        }
    }
    @FXML
    private void TimeBoxHandle(ActionEvent event) {
        String timeStr = TimeBox.getValue();
        String restaurantName = RestaurantCombo.getValue();
        int restaurantId = switch (restaurantName) {
            case "Haifa Branch" -> 1;
            case "Tel-Aviv Branch" -> 2;
            case "Nahariya Branch" -> 3;
            default -> 0;
        };

        if (restaurantId != 0 && timeStr != null) {
            // Send a request for reserved tables within ±1 hour of the selected time
            String message = "get_reserved_tables_within_hour;" + restaurantId + ";" + timeStr;
            Client.getClient().sendToServer(message);
        }
    }
    @FXML
    private void selectTable(ActionEvent event) {
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
    private void updateReservedTables(String tableNum) {
        // Convert tableNum to an integer
        int tableToModify = Integer.parseInt(tableNum);

        if (reservedTables == null) {
            reservedTables = new int[0]; // Initialize to an empty array if null
        }

        // Check if the table number exists in the array
        boolean exists = Arrays.stream(reservedTables).anyMatch(num -> num == tableToModify);

        if (exists) {
            // If it exists, remove it from the list (deselect)
            reservedTables = Arrays.stream(reservedTables)
                    .filter(num -> num != tableToModify)
                    .toArray();
        } else {
            // If not, add it to the list (select)
            int[] newReservedTables = Arrays.copyOf(reservedTables, reservedTables.length + 1);
            newReservedTables[reservedTables.length] = tableToModify;
            reservedTables = newReservedTables;
        }

        // Sort the array for consistent display
        Arrays.sort(reservedTables);
    }

    @FXML
    public void initialize() {
        System.out.println("Registering EventBus in TablesViewController...");
        EventBus.getDefault().register(this);
        System.out.println("EventBus registered.");
        requestAllRestaurants();
    }


    private void requestAllRestaurants() {
        String message = "get_all_restaurants";
        Client.getClient().sendToServer(message);
        System.out.println("Requested restaurant list from server.");
    }

    @Subscribe
    public void onRestaurantListReceived(List<Restaurant> restaurants) {
        Platform.runLater(() -> {
            if (restaurants != null && !restaurants.isEmpty()) {
                System.out.println("Populating combo box with " + restaurants.size() + " restaurants.");

                ObservableList<String> restaurantNames = FXCollections.observableArrayList();
                for (Restaurant restaurant : restaurants) {
                    System.out.println("Adding to combo box: " + restaurant.getName());
                    restaurantNames.add(restaurant.getName());
                }
                RestaurantCombo.setItems(restaurantNames);
                System.out.println("Successfully populated combo box.");
            } else {
                System.out.println("No restaurants to populate.");
            }
        });
    }




    public void cleanup() {
        EventBus.getDefault().unregister(this);
        System.out.println("EventBus unregistered from TablesViewController.");
    }


    @Subscribe
    public void onRestaurantDetailsReceived(Restaurant restaurant) {
        Platform.runLater(() -> {
            populateTimeBox(restaurant.getOpeningTime(), restaurant.getClosingTime());
        });
    }
}
