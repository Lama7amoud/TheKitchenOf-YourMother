package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class PrimaryController {

	@FXML
	private Button menuButton;

	@FXML
	private void showMenu()throws IOException {
		Platform.runLater(() -> {
            try {
                App.setRoot("menu");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
		}
}


