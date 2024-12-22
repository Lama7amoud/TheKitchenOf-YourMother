package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class PrimaryController {



	@FXML
	private Button btn0;

	@FXML
	private Button btn1;

	@FXML
	private Button btn2;

	@FXML
	private Button btn3;

	@FXML
	private Button btn4;

	@FXML
	private Button btn5;

	@FXML
	private Button btn6;

	@FXML
	private Button btn7;

	@FXML
	private Button btn8;

	@FXML
	private Button playAgain;

	@FXML
	private Label statusLabel;

	@FXML
	private GridPane gameBoardGrid;

	private SimpleClient client;
	private String playerSymbol = "X";
	private String competitorSymbol = "O";
	private boolean isPlayerTurn = false;
	private String[][] gameBoard = {
			{"", "", ""},
			{"", "", ""},
			{"", "", ""}
	};


	@FXML
	void sendWarning(ActionEvent event) {
		try {
			SimpleClient.getClient("0", 0).sendToServer("#warning");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {
		try {
			EventBus.getDefault().register(this);

			client = SimpleClient.getClient("0", 0);
			client.openConnection();
			client.sendToServer("add new client");
			playerSymbol = client.getSymbol();
			competitorSymbol = playerSymbol.equals("X") ? "O" : "X";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void BtnClickFunc(ActionEvent event) throws IOException {
		if (!isPlayerTurn)
			return;
		Button clickedCell = (Button) event.getSource();
		int btnIndex = Integer.parseInt(clickedCell.getId().replace("btn", ""));
		int col = btnIndex / 3;
		int row = btnIndex % 3;
		gameBoard[row][col] = playerSymbol; // Update the game board
		if (playerSymbol.equals("X")) {
			clickedCell.setTextFill(javafx.scene.paint.Color.GREEN);
		} else if (playerSymbol.equals("O")) {
			clickedCell.setTextFill(javafx.scene.paint.Color.RED);
		}
		clickedCell.setText(playerSymbol);
		clickedCell.setDisable(true);

		// Check if there is a WINNER or it's a TEEKO
		boolean winner = false;
		for (int i = 0; i < 3; i++) {
			if ((gameBoard[i][0].equals(playerSymbol) && gameBoard[i][1].equals(playerSymbol) && gameBoard[i][2].equals(playerSymbol)) ||
					(gameBoard[0][i].equals(playerSymbol) && gameBoard[1][i].equals(playerSymbol) && gameBoard[2][i].equals(playerSymbol)) ||
					(gameBoard[0][0].equals(playerSymbol) && gameBoard[1][1].equals(playerSymbol) && gameBoard[2][2].equals(playerSymbol)) ||
					(gameBoard[0][2].equals(playerSymbol) && gameBoard[1][1].equals(playerSymbol) && gameBoard[2][0].equals(playerSymbol))) {
				winner = true;
			}
		}
		// If there's a winner, display win message
		if (winner == true) {
			statusLabel.setText("You did it! The board is yours!");
			gameBoardGrid.setStyle("-fx-background-color: green;");
			playAgain.setDisable(false);
			disableAllCells();

			try {
				TurnInfo data = new TurnInfo(btnIndex, playerSymbol);
				SimpleClient.getClient("0", 0).sendToServer(data);
				String winMessage = "competitorwin";
				SimpleClient.getClient("0", 0).sendToServer(winMessage);

			}catch (IOException e) {
				e.printStackTrace();
				statusLabel.setText("Failed to communicate with the server!");
			}
			return;

		}
		// If the board is full and there's no winner, it's a TEEKO
		else if (isTheGameBoardFull()) {
			gameBoardGrid.setStyle("-fx-background-color: yellow;");
			statusLabel.setText("TEEKO! No winner this time. ");
			disableAllCells();
			playAgain.setDisable(false);

			try {
				TurnInfo data = new TurnInfo(btnIndex, playerSymbol);
				SimpleClient.getClient("0", 0).sendToServer(data);
				String winMessage = "TEEKO";
				SimpleClient.getClient("0", 0).sendToServer(winMessage);
			}catch (IOException e) {
				e.printStackTrace();
				statusLabel.setText("Failed to communicate with the server!");
			}

			return;
		}
		else {

			// Send the player's move to the server and update the game status
			try {
				TurnInfo data = new TurnInfo(btnIndex, playerSymbol);
				SimpleClient.getClient("0", 0).sendToServer(data);
				isPlayerTurn = false;
				statusLabel.setText("Competitor's turn");
			}catch (IOException e) {
				e.printStackTrace();
				statusLabel.setText("Failed to send move to server!");
			}
		}


	}
	@FXML
	void playAgain(ActionEvent event) {
		try {
			SimpleClient.getClient("0", 0).sendToServer("playAgain");
			ClearAndReset();
			isPlayerTurn = true;
			statusLabel.setText("Rematch started! it's your turn.");
		} catch (IOException e) {
			e.printStackTrace();
			statusLabel.setText("An error occurred during rematch.");
		}
	}

	@Subscribe
	public void ExternalIntervation(String message) {
		Platform.runLater(() -> {
			try {
				switch (message) {

					case "playerTurn":
						handlePlayerTurn();
						break;
					case "playAgain":
						handlePlayAgain();
						break;
					case "competitorTurn":
						handlecompetitorTurn();
						break;
					case "competitorLeft":
						handlecompetitorLeft();
						break;
					case "TEEKO":
						handleTEEKO();
						break;
					case "competitorwin":
						handlePlayerLoss();
						break;
					default:

						break;
				}
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}



	private void handlecompetitorLeft() throws NoSuchFieldException, IllegalAccessException {
		ClearAndReset();
		isPlayerTurn = false;
		statusLabel.setText("Your competitor left! Waiting for a new one...");
	}

	private void handlePlayAgain() throws NoSuchFieldException, IllegalAccessException {isPlayerTurn = false;
		ClearAndReset();
		isPlayerTurn = false;

		statusLabel.setText("Rematch started! Sit tight, it's not your turn.");
	}

	private void handlePlayerTurn() {
		isPlayerTurn = true;
		statusLabel.setText("Your move! Make it count.");
	}

	private void handlecompetitorTurn() {
		isPlayerTurn = false;
		statusLabel.setText("Competitor's turn");
	}

	private void handlePlayerLoss() {
		showGameEndMessage("You lost! Better luck next time.");
		gameBoardGrid.setStyle("-fx-background-color: red;");
	}

	private void handleTEEKO() {
		showGameEndMessage("TEEKO! No winner this time.");
		gameBoardGrid.setStyle("-fx-background-color: yellow;");
	}

	private void showGameEndMessage(String message) {

		playAgain.setDisable(false);
		statusLabel.setText(message);
		disableAllCells();
	}

	@Subscribe
	public void competitorMoves(TurnInfo event) {
		int row = event.getBtnIndex() % 3;
		int col = event.getBtnIndex() / 3;
		gameBoard[row][col] = competitorSymbol; // Update the game board with the competitor's move

		Platform.runLater(() -> {
			Button competitorButton = getCellByIndex(event.getBtnIndex());
			if (competitorButton != null) {
				competitorButton.setText(event.getPlayerSymbol());
				competitorButton.setDisable(true);
			}
			if (event.getPlayerSymbol().equals("X")) {
				competitorButton.setTextFill(javafx.scene.paint.Color.GREEN); // Green for "X"
			} else if (event.getPlayerSymbol().equals("O")) {
				competitorButton.setTextFill(javafx.scene.paint.Color.RED); // Red for "O"
			}
			// Switch the turn to the player
			isPlayerTurn = true;
			statusLabel.setText("Your move! Make it count.");
		});
	}

	private boolean isTheGameBoardFull() {
		return java.util.Arrays.stream(gameBoard)
				.flatMap(java.util.Arrays::stream)
				.noneMatch(String::isEmpty);
	}

	private void disableAllCells() {
		for (int i = 0; i < 9; i++) {
			Button btn = getCellByIndex(i);
			if (btn != null) {
				btn.setDisable(true);
			}
		}
	}

	private Button getCellByIndex(int index) {
		try {
			return (Button) getClass().getDeclaredField("btn" + index).get(this);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}


	private void ClearAndReset() {
		gameBoardGrid.setStyle("-fx-background-color:  #ADD8E6;");
		// Reset the game board
		gameBoard = new String[][]{
				{"", "", ""},
				{"", "", ""},
				{"", "", ""}
		};

		// Reset all the buttons
		for (int i = 0; i < 9; i++) {
			Button button = getCellByIndex(i);
			if (button != null) {
				button.setText("");
				button.setDisable(false);
			}
		}
		playAgain.setDisable(true);

	}

	public void disconnect() {
		EventBus.getDefault().unregister(this);
		try {
			client.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}






