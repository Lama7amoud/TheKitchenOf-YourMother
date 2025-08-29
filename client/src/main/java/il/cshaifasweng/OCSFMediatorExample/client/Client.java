package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import il.cshaifasweng.OCSFMediatorExample.client.events.MessageEvent;

public class Client extends AbstractClient {

    private static Client client = null;
    public static String host;
    public static int port;
    static User userAtt;
    private static final String CLIENT_ID = UUID.randomUUID().toString();
    public static String getClientId() {
        return CLIENT_ID;
    }

    private static List<Meal> menu;

    private updateMenuPageController updateMenuController;

    public Client(String host, int port) {
        super(host, port);
        this.host = host;
        this.port = port;
    }

    public static Client getClient() {
        if (client == null) {
            client = new Client(host, port);
        }
        if (userAtt == null) {
            userAtt = getClientAttributes();
        }
        return client;
    }

    public void setUpdateMenuController(updateMenuPageController controller) {
        this.updateMenuController = controller;
    }

    @Override
    public void sendToServer(Object msg) {
        try {
            super.sendToServer(msg);
        } catch (IOException e) {
            e.printStackTrace(); // or show a dialog
        }
    }

    public static void resetClientAttributes() {
        userAtt.resetAttributes();
    }

    public static User getClientAttributes() {
        if (userAtt == null) {
            return userAtt = new User();
        }
        return userAtt; // Singleton
    }

    public static String getClientUsername() {
        if (userAtt.getUsername() != null) {
            return userAtt.getUsername();
        }
        return null;
    }

    public static List<Meal> getMenu() {
        return menu;
    }
/*    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof String) {

            String strMsg = (String) msg;



            if (strMsg.equals("Reservation cancelled successfully")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Cancelled");
                    alert.setHeaderText(null);
                    alert.setContentText("Your reservation has been cancelled.");
                    alert.showAndWait();
                    App.switchScreen("Main Page");
                });
                return;
            }

            if (strMsg.equals("MealExists")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("A meal with this name already exists!");
                    alert.showAndWait();
                    return;
                });
            }
            if (strMsg.startsWith("MealCategory:")) {
                String category = strMsg.substring("MealCategory:".length()).trim();

                if (updateMenuController != null) {
                    if (category.equals("NotFound")) {
                        updateMenuController.handleMealNotFound();
                    } else {
                        updateMenuController.handleMealCategoryResponse(category);
                    }
                }
                return;
            }
            if (strMsg.equals("Cancellation failed: Reservation not found")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Cancellation failed. Reservation not found.");
                    alert.showAndWait();
                });
                return;
            }
            if (strMsg.startsWith("id_exists:")) {
                boolean exists = Boolean.parseBoolean(strMsg.split(":")[1].trim());
                EventBus.getDefault().post(new IdCheckEvent(exists));
                return;
            }

            System.out.println(strMsg); // Log any other message

            if (strMsg.equals("client added successfully")) {
                App.switchScreen("Log In Page");
                return;
            }

            if (strMsg.equals("Reservation failed: ID already used.")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Reservation Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("A reservation already exists for this ID.");
                    alert.showAndWait();
                });
                return;
            }

            if (strMsg.equals("Reservation saved successfully")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Your reservation has been saved successfully.");
                    alert.showAndWait();
                    App.switchScreen("Main Page");
                });
                return;
            }
        }


        // Handle other object types
        if (msg instanceof Warning) {
            EventBus.getDefault().post(new WarningEvent((Warning) msg));
        } else if (msg instanceof List<?>) {
            List<?> list = (List<?>) msg;
            if (!list.isEmpty()) {
                Object first = list.get(0);
                if (first instanceof Meal) {
                    menu = (List<Meal>) list;
                    EventBus.getDefault().post(menu);
                } else if (first instanceof PriceConfirmation) {
                    EventBus.getDefault().post((List<PriceConfirmation>) list);
                } else if (first instanceof Discounts) {
                    EventBus.getDefault().post((List<Discounts>) list);
                } else if (first instanceof Feedback) {
                    EventBus.getDefault().post((List<Feedback>) list);
                } else if (first instanceof HostingTable) {
                    EventBus.getDefault().post((List<HostingTable>) list);
                }
            }
    }  else if (msg instanceof List<?>) {
            List<?> dataList = (List<?>) msg;
            if (!dataList.isEmpty() && dataList.get(0) instanceof MonthlyReport) {
                List<MonthlyReport> reports = (List<MonthlyReport>) dataList;

                // You can now post the reports to EventBus or open a new page
                EventBus.getDefault().post(new MonthlyReportsEvent(reports));
            }
    }  else if (msg instanceof User) {
            userAtt.copyUser((User) msg);
            String response = "Authorized user request:" + userAtt.getMessageToServer();
            EventBus.getDefault().post(response);
        } else if (msg instanceof Restaurant) {
            System.out.println("Restaurant " + ((Restaurant) msg).getId() + " has received to client side");
            EventBus.getDefault().post(msg);
        } else if (msg instanceof Reservation) {
            EventBus.getDefault().post(msg);
        }
    }*/
@Override
protected void handleMessageFromServer(Object msg) {

    if (msg instanceof Message) {
        Message message = (Message) msg;
        if ("reservation_update".equals(message.getType())) {
            Reservation updated = (Reservation) message.getData();
            EventBus.getDefault().post(updated);  // post to EventBus for UI use
            return;
        }
    }
    if (msg instanceof String) {
        String strMsg = (String) msg;
        System.out.println("[Client] <handleMessageFromServer> got raw server message: " + strMsg);
        EventBus.getDefault().post(new MessageEvent(strMsg));
        if (strMsg.equals("Reservation cancelled successfully")) {
            return;
        }

        /*if (strMsg.equals("Reservation saved successfully")) {
             Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Your reservation has been saved successfully.");
                alert.showAndWait();
                App.switchScreen("Main Page");
            });
            return;
        }*/

        if(strMsg.startsWith("complaint") || strMsg.startsWith("feedback")|| strMsg.startsWith("user") || strMsg.equals("Monthly report updated")){
            EventBus.getDefault().post(strMsg);
            return;
        }

        if (strMsg.startsWith("MealCategory:")) {
            String category = strMsg.substring("MealCategory:".length()).trim();

            if (updateMenuController != null) {
                if (category.equals("NotFound")) {
                    updateMenuController.handleMealNotFound();
                } else {
                    updateMenuController.handleMealCategoryResponse(category);
                }
            }
            return;
        }

/*        if (strMsg.equals("Reservation failed: ID already used.")) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Reservation Failed");
                alert.setHeaderText(null);
                alert.setContentText("A reservation already exists for this ID.");
                alert.showAndWait();
            });
            return;
        }*/


        //error pops when time conflict
        if (strMsg.startsWith("Reservation failed: ID already used.")) {
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText(null);
                a.setContentText(strMsg);
                a.showAndWait();
            });
            return;
        }

