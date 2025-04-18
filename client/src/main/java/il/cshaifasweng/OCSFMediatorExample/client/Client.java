package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
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
            String msgStr = (String) msg;
            System.out.println(msgStr);

            if (msgStr.equals("client added successfully")) {
                App.switchScreen("Log In Page");
            } else if (msgStr.equals("MealExists")) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("A meal with this name already exists!");
                    alert.showAndWait();
                });
            } else if (msgStr.startsWith("MealCategory:")) {
                String category = msgStr.substring("MealCategory:".length()).trim();

                if (updateMenuController != null) {
                    if (category.equals("NotFound")) {
                        updateMenuController.handleMealNotFound();
                    } else {
                        updateMenuController.handleMealCategoryResponse(category);
                    }
                }
            }

        } else if (msg instanceof Warning) {
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
                }
            }

        } else if (msg instanceof AuthorizedUser) {
            userAtt.copyUser((AuthorizedUser) msg);
            System.out.println("response: " + userAtt.getMessageToServer());
            EventBus.getDefault().post("Authorized user request:" + userAtt.getMessageToServer());

        } else if (msg instanceof Restaurant) {
            System.out.println("Restaurant " + ((Restaurant) msg).getId() + " has received to client side");
            EventBus.getDefault().post(msg);
        }
    }
}
