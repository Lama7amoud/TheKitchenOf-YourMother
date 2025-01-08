package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;


public class SimpleServer extends AbstractServer {

	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		System.out.println(msgString);
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if (msgString.equals("Request menu")) {
			try {
				List<Meal> menu = DataManager.requestMenu();
				if(menu != null && !menu.isEmpty()) {
					client.sendToClient(menu);
				}
				else{
					System.out.println("empty menu");
					client.sendToClient("No menu available");
				}
			} catch (Exception exception){
				exception.printStackTrace();
			}

		}

		else if (msgString.startsWith("Update price")) {
			// Remove the "Update price" prefix
			String details = msgString.substring("Update price".length()).trim();

			// Assuming meal name and price are enclosed in double quotes!
			String[] parts = details.split("\"");

			// Extract meal name (inside the first quotes) and meal price (inside the second quotes)
			String mealName = parts[1];
			int mealPrice = Integer.parseInt(parts[2].trim());

			try {
				// Call the DataManager function to update the meal price
				if(DataManager.updateMealPrice(mealName, mealPrice) != 1){
					System.out.println("Update meal failed");
					client.sendToClient(mealName + " price update has failed");
				}
				else {
					client.sendToClient(mealName + " price has updated successfully");
					System.out.println("price has updated successfully");

				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					client.sendToClient(mealName + " price update has failed");
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		} else if (msgString.startsWith("Remove client")) {
			if(!SubscribersList.isEmpty()){
				for (SubscribedClient subscribedClient : SubscribersList) {
					if (subscribedClient.getClient().equals(client)) {
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}

		} else{
			System.out.println("The server didn't recognize this " + msgString + " signal");
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
