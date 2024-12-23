package il.cshaifasweng.OCSFMediatorExample.server;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.client.TurnInfo;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;



public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	public static ConnectionToClient player1 = null;
	public int player1symbol = 1;
	public int player2symbol = 0;
	public static ConnectionToClient player2 = null;

	public SimpleServer(int port) {
		super(port);

	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();

		if (msgString.startsWith("#warning")) {
			sendWarningToClient(client);
		} else if (isCompetitorMessage(msgString)) {
			relayCompetitorMessage(msg, client);
		} else if (msg instanceof TurnInfo) {
			relayTurnInfo(msg, client);
		} else if ("add new client".equals(msgString)) {
			addNewClient(client);
		} else if ("remove client".equals(msgString)) {
			removeClient(client);
		}
	}

	private void sendWarningToClient(ConnectionToClient client) {
		Warning warning = new Warning("Warning from server!");
		try {
			client.sendToClient(warning);
			System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isCompetitorMessage(String msgString) {
		return "competitorwin".equals(msgString) || "playAgain".equals(msgString) || "TEEKO".equals(msgString);
	}

	private void relayCompetitorMessage(Object msg, ConnectionToClient client) {
		try {
			if (client == player1) {
				player2.sendToClient(msg);
			} else if (client == player2) {
				player1.sendToClient(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void relayTurnInfo(Object msg, ConnectionToClient client) {
		try {
			if (client == player1) {
				player2.sendToClient(msg);
			} else if (client == player2) {
				player1.sendToClient(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addNewClient(ConnectionToClient client) {
		SubscribedClient newConnection = new SubscribedClient(client);
		SubscribersList.add(newConnection);

		try {
			if (client == player2) {
				player2.sendToClient("competitorTurn");
			} else if (client == player1 && player2 != null) {
				player1.sendToClient("competitorTurn");
			}
			client.sendToClient("client added successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void removeClient(ConnectionToClient client) {
		SubscribersList.removeIf(subscribedClient -> subscribedClient.getClient().equals(client));
	}
	@Override
	protected void clientConnected(ConnectionToClient client) {
		try {
			if (player1 == null) {
				playersymbol(client, "player1");
				notifyCompetitor(player2, "playerTurn");
			} else if (player2 == null) {
				playersymbol(client, "player2");
				notifyCompetitor(player1, "playerTurn");
			} else {
				notifyClientAndClose(client, "Game is full. Please try again later.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void playersymbol(ConnectionToClient client, String playerRole) throws IOException {
		if ("player1".equals(playerRole)) {
			player1 = client;
			player1.sendToClient(player1symbol == 1 ? "Yess , you are player X" : "Yess , you are player O");
		} else if ("player2".equals(playerRole)) {
			player2 = client;
			player2.sendToClient(player2symbol == 0 ? "Yess , you are player O" : "Yess , you are player X");
		}
	}

	private void notifyCompetitor(ConnectionToClient competitor, String message) throws IOException {
		if (competitor != null) {
			competitor.sendToClient(message);
		}
	}

	private void notifyClientAndClose(ConnectionToClient client, String message) throws IOException {
		client.sendToClient(message);
		client.close();
	}

	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		try {
			if (client == player1) {
				notifyCompetitorLeft(player2);
				player1 = null;
			} else if (client == player2) {
				notifyCompetitorLeft(player1);
				player2 = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void notifyCompetitorLeft(ConnectionToClient competitor) throws IOException {
		if (competitor != null) {
			competitor.sendToClient("competitorLeft");
		}
	}

	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


}

