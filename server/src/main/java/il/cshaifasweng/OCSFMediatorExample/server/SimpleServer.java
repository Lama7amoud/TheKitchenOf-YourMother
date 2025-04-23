package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.client.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
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
		try {
			// === String commands ===
			if (msg instanceof String) {
				String command = (String) msg;

				if (command.startsWith("check_reservation_id;")) {
					String idToCheck = command.split(";")[1].trim();
					boolean exists = DataManager.checkIfIdHasReservation(idToCheck);
					client.sendToClient("id_exists:" + exists);
					return;
				}

				if (command.startsWith("add client")) {
					SubscribedClient connection = new SubscribedClient(client);
					SubscribersList.add(connection);
					client.sendToClient("client added successfully");
					return;
				}

				if (command.startsWith("logIn:")) {
					AuthorizedUser currentUser = DataManager.checkPermission(command);
					client.sendToClient(currentUser);
					return;
				}

				if (command.startsWith("Get branch details")) {
					int index = command.indexOf(";");
					String restaurantId = command.substring(index + 1).trim();
					Restaurant restaurant = DataManager.getRestaurant(restaurantId);
					if (restaurant != null) {
						client.sendToClient(restaurant);
					} else {
						client.sendToClient("No available restaurant details");
					}
					return;
				}
				if (command.startsWith("cancel_reservation;")) {
					String idNumber = command.split(";")[1].trim();
					boolean success = DataManager.cancelReservationById(idNumber);
					client.sendToClient(success ? "Reservation cancelled successfully" : "Cancellation failed: Reservation not found");
					return;
				}

				if (command.equals("Request menu")) {
					List<Meal> menu = DataManager.requestMenu();
					client.sendToClient(menu != null && !menu.isEmpty() ? menu : "No menu available");
					return;
				}

				if (command.startsWith("Update price")) {
					String details = command.substring("Update price".length()).trim();
					String[] parts = details.split("\"");
					String mealName = parts[1];
					double mealPrice = Double.parseDouble(parts[3].trim());

					if (DataManager.updateMealPrice(mealName, mealPrice) != 1) {
						client.sendToClient(mealName + " price update has failed");
					} else {
						client.sendToClient(mealName + " price has updated successfully");
						List<Meal> updatedMenu = DataManager.requestMenu();
						if (updatedMenu != null && !updatedMenu.isEmpty()) {
							sendToAllClients(updatedMenu);
						}
					}
					return;
				}

				if (command.startsWith("remove client")) {
					String username = command.split(";")[1].trim();
					if (!username.equals("Customer")) {
						DataManager.disconnectUser(username);
					}
					SubscribersList.removeIf(sub -> sub.getClient().equals(client));
					return;
				}

				if (command.startsWith("log out")) {
					String username = command.split(";")[1].trim();
					DataManager.disconnectUser(username);
					return;
				}

				if (command.startsWith("#warning")) {
					Warning warning = new Warning("Warning from server!");
					client.sendToClient(warning);
					return;
				}

				System.out.println("The server didn't recognize this " + command + " signal");
			}

			// === ReservationRequest wrapper ===
			if (msg instanceof ReservationRequest) {
				ReservationRequest request = (ReservationRequest) msg;
				Reservation reservation = request.getReservation();
				boolean shouldSave = request.isShouldSave();

				if (shouldSave) {
					System.out.println("Saving reservation to DB...");
					boolean exists = DataManager.checkIfIdHasReservation(reservation.getIdNumber());
					if (exists) {
						client.sendToClient("Reservation failed: ID already used.");
					} else {
						DataManager.saveReservation(reservation);
						client.sendToClient("Reservation saved successfully");
					}
				} else {
					System.out.println("Checking availability for reservation...");
					List<HostingTable> availability = DataManager.getAvailableTables(reservation);
					System.out.println("Available tables: " + availability.size());
					client.sendToClient(availability);
				}
				return;
			}

			// === Raw Reservation (e.g., from ConfirmOrder initialize) ===
			if (msg instanceof Reservation) {
				System.out.println("Received raw Reservation (availability check assumed)");
				List<HostingTable> availability = DataManager.getAvailableTables((Reservation) msg);
				System.out.println("Available tables: " + availability.size());
				client.sendToClient(availability);
				return;
			}

		} catch (Exception e) {
			System.err.println("Exception during message handling:");
			e.printStackTrace();
		}
	}

	public void sendToAllClients(Object message) {
		for (SubscribedClient subscribedClient : SubscribersList) {
			try {
				subscribedClient.getClient().sendToClient(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
