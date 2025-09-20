package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import java.util.List;

public class SimpleServer extends AbstractServer {

	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
		System.out.println("[SimpleServer] Listening for new connections on port " + port);
	}

	private Timer reportTimer;

	// ─────────────────────────────────────────────────────────────────────────────
	// Cancellation fee helpers
	// ─────────────────────────────────────────────────────────────────────────────
	private int computeOrderCancellationFee(Reservation r) {
		// Robust: use receivingTime if present; otherwise fall back to reservationTime
		LocalDateTime now = LocalDateTime.now();

		LocalDateTime pickup = r.getReceivingTime();
		if (pickup == null) {
			pickup = r.getReservationTime(); // fallback for older flows
		}
		if (pickup == null) {
			System.out.println("[CANCEL-FEE] pickup/reservation time is null → fee=0");
			return 0;
		}

		long minutesToPickup = java.time.temporal.ChronoUnit.MINUTES.between(now, pickup);

		// Debug to verify your band selection
		System.out.printf("[CANCEL-FEE] resId=%d now=%s pickup=%s minutesToPickup=%d%n",
				r.getId(), now, pickup, minutesToPickup);

		double totalOrderPrice = DataManager.getTotalOrderPriceForReservation(r.getId());
		double feeAmount;

		if (minutesToPickup >= 180) {          // >= 3 hours
			feeAmount = 0.0;
		} else if (minutesToPickup >= 60) {    // 1–3 hours
			feeAmount = totalOrderPrice * 0.50;
		} else if (minutesToPickup >= 0) {     // 0–59 minutes
			feeAmount = totalOrderPrice;
		} else {                               // time already passed
			feeAmount = totalOrderPrice;
		}
		return (int) Math.ceil(feeAmount);
	}

	private int computeReservationCancellationFee(Reservation r) {
		long minutesToReservation =
				java.time.Duration.between(LocalDateTime.now(), r.getReservationTime()).toMinutes();
		if (minutesToReservation <= 60) {
			return r.getTotalGuests() * 10;
		}
		return 0;
	}

	// ─────────────────────────────────────────────────────────────────────────────

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {

		String msgString;
		if (msg instanceof String) {
			msgString = msg.toString();
		} else {
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
		} else if (msgString.startsWith("add client")) {
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if (msgString.startsWith("feedback;")) {
			// Keep -1 to preserve empty fields if any
			String[] parts = msgString.split(";", -1);

			if (parts.length < 6) {
				try { client.sendToClient("feedback has not inserted"); } catch (IOException ignored) {}
				return;
			}

			String feedbackMsg = parts[1];
			int rating;
			int restaurantId;
			try {
				rating = Integer.parseInt(parts[2].trim());
				restaurantId = Integer.parseInt(parts[3].trim());
			} catch (NumberFormatException nfe) {
				try { client.sendToClient("feedback has not inserted"); } catch (IOException ignored) {}
				return;
			}

			String idNumber = parts[4].trim();
			String name     = parts[5].trim();

			boolean ok = DataManager.userMatchesActiveReservationForFeedback(idNumber, name, restaurantId);
			if (!ok) {
				try { client.sendToClient("user not exist"); } catch (IOException ignored) {}
				return;
			}

			boolean feedbackConfirm = DataManager.saveFeedback(feedbackMsg, rating, restaurantId, idNumber, name);

			try {
				if (feedbackConfirm) {
					client.sendToClient("feedback inserted successfully");
					sendToAllClients("feedback updated");
					List<Feedback> updated = DataManager.getManagerFeedback();
					sendToAllClients(updated);
				} else {
					client.sendToClient("feedback has not inserted");
				}
				client.sendToClient(DataManager.getManagerFeedback());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (msgString.startsWith("logIn:")) {
			User currentUser = DataManager.checkPermission(msgString);
			try {
				System.out.println(currentUser.getMessageToServer());
				client.sendToClient(currentUser);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if (msgString.startsWith("Get branch details")) {
			try {
				int index = msgString.indexOf(";");
				String restaurantId = msgString.substring(index + 1).trim();
				Restaurant restaurant = DataManager.getRestaurant(restaurantId);
				if (restaurant != null) {
					client.sendToClient(restaurant);
				} else {
					System.out.println("Restaurant not found");
					client.sendToClient("No available restaurant details");
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else if (msgString.startsWith("CheckTablesAvailability")) {
			try {
				String[] parts = msgString.split(";");
				String[] details = Arrays.copyOfRange(parts, 1, parts.length);
				List<String[]> reservedTables = DataManager.findOverlappingReservations(details);
				client.sendToClient(reservedTables);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		// ─────────────────────────────────────────────────────────────────────────
		// TAKE-AWAY CANCELLATION (by ID only) → use unified helper
		// ─────────────────────────────────────────────────────────────────────────
		else if (msgString.startsWith("cancel_order;")) {
			System.out.println("[SimpleServer] → Entered cancel_order branch");
			String idNumber = msgString.split(";")[1].trim();

			Reservation reservation = DataManager.getActiveReservationById(idNumber, -1);
			if (reservation == null || !reservation.isTakeAway()) {
				try {
					client.sendToClient("no_order_found");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					int feeShekels = computeOrderCancellationFee(reservation);
					boolean paidByVisa = reservation.getVisa() != null && !reservation.getVisa().trim().isEmpty();

					if (feeShekels > 0) {
						if (paidByVisa) {
							client.sendToClient("confirm_order_cancellation;" + feeShekels + ";" + idNumber);
						} else {
							reservation.setAmountDue(feeShekels);
							reservation.setStatus("off");
							reservation.setCancellationStatus("cancelled");
							reservation.setPayed(false);
							DataManager.updateReservation(reservation);
							client.sendToClient("order_cancellation_debt;" + feeShekels + ";" + idNumber);
						}
					} else {
						reservation.setStatus("off");
						reservation.setCancellationStatus("cancelled");
						DataManager.updateReservation(reservation);
						client.sendToClient("order_cancellation_success;0");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			sendToAllClients("Monthly report updated");
		}
		// ─────────────────────────────────────────────────────────────────────────
		else if (msgString.startsWith("cancel_order_exact;")) {
			String[] p = msgString.split(";");
			String idNumber = p[1].trim();
			long resId = Long.parseLong(p[2].trim());
			int restaurantId = Integer.parseInt(p[3].trim());

			Reservation r = DataManager.getActiveReservationByIdAndReservationId(idNumber, resId, restaurantId);
			if (r == null || !r.isTakeAway()) {
				try { client.sendToClient("no_order_found"); } catch (IOException ignored) {}
				return;
			}

			int fee = computeOrderCancellationFee(r);
			boolean paidByVisa = r.getVisa() != null && !r.getVisa().trim().isEmpty();

			try {
				if (fee > 0) {
					if (paidByVisa) {
						client.sendToClient("confirm_order_cancellation;" + fee + ";" + idNumber);
					} else {
						r.setAmountDue(fee);
						r.setStatus("off");
						r.setCancellationStatus("cancelled");
						r.setPayed(false);
						DataManager.updateReservation(r);
						client.sendToClient("order_cancellation_debt;" + fee + ";" + idNumber);
						sendToAllClients("Monthly report updated");
					}
				} else {
					r.setStatus("off");
					r.setCancellationStatus("cancelled");
					DataManager.updateReservation(r);
					client.sendToClient("order_cancellation_success;0");
					sendToAllClients("Monthly report updated");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// ─────────────────────────────────────────────────────────────────────────
		else if (msgString.startsWith("cancel_reservation_exact;")) {
			String[] p = msgString.split(";");
			String idNumber = p[1].trim();
			long resId = Long.parseLong(p[2].trim());
			int restaurantId = Integer.parseInt(p[3].trim());

			Reservation r = DataManager.getActiveReservationByIdAndReservationId(idNumber, resId, restaurantId);
			if (r == null || r.isTakeAway()) { // not a sit-in reservation
				try { client.sendToClient("no_reservation_found"); } catch (IOException ignored) {}
				return;
			}

			int fee = computeReservationCancellationFee(r);
			boolean paidByVisa = r.getVisa() != null && !r.getVisa().trim().isEmpty();

			try {
				if (fee > 0) {
					if (paidByVisa) {
						client.sendToClient("confirm_cancellation;" + fee + ";" + idNumber);
					} else {
						r.setAmountDue(fee);
						r.setStatus("off");
						r.setCancellationStatus("cancelled");
						r.setPayed(false);
						DataManager.updateReservation(r);
						client.sendToClient("reservation_cancellation_debt;" + fee + ";" + idNumber);
						sendToAllClients("Monthly report updated");
					}
				} else {
					r.setStatus("off");
					r.setCancellationStatus("cancelled");
					DataManager.updateReservation(r);
					client.sendToClient("cancellation_success;0");
					sendToAllClients("Monthly report updated");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// ─────────────────────────────────────────────────────────────────────────
		else if (msgString.startsWith("process_order_cancellation;")) {
			// format: process_order_cancellation;<idNumber>;<fee>
			String[] parts = msgString.split(";");
			String idNumber = parts[1];
			int fee = Integer.parseInt(parts[2]);

			Reservation reservation = DataManager.getActiveReservationById(idNumber, -1);
			if (reservation == null) {
				try {
					client.sendToClient("order_cancellation_failed;no_order_found");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				boolean paidByVisa = reservation.getVisa() != null && !reservation.getVisa().trim().isEmpty();
				try {
					if (paidByVisa) {
						boolean paymentSucceeded = true; // integrate gateway if needed
						if (paymentSucceeded) {
							reservation.setStatus("off");
							reservation.setCancellationStatus("cancelled");
							DataManager.updateReservation(reservation);
							client.sendToClient("order_cancellation_success;" + fee);
						} else {
							client.sendToClient("order_cancellation_failed;visa_error");
						}
					} else {
						reservation.setAmountDue(fee);
						reservation.setStatus("off");
						reservation.setCancellationStatus("cancelled");
						reservation.setPayed(false);
						DataManager.updateReservation(reservation);
						client.sendToClient("order_cancellation_debt;" + fee + ";" + idNumber);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			sendToAllClients("Monthly report updated");
		}
		// ─────────────────────────────────────────────────────────────────────────
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
						reservation.setCancellationStatus("cancelled");
						DataManager.updateReservation(reservation);
						System.out.println("[SimpleServer]   → Sending cancellation_success;0");
						client.sendToClient("cancellation_success;0");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			sendToAllClients("Monthly report updated");
		}
		// ─────────────────────────────────────────────────────────────────────────
		else if (msgString.startsWith("process_cancellation;")) {
			String[] parts = msgString.split(";");
			String idNumber = parts[1];
			int fee = Integer.parseInt(parts[2]);

			Reservation reservation = DataManager.getActiveReservationById(idNumber, -1);

			try {
				if (reservation == null) {
					client.sendToClient("cancellation_failed;no_reservation_found");
				} else if (reservation.getVisa() != null) {
					reservation.setStatus("off");
					reservation.setCancellationStatus("cancelled");
					DataManager.updateReservation(reservation);
					client.sendToClient("cancellation_success;" + fee);
				} else {
					reservation.setAmountDue(fee);
					reservation.setStatus("off");
					reservation.setPayed(false);
					DataManager.updateReservation(reservation);
					client.sendToClient("reservation_cancellation_debt;" + fee + ";" + idNumber);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			sendToAllClients("Monthly report updated");
		}
		// ─────────────────────────────────────────────────────────────────────────
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
		} else if (msgString.startsWith("Get complaints")) {
			try {
				List<Complaint> list = DataManager.getComplaint();

				List<Complaint> filtered = list == null
						? new ArrayList<>()
						: list.stream()
						.filter(c -> !c.getStatus())
						.toList();

				sendToAllClients(filtered);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msgString.equals("Request Menu")) {
			try {
				List<Meal> menu = DataManager.requestMenu();
				if (menu != null && !menu.isEmpty()) {
					client.sendToClient(menu);
				} else {
					System.out.println("empty menu");
					client.sendToClient("No menu available");
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} else if (msgString.startsWith("UpdateComplaint;")) {
			String[] parts = msgString.split(";", 4);

			int complaintId = Integer.parseInt(parts[1]);
			String newResponse = parts[2];
			double newRefund = Double.parseDouble(parts[3]);

			DataManager.updateComplaint(complaintId, newResponse, newRefund);

			List<Complaint> com = DataManager.getComplaint();
			sendToAllClients(com);
		} else if (msgString.startsWith("Update price")) {
			String details = msgString.substring("Update price".length()).trim();
			String[] parts = details.split("\"");

			String mealName = parts[1];
			double newPrice = Double.parseDouble(parts[3].trim());
			double oldPrice = DataManager.getCurrentMealPrice(mealName);
			sendToAllClients("Get Confirm \"" + mealName + "\"" + oldPrice + "\"" + newPrice);
			List<PriceConfirmation> listPrice = DataManager.addPriceConfirmation(mealName, oldPrice, newPrice);
			sendToAllClients(listPrice);
		} else if (msgString.startsWith("Update discount")) {
			String details = msgString.substring("Update discount".length()).trim();
			String[] parts = details.split("\"");

			double percentage = Double.parseDouble(parts[1].trim());
			String category = parts[3].trim();
			System.out.println(percentage);
			System.out.println(category);
			sendToAllClients("Get Discount Confirm \"" + percentage);
			List<Discounts> dis = DataManager.addDiscountConfirmation(percentage, category);
			sendToAllClients(dis);
		} else if (msgString.startsWith("Update description")) {
			String details = msgString.substring("Update Description".length()).trim();

			String[] parts = details.split("\"");

			String mealName = parts[1];
			String mealIngredient = parts[3].trim();

			try {
				if (DataManager.updateMealIngredient(mealName, mealIngredient) != 1) {
					System.out.println("Update meal failed");
					client.sendToClient(mealName + " Description update has failed");
				} else {
					client.sendToClient(mealName + " Description has updated successfully");
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
		} else if (msgString.startsWith("complaint")) {
			String[] parts = msgString.split(";");

			String complainttxt = parts[1];
			int restaurantId = Integer.parseInt(parts[2].trim());
			String id = parts[3].trim();
			String name = parts[4].trim();
			String email = parts[5].trim();

			Complaint saved = null;
			List<Complaint> s = null;
			boolean exists = DataManager.hasReservationForUser(id, restaurantId, name, email);

			if (exists) {
				saved = DataManager.saveComplaintReturn(complainttxt, restaurantId, id, name, email);
				s = DataManager.getComplaint();
			}

			try {
				if (saved != null) {
					client.sendToClient("complaint inserted successfully");
					sendToAllClients("Monthly report updated");
					sendToAllClients(s);
				} else {
					client.sendToClient(exists ? "complaint has not inserted" : "user not exist");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (msgString.startsWith("request_reports_daily_as_monthly")) {
			List<DailyReport> reports = DataManager.getReportsByMonth(msgString);
			if (reports != null) {
				try {
					client.sendToClient(reports);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (msgString.startsWith("request_prev_only_monthly_reports")) {
			String[] parts = msgString.split(";");
			int restaurantId = Integer.parseInt(parts[1]);
			int month = Integer.parseInt(parts[2]);
			int year = Integer.parseInt(parts[3]);

			List<MonthlyReport> report = DataManager.getPrevMonthlyReport(restaurantId, month, year);
			try {
				client.sendToClient(report);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		} else if (msgString.startsWith("Update preferences")) {
			String details = msgString.substring("Update preferences".length()).trim();

			String[] parts = details.split("\"");

			String mealName = parts[1];
			String mealIngredient = parts[3].trim();

			try {
				if (DataManager.updateMealPref(mealName, mealIngredient) != 1) {
					System.out.println("Update meal failed");
					client.sendToClient(mealName + " preferences update has failed");
				} else {
					client.sendToClient(mealName + " preferences has updated successfully");
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
		} else if (msgString.startsWith("RequestMealCategory")) {
			String name = msgString.substring("RequestMealCategory".length()).trim().replace("\"", "");
			String category = DataManager.getMealCategoryByName(name);
			if (category != null) {
				try {
					client.sendToClient("MealCategory:" + category);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				try {
					client.sendToClient("MealCategory:NotFound");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		} else if (msgString.startsWith("payment-confirmed:")) {
			String customerId = msgString.split(":")[1];
			DataManager.markPaymentAsPaidInDatabase(customerId);
		} else if (msgString.startsWith("remove client")) {
			int index = msgString.indexOf(";");
			String username = msgString.substring(index + 1).trim();
			if (!(username.equals("Customer"))) {
				DataManager.disconnectUser(username);
			}
			if (!SubscribersList.isEmpty()) {
				for (SubscribedClient subscribedClient : SubscribersList) {
					if (subscribedClient.getClient().equals(client)) {
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		} else if (msgString.startsWith("log out")) {
			int index = msgString.indexOf(";");
			String username = msgString.substring(index + 1).trim();
			DataManager.disconnectUser(username);
		} else if (msgString.startsWith("Add Meal")) {
			String details = msgString.substring("Add meal".length()).trim();

			String[] parts = details.split("\"");

			try {
				String Name = parts[1];
				String Description = parts[3];
				String Preferences = parts[5];
				double Price = Double.parseDouble(parts[7]);
				String Image = parts[9];
				String Category = parts[11];

				if (DataManager.mealExist(Name)) {
					client.sendToClient("MealExists");
				} else {

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
		} else if (msgString.startsWith("Remove Meal")) {
			String details = msgString.substring("Remove Meal".length()).trim();
			String mealName = details.replace("\"", "");

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
					client.sendToClient("Meal orders saved");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// ReservationRequest & Reservation branches (unchanged logic except earlier)
		else if (msg instanceof ReservationRequest) {
			ReservationRequest request = (ReservationRequest) msg;
			Reservation reservation = request.getReservation();
			boolean shouldSave = request.isShouldSave();

			if (shouldSave) {
				System.out.println("Saving reservation to DB...");

				if (reservation.isTakeAway()) {
					try {
						boolean ok = DataManager.saveReservation(reservation);
						if (!ok) {
							client.sendToClient("Reservation failed: time conflict");
							return;
						}

						client.sendToClient("Reservation saved successfully");
						client.sendToClient(reservation);

						DataManager.updateDailyReport(reservation);
						sendToAllClients("Monthly report updated");
						return;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}

				boolean clash = DataManager.hasActiveReservationAround(
						reservation.getIdNumber(),
						reservation.getReservationTime(),
						90
				);
				if (clash) {
					try {
						client.sendToClient("Reservation failed: ID already used.");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
					return;
				}

				try {
					boolean ok = DataManager.saveReservation(reservation);
					if (!ok) {
						client.sendToClient("Reservation failed: time conflict");
						return;
					}

					long id = reservation.getId();
					client.sendToClient("Reservation saved successfully#" + id);

					DataManager.updateDailyReport(reservation);
					sendToAllClients("Monthly report updated");

					client.sendToClient(reservation);

					sendToAllClientsExceptSender(new Message("reservation_update", reservation), client);

					List<HostingTable> updatedAvailability = DataManager.getAvailableTables(reservation);
					sendToAllClients(updatedAvailability);
				} catch (IOException e) {
					e.printStackTrace();
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
		} else if (msg instanceof Reservation) {
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
		} else if (msgString.equals("Get Discount Confirmations")) {
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
		} else if (msgString.equals("get_all_restaurants")) {
			try {
				List<Restaurant> restaurants = DataManager.getAllRestaurants();
				if (restaurants != null && !restaurants.isEmpty()) {
					System.out.println("Sending list of restaurants to client. Size: " + restaurants.size());
					client.sendToClient(restaurants);
				} else {
					System.out.println("No restaurants found.");
					client.sendToClient(new ArrayList<Restaurant>());
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					client.sendToClient("Error: Could not fetch restaurants.");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} else if (msgString.startsWith("Confirm Price")) {
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
		} else if (msgString.startsWith("Reject Price")) {
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
		} else if (msgString.startsWith("check_id_type_exact:")) {
			String[] parts = msgString.split(":");
			if (parts.length < 4) { try { client.sendToClient("id_type:not_found"); } catch (IOException ignored) {} return; }

			String idNumber = parts[1].trim();
			long reservationId = Long.parseLong(parts[2].trim());
			int restaurantId = Integer.parseInt(parts[3].trim());

			Reservation r = DataManager.getActiveReservationByIdAndReservationId(idNumber, reservationId, restaurantId);
			try {
				if (r == null) {
					client.sendToClient("id_type:not_found");
				} else if (r.isTakeAway()) {
					client.sendToClient("id_type:takeaway");
				} else {
					client.sendToClient("id_type:reservation");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (msgString.startsWith("Confirm Discount")) {
			String details = msgString.substring("Confirm Discount".length()).trim();
			String[] parts = details.split("\"");

			double discount = Double.parseDouble(parts[1].trim());
			int id = Integer.parseInt(parts[3].trim());
			String category = parts[5].trim();

			int updated = DataManager.makediscount(discount, category);
			boolean x = DataManager.removeDiscountConfirmation(id);
			if (updated == 1 && x) {
				List<Meal> updatedMenu = DataManager.requestMenu();
				sendToAllClients(updatedMenu);
				List<Discounts> updateDiscountConfirmation = DataManager.getDiscountConfirmations();
				sendToAllClients(updateDiscountConfirmation);
			}
		} else if (msgString.startsWith("Check open times for res")) {
			String[] parts = msgString.split(";");
			int resId = Integer.parseInt(parts[1]);
			Restaurant res = DataManager.getRestaurantById(resId);
			try {
				client.sendToClient(res);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (msgString.startsWith("Reject Discount")) {
			String details = msgString.substring("Reject Discount".length()).trim();
			String[] parts = details.split("\"");

			double discount = Double.parseDouble(parts[1]);
			int id = Integer.parseInt(parts[3]);

			boolean x = DataManager.removeDiscountConfirmation(id);
			if (x) {
				List<Discounts> updateDiscountConfirmation = DataManager.getDiscountConfirmations();
				sendToAllClients(updateDiscountConfirmation);
			}
		} else if (msgString.startsWith("Change Category Meal")) {
			String details = msgString.substring("Change Category Meal".length()).trim();
			String[] parts = details.split("\"");

			try {
				String name = parts[1];
				String fromCategory = parts[3];
				String toCategory = parts[5];

				String newCategory = DataManager.changeMealCategory(name, fromCategory, toCategory);
				if (newCategory != null) {
					client.sendToClient("Meal '" + name + "' category changed to " + toCategory);

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
		} else {
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
}
