package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
    private static Stage appStage;
    private boolean stop = false;

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setWindowTitle(String title) {
        appStage.setTitle(title);
    }

    public static void setContent(String pageName) throws IOException {
        Parent root = loadFXML(pageName);
        scene = new Scene(root);
        appStage.setScene(scene);
        appStage.show();
    }

    public static void switchScreen(String screenName) {
                Map<String, String> screenMappings = Map.ofEntries(
                        Map.entry("Main Page", "primary"),
                        Map.entry("Log In Page", "logIn"),
                        Map.entry("Menu Page", "menu"),
                        Map.entry("Personal Area Page", "personalAreaPage"),
                        Map.entry("Management Page", "managerPage"),
                        Map.entry("Feedback Page", "feedbackPage"),
                        Map.entry("Order Tables Page", "orderTablesPage"),
                        Map.entry("Branch Page", "branchPage"),
                        Map.entry("Tables Page", "tablesViewPage"),
                        Map.entry("Haifa Menu Page", "HaifaMenu"),
                        Map.entry("TelAviv Menu Page", "Tel_AvivMenu"),
                        Map.entry("Nahariya Menu Page" ,"NahariyaMenu")

                );




        String contentName = screenMappings.get(screenName);
        if (contentName != null) {
            Platform.runLater(() -> {
                setWindowTitle(screenName);
                try {
                    setContent(contentName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;
        scene = new Scene(loadFXML("mainPage"), 790, 480);
        stage.setScene(scene);

        stage.show();
    }

    @Override
    public void stop() {
        if (stop) {
            return;
        }
        stop = true;

        try {
            Client clientInstance = Client.getClient();

            if (clientInstance != null && clientInstance.isConnected()) {
                clientInstance.sendToServer("remove client;" + Client.getClientUsername());
            }
            clientInstance.closeConnection();
            super.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onWarningEvent(WarningEvent event) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING,
                    String.format("Message: %s\nTimestamp: %s\n",
                            event.getWarning().getMessage(),
                            event.getWarning().getTime().toString())
            );
            alert.show();
        });

    }

}