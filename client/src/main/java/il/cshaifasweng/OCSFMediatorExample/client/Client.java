package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import net.bytebuddy.asm.Advice;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.io.IOException;
import java.util.List;

public class Client extends AbstractClient {

    private static Client client = null;
    public static String host;
    public static int port;
    static AuthorizedUser userAtt;


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

    public static AuthorizedUser getClientAttributes() {
        if (userAtt == null) {
            return userAtt = new AuthorizedUser();
        }
        return userAtt;
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
    @Override
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
    }  else if (msg instanceof AuthorizedUser) {
            userAtt.copyUser((AuthorizedUser) msg);
            String response = "Authorized user request:" + userAtt.getMessageToServer();
            EventBus.getDefault().post(response);
        } else if (msg instanceof Restaurant) {
            System.out.println("Restaurant " + ((Restaurant) msg).getId() + " has received to client side");
            EventBus.getDefault().post(msg);
        } else if (msg instanceof Reservation) {
            EventBus.getDefault().post(msg);
        }
    }
}
