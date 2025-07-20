package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.client.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import java.time.LocalTime;
import java.util.List;

public class SimpleServer extends AbstractServer {

	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		System.out.println("[SimpleServer] Listening for new connections on port " + port);
	}

	private Timer reportTimer;

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {

		String msgString;
		if(msg instanceof String){
			msgString = msg.toString();
		}

		else{
			msgString = "";
		}
		System.out.println(msgString);
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (msgString.startsWith("check_reservation_id;")) {
			String idToCheck = msgString.split(";")[1].trim();
			boolean exists = DataManager.checkIfIdHasReservation(idToCheck);
            try {
                client.sendToClient("id_exists:" + exists);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
		} else if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if (msgString.startsWith("feedback;")) {
			String[] parts = msgString.split(";", 5);
			int userId = Integer.parseInt(parts[1]);
			String message = parts[2];
			int rating = Integer.parseInt(parts[3]);
			String restaurant = parts[4];
			System.out.println(restaurant);

			Feedback feedback = new Feedback(userId, message, rating, LocalDateTime.now(), restaurant);
			DataManager.saveFeedback(DataManager.getPassword(), feedback);
			System.out.println("Feedback saved successfully.");

			List<Feedback> updatedFeedback = DataManager.getManagerFeedback();
			sendToAllClients(updatedFeedback);
		}

		else if (msgString.startsWith("complaint;")) {
			String[] parts = msgString.split(";", 4);
			int userId = Integer.parseInt(parts[1]);
			String complaintMessage = parts[2];
			String status = parts[3];

			// Fetch reservation to get the client's email
			Reservation reservation = DataManager.getReservationByUserId(userId);
			if (reservation == null) {
				System.out.println(" Reservation not found for userId: " + userId);
				return;
			}

			String clientEmail = reservation.getEmail();

			// Create complaint entity
			Complaint complaint = new Complaint(userId, complaintMessage, status, LocalDateTime.now(), reservation.getRestaurant());
			DataManager.saveComplaint(complaint);

			// Send email
			String subject = "We've received your complaint";
			String content = "Hi " + reservation.getName() + ",\n\nWe have received your complaint. Our team will look into it and get back to you shortly.\n\nThank you!";
			EmailSender.sendEmail(clientEmail, subject, content);

			// Start a 3-minute timer
			new java.util.Timer().schedule(
					new java.util.TimerTask() {
						@Override
						public void run() {
							System.out.println("3-minute timer completed for userId: " + userId);
						}
					},
					180_000 // 3 minutes
			);

			System.out.println("Complaint saved and email sent.");
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
		else if (msgString.startsWith("CheckTablesAvailability")) {
			try {
				String[] parts = msgString.split(";");
				String[] details = Arrays.copyOfRange(parts, 1, parts.length);
				List<String[]> reservedTables = DataManager.findOverlappingReservations(details);
				client.sendToClient(reservedTables);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}else if (msgString.startsWith("cancel_order;")) {
			System.out.println("[SimpleServer] → Entered cancel_order branch");
			String idNumber = msgString.split(";")[1].trim();

			// 1) Fetch the active “on” take‐away reservation
			Reservation reservation = DataManager.getActiveReservationById(idNumber, -1);
			if (reservation == null) {
				try {
					client.sendToClient("no_order_found");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (!reservation.isTakeAway()) {
					// Not a take‐away order
					try {
						client.sendToClient("no_order_found");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// 3) Compute time difference and fee
					LocalDateTime now = LocalDateTime.now();
					LocalDateTime pickupTime = reservation.getReceivingTime();
					Duration diff = Duration.between(now, pickupTime);

					try {
						long resId = reservation.getId();
						double totalOrderPrice = DataManager.getTotalOrderPriceForReservation(resId);

						double feeAmount;
						if (diff.toHours() >= 3) {
							feeAmount = 0.0;
						} else if (diff.toHours() >= 1) {
							feeAmount = totalOrderPrice * 0.50;
						} else if (diff.toMinutes() >= 0) {
							feeAmount = totalOrderPrice;
						} else {
							feeAmount = totalOrderPrice;
						}

						int feeShekels = (int) Math.ceil(feeAmount);
						String visa = reservation.getVisa();
						boolean paidByVisa = visa != null && !visa.trim().isEmpty();

						if (feeShekels > 0) {
							if(paidByVisa) {
								client.sendToClient("confirm_order_cancellation;" + feeShekels + ";" + idNumber);
							} else {
								reservation.setAmountDue(feeShekels);
								reservation.setStatus("off");
								reservation.setPayed(false);
								DataManager.updateReservation(reservation);
								System.out.println("[SimpleServer] → about to send order_cancellation_debt; fee="
										+ feeShekels + " id=" + idNumber);
								client.sendToClient("order_cancellation_debt;" + feeShekels + ";" + idNumber);
							}
						} else {
							// ── ≥ 3 hours → no fee.  Cancel immediately:
							reservation.setStatus("off");
							DataManager.updateReservation(reservation);

							if (reservation.getVisa() != null) {
								client.sendToClient("order_cancellation_success;0");
							} else {
								// Even if feeShekels==0 AND cash, we still want a “debt = 0” message
								reservation.setAmountDue(0);
								reservation.setPayed(false);
								DataManager.updateReservation(reservation);
								System.out.println("[SimpleServer] → about to send order_cancellation_debt; fee="
										+ feeShekels + " id=" + idNumber);
								client.sendToClient("order_cancellation_debt;0;" + idNumber);
							}
						}


					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else if (msgString.startsWith("process_order_cancellation;")) {
			String[] parts = msgString.split(";");
			String idNumber = parts[1];
			int fee = Integer.parseInt(parts[2]);

			// Re‐fetch the active reservation (just to be safe):
			Reservation reservation = DataManager.getActiveReservationById(idNumber, -1);
			String visa = reservation.getVisa();
			boolean paidByVisa = visa != null && !visa.trim().isEmpty();
			if (reservation != null) {
				if (paidByVisa) {
					// Visa path: charge VISA and send “order_cancellation_success;<fee>”
					boolean paymentSucceeded = true;
					try {
						if (paymentSucceeded) {
							reservation.setStatus("off");
							// Leave isPayed = true for Visa (assuming you had set it true at time of ordering)
							DataManager.updateReservation(reservation);
							client.sendToClient("order_cancellation_success;" + fee);
						} else {
							client.sendToClient("order_cancellation_failed;visa_error");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// ------------- CASH PATH -------------
					// 1) Compute total due (the same logic you already had):
					double totalOrderPrice =
							DataManager.getTotalOrderPriceForReservation(reservation.getId());
					int totalShekels = (int) Math.ceil(totalOrderPrice);

					// 2) Instead of marking as “paid,” leave isPayed = false, but record “debt”:
					reservation.setAmountDue(totalShekels);
					reservation.setStatus("off");      // still “off” (canceled)
					reservation.setPayed(false);       // explicitly ensure isPayed stays false
					DataManager.updateReservation(reservation);

					// 3) Notify client – “you have a debt of ₪ totalShekels under your ID”:
					try {
						client.sendToClient("order_cancellation_debt;" + totalShekels + ";" + idNumber);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				// No reservation found at all
				try {
					client.sendToClient("order_cancellation_failed;no_order_found");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		// ───────── (D) “cancel_reservation;<idNumber>” branch (unchanged) ─────────
		else if (msgString.startsWith("cancel_reservation;")) {
			System.out.println("[SimpleServer]   → Entered cancel_reservation branch");
			String idNumber = msgString.split(";")[1].trim();
			Reservation reservation = DataManager.getActiveReservationById(idNumber, -1);
			if (reservation == null) {
				try {
					client.sendToClient("no_reservation_found");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime reservationTime = reservation.getReservationTime();
				Duration diff = Duration.between(now, reservationTime);

				try {
					if (diff.toMinutes() <= 60) {
						int fee = reservation.getTotalGuests() * 10;
						System.out.println("[SimpleServer]   → Reservation within one hour (fee=" + fee + "). Sending confirm_cancellation;" + fee + ";" + idNumber);
						client.sendToClient("confirm_cancellation;" + fee + ";" + idNumber);
					} else {
						System.out.println("[SimpleServer]   → Reservation > 1 hour away. Cancelling with no fee.");
						reservation.setStatus("off");
						DataManager.updateReservation(reservation);
						System.out.println("[SimpleServer]   → Sending cancellation_success;0");
						client.sendToClient("cancellation_success;0");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// ───────── (D) “process_cancellation;<idNumber>;<fee>” branch (unchanged) ─────────
		else if (msgString.startsWith("process_cancellation;")) {
			String[] parts = msgString.split(";");
			String idNumber = parts[1];
			int fee = Integer.parseInt(parts[2]);

			Reservation reservation = DataManager.getActiveReservationById(idNumber, -1);

			try {
				if (reservation == null) {
					client.sendToClient("cancellation_failed;no_reservation_found");
				} else if (reservation.getVisa() != null) {
					// Visa path
					reservation.setStatus("off");
					DataManager.updateReservation(reservation);
					client.sendToClient("cancellation_success;" + fee);
				} else {
					// Cash path
					reservation.setAmountDue(fee);
					reservation.setStatus("off");
					reservation.setPayed(false);
					DataManager.updateReservation(reservation);
					client.sendToClient("reservation_cancellation_debt;" + fee + ";" + idNumber);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}



		// ───────── (E) “Get Manager feedback” branch (unchanged) ─────────
		else if (msgString.startsWith("Get Manager feedback")) {
			try {
				List<Feedback> list = DataManager.getManagerFeedback();
				if (list != null && !list.isEmpty()) {
					sendToAllClients(list);

				} else {
					sendToAllClients(new ArrayList<Feedback>());

				}
			} catch (Exception e) {
				e.printStackTrace();
			}


		} else if (msgString.equals("Request Menu")) {
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
			List<PriceConfirmation> listPrice = DataManager.addPriceConfirmation(mealName, oldPrice, newPrice);
			sendToAllClients(listPrice);
		}

		else if (msgString.startsWith("Update discount")) {
			String details = msgString.substring("Update discount".length()).trim();
			String[] parts = details.split("\"");

			double percentage = Double.parseDouble(parts[1].trim());
			String category = parts[3].trim();
			System.out.println(percentage);
			System.out.println(category);
			sendToAllClients("Get Discount Confirm \"" + percentage);
			List<Discounts> dis =DataManager.addDiscountConfirmation(percentage,category);
			sendToAllClients(dis);
		}



		else if (msgString.startsWith("Update description")) {
			String details = msgString.substring("Update Description".length()).trim();

			String[] parts = details.split("\"");

			String mealName = parts[1];
			String mealIngredient = parts[3].trim();

			try {
				// Call the DataManager function to update the meal price
				if(DataManager.updateMealIngredient(mealName, mealIngredient) != 1){
					System.out.println("Update meal failed");
					client.sendToClient(mealName + " Description update has failed");
				}
				else {
					client.sendToClient(mealName + " Description has updated successfully");
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
					System.out.println("Description has updated successfully");

				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					client.sendToClient(mealName + "Description update has failed");
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}
		else if (msgString.equals("REQUEST_MONTHLY_REPORTS")) {
			List<MonthlyReport> reports = DataManager.getAllReports();  // Make sure this method exists

			// Send back the list directly (Java serialization)
			try {
				client.sendToClient(reports);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (msgString.startsWith("Update preferences")) {
			String details = msgString.substring("Update preferences".length()).trim();

			String[] parts = details.split("\"");

			String mealName = parts[1];
			String mealIngredient = parts[3].trim();

			try {

				if(DataManager.updateMealPref(mealName, mealIngredient) != 1){
					System.out.println("Update meal failed");
					client.sendToClient(mealName + " preferences update has failed");
				}
				else {
					client.sendToClient(mealName + " preferences has updated successfully");
					try {
						List<Meal> menu = DataManager.requestMenu();
						if(menu != null && !menu.isEmpty()) {
							sendToAllClients(menu); //
						}
						else{
							System.out.println("empty menu");
							client.sendToClient("No menu available");
						}
					} catch (Exception exception){
						exception.printStackTrace();
					}
					System.out.println("preferences has updated successfully");

				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					client.sendToClient(mealName + "preferences update has failed");
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}

		else if (msgString.startsWith("RequestMealCategory")) {
			String name = msgString.substring("RequestMealCategory".length()).trim().replace("\"", "");
			String category = DataManager.getMealCategoryByName(name);
			if (category != null) {
				try {
					client.sendToClient( "MealCategory:" + category);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				try {
					client.sendToClient( "MealCategory:NotFound");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		else if (msgString.startsWith("payment-confirmed:")) {
			String customerId = msgString.split(":")[1];
			DataManager.markPaymentAsPaidInDatabase(customerId);
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

				if (DataManager.mealExist(Name)) {
					client.sendToClient("MealExists");
				}
				else {

					if (DataManager.addMeal(Name, Description, Preferences, Price, Image, Category) != 1) {
						client.sendToClient(Name + " add failed");
					} else {
						client.sendToClient("Meal has been added successfully");

						try {
							List<Meal> menu = DataManager.requestMenu();
							if (menu != null && !menu.isEmpty()) {
								sendToAllClients(menu);
							} else {

								client.sendToClient("No menu available");
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}


					}
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
		} else if (msg instanceof List<?>) {
		List<?> incoming = (List<?>) msg;
		if (!incoming.isEmpty() && incoming.get(0) instanceof MealOrder) {
			List<MealOrder> orders = (List<MealOrder>) incoming;
			List<MealOrder> mergedorder = DataManager.mergeDuplicateOrders(orders);
			DataManager.saveMealOrders(mergedorder);
			try {
				/*client.sendToClient("Reservation saved successfully");*/
				client.sendToClient("Meal orders saved");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		/*else if (msg instanceof ReservationRequest) {
			ReservationRequest request = (ReservationRequest) msg;
			Reservation reservation = request.getReservation();
			boolean shouldSave = request.isShouldSave();

			if (shouldSave) {
				System.out.println("Saving reservation to DB...");
				boolean exists = DataManager.checkIfIdHasReservation(reservation.getIdNumber());
				if (exists) {
					try {
						client.sendToClient("Reservation failed: ID already used.");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					DataManager.saveReservation(reservation);
					try {
						client.sendToClient("Reservation saved successfully");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			} else {
				System.out.println("Checking availability for reservation...");
				List<HostingTable> availability = DataManager.getAvailableTables(reservation);
				System.out.println("Available tables: " + availability.size());
				try {
					client.sendToClient(availability);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}*/
		else if (msg instanceof ReservationRequest) {
			ReservationRequest request = (ReservationRequest) msg;
			Reservation reservation = request.getReservation();
			boolean shouldSave = request.isShouldSave();

			if (shouldSave) {
				System.out.println("Saving reservation to DB...");
				boolean exists = DataManager.checkIfIdHasReservation(reservation.getIdNumber());
				if (exists) {
					try {
						client.sendToClient("Reservation failed: ID already used.");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {
					try {
						DataManager.saveReservation(reservation);

						// Notify only this client
						client.sendToClient("Reservation saved successfully");

						// Send reservation to this client only, for client-side processing if needed
						client.sendToClient(reservation);

						// Notify all other clients about reservation update
						sendToAllClientsExceptSender(new Message("reservation_update", reservation), client);

						//sent to all clients for update
						List<HostingTable> updatedAvailability = DataManager.getAvailableTables(reservation);
						sendToAllClients(updatedAvailability);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Checking availability for reservation...");
				List<HostingTable> availability = DataManager.getAvailableTables(reservation);
				System.out.println("Available tables: " + availability.size());
				try {
					client.sendToClient(availability);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		else if (msg instanceof Reservation) {
			System.out.println("Received raw Reservation (availability check assumed)");
			List<HostingTable> availability = DataManager.getAvailableTables((Reservation) msg);
			System.out.println("Available tables: " + availability.size());
            try {
                client.sendToClient(availability);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (msgString.equals("Get Price Confirmations")) {
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
		else if (msgString.equals("get_all_restaurants")) {
			try {
				List<Restaurant> restaurants = DataManager.getAllRestaurants();
				if (restaurants != null && !restaurants.isEmpty()) {
					System.out.println("Sending list of restaurants to client. Size: " + restaurants.size());
					client.sendToClient(restaurants);
				} else {
					System.out.println("No restaurants found.");
					client.sendToClient(new ArrayList<Restaurant>());  // Send an empty list to avoid client-side hang
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					client.sendToClient("Error: Could not fetch restaurants.");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
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
		else if (msgString.startsWith("check_id_type:")) {
			System.out.println("Inside check_id_type: " + msgString);

			String[] parts = msgString.split(":");
			String idNumber = parts[1].trim();
			int restaurantId = Integer.parseInt(parts[2].trim());

			Reservation reservation = DataManager.getActiveReservationById(idNumber, restaurantId);

			try {
				if (reservation == null) {
					client.sendToClient("id_type:not_found");
				} else if (reservation.isTakeAway()) {
					client.sendToClient("id_type:takeaway");
				} else {
					client.sendToClient("id_type:reservation");
				}
			} catch (IOException e) {
				e.printStackTrace();
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

						sendToAllClients(DataManager.requestMenu());

					} else {

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
	public void sendToAllClientsExceptSender(Object msg, ConnectionToClient sender) {
		for (Thread clientThread : getClientConnections()) {
			if (clientThread instanceof ConnectionToClient) {
				ConnectionToClient client = (ConnectionToClient) clientThread;
				if (client != sender) {
					try {
						client.sendToClient(msg);
					} catch (IOException e) {
						System.err.println("Failed to send to client: " + client);
						e.printStackTrace();
					}
				}
			}
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

	private void scheduleReportGeneration() {
		reportTimer = new Timer();

		// Immediate first generation
		generateAllReports();

		// Every 5 minutes after that
		reportTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				generateAllReports();
			}
		}, 5 * 60 * 1000, 5 * 60 * 1000); // 5 minutes
	}

	private void generateAllReports() {
		List<Restaurant> restaurants = DataManager.getAllRestaurants();
		for (Restaurant restaurant : restaurants) {
			DataManager.generateReportForRestaurant(restaurant);
		}
		System.out.println("[REPORT SYSTEM] Reports generated at: " + LocalTime.now());
	}

}