        if (strMsg.equals("client added successfully")) {
            App.switchScreen("Log In Page");
            return;
        }

        if (strMsg.startsWith("id_exists:")) {
            boolean exists = Boolean.parseBoolean(strMsg.split(":")[1].trim());
            EventBus.getDefault().post(new IdCheckEvent(exists));
            return;
        }

        System.out.println("Received from server: " + strMsg); // Log any other message
    }

    // Handle warnings
    if (msg instanceof Warning) {
        EventBus.getDefault().post(new WarningEvent((Warning) msg));
    }
    
    // Handle list of objects
    if (msg instanceof List<?>) {
        List<?> list = (List<?>) msg;
        if (!list.isEmpty()) {
            Object first = list.get(0);

            if (first instanceof Meal) {
                menu = (List<Meal>) list;
                System.out.println("Received list of meals from server: " + menu.size());
                EventBus.getDefault().post(menu);
            } else if (first instanceof PriceConfirmation) {
                System.out.println("Received list of price confirmations from server.");
                EventBus.getDefault().post((List<PriceConfirmation>) list);
            } else if (first instanceof Discounts) {
                System.out.println("Received list of discounts from server.");
                EventBus.getDefault().post((List<Discounts>) list);
            } else if (first instanceof Feedback) {
                System.out.println("Received list of feedback from server.");
                EventBus.getDefault().post((List<Feedback>) list);
            } else if (first instanceof Complaint) {
                System.out.println("Received list of complaint from server.");
                EventBus.getDefault().post((List<Complaint>) list);
            } else if (first instanceof HostingTable) {
                System.out.println("Received list of hosting tables from server.");
                EventBus.getDefault().post((List<HostingTable>) list);
            } else if (first instanceof DailyReport) {
                System.out.println("Received list of daily reports as monthly from server.");
                EventBus.getDefault().post((List<DailyReport>) list);
            } else if (first instanceof MonthlyReport) {
                System.out.println("Received list of prev only months reports from server.");
                EventBus.getDefault().post((List<MonthlyReport>) list);
            } else if (first instanceof Restaurant) {
                List<Restaurant> restaurantList = (List<Restaurant>) list;
                System.out.println("Received list of restaurants from server: " + restaurantList.size());
                EventBus.getDefault().post(restaurantList);
            } else if (first instanceof MealOrder) {
                System.out.println("Received list of meal orders from server: " + list.size());
                EventBus.getDefault().post((List<MealOrder>) list);
            } else if (first instanceof String[]) {
                System.out.println("Received list of reservations for the restaurant map: " + list.size());
                EventBus.getDefault().post((List<String[]>) list);

            } else {
                System.out.println("Received an unrecognized list from server.");
            }
        } else {
            System.out.println("Received an empty list from server.");
        }
    }

    // Handle single Restaurant object
    if (msg instanceof Restaurant) {
        Restaurant restaurant = (Restaurant) msg;
        System.out.println("Restaurant " + restaurant.getId() + " received on client side");
        userAtt.setRestaurant(restaurant);
        EventBus.getDefault().post(restaurant);
        System.out.println("Stored restaurant in userAtt: " + userAtt.getRestaurant().getId());
    }


    // Handle single Reservation object
    if (msg instanceof Reservation) {
        Reservation reservation = (Reservation) msg;
        if (reservation.getSenderId() != null && reservation.getSenderId().equals(Client.getClientId())) {
            return; // Ignore own reservation
        }

        System.out.println("Reservation saved, ID: " + ((Reservation) msg).getId());
        EventBus.getDefault().post(msg);
    }

    // Handle User object
    if (msg instanceof User) {
        userAtt.copyUser((User) msg);
        String response = "Authorized user request: " + userAtt.getMessageToServer();
        EventBus.getDefault().post(response);
    }
}


}
