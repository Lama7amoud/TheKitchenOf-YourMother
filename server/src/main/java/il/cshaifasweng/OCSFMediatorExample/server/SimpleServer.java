package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.client.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		else if(msgString.startsWith("logIn:")){
			AuthorizedUser currentUser = DataManager.checkPermission(msgString);
			try {
				System.out.println(currentUser.getMessageToServer());
				client.sendToClient(currentUser);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if(msgString.startsWith("Get branch details")){
			try {
				int index = msgString.indexOf(";");
				String restaurantId = msgString.substring(index + 1).trim();
				Restaurant restaurant = DataManager.getRestaurant(restaurantId);
				if(restaurant != null) {
					client.sendToClient(restaurant);
				}
				else{
					System.out.println("Restaurant not found");
					client.sendToClient("No available restaurant details");
				}
			}
			catch (Exception exception){
				exception.printStackTrace();
			}
		}
		else if (msgString.startsWith("Request")) {
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
		/*else if (msgString.equals("Request Haifa menu")) {
			try {
				List<Meal> HaifaMenu = DataManager.requestHaifaMenu();
				if (HaifaMenu != null && !HaifaMenu.isEmpty()) {
					client.sendToClient(HaifaMenu);
				} else {
					System.out.println("empty menu");
					client.sendToClient("No menu available");
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		else if (msgString.equals("Request Tel-Aviv menu")) {
			try {
				List<Meal> TelAvivMenu = DataManager.requestTelAvivMenu();
				if (TelAvivMenu != null && !TelAvivMenu.isEmpty()) {
					client.sendToClient(TelAvivMenu);
				} else {
					System.out.println("empty menu");
					client.sendToClient("No menu available");
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

		else if (msgString.equals("Request Nahariya menu")) {
			try {
				List<Meal> NahariyaMenu = DataManager.requestNahariyaMenu();
				if (NahariyaMenu != null && !NahariyaMenu.isEmpty()) {
					client.sendToClient(NahariyaMenu);
				} else {
					System.out.println("empty menu");
					client.sendToClient("No menu available");
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}*/

		else if (msgString.startsWith("Update price")) {
			// Remove the "Update price" prefix
			String details = msgString.substring("Update price".length()).trim();

			// Assuming meal name and price are enclosed in double quotes!
			String[] parts = details.split("\"");

			// Extract meal name (inside the first quotes) and meal price (inside the second quotes)
			// Remember that parts[] now looks like this: ["","meal name","","meal price"]
			String mealName = parts[1];
			double mealPrice = Double.parseDouble(parts[3].trim());

			try {
				// Call the DataManager function to update the meal price
				if(DataManager.updateMealPrice(mealName, mealPrice) != 1){
					System.out.println("Update meal failed");
					client.sendToClient(mealName + " price update has failed");
				}
				else {
					client.sendToClient(mealName + " price has updated successfully");
					try {
						List<Meal> menu = DataManager.requestMenu();
						if(menu != null && !menu.isEmpty()) {
							sendToAllClients(menu); // update to all clients
						}
						else{
							System.out.println("empty menu");
							client.sendToClient("No menu available");
						}
					} catch (Exception exception){
						exception.printStackTrace();
					}
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
		} else if (msgString.startsWith("Update Ingredient ")) {
			// Remove the "Update Ingredient" prefix
			String details = msgString.substring("Update Ingredient ".length()).trim();

			// Assuming meal name and price are enclosed in double quotes!
			String[] parts = details.split("\"");

			// Extract meal name (inside the first quotes) and meal price (inside the second quotes)
			// Remember that parts[] now looks like this: ["","meal name","","meal price"]
			String mealName = parts[1];
			String mealIngredient = parts[3].trim();

			try {
				// Call the DataManager function to update the meal price
				if(DataManager.updateMealIngredient(mealName, mealIngredient) != 1){
					System.out.println("Update meal failed");
					client.sendToClient(mealName + " Ingredient update has failed");
				}
				else {
					client.sendToClient(mealName + "Ingredient has updated successfully");
					try {
						List<Meal> menu = DataManager.requestMenu();
						if(menu != null && !menu.isEmpty()) {
							sendToAllClients(menu); // update to all clients
						}
						else{
							System.out.println("empty menu");
							client.sendToClient("No menu available");
						}
					} catch (Exception exception){
						exception.printStackTrace();
					}
					System.out.println("Ingredient has updated successfully");

				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					client.sendToClient(mealName + " Ingredient update has failed");
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		else if (msgString.startsWith("remove client")) {
			int index = msgString.indexOf(";");
			String username = msgString.substring(index + 1).trim();
			if(!(username.equals("Customer"))){
				DataManager.disconnectUser(username);
			}
			if(!SubscribersList.isEmpty()){
				for (SubscribedClient subscribedClient : SubscribersList) {
					if (subscribedClient.getClient().equals(client)) {
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}

		}else if(msgString.startsWith("log out")){
			int index = msgString.indexOf(";");
			String username = msgString.substring(index + 1).trim();
			DataManager.disconnectUser(username);
		}
		else {
			System.out.println("The server didn't recognize this " + msgString + " signal");
		}
	}

	public void sendToAllClients(Object message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}




}
