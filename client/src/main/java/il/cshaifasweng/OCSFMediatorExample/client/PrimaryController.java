package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class PrimaryController {

	@FXML
	private Button menuButton;

	@FXML
	private void showMenu()throws IOException {
		App.setRoot("menu");
		}




}
