// Unified MenuController for Haifa, Tel Aviv, and Nahariya with restaurant interest support and adaptive row height

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.control.Control;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckBox;

import java.io.File;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuController {


    @FXML
    private Button searchButton;

    @FXML
    private TextField searchText;

    @FXML
    private ComboBox<String> combo;
    @FXML
    private TableView<Meal> menuTable;

    @FXML
    private TableColumn<Meal, String> nameColumn;

    @FXML
    private TableColumn<Meal, String> descriptionColumn;

    @FXML
    private TableColumn<Meal, String> preferencesColumn;

    @FXML
    private TableColumn<Meal, Double> priceColumn;

    @FXML
    private TableColumn<Meal, String> imageColumn;


    @FXML
    private TableColumn<Meal, Void> editColumn;

    @FXML
    private TableColumn<Meal, Void> quantityColumn;



    private ObservableList<Meal> menuData = FXCollections.observableArrayList();
    private List<Meal> fullMealList = new ArrayList<>();



    private static int restaurantInterest = 1; // default to Haifa

    public static void setRestaurantInterest(int interest) {
        restaurantInterest = interest;
    }
    public static int getRestaurantInterest() {
        return restaurantInterest;
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        AuthorizedUser user = Client.getClientAttributes();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("mealName"));
        nameColumn.setCellFactory(column -> new TableCell<>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Meal meal = getTableView().getItems().get(getIndex());
                    if (meal.getMealCategory().equals("header")) {
                        text.setStyle("-fx-font-weight: bold; -fx-fill: #333;");
                    } else {
                        text.setStyle(""); // reset
                    }
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });
        preferencesColumn.setCellValueFactory(new PropertyValueFactory<>("mealPreferences"));
        preferencesColumn.setCellFactory(column -> new TableCell<Meal, String>() {
            private final VBox vbox = new VBox(5);
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                vbox.getChildren().clear();

                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Meal meal = getTableView().getItems().get(getIndex());

                    if (meal.getMealCategory().equals("header")) {
                        setGraphic(null);
                        setText(null);
                        return;
                    }

                    AuthorizedUser user = Client.getClientAttributes();

                    if (user != null) {
                        if (user.getPermissionLevel() == 0) { // Customer - editable checkboxes
                            String[] preferences = item.split(",");
                            for (String pref : preferences) {
                                CheckBox checkBox = new CheckBox(pref.trim());
                                checkBox.setDisable(false); // Customer can edit
                                vbox.getChildren().add(checkBox);
                            }
                            setGraphic(vbox);
                            setText(null);
                        } else if (user.getPermissionLevel() == 5) { // Dietitian - plain text
                            setGraphic(null);
                            setText(item); // Just show text, no checkboxes
                        } else { // All other users - non-editable checkboxes
                            String[] preferences = item.split(",");
                            for (String pref : preferences) {
                                CheckBox checkBox = new CheckBox(pref.trim());
                                checkBox.setDisable(true); // Non-editable
                                vbox.getChildren().add(checkBox);
                            }
                            setGraphic(vbox);
                            setText(null);
                        }
                    } else { // If user is null, default non-editable checkboxes
                        String[] preferences = item.split(",");
                        for (String pref : preferences) {
                            CheckBox checkBox = new CheckBox(pref.trim());
                            checkBox.setDisable(true); // Default to non-editable
                            vbox.getChildren().add(checkBox);
                        }
                        setGraphic(vbox);
                        setText(null);
                    }
                }
            }
        });





        priceColumn.setCellValueFactory(new PropertyValueFactory<>("mealPrice"));


        priceColumn.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    Meal meal = getTableView().getItems().get(getIndex());
                    if (meal.getMealCategory().equals("header")) {
                        setText("");
                        return;
                    }
                    setText(String.format("%.2f", item));
                }
            }
        });





        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        combo.getItems().addAll("Meal Name", "Ingredients","Description", "Price");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("mealDescription"));
        descriptionColumn.setCellFactory(column -> new TableCell<>() {
            private final Text text = new Text();
            {
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(10));
                setPrefHeight(Control.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Meal meal = getTableView().getItems().get(getIndex());
                    if (meal.getMealCategory().equals("header")) {
                        setGraphic(null);
                        setText("");
                        return;
                    }
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });








        // Handle image display
        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);

                    Image image = null;

                    try {
                        // Try loading from file system (real-time image after adding)
                        File diskImage = new File(System.getProperty("user.dir") + "/src/main/resources" + imagePath);
                        if (diskImage.exists()) {
                            image = new Image(diskImage.toURI().toString());
                        } else {
                            // Fallback to resource if not found on disk (for built-in meals)
                            image = new Image(String.valueOf(PrimaryController.class.getResource(imagePath)));
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to load image for: " + imagePath);
                    }

                    imageView.setImage(image);
                    setGraphic(imageView);
                }
            }
        });

        menuTable.setEditable(true); // Allow editing
        menuTable.setPlaceholder(new Label("No meals available"));
        menuTable.setItems(menuData);
        menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (user != null && user.getPermissionLevel() == 5) {

            descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            descriptionColumn.setOnEditCommit(event -> {
                Meal meal = event.getRowValue();
                String newDesc = event.getNewValue();
                meal.setMealDescription(newDesc);
                try {
                    Client.getClient().sendToServer("Update description \"" + meal.getMealName() + "\" \"" + newDesc + "\"");
                    System.out.println("Updated description: " + meal.getMealName() + " -> " + newDesc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            preferencesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            preferencesColumn.setOnEditCommit(event -> {
                Meal meal = event.getRowValue();
                String newPref = event.getNewValue();

                if (newPref.isEmpty())
                {
                    showAlert("Preferences cannot be empty.");
                    return;
                }
                ///

                if (!isValidPreferences(newPref)) {
                    showAlert("Preferences must be separated by commas (,) with no leading, trailing, or consecutive commas.");
                    return;
                }

                meal.setMealPreferences(newPref);
                try {
                    Client.getClient().sendToServer("Update preferences \"" + meal.getMealName() + "\" \"" + newPref + "\"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


            editColumn.setCellFactory(col -> new TableCell<>() {
                private final TextField priceField = new TextField();
                private final Button editButton = new Button("Edit");

                {
                    editButton.setOnAction(e -> {
                        Meal meal = getTableView().getItems().get(getIndex());
                        String text = priceField.getText().trim();
                        try {
                            try {
                                double newPrice = Double.parseDouble(text);
                                if (newPrice < 0) {
                                    showAlert("Price must be non-negative.");

                                    return;
                                }
                                Client.getClient().sendToServer("Update price \"" + meal.getMealName() + "\" \"" + newPrice + "\"");
                                priceField.clear();
                            } catch (NumberFormatException ex) {
                                showAlert("Price must be a valid number.");
                            }

                            priceField.clear();
                        } catch (Exception ex) {
                            System.err.println("Invalid price entered.");
                        }
                    });

                    editButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black;");
                    editButton.setPrefWidth(60);
                    priceField.setPromptText("New price");
                    priceField.setMaxWidth(80);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Meal meal = getTableView().getItems().get(getIndex());
                        if (meal.getMealCategory().equals("header")) {
                            setGraphic(null); // Hide for header rows
                            return;
                        }
                        setGraphic(new HBox(5, priceField, editButton));
                    }
                }
            });


            TableColumn<Meal, Void> removeColumn = new TableColumn<>("Remove");
            removeColumn.setCellFactory(col -> new TableCell<>() {
                private final Button removeButton = new Button("❌");

                {
                    removeButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                    removeButton.setOnAction(e -> {
                        Meal meal = getTableView().getItems().get(getIndex());
                        String mealName = meal.getMealName();
                        String messageToSend = String.format("Remove Meal \"%s\"", mealName);
                        Client.getClient().sendToServer(messageToSend);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Meal meal = getTableView().getItems().get(getIndex());
                        if (meal.getMealCategory().equals("header")) {
                            setGraphic(null); // Hide for header rows
                            return;
                        }
                        setGraphic(removeButton);
                    }
                }
            });


            menuTable.getColumns().add(removeColumn);
        } else {
            editColumn.setVisible(false);
        }

        if (user != null && user.getPermissionLevel() == 0) {
            quantityColumn = new TableColumn<>("Quantity");
            quantityColumn.setPrefWidth(150);

            quantityColumn.setCellFactory(col -> new TableCell<>() {
                private final Button addButton = new Button("+");
                private final Button subButton = new Button("-");
                private final TextField qtyField = new TextField("0");

                {
                    qtyField.setPrefWidth(40);
                    qtyField.setEditable(false);

                    addButton.setOnAction(e -> {
                        int current = Integer.parseInt(qtyField.getText());
                        qtyField.setText(String.valueOf(current + 1));
                    });

                    subButton.setOnAction(e -> {
                        int current = Integer.parseInt(qtyField.getText());
                        if (current > 0) {
                            qtyField.setText(String.valueOf(current - 1));
                        }
                    });

                    addButton.setStyle("-fx-background-color: lightgreen;");
                    subButton.setStyle("-fx-background-color: lightcoral;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Meal meal = getTableView().getItems().get(getIndex());
                        if (meal.getMealCategory().equals("header")) {
                            setGraphic(null);
                            return;
                        }
                        HBox box = new HBox(5, subButton, qtyField, addButton);
                        setGraphic(box);
                    }
                }
            });

            // Add it to the table
            menuTable.getColumns().add(quantityColumn);
        }


        Client.getClient().sendToServer("Request Menu");

    }




    @Subscribe
    public void ExternalIntervention(Object msg) {
        if (msg instanceof List<?>) {
            List<?> list = (List<?>) msg;
            if (!list.isEmpty() && list.get(0) instanceof Meal) {
                Platform.runLater(() -> {
                    List<Meal> allMeals = (List<Meal>) list;
                    List<Meal> filtered = filterByInterest(allMeals);
                    fullMealList = filtered; // Save original full list
                    menuData.setAll(fullMealList); // Set data to TableView

                    // Use dynamic row height adjustment instead of fixed cell size
                    menuTable.setRowFactory(tv -> new TableRow<>() {
                        @Override
                        protected void updateItem(Meal item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setPrefHeight(Control.USE_COMPUTED_SIZE);
                            } else if ("header".equals(item.getMealCategory())) {
                                setPrefHeight(35); // Slightly smaller for header
                            } else {
                                setPrefHeight(Control.USE_COMPUTED_SIZE); // Adjust for content dynamically
                            }
                        }
                    });
                });
            }
        }
    }

    private boolean isValidPreferences(String preferences) {
        if (preferences.startsWith(",") || preferences.endsWith(",")) return false;
        if (preferences.contains(",,")) return false;

        String[] parts = preferences.split(",");
        for (String part : parts) {
            if (part.trim().isEmpty()) return false;
        }
        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    private List<Meal> filterByInterest(List<Meal> meals) {
        String specialCategory = switch (restaurantInterest) {
            case 1 -> "special1";
            case 2 -> "special2";
            case 3 -> "special3";
            default -> "";
        };

        List<Meal> shared = meals.stream()
                .filter(meal -> meal.getMealCategory().equals("shared meal"))
                .collect(Collectors.toList());

        List<Meal> special = meals.stream()
                .filter(meal -> meal.getMealCategory().equals(specialCategory))
                .collect(Collectors.toList());

        List<Meal> result = new ArrayList<>();

        // Fake header for shared meals
        Meal sharedHeader = new Meal("🍽 Shared Meals", "", "", 0.0, "", "header");
        result.add(sharedHeader);
        result.addAll(shared);

        // Fake header for special meals
        Meal specialHeader = new Meal("🌟 Special Meals", "", "", 0.0, "", "header");
        result.add(specialHeader);
        result.addAll(special);

        return result;
    }



    @FXML
    void searchFunc(ActionEvent event) {
        String selectedCriterion = combo.getValue();
        String searchTerm = searchText.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            // Show full table when search is empty
            menuData.setAll(fullMealList);
            resizeTable(); // optional
            return;
        }
        if (selectedCriterion == null) return;
        List<Meal> filtered = new ArrayList<>();
        for (Meal meal : fullMealList) {
            switch (selectedCriterion) {
                case "Meal Name":
                    if (meal.getMealName().toLowerCase().contains(searchTerm)) {
                        filtered.add(meal);
                    }
                    break;
                case "Ingredients":
                    if (meal.getMealPreferences().toLowerCase().contains(searchTerm)) {
                        filtered.add(meal);
                    }
                    break;
                case "Description":
                    if (meal.getMealDescription().toLowerCase().contains(searchTerm)) {
                        filtered.add(meal);
                    }
                    break;
                case "Price":
                    if (String.valueOf(meal.getMealPrice()).contains(searchTerm)) {
                        filtered.add(meal);
                    }
                    break;
            }
        }

        menuData.setAll(filtered); // update content but keep cell logic
        resizeTable(); // optional
    }
    private void resizeTable() {
        menuTable.setFixedCellSize(60);
        menuTable.prefHeightProperty().bind(
                menuTable.fixedCellSizeProperty().multiply(Bindings.size(menuTable.getItems()).add(1.01))
        );
        menuTable.minHeightProperty().bind(menuTable.prefHeightProperty());
        menuTable.maxHeightProperty().bind(menuTable.prefHeightProperty());
    }
    @FXML
    void back_to_main_func() {
        String page = "Main Page";
        App.switchScreen(page);
    }
}