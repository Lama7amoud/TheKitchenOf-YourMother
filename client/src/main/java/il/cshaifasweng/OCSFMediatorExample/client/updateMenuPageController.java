package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import javafx.scene.control.Button;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.stream.Collectors;


public class updateMenuPageController {
    @FXML
    private Button chooseImageButton;



    @FXML
    void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Meal Image");

        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());

        if (selectedFile != null) {
            imagePath.setText(selectedFile.getAbsolutePath());

        }
    }

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
    private TextField discountValue;

    @FXML
    private TextField oldCategoryField;

    @FXML
    private ComboBox<String> toCategoryCombo;

    @FXML
    private Button checkCategoryButton;
    @FXML
    private TextField fromCategoryField;

    @FXML
    private Button changeButton;

    @FXML
    private TextField changeCategoryMealName;

    @FXML
    private ComboBox<String> comboDiscount;

    @FXML
    private ComboBox<String> mealCategoryCombo;





    private final List<String> allCategories = List.of("Haifa", "Tel-Aviv", "Nahariya", "All");

    String[] restaurantTitles = {"Haifa", "Tel-Aviv", "Nahariya"};

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        Platform.runLater(() -> {
            comboDiscount.getItems().addAll(
                    "shared meals",
                    "specials of Haifa",
                    "specials of Tel-Aviv",
                    "specials of Nahariya"
            );

            mealCategoryCombo.getItems().addAll("Haifa", "Tel-Aviv", "Nahariya", "All");

            AuthorizedUser user = Client.getClientAttributes();
            helloTitleLabel.setText("Hello " + user.getFirstname());
            managerTypeLabel.setText("Dietitian");
            Client.getClient().setUpdateMenuController(this);

        });
    }


    @FXML
    void backFunc(ActionEvent event) {
        String page = "Personal Area Page";
        App.switchScreen(page);

    }

    @FXML
    void get_haifa_menu_func(ActionEvent event) {
        MenuController.setRestaurantInterest(1);
        String page = "Menu Page";

        //String page = "Personal Area Page";
        App.switchScreen(page);

    }
    @FXML
    void get_telaviv_menu_func(ActionEvent event) {
        MenuController.setRestaurantInterest(2);
        String page = "Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);

    }
    @FXML
    void get_nahariya_menu_func(ActionEvent event) {
        MenuController.setRestaurantInterest(3);
        String page = "Menu Page";
        //String page = "Personal Area Page";
        App.switchScreen(page);
    }

    @FXML
    void add_meal_func(ActionEvent event) {
        String Name = addMealName.getText().trim();
        String Description = mealDescription.getText().trim();
        String Preferences = mealPreferences.getText().trim();
        //String Image = imagePath.getText().trim();
        String selectedCategory = mealCategoryCombo.getValue();  // ComboBox instead of TextField
        String inputImagePath = imagePath.getText().trim();


        if (Name.isEmpty() || Description.isEmpty() || Preferences.isEmpty() ||
                inputImagePath.isEmpty() || selectedCategory == null || mealPrice.getText().isEmpty()) {
            showAlert("All fields must be filled to add a meal!");
            return;
        }

        if (!isValidPreferencesFormat(Preferences)) {
            showAlert("Preferences must contain parts separated by commas, with no extra or missing commas.");
            return;
        }

        double Price;
        try {
            Price = Double.parseDouble(mealPrice.getText());
            if (Price < 0) {
                showAlert("Price must be non-negative!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid price format!");
            return;
        }


        // Convert category name
        String Category = switch (selectedCategory) {
            case "Haifa" -> "special1";
            case "Tel-Aviv" -> "special2";
            case "Nahariya" -> "special3";
            case "All" -> "shared meal";
            default -> "";
        };
// Path to meals folder inside your project
        File destinationDir = new File("C:\\Users\\lamah\\IdeaProjects\\TheKitchenOf-YourMotherm3odkan\\client\\src\\main\\resources\\il\\cshaifasweng\\OCSFMediatorExample\\client\\meals");
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
            String messageToSend = String.format("Add Meal \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"",
                    Name, Description, Preferences, Price, imageFileName, Category);
            Client.getClient().sendToServer(messageToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Clear fields
        addMealName.clear();
        mealDescription.clear();
        mealPreferences.clear();
        imagePath.clear();
        mealPrice.clear();
        mealCategoryCombo.getSelectionModel().clearSelection();
    }

    @FXML
    void change_category_func(ActionEvent event) {
        String name = changeCategoryMealName.getText().trim();
        String from = fromCategoryField.getText().trim();
        String to = toCategoryCombo.getValue();

        if (name.isEmpty()) {
            showAlert("Please enter the meal name.");
            return;
        }

        if (from.isEmpty()) {
            showAlert("Please check the current category first.");
            return;
        }

        if (to == null || to.isEmpty()) {
            showAlert("Please select a new category from the combo box.");
            return;
        }

        // Convert display values to backend values
        from = switch (from) {
            case "Haifa" -> "special1";
            case "Tel-Aviv" -> "special2";
            case "Nahariya" -> "special3";
            case "All" -> "shared meal";
            default -> "";
        };

        to = switch (to) {
            case "Haifa" -> "special1";
            case "Tel-Aviv" -> "special2";
            case "Nahariya" -> "special3";
            case "All" -> "shared meal";
            default -> "";
        };

        try {
            String messageToSend = String.format("Change Category Meal \"%s\" \"%s\" \"%s\"", name, from, to);
            Client.getClient().sendToServer(messageToSend);
            System.out.println("Sent to server: " + messageToSend);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to send category change request.");
        }
    }



    @Subscribe
    public void handleCategoryString(Object msg) {
        if (msg instanceof String) {
            String category = (String) msg;

            if (!category.isEmpty()) {
                Platform.runLater(() -> {
                    String displayCategory;

                    switch (category) {
                        case "shared meal":
                            displayCategory = "All";
                            break;
                        case "special1":
                            displayCategory = "Haifa";
                            break;
                        case "special2":
                            displayCategory = "Tel-Aviv";
                            break;
                        case "special3":
                            displayCategory = "Nahariya";
                            break;
                        default:
                            displayCategory = "Unknown";
                            break;
                    }

                    oldCategory.setText(displayCategory);

                    toCategoryCombo.getItems().setAll(
                            allCategories.stream()
                                    .filter(cat -> !cat.equals(displayCategory))
                                    .collect(Collectors.toList())
                    );
                });
            }
        }
    }

    @FXML
    void checkMealCategory(ActionEvent event) {
        String mealName = changeCategoryMealName.getText().trim();
        if (mealName.isEmpty()) {
            showAlert("Please enter a meal name to check.");
            return;
        }
        try {
            String msg = "RequestMealCategory \"" + mealName + "\"";
            Client.getClient().sendToServer(msg);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to contact the server.");
        }
    }


    @FXML
    void discount_func(ActionEvent event) {
        String discountText = discountValue.getText().trim();
        String category = comboDiscount.getValue();

        if (discountText.isEmpty() || category == null) {
            showAlert("Please fill in the discount value and select a category.");
            return;
        }

        double discount;
        try {
            discount = Double.parseDouble(discountText);
            if (discount < 1 || discount > 100) {
                showAlert("Discount must be between 1 and 100!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid discount number format!");
            return;
        }

        // Convert category
        if (category.equals("shared meals")) category = "shared meal";
        else if (category.equals("specials of Haifa")) category = "special1";
        else if (category.equals("specials of Tel-Aviv")) category = "special2";
        else if (category.equals("specials of Nahariya")) category = "special3";

        try {
            String message = "Update discount \"" + discount + "\" \"" + category + "\"";
            Client.getClient().sendToServer(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Clear fields
        discountValue.clear();
        comboDiscount.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void handleMealCategoryResponse(String category) {
        Platform.runLater(() -> {
            String displayCategory = switch (category) {
                case "special1" -> "Haifa";
                case "special2" -> "Tel-Aviv";
                case "special3" -> "Nahariya";
                case "shared meal" -> "All";
                default -> "Unknown";
            };

            fromCategoryField.setText(displayCategory);

            toCategoryCombo.getItems().setAll(
                    allCategories.stream()
                            .filter(cat -> !cat.equals(displayCategory))
                            .collect(Collectors.toList())
            );
        });
    }

    public void handleMealNotFound() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Meal Not Found");
            alert.setHeaderText(null);
            alert.setContentText("No meal with this name exists in the database.");
            alert.showAndWait();
        });
    }

    private boolean isValidPreferencesFormat(String preferences) {
        // Check no leading/trailing commas and no consecutive commas
        if (preferences.startsWith(",") || preferences.endsWith(",") || preferences.contains(",,")) {
            return false;
        }

        // Split by comma and check for non-empty trimmed parts
        String[] parts = preferences.split(",");
        for (String part : parts) {
            if (part.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }



}
