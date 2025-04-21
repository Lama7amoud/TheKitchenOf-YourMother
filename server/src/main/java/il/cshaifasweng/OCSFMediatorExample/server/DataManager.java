package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;

import java.util.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

import il.cshaifasweng.OCSFMediatorExample.entities.HostingTable;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.ReservedTime;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


public class DataManager {

    private static Session session;
    private static String password = "";


    private static SessionFactory getSessionFactory(String password) throws HibernateException {


        Configuration configuration = new Configuration();
        // Override the password
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(AuthorizedUser.class);
        configuration.addAnnotatedClass(Restaurant.class);
        configuration.addAnnotatedClass(HostingTable.class);
        configuration.addAnnotatedClass(Reservation.class);
        configuration.addAnnotatedClass(ReservedTime.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static List<Meal> getMenu() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
        query.from(Meal.class);

        List<Meal> data = session.createQuery(query).getResultList();
        return data;
    }


    private static void generateData() throws Exception {
    // Create Italian restaurants
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("Haifa-Mom Kitchen");
        restaurant1.setAddress("German Colony");
        restaurant1.setLocation("Haifa");
        restaurant1.setPhoneNumber("123-456-7890");
        restaurant1.setOpeningTime(10.00);  // 10:00 AM
        restaurant1.setClosingTime(23.00);  // 11:00 PM
        restaurant1.setHolidays("Sunday");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Tel-Aviv-Mom Kitchen");
        restaurant2.setAddress("Rothschild");
        restaurant2.setLocation("Tel-Aviv");
        restaurant2.setPhoneNumber("987-654-3210");
        restaurant2.setOpeningTime(9.00);   // 9:00 AM
        restaurant2.setClosingTime(22.00);  // 10:00 PM
        restaurant2.setHolidays("Saturday");

        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Nahariya-Mom Kitchen");
        restaurant3.setAddress("Weizman");
        restaurant3.setLocation("Nahariya");
        restaurant3.setPhoneNumber("555-123-4567");
        restaurant3.setOpeningTime(11.00);  // 11:00 AM
        restaurant3.setClosingTime(23.00);  // 11:00 PM
        restaurant3.setHolidays("Monday");


        session.save(restaurant1);
        session.save(restaurant2);
        session.save(restaurant3);
        session.flush();
        // Create Italian meals
        Meal meal1 = new Meal("Margherita Pizza", "Classic pizza with fresh mozzarella and basil", "Vegetarian", 10.99);
        Meal meal2 = new Meal("Spaghetti Carbonara", "Pasta with pancetta, eggs, and Parmesan cheese", "Contains dairy", 13.99);
        Meal meal3 = new Meal("Lasagna", "Layered pasta with rich meat sauce and creamy béchamel", "Contains dairy", 14.99);
        Meal meal4 = new Meal("Risotto alla Milanese", "Creamy risotto with saffron and Parmesan", "Vegetarian", 12.99);
        Meal meal5 = new Meal("Tiramisu", "Classic Italian dessert with coffee-soaked ladyfingers and mascarpone cream", "Contains dairy", 6.99);
        Meal meal6 = new Meal("Fettuccine Alfredo", "Pasta in a creamy Parmesan cheese sauce", "Vegetarian", 12.49);
        Meal meal7 = new Meal("Caprese Salad", "Fresh tomatoes, mozzarella, and basil with olive oil", "Vegetarian", 8.99);

        // Associate meals with restaurants
        meal1.getRestaurants().add(restaurant1);
        meal1.getRestaurants().add(restaurant2);

        meal2.getRestaurants().add(restaurant1);
        meal2.getRestaurants().add(restaurant3);

        meal3.getRestaurants().add(restaurant2);
        meal3.getRestaurants().add(restaurant3);

        meal4.getRestaurants().add(restaurant1);
        meal5.getRestaurants().add(restaurant2);
        meal6.getRestaurants().add(restaurant3);
        meal7.getRestaurants().add(restaurant1);

        // Persist meals to the database
        session.save(meal1);
        session.save(meal2);
        session.save(meal3);
        session.save(meal4);
        session.save(meal5);
        session.save(meal6);
        session.save(meal7);

        session.flush();

        // Create authorized users
        AuthorizedUser user1 = new AuthorizedUser();
        user1.setUsername("Sharbel");
        user1.setPassword("password123");
        user1.setFirstname("Sharbel");
        user1.setLastname("Maroun");
        user1.setIDNum("206538444");
        user1.setAge((short) 25);
        user1.setRestaurant(restaurant1);
        user1.setConnected(false);
        user1.setPermissionLevel((short) 4);

        AuthorizedUser user2 = new AuthorizedUser();
        user2.setUsername("Falah");
        user2.setPassword("password456");
        user2.setFirstname("Falah");
        user2.setLastname("Abu Raya");
        user2.setIDNum("206538446");
        user2.setAge((short) 26);
        user2.setRestaurant(restaurant2);
        user2.setConnected(false);
        user2.setPermissionLevel((short) 5);

        AuthorizedUser user3 = new AuthorizedUser();
        user3.setUsername("Mohammed");
        user3.setPassword("password789");
        user3.setFirstname("Mohammed");
        user3.setLastname("Abu Saleh");
        user3.setIDNum("206538466");
        user3.setAge((short) 25);
        user3.setRestaurant(restaurant3);
        user3.setConnected(false);
        user3.setPermissionLevel((short) 2);

        // Save users in the database
        session.save(user1);
        session.save(user2);
        session.save(user3);

        // Insert tables for Haifa branch (13 tables)
        for (int i = 1; i <= 13; i++) {
            HostingTable table = new HostingTable();
            table.setTableNumber(i);
            table.setSeatsNumber((i % 3) + 2);  // Seats: 2–4
            table.setRestaurant(restaurant1);
            table.setInside(i % 2 == 0);
            table.setReservedTimes(new ArrayList<>());
            session.save(table);
        }

        // Insert tables for Tel Aviv branch (8 tables)
        for (int i = 1; i <= 8; i++) {
            HostingTable table1 = new HostingTable();
            table1.setTableNumber(i);
            table1.setSeatsNumber((i % 3) + 2);  // Seats: 2–4
            table1.setRestaurant(restaurant2);
            table1.setInside(i % 2 == 0);
            table1.setReservedTimes(new ArrayList<>());
            session.save(table1);
        }

        // Insert tables for Nahariya branch (14 tables)
        for (int i = 1; i <= 14; i++) {
            HostingTable table2 = new HostingTable();
            table2.setTableNumber(i);
            table2.setSeatsNumber((i % 3) + 2);  // Seats: 2–4
            table2.setRestaurant(restaurant3);
            table2.setInside(i % 2 == 0);
            table2.setReservedTimes(new ArrayList<>());
            session.save(table2);
        }
    }

    public static Restaurant getRestaurant(String restaurantId){
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        int id = Integer.parseInt(restaurantId); // Convert String to int
        Restaurant restaurant = session.get(Restaurant.class, id); // Fetch restaurant by ID

        if (session != null) {
            session.close();
            System.out.println("session closed");
        }

        return restaurant;


    }

    static List<Meal> requestMenu(){
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            return getMenu();

        } catch (Exception exception) {
            System.err.println("An error occured");
            exception.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
                System.out.println("session closed");
            }
        }
    }

    static AuthorizedUser checkPermission(String details) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            int spaceIndex = details.indexOf(" ");
            details = details.substring(spaceIndex + 1);

            String[] parts = details.split(";");
            System.out.println(Arrays.toString(parts));
            if (parts.length < 2) {
                AuthorizedUser errorUser = new AuthorizedUser();
                errorUser.setMessageToServer("Invalid input format. Expected 'username;password'");
                return errorUser;
            }

            String username = parts[0].trim();
            String password = parts[1].trim();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<AuthorizedUser> query = builder.createQuery(AuthorizedUser.class);
            Root<AuthorizedUser> root = query.from(AuthorizedUser.class);

            // Search for user by username
            query.select(root).where(builder.equal(root.get("username"), username));
            AuthorizedUser user = session.createQuery(query).uniqueResult();

            if (user == null) {
                AuthorizedUser errorUser = new AuthorizedUser();
                errorUser.setMessageToServer("User does not exist");
                return errorUser;
            }

            // Verify password
            if (!user.getPassword().equals(password)) {
                AuthorizedUser errorUser = new AuthorizedUser();
                errorUser.setMessageToServer("Incorrect password");
                return errorUser;
            }

            // Check if the user is already connected
            if (user.isConnected()) {
                AuthorizedUser errorUser = new AuthorizedUser();
                errorUser.setMessageToServer("You are already connected with this user on another device. Multiple connections are not allowed.");
                return errorUser;
            }

            user.setConnected(true);
            session.update(user);
            session.getTransaction().commit();

            AuthorizedUser currentUser = new AuthorizedUser();
            currentUser.copyUser(user);
            currentUser.setMessageToServer("Login successful");

            return currentUser;

        } catch (Exception exception) {
            System.err.println("An error occurred");
            exception.printStackTrace();
            AuthorizedUser errorUser = new AuthorizedUser();
            errorUser.setMessageToServer("An error occurred while checking permissions");
            return errorUser;
        } finally {
            if (session != null) {
                session.close();
                System.out.println("Session closed");
            }
        }
    }

    static void disconnectUser(String username) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<AuthorizedUser> query = builder.createQuery(AuthorizedUser.class);
            Root<AuthorizedUser> root = query.from(AuthorizedUser.class);

            // Search for user by username
            query.select(root).where(builder.equal(root.get("username"), username));

            // Get the user object from the query result
            AuthorizedUser user = session.createQuery(query).uniqueResult();

            if (user != null) {
                // Set isConnected to false
                user.setConnected(false);
                // Update the user in the database
                session.update(user);
                session.getTransaction().commit();
                System.out.println("The user " + username + " disconnected successfully.");
            } else {
                System.out.println("The user " + username + " not found. May it's a customer");
            }
        } catch (Exception exception) {
            System.err.println("An error occurred");
            exception.printStackTrace();
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();  // Rollback transaction on error
            }
        } finally {
            if (session != null) {
                session.close();
                System.out.println("Session closed");
            }
        }
    }



    static int updateMealPrice(String mealName, double mealPrice) {
        try {
            System.out.println("update method reached");
            // Start a session and transaction
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            System.out.println("meal name in update function" + mealName);
            System.out.println("meal price in update function" + mealPrice);
            // Build the query to update the meal price
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Meal> updateQuery = builder.createCriteriaUpdate(Meal.class);
            Root<Meal> root = updateQuery.from(Meal.class);
            updateQuery.set("mealPrice", mealPrice) // Use mealPrice directly
                    .where(builder.equal(root.get("mealName"), mealName)); // Find the meal by name


            // Execute the update
            int rowsUpdated = session.createQuery(updateQuery).executeUpdate();

            // Commit the transaction
            session.getTransaction().commit();

            return rowsUpdated; // Return number of rows updated (1 for success, 0 for failure)
        } catch (Exception e) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return 0; // Indicate failure
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter database password: ");
        password = scanner.nextLine();

        if(!requestMenu().isEmpty())
            return;
        try {

            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            generateData();

            session.getTransaction().commit(); // Save everything.
        } catch (Exception exception) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static boolean isTimeAvailable(Restaurant restaurant, String timeSlot) {
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
            Root<Reservation> root = query.from(Reservation.class);
            query.select(root).where(
                    cb.and(
                            cb.equal(root.get("restaurant"), restaurant),
                            cb.equal(root.get("timeSlot"), timeSlot)
                    )
            );

            List<Reservation> results = session.createQuery(query).getResultList();
            return results.isEmpty(); // true = available

        } finally {
            session.close();
        }
    }

    public static void saveReservation(Reservation reservation) {
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            // Do not save if ID already exists
            boolean exists = checkIfIdHasReservation(reservation.getIdNumber());
            if (exists) {
                System.out.println("Reservation ID already exists. Skipping save.");
                session.getTransaction().rollback();
                return;
            }

            // Save the reservation FIRST to assign its ID
            session.save(reservation);

            // Save ReservedTime instances using the now-persistent reservation
            if (reservation.getReservedTables() != null) {
                LocalDateTime startTime = reservation.getReservationTime();
                for (HostingTable table : reservation.getReservedTables()) {
                    ReservedTime rt = new ReservedTime(table, startTime, reservation);
                    session.save(rt);
                }
            }

            session.getTransaction().commit();
            System.out.println(">>> Reservation and reserved times committed");

        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println(">>> Error during reservation save:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }




    public static List<HostingTable> getAvailableTables(Reservation reservation) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<HostingTable> availableTables = new ArrayList<>();

        if (reservation.getSittingType() == null) {
            System.err.println("Sitting type is null in reservation!");
            return new ArrayList<>();
        }

        try {
            Restaurant restaurant = reservation.getRestaurant();
            LocalDateTime requestedTime = reservation.getReservationTime();
            LocalDateTime requestedEnd = requestedTime.plusMinutes(60);

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<HostingTable> tableQuery = cb.createQuery(HostingTable.class);
            Root<HostingTable> tableRoot = tableQuery.from(HostingTable.class);
            tableQuery.select(tableRoot).where(
                    cb.equal(tableRoot.get("restaurant").get("id"), restaurant.getId())
            );

            List<HostingTable> allTables = session.createQuery(tableQuery).getResultList();

            boolean wantInside = reservation.getSittingType().equalsIgnoreCase("Inside");
            allTables = allTables.stream()
                    .filter(table -> table.isInside() == wantInside)
                    .collect(Collectors.toList());
            // Step 1: Fetch all ReservedTimes for the given restaurant
            CriteriaQuery<ReservedTime> reservedQuery = cb.createQuery(ReservedTime.class);
            Root<ReservedTime> reservedRoot = reservedQuery.from(ReservedTime.class);
            reservedQuery.select(reservedRoot).where(
                    cb.equal(reservedRoot.get("table").get("restaurant").get("id"), restaurant.getId())
            );

            List<ReservedTime> reservedList = session.createQuery(reservedQuery).getResultList();

            for (HostingTable table : allTables) {
                boolean isAvailable = true;
                for (ReservedTime rt : reservedList) {
                    if (rt.getTable() == null || !Objects.equals(rt.getTable().getId(), table.getId())) continue;

                    LocalDateTime reservedStart = rt.getReservedTime();
                    LocalDateTime blockStart = reservedStart.minusMinutes(60); // block 1 hour before
                    LocalDateTime blockEnd = reservedStart.plusMinutes(60);    // block 1 hour after


                    if (requestedTime.isBefore(blockEnd) && blockStart.isBefore(requestedEnd)) {
                        isAvailable = false;
                        break;
                    }
                }

                if (isAvailable) {
                    table.getReservedTimes().size(); // force initialize
                    availableTables.add(table);
                }
            }


            session.getTransaction().commit();
            return availableTables;

        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return availableTables;
        } finally {
            session.close();
        }
    }
    public static boolean checkIfIdHasReservation(String idNumber) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<Reservation> root = query.from(Reservation.class);
            query.select(cb.count(root)).where(cb.equal(root.get("idNumber"), idNumber));

            Long count = session.createQuery(query).getSingleResult();
            return count > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
}
