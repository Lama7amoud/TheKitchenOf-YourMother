package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.LocalDateTime;
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
		else if (msgString.startsWith("feedback;")) {
			String[] parts = msgString.split(";", 4);
			if (parts.length < 4) {
				System.out.println("Invalid feedback format");
				return;
			}

			int userId = Integer.parseInt(parts[1]);
			String message = parts[2];
			int rating = Integer.parseInt(parts[3]);

			Feedback feedback = new Feedback(userId, message, rating, LocalDateTime.now());
			DataManager.saveFeedback(feedback);
			System.out.println("Feedback saved successfully.");
		}

		else  if (msgString.equals("REQUEST_MONTHLY_REPORTS")) {
			// Fetch the monthly reports from DataManager
			List<MonthlyReport> reports = DataManager.getAllReports();

			// Prepare the string response
			StringBuilder response = new StringBuilder();
			for (MonthlyReport report : reports) {
				// Append the date and total delivery orders
				response.append("Report for: ").append(report.getReportDate()).append("\n");
				response.append("Total Delivery Orders: ").append(report.getTotalDeliveryOrders()).append("\n");
				// Add more details as needed (e.g., complaints, customer count, etc.)
				response.append("-------------------------------------------------\n");
			}

			// Send the reports back to the client as a string
			try {
				client.sendToClient("REQUEST_MONTHLY_REPORTS\n" + response.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (msgString.startsWith("complaint;")) {
			String[] parts = msgString.split(";", 4);
			if (parts.length < 4) {
				System.out.println("Invalid complaint format");
				return;
			}

			int userId = Integer.parseInt(parts[1]);
			String complaintText = parts[2];
			String status = parts[3];

			Complaint complaint = new Complaint(userId, complaintText, status,  LocalDateTime.now());
			DataManager.saveComplaint(complaint);
			System.out.println("Complaint saved successfully.");
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

		else if (msgString.startsWith("Update price")) {
			String details = msgString.substring("Update price".length()).trim();
			String[] parts = details.split("\"");

			String mealName = parts[1];
			double newPrice = Double.parseDouble(parts[3].trim());
			double oldPrice = DataManager.getCurrentMealPrice(mealName);
			sendToAllClients("Get Confirm \""+mealName+"\""+oldPrice+"\""+newPrice);
			DataManager.addPriceConfirmation(mealName, oldPrice, newPrice);
		}

		else if (msgString.startsWith("Update discount")) {
			String details = msgString.substring("Update discount".length()).trim();
			String[] parts = details.split("\"");

			double percentage = Double.parseDouble(parts[1].trim());
			String category = parts[3].trim();
			System.out.println(percentage);
			sendToAllClients("Get Discount Confirm \"" + percentage);
			DataManager.addDiscountConfirmation(percentage,category);
		}



		else if (msgString.startsWith("Update Ingredient ")) {
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


		} else if (msgString.startsWith("Add Meal")) {

			String details = msgString.substring("Add meal".length()).trim();

			String[] parts = details.split("\"");

			try {
				// Extract values from quotes: skip empty strings from split
				String Name = parts[1];
				String Description = parts[3];
				String Preferences = parts[5];
				double Price = Double.parseDouble(parts[7]);
				String Image = parts[9];
				String Category = parts[11];



				// Call your data manager to insert the meal
				if (DataManager.addMeal(Name, Description, Preferences, Price, Image,Category) != 1) {
					System.out.println("Add meal failed");
					client.sendToClient(Name + " add failed");
				} else {
					client.sendToClient( "Meal has been added successfully");

					// Optionally update the menu for all clients
					try {
						List<Meal> menu = DataManager.requestMenu();
						if (menu != null && !menu.isEmpty()) {
							sendToAllClients(menu);
						} else {
							System.out.println("empty menu");
							client.sendToClient("No menu available");
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}

					System.out.println("Meal has been added successfully");
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					client.sendToClient("Error while adding meal: " + e.getMessage());
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		else if (msgString.startsWith("Remove Meal")) {
			String details = msgString.substring("Remove Meal".length()).trim();
			String mealName = details.replace("\"", ""); // Remove quotes

			String category = DataManager.removeMealByName(mealName);

			try {
				if (category != null) {
					client.sendToClient("Meal '" + mealName + "' removed successfully.");

					switch (category) {
						case "shared meal" -> {
							sendToAllClients(DataManager.requestMenu());
							sendToAllClients(DataManager.requestMenu());
							sendToAllClients(DataManager.requestMenu());
						}
						case "special1" -> sendToAllClients(DataManager.requestMenu());
						case "special2" -> sendToAllClients(DataManager.requestMenu());
						case "special3" -> sendToAllClients(DataManager.requestMenu());
						default -> System.out.println("Unknown category: " + category);
					}
				} else {
					client.sendToClient("Failed to remove meal: " + mealName);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		else if (msgString.equals("Get Price Confirmations")) {
			try {
				List<PriceConfirmation> list = DataManager.getPriceConfirmations();
				if (list != null && !list.isEmpty()) {
					client.sendToClient(list);
				} else {
					client.sendToClient(new ArrayList<PriceConfirmation>());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (msgString.equals("Get Discount Confirmations")) {
			try {
				List<Discounts> list = DataManager.getDiscountConfirmations();
				if (list != null && !list.isEmpty()) {
					client.sendToClient(list);
				} else {
					client.sendToClient(new ArrayList<Discounts>());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (msgString.startsWith("Confirm Price")) {
			String details = msgString.substring("Confirm Price".length()).trim();
			String[] parts = details.split("\"");
			String mealName = parts[1];
			double newPrice = Double.parseDouble(parts[3]);
			int id = Integer.parseInt(parts[5]);

			int updated = DataManager.updateMealPrice(mealName, newPrice);
			boolean x = DataManager.removePriceConfirmation(id);
			if (updated == 1 && x) {
				List<Meal> updatedMenu = DataManager.requestMenu();
				sendToAllClients(updatedMenu);
				List<PriceConfirmation> updatePriceConfirmation = DataManager.getPriceConfirmations();
				sendToAllClients(updatePriceConfirmation);
			}
		}

		else if (msgString.startsWith("Reject Price")) {
			String details = msgString.substring("Reject Price".length()).trim();
			String[] parts = details.split("\"");

			String mealName = parts[1];
			double newPrice = Double.parseDouble(parts[3]);
			int id = Integer.parseInt(parts[5]);

			boolean x = DataManager.removePriceConfirmation(id);
			if (x) {
				List<PriceConfirmation> updatePriceConfirmation = DataManager.getPriceConfirmations();
				sendToAllClients(updatePriceConfirmation);
			}
		}


		else if (msgString.startsWith("Confirm Discount")) {
			String details = msgString.substring("Confirm Discount".length()).trim();
			String[] parts = details.split("\"");

			double discount = Double.parseDouble(parts[1].trim());
			int id = Integer.parseInt(parts[3].trim());
			String category = parts[5].trim();

			int updated = DataManager.makediscount(discount,category);
			boolean x = DataManager.removeDiscountConfirmation(id);
			if (updated == 1 && x) {
				List<Meal> updatedMenu = DataManager.requestMenu();
				sendToAllClients(updatedMenu);
				List<Discounts> updateDiscountConfirmation = DataManager.getDiscountConfirmations();
				sendToAllClients(updateDiscountConfirmation);
			}
		}

		else if (msgString.startsWith("Reject Discount")) {
			String details = msgString.substring("Reject Discount".length()).trim();
			String[] parts = details.split("\"");


			double discount = Double.parseDouble(parts[1]);
			int id = Integer.parseInt(parts[3]);

			boolean x = DataManager.removeDiscountConfirmation(id);
			if (x) {
				List<Discounts> updateDiscountConfirmation = DataManager.getDiscountConfirmations();
				sendToAllClients(updateDiscountConfirmation);
			}
		}

		else if (msgString.startsWith("Change Category Meal")){
			String details = msgString.substring("Change Category Meal".length()).trim();
			String[] parts = details.split("\"");

			try {
				String name = parts[1];
				String fromCategory = parts[3];
				String toCategory = parts[5];

				String newCategory = DataManager.changeMealCategory(name, fromCategory, toCategory);
				//System.out.println(newCategory);
				if (newCategory != null) {
					client.sendToClient("Meal '" + name + "' category changed to " + toCategory);

					// Refresh relevant menus
					if (fromCategory.equals("shared meal") || toCategory.equals("shared meal")) {
						// If going to or from shared → update all


						sendToAllClients(DataManager.requestMenu());

					} else {
						// Just between specials
						if (fromCategory.equals("special1") || toCategory.equals("special1")) {
							sendToAllClients(DataManager.requestMenu());
						}
						if (fromCategory.equals("special2") || toCategory.equals("special2")) {
							sendToAllClients(DataManager.requestMenu());
						}
						if (fromCategory.equals("special3") || toCategory.equals("special3")) {
							sendToAllClients(DataManager.requestMenu());
						}
					}
				} else {
					client.sendToClient("Failed to change category for meal: " + name);
				}

			} catch (Exception e) {
				e.printStackTrace();
				try {
					client.sendToClient("Error processing category change.");
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
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
