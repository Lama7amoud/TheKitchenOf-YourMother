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
        preferencesColumn.setCellValueFactory(new PropertyValueFactory<>("mealPreferences"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("mealPrice"));
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
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });

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
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });

        preferencesColumn.setCellFactory(column -> new TableCell<>() {
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
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    try {
                        imageView.setImage(new Image(String.valueOf(PrimaryController.class.getResource(imagePath))));
                    } catch (Exception e) {
                        imageView.setImage(null);
                    }
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

            editColumn.setCellFactory(col -> new TableCell<>() {
                private final TextField priceField = new TextField();
                private final Button editButton = new Button("Edit");

                {
                    editButton.setOnAction(e -> {
                        Meal meal = getTableView().getItems().get(getIndex());
                        String text = priceField.getText().trim();
                        try {
                            double newPrice = Double.parseDouble(text);
                            Client.getClient().sendToServer("Update price \"" + meal.getMealName() + "\" \"" + newPrice + "\"");
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
                        HBox box = new HBox(5, priceField, editButton);
                        setGraphic(box);
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
                        try {
                            String messageToSend = String.format("Remove Meal \"%s\"", mealName);
                            Client.getClient().sendToServer(messageToSend);

                            System.out.println("Remove Meal \"" + meal.getMealName() + "\"");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : removeButton);
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
                        HBox box = new HBox(5, subButton, qtyField, addButton);
                        setGraphic(box);
                    }
                }
            });

            // Add it to the table
            menuTable.getColumns().add(quantityColumn);
        }


        try {
            Client.getClient().sendToServer("Request Menu");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    @Subscribe
    public void ExternalIntervention(Object msg) {
        if (msg instanceof List<?>) {
            List<?> list = (List<?>) msg;
            if (!list.isEmpty() && list.get(0) instanceof Meal) {
                Platform.runLater(() -> {
                    List<Meal> allMeals = (List<Meal>) list;
                    List<Meal> filtered = filterByInterest(allMeals);
                    fullMealList = filtered;         // Save original full list
                    menuData.clear();
                    menuData.addAll(fullMealList);   // Set to TableView


                    // Dynamically resize table height to fit rows
                    menuTable.setFixedCellSize(60);
                    menuTable.prefHeightProperty().bind(
                            menuTable.fixedCellSizeProperty().multiply(Bindings.size(menuTable.getItems()).add(1.01))
                    );
                    menuTable.minHeightProperty().bind(menuTable.prefHeightProperty());
                    menuTable.maxHeightProperty().bind(menuTable.prefHeightProperty());
                });
            }
        }
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
        result.addAll(shared);
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