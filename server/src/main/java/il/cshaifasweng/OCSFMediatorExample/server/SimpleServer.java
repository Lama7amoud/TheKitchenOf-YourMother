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
			if (msg instanceof String) {
				String command = (String) msg;

				if (command.equals("save_reservation")) {
					client.setInfo("save_reservation", true);
					return;
				}

				if (command.startsWith("check_reservation_id;")) {
					String idToCheck = command.split(";")[1].trim();
					boolean exists = DataManager.checkIfIdHasReservation(idToCheck);
					try {
						client.sendToClient("id_exists:" + exists);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}

				if (command.startsWith("add client")) {
					SubscribedClient connection = new SubscribedClient(client);
					SubscribersList.add(connection);
					try {
						client.sendToClient("client added successfully");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}

				if (command.startsWith("logIn:")) {
					AuthorizedUser currentUser = DataManager.checkPermission(command);
					try {
						client.sendToClient(currentUser);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}

				if (command.startsWith("Get branch details")) {
					try {
						int index = command.indexOf(";");
						String restaurantId = command.substring(index + 1).trim();
						Restaurant restaurant = DataManager.getRestaurant(restaurantId);
						if (restaurant != null) {
							client.sendToClient(restaurant);
						} else {
							client.sendToClient("No available restaurant details");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				}

				if (command.equals("Request menu")) {
					try {
						List<Meal> menu = DataManager.requestMenu();
						client.sendToClient(menu != null && !menu.isEmpty() ? menu : "No menu available");
					} catch (Exception e) {
						e.printStackTrace();
					}
					return;
				}

				if (command.startsWith("Update price")) {
					String details = command.substring("Update price".length()).trim();
					String[] parts = details.split("\"");
					String mealName = parts[1];
					double mealPrice = Double.parseDouble(parts[3].trim());

					try {
						if (DataManager.updateMealPrice(mealName, mealPrice) != 1) {
							client.sendToClient(mealName + " price update has failed");
						} else {
							client.sendToClient(mealName + " price has updated successfully");
							List<Meal> menu = DataManager.requestMenu();
							if (menu != null && !menu.isEmpty()) {
								sendToAllClients(menu);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						try {
							client.sendToClient(mealName + " price update has failed");
						} catch (IOException ioException) {
							ioException.printStackTrace();
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
					try {
						client.sendToClient(warning);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}

				System.out.println("The server didn't recognize this " + command + " signal");
			}

			if (msg instanceof Reservation) {
				Reservation reservation = (Reservation) msg;

				Object saveFlag = client.getInfo("save_reservation");
				if (saveFlag != null && (boolean) saveFlag) {
					System.out.println("Saving reservation to DB...");
					boolean exists = DataManager.checkIfIdHasReservation(reservation.getIdNumber());
					if (exists) {
						client.sendToClient("Reservation failed: ID already used.");
					} else {
						DataManager.saveReservation(reservation);
						client.sendToClient("Reservation saved successfully");
					}
					client.setInfo("save_reservation", false);

				} else {
					System.out.println("Checking availability for reservation...");
					List<HostingTable> availability = DataManager.getAvailableTables(reservation);
					System.out.println("Available tables: " + availability.size());
					client.sendToClient(availability);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
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
