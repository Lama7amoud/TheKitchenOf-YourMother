        // Unified MenuController for Haifa, Tel Aviv, and Nahariya with restaurant interest support and adaptive row height

        package il.cshaifasweng.OCSFMediatorExample.client;

        import il.cshaifasweng.OCSFMediatorExample.entities.*;
        import javafx.application.Platform;
        import javafx.beans.binding.Bindings;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
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

        import javafx.stage.Modality;
        import javafx.stage.Stage;
        import org.greenrobot.eventbus.EventBus;
        import org.greenrobot.eventbus.Subscribe;
        import javafx.scene.layout.HBox;

        import java.io.IOException;
        import java.time.LocalDateTime;
        import java.time.LocalTime;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.stream.Collectors;

        import static il.cshaifasweng.OCSFMediatorExample.client.Client.userAtt;

        public class MenuController {


            private boolean alreadySentReservation = false;

            private boolean isActive = false;


            @FXML
            private Label totalToPayLabel;

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

            @FXML
            private Button payWithCashButton;

            @FXML
            private Button payWithVisaButton;

            private final Map<Meal, HBox> quantityMap = new HashMap<>();
            private Map<Meal, VBox> preferencesMap = new HashMap<>();

            private ObservableList<Meal> menuData = FXCollections.observableArrayList();
            private List<Meal> fullMealList = new ArrayList<>();
            private Map<String, Integer> mealQuantities = new HashMap<>();
            private Map<String, List<String>> selectedPreferencesMap = new HashMap<>();




            private static int restaurantInterest = 1; // default to Haifa

            public static void setRestaurantInterest(int interest) {
                restaurantInterest = interest;
            }
            public static int getRestaurantInterest() {
                return restaurantInterest;
            }

            @FXML
            void initialize() {
                Platform.runLater(() -> {
                    if (EventBus.getDefault().isRegistered(this)) {
                        EventBus.getDefault().unregister(this);
                    }
                    EventBus.getDefault().register(this);
                    isActive = true;
                    payWithVisaButton.setDisable(true);
                    payWithCashButton.setDisable(true);
                    if (OrderData.getInstance().getFullName() == null) {
                        payWithCashButton.setVisible(false);
                        payWithVisaButton.setVisible(false);
                        totalToPayLabel.setVisible(false);
                    } else {
                        payWithCashButton.setVisible(true);
                        payWithVisaButton.setVisible(true);
                        totalToPayLabel.setText("Total: 0.00 ₪");
                        totalToPayLabel.setVisible(true);
                    }
                    // Update restaurant interest from the user's choice
                    short restaurantInterest = userAtt.getRestaurantInterest();
                    MenuController.setRestaurantInterest(restaurantInterest);
                    System.out.println("Initialized Menu with restaurant interest: " + restaurantInterest);
                    User user = Client.getClientAttributes();

                    Restaurant restaurant = Client.getClientAttributes().getRestaurant();
                    System.out.println("Restaurant in Menu Page: " + (restaurant != null ? restaurant.getId() : "null"));

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

                                User user = Client.getClientAttributes();

                                if (user != null) {
                                    if (user.getPermissionLevel() == 0) { // Customer - editable checkboxes
                                        String[] preferences = item.split(",");
                                        for (String pref : preferences) {
                                            String trimmedPref = pref.trim();
                                            CheckBox checkBox = new CheckBox(trimmedPref);
                                            checkBox.setDisable(false);

                                            // Restore previous selection if exists
                                            String mealKey = meal.getMealName() + "_" + getIndex();
                                            List<String> selected = selectedPreferencesMap.getOrDefault(mealKey, new ArrayList<>());
                                            checkBox.setSelected(selected.contains(trimmedPref));

                                            // Track checkbox selection changes
                                            checkBox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                                                selectedPreferencesMap.putIfAbsent(mealKey, new ArrayList<>());
                                                if (isNowSelected) {
                                                    if (!selectedPreferencesMap.get(mealKey).contains(trimmedPref)) {
                                                        selectedPreferencesMap.get(mealKey).add(trimmedPref);
                                                    }
                                                } else {
                                                    selectedPreferencesMap.get(mealKey).remove(trimmedPref);
                                                }
                                            });

                                            vbox.getChildren().add(checkBox);

                                        }
                                        preferencesMap.put(meal, vbox);
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
                    combo.getItems().addAll("Meal Name", "Ingredients", "Description", "Price");
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
                    // Real-time image display from file:// URI
                    imageColumn.setCellFactory(column -> new TableCell<Meal, String>() {
                        private final ImageView imageView = new ImageView();

                        @Override
                        protected void updateItem(String imagePath, boolean empty) {
                            super.updateItem(imagePath, empty);
                            if (empty || imagePath == null || imagePath.isEmpty()) {
                                setGraphic(null);
                                return;
                            }
                            imageView.setFitWidth(50);
                            imageView.setFitHeight(50);
                            try {
                                Image img = new Image(imagePath, true);   // direct load of file:///… URI
                                imageView.setImage(img);
                                setGraphic(imageView);
                            } catch (Exception ex) {
                                System.out.println("Failed to load image: " + imagePath);
                                setGraphic(null);
                            }
                        }
                    });


                    menuTable.setEditable(true); // Allow editing
                    menuTable.setPlaceholder(new Label("No meals available"));
                    menuTable.setItems(menuData);
                    menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                    menuTable.setRowFactory(tv -> new TableRow<Meal>() {
                        @Override
                        protected void updateItem(Meal item, boolean empty) {
                            super.updateItem(item, empty);
                            setVisible(item != null && !empty);
                            setManaged(item != null && !empty);
                            if (item == null || empty) {
                                setPrefHeight(0);
                            } else {
                                setPrefHeight(Region.USE_COMPUTED_SIZE);
                            }
                        }
                    });


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

                            if (newPref.isEmpty()) {
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
                                priceField.setPromptText("new price");
                                priceField.setMaxWidth(100);
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

                    if (user != null && user.getPermissionLevel() == 0 && OrderData.getInstance().getFullName() != null) {
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
                                    Meal meal = getTableView().getItems().get(getIndex());
                                    String mealName = meal.getMealName();
                                    int current = mealQuantities.getOrDefault(mealName, 0);
                                    mealQuantities.put(mealName, current + 1);
                                    menuData.setAll(buildExpandedMenuList(fullMealList));
                                    updateTotalToPay();
                                });

                                subButton.setOnAction(e -> {
                                    Meal meal = getTableView().getItems().get(getIndex());
                                    String mealName = meal.getMealName();
                                    int current = mealQuantities.getOrDefault(mealName, 0);

                                    if (current > 0) {
                                        mealQuantities.put(mealName, current - 1);
                                        menuData.setAll(buildExpandedMenuList(fullMealList));
                                    }
                                    updateTotalToPay();
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


                                    meal = getTableView().getItems().get(getIndex());
                                    String mealName = meal.getMealName();

                                    long countBefore = getTableView().getItems().subList(0, getIndex()).stream()
                                            .filter(m -> m.getMealName().equals(mealName))
                                            .count();

                                    if (meal.getMealCategory().equals("header") || countBefore > 0) {
                                        setGraphic(null); // Show counter only for first row of each meal
                                        return;
                                    }


                                    qtyField.setText(String.valueOf(mealQuantities.getOrDefault(mealName, 0)));
                                    HBox box = new HBox(5, subButton, qtyField, addButton);
                                    quantityMap.put(meal, box);
                                    setGraphic(box);

                                    /*HBox box = new HBox(5, subButton, qtyField, addButton);
                                    quantityMap.put(getTableView().getItems().get(getIndex()), box);//added
                                    setGraphic(box);*/
                                }
                            }
                        });

                        // Add it to the table
                        menuTable.getColumns().add(quantityColumn);
                    }


                    Client.getClient().sendToServer("Request Menu");
                });
            }

            private VBox getPreferencesVBoxForMeal(Meal meal) {
                return preferencesMap.get(meal);
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



                            // Initialize quantities map if first time
                            for (Meal meal : filtered) {
                                if (!meal.getMealCategory().equals("header")) {
                                    mealQuantities.putIfAbsent(meal.getMealName(), 0);
                                }
                            }
                            menuData.setAll(buildExpandedMenuList(filtered));
                            //menuData.setAll(fullMealList); // Set data to TableView

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
            private Meal duplicateMealWithoutCounter(Meal original) {
                return new Meal(
                        original.getMealName(),
                        original.getMealDescription(),
                        original.getMealPreferences(),
                        original.getMealPrice(),
                        original.getImagePath(),
                        original.getMealCategory()
                );
            }
            private List<Meal> buildExpandedMenuList(List<Meal> fullList) {
                List<Meal> expanded = new ArrayList<>();
                for (Meal meal : fullList) {
                    if (meal.getMealCategory().equals("header")) {
                        expanded.add(meal);
                        continue;
                    }

                    // Always add one base row, regardless of quantity (even if 0)
                    expanded.add(meal);

                    int qty = mealQuantities.getOrDefault(meal.getMealName(), 0);
                    for (int i = 1; i < qty; i++) {
                        expanded.add(duplicateMealWithoutCounter(meal)); // duplicates for qty > 1
                    }
                }
                return expanded;
            }







            @FXML
            void searchFunc(ActionEvent event) {
                String selectedCriterion = combo.getValue();
                String searchTerm = searchText.getText().trim().toLowerCase();

                if (searchTerm.isEmpty()) {
                    // Show full table when search is empty
                    menuData.setAll(fullMealList);
                    resizeTable();
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
                resizeTable();
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
                EventBus.getDefault().unregister(this);
                isActive = false;
                App.switchScreen(page);
            }

            /*@FXML
            void payWithCash(ActionEvent event) {
                try {
                    // Build reservation from OrderData
                    Reservation reservation = new Reservation();
                    reservation.setName(OrderData.getInstance().getFullName());
                    reservation.setPhoneNumber(OrderData.getInstance().getPhoneNumber());
                    reservation.setIdNumber(OrderData.getInstance().getIdNumber());
                    reservation.setAddress(OrderData.getInstance().getAddress());
                    reservation.setVisa("cash");
                    reservation.setPayed(true);
                    reservation.setTakeAway(true);
                    reservation.setTotalGuests(OrderData.getInstance().getGuestCount());
                    reservation.setSittingType(OrderData.getInstance().getSittingType());
                    reservation.setStatus("on");
                    reservation.setReservationTime(LocalDateTime.now());

                    LocalDateTime receivingTime = LocalDateTime.of(
                            OrderData.getInstance().getDate(),
                            LocalTime.parse(OrderData.getInstance().getPreferredTime())
                    );
                    reservation.setReceivingTime(receivingTime);

                    Restaurant restaurant = Client.getClientAttributes().getRestaurant();
                    if (restaurant != null) {
                        reservation.setRestaurant(restaurant);
                    } else {
                        System.out.println("Restaurant is null! Cannot set restaurant in reservation.");
                    }

                    // Send reservation
                    Client.getClient().sendToServer(new ReservationRequest(reservation, true));

                    // Prepare meal orders
                    List<MealOrder> ordersToSave = new ArrayList<>();
                    for (Meal meal : menuTable.getItems()) {
                        if (!"header".equals(meal.getMealCategory())) {
                            HBox box = quantityMap.get(meal);
                            if (box == null) continue;

                            TextField qtyField = (TextField) box.getChildren().get(1);
                            int quantity = Integer.parseInt(qtyField.getText());
                            if (quantity <= 0) continue;

                            VBox prefsBox = getPreferencesVBoxForMeal(meal);
                            StringBuilder selectedPrefs = new StringBuilder();
                            if (prefsBox != null) {
                                for (javafx.scene.Node node : prefsBox.getChildren()) {
                                    if (node instanceof CheckBox) {
                                        CheckBox cb = (CheckBox) node;
                                        if (cb.isSelected()) {
                                            if (!selectedPrefs.isEmpty()) selectedPrefs.append(", ");
                                            selectedPrefs.append(cb.getText());
                                        }
                                    }
                                }
                            }


                            MealOrder mo = new MealOrder(
                                    reservation.getId(),              // Long
                                    meal.getMealName(),               // String
                                    selectedPrefs.toString(),         // String
                                    quantity,                         // int
                                    quantity * meal.getMealPrice()    // double
                            );

                            ordersToSave.add(mo);
                        }
                    }

                    // Send to server
                    Client.getClient().sendToServer(ordersToSave);

                    // Confirmation popup
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Reservation Complete");
                        alert.setHeaderText(null);
                        alert.setContentText("Your reservation and order have been saved successfully. Thank you!");
                        alert.showAndWait();
                        App.switchScreen("Main Page");
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    */

    /*

            @FXML
            void payWithCash(ActionEvent event) {
                try {
                    // Build reservation from OrderData
                    Reservation reservation = new Reservation();
                    reservation.setName(OrderData.getInstance().getFullName());
                    reservation.setPhoneNumber(OrderData.getInstance().getPhoneNumber());
                    reservation.setIdNumber(OrderData.getInstance().getIdNumber());
                    reservation.setAddress(OrderData.getInstance().getAddress());
                    reservation.setVisa("cash");
                    reservation.setPayed(true);
                    reservation.setTakeAway(true);
                    reservation.setTotalGuests(OrderData.getInstance().getGuestCount());
                    reservation.setSittingType(OrderData.getInstance().getSittingType());
                    reservation.setStatus("on");
                    reservation.setReservationTime(LocalDateTime.now());
                    reservation.setEmail(OrderData.getInstance().getEmail());


                    LocalDateTime receivingTime = LocalDateTime.of(
                            OrderData.getInstance().getDate(),
                            LocalTime.parse(OrderData.getInstance().getPreferredTime())
                    );
                    reservation.setReceivingTime(receivingTime);

                    Restaurant restaurant = Client.getClientAttributes().getRestaurant();
                    if (restaurant != null) {
                        reservation.setRestaurant(restaurant);
                    } else {
                        System.out.println("Restaurant is null! Cannot set restaurant in reservation.");
                    }

                    // Send reservation to server (only)
                    Client.getClient().sendToServer(new ReservationRequest(reservation, true));

                    // Don't build or send MealOrders here — wait for reservation with ID

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    */




            @FXML
            void payWithCash(ActionEvent event) {
                OrderData.getInstance().setVisa("cash");
                finalizeOrderAndReservation("Cash");
            }

            @FXML
            void payWithVisa(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("visaPayment.fxml"));
                    Parent popupRoot = loader.load();

                    VisaPaymentController controller = loader.getController();
                    controller.setParentController(this); // So it can call finalizeOrderAndReservation later

                    Stage popupStage = new Stage();
                    popupStage.setTitle("Visa Payment");
                    popupStage.setScene(new Scene(popupRoot));
                    popupStage.initModality(Modality.APPLICATION_MODAL);
                    popupStage.showAndWait();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            public void finalizeOrderAndReservation(String paymentType) {
                try {
                    OrderData data = OrderData.getInstance();

                    Reservation reservation = new Reservation();
                    reservation.setName(data.getFullName());
                    reservation.setPhoneNumber(data.getPhoneNumber());
                    reservation.setIdNumber(data.getIdNumber());
                    reservation.setAddress(data.getAddress());
                    reservation.setEmail(data.getEmail());
                    reservation.setTakeAway(true);
                    reservation.setSittingType(data.getSittingType());
                    reservation.setTotalGuests(data.getGuestCount());
                    reservation.setReservationTime(LocalDateTime.of(data.getDate(), LocalTime.parse(data.getPreferredTime())));
                    reservation.setStatus("on");
                    reservation.setVisa(OrderData.getInstance().getVisa());
                    reservation.setExpirationMonth(OrderData.getInstance().getExpirationMonth());
                    reservation.setExpirationYear(OrderData.getInstance().getExpirationYear());
                    reservation.setCvv(OrderData.getInstance().getCvv());


                    // Receiving time
                    LocalDateTime receivingTime = LocalDateTime.now();
                    reservation.setReceivingTime(receivingTime);

                    // Restaurant
                    Restaurant restaurant = Client.getClientAttributes().getRestaurant();
                    if (restaurant != null) {
                        reservation.setRestaurant(restaurant);
                    } else {
                        System.out.println("Restaurant is null! Cannot set restaurant in reservation.");
                    }

                    reservation.setSittingType("TAKEAWAY");              // avoid "sitting type is null"


                    if (paymentType.equals("Cash")) {
                        // tell the server “this is a cash order” by leaving Visa = null:
                        reservation.setVisa(null);
                        reservation.setPayed(false);   // not paid yet, we’ll collect on pickup
                    } else {
                        // card‐path
                        reservation.setVisa(data.getVisa());
                        reservation.setPayed(true);
                    }

                    if (!alreadySentReservation) {
                        Client.getClient().sendToServer(new ReservationRequest(reservation, true));
                        alreadySentReservation = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Subscribe
            public void onReservationConfirmed(Reservation reservation) {
                if (!isActive || !reservation.isTakeAway()) return;

                List<MealOrder> ordersToSave = new ArrayList<>();
                List<Meal> items = menuTable.getItems();
                int i = 0;

                while (i < items.size()) {
                    Meal current = items.get(i);

                    // Skip headers
                    if ("header".equals(current.getMealCategory())) {
                        i++;
                        continue;
                    }

                    String mealName = current.getMealName();
                    int quantity = mealQuantities.getOrDefault(mealName, 0);
                    if (quantity <= 0) {
                        // Skip this group entirely
                        // Move to next different meal
                        while (i < items.size() && items.get(i).getMealName().equals(mealName)) {
                            i++;
                        }
                        continue;
                    }

                    // Count group size (number of rows for this meal)
                    int groupStart = i;
                    int groupEnd = i;
                    while (groupEnd < items.size() &&
                            items.get(groupEnd).getMealName().equals(mealName) &&
                            !"header".equals(items.get(groupEnd).getMealCategory())) {
                        groupEnd++;
                    }

                    // Add one MealOrder per row (quantity 1 each)
                    for (int j = groupStart; j < groupEnd; j++) {
                        Meal meal = items.get(j);
                        String key = meal.getMealName() + "_" + j;
                        List<String> selectedPrefs = selectedPreferencesMap.getOrDefault(key, new ArrayList<>());
                        String joinedPrefs = String.join(", ", selectedPrefs);

                        MealOrder mo = new MealOrder(
                                reservation.getId(),
                                meal.getMealName(),
                                joinedPrefs,
                                1,
                                meal.getMealPrice()
                        );
                        ordersToSave.add(mo);
                    }

                    i = groupEnd; // Skip to next group
                }

                // Send to server
                Client.getClient().sendToServer(ordersToSave);

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Takeaway Order");
                    alert.setHeaderText("Order Saved Successfully");
                    alert.setContentText("Your takeaway order has been saved. Reservation ID: " + reservation.getId());
                    alert.showAndWait();
                    EventBus.getDefault().unregister(this);
                    isActive = false;
                    App.switchScreen("Main Page");
                });
            }




            /*
            private <S, T> TableCell<S, T> getCellAt(TableView<S> table, int rowIndex, TableColumn<S, T> column) {
                for (TableColumn<S, ?> col : table.getColumns()) {
                    if (col == column) {
                        for (Object obj : column.getCellObservableValue(rowIndex).getDependencies()) {
                            if (obj instanceof TableCell) {
                                return (TableCell<S, T>) obj;
                            }
                        }
                    }
                }
                return null;
            }

        */
          /*  private VBox getPreferencesVBoxForRow(int rowIndex) {
                Meal meal = menuTable.getItems().get(rowIndex);
                for (TableColumn<Meal, ?> column : menuTable.getColumns()) {
                    if (column == preferencesColumn) {
                        TableCell<Meal, String> cell = (TableCell<Meal, String>) column.getCellFactory().call(column);
                        cell.updateIndex(rowIndex);
                        javafx.scene.Node graphic = cell.getGraphic();
                        if (graphic instanceof VBox) {
                            return (VBox) graphic;
                        }
                    }
                }
                return null;
            }*/


        /*    private void saveMealOrders() {
                try {
                    List<MealOrder> orders = OrderData.getInstance().getMealOrders();
                    Client.getClient().sendToServer(orders);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/


            @FXML
            void saveReservationToDatabase() {
                // Retrieve the essential data from OrderData
                String fullName = OrderData.getInstance().getFullName();
                String phoneNumber = OrderData.getInstance().getPhoneNumber();
                String idNumber = OrderData.getInstance().getIdNumber();
                String address = OrderData.getInstance().getAddress();
                String preferredTime = OrderData.getInstance().getPreferredTime();

                // Convert preferredTime to LocalDateTime (assuming preferredTime is a valid string)
                LocalDateTime reservationTime;
                try {
                    reservationTime = LocalDateTime.parse(preferredTime);
                } catch (Exception e) {
                    System.err.println("Invalid reservation time format.");
                    return;
                }
                // Log the data for verification before saving
                System.out.println("Saving reservation with the following data:");
                System.out.println("Full Name: " + fullName);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("ID Number: " + idNumber);
                System.out.println("Address: " + address);
                System.out.println("Preferred Time: " + reservationTime);

                try {
                    // Create a Reservation object with the essential fields
                    Reservation reservation = new Reservation(fullName, phoneNumber, idNumber, address, reservationTime);

                    // Send to the server to save in the database
                    Client.getClient().sendToServer("save_reservation;" + reservation);
                    System.out.println("Reservation saved to the database.");
                } catch (Exception e) {
                    System.err.println("Error while saving reservation: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            private void updateTotalToPay() {
                double total = fullMealList.stream()
                        .filter(m -> !"header".equals(m.getMealCategory()))
                        .mapToDouble(m -> mealQuantities.getOrDefault(m.getMealName(), 0) * m.getMealPrice())
                        .sum();

                Platform.runLater(() -> {
                        totalToPayLabel.setText(String.format("Total: %.2f ₪", total));
                        if (total > 0){
                            payWithVisaButton.setDisable(false);
                            payWithCashButton.setDisable(false);
                        } else {
                            payWithVisaButton.setDisable(true);
                            payWithCashButton.setDisable(true);
                        }
                });
            }


        }