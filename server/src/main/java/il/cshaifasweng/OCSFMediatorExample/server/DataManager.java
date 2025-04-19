package il.cshaifasweng.OCSFMediatorExample.server;

import java.util.*;
import java.time.LocalDate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.fxml.FXML;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class DataManager {

    private static Session session;
    private static String password = "";
    public static String getPassword(){
        return password;
    }

    private static SessionFactory getSessionFactory(String password) throws HibernateException {

        Configuration configuration = new Configuration();
        // Override the password
        configuration.setProperty("hibernate.connection.password", password);
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(PriceConfirmation.class);
        configuration.addAnnotatedClass(Discounts.class);

        configuration.addAnnotatedClass(Feedback.class);
        configuration.addAnnotatedClass(Complaint.class);
        //configuration.addAnnotatedClass(MonthlyReport.class);

        configuration.addAnnotatedClass(AuthorizedUser.class);
        configuration.addAnnotatedClass(Restaurant.class);
        configuration.addAnnotatedClass(Business.class);
        configuration.addAnnotatedClass(HostingTable.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    // Save a generated report
    /*
    public static void saveMonthlyReport(MonthlyReport report) {
        Transaction transaction = null;
        SessionFactory sessionFactory = getSessionFactory(password);
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(report);
            transaction.commit();
            System.out.println("Monthly report saved to the database.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (sessionFactory != null) sessionFactory.close();
        }
    }*/

    public static Map<String, Integer> getComplaintHistogram(String password, LocalDate start, LocalDate end) {
        Map<String, Integer> histogram = new HashMap<>();
        try (Session session = getSessionFactory(password).openSession()) {
            List<Complaint> complaints = session.createQuery("FROM Complaint", Complaint.class).list();
            for (Complaint complaint : complaints) {
                LocalDate date = LocalDate.from(complaint.getSubmittedAt());
                if ((date.isEqual(start) || date.isAfter(start)) && (date.isEqual(end) || date.isBefore(end))) {
                    String key = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                    histogram.put(key, histogram.getOrDefault(key, 0) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return histogram;
    }

    private static List<Meal> getMenu() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
        query.from(Meal.class);

        List<Meal> data = session.createQuery(query).getResultList();
        return data;
    }

    /*
    public static Map<LocalDate, Integer> getCustomerCountPerDay(String password, LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> customerCount = new HashMap<>();
        try (Session session = getSessionFactory(password).openSession()) {
            List<Customer> customers = session.createQuery("FROM Customer", Customer.class).list();
            for (Customer customer : customers) {
                LocalDate date = customer.getVisitDate(); // Assuming 'visitDate' is the field
                if ((date.isEqual(start) || date.isAfter(start)) && (date.isEqual(end) || date.isBefore(end))) {
                    customerCount.put(date, customerCount.getOrDefault(date, 0) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerCount;
    }*/
/*
    public static int getDeliveryOrdersCount(String password, LocalDate start, LocalDate end) {
        int count = 0;
        try (Session session = getSessionFactory(password).openSession()) {
            List<Order> orders = session.createQuery("FROM Order", Order.class).list();
            for (Order order : orders) {
                LocalDate date = order.getDate();
                if ((date.isEqual(start) || date.isAfter(start)) && (date.isEqual(end) || date.isBefore(end))) {
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }*/

    /* private static List<Meal> getMealsByRestaurantId(int restaurantId) {
         try {
             CriteriaBuilder builder = session.getCriteriaBuilder();
             CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
             Root<Meal> root = query.from(Meal.class);

             // Join the meal with its associated restaurants and filter by restaurant ID
             query.select(root).where(
                     builder.equal(root.join("restaurants").get("id"), restaurantId)
             );

             List<Meal> meals = session.createQuery(query).getResultList();
             return meals;
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }
 */
    private static void generatePrice() throws Exception {
        //PriceConfirmation priceConfirmation = new PriceConfirmation("mna",9,11);
        //session.save(priceConfirmation);
        //session.flush();

    }

    private static void generateDiscount() throws Exception {
        //Discounts discounts = new Discounts(12.4);
        //session.save(discounts);
        //session.flush();

    }


    private static void generateData() throws Exception {

        Business momKitchenLtd = new Business();
        momKitchenLtd.setName("Mom's Kitchen Ltd.");
        momKitchenLtd.setOwnerName("Sharbel Maroun");
        momKitchenLtd.setEmail("contact@momskitchen.com");
        momKitchenLtd.setPhoneNumber("04-1234567");
        momKitchenLtd.setAddress("Main Office, Haifa");
        momKitchenLtd.setRegistrationNumber("MKL12345678"); // assume that this code's prefix is an agreement for the businesses.
        momKitchenLtd.setCreationDate(LocalDate.of(2020, 1, 15));

        session.save(momKitchenLtd);
        // Create Italian restaurants
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("Mom Kitchen");
        restaurant1.setAddress("German Colony");
        restaurant1.setLocation("Haifa");
        restaurant1.setPhoneNumber("123-456-7890");
        restaurant1.setActivityHours("Monday-Saturday: 10:00 AM - 11:00 PM");
        restaurant1.setBusiness(momKitchenLtd);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Mom Kitchen");
        restaurant2.setAddress("Rothschild");
        restaurant2.setLocation("Tel-Aviv");
        restaurant2.setPhoneNumber("987-654-3210");
        restaurant2.setActivityHours("Monday-Sunday: 9:00 AM - 10:00 PM");
        restaurant2.setBusiness(momKitchenLtd);

        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Mom Kitchen");
        restaurant3.setAddress("Weizman");
        restaurant3.setLocation("Nahariya");
        restaurant3.setPhoneNumber("555-123-4567");
        restaurant3.setActivityHours("Tuesday-Sunday: 11:00 AM - 12:00 AM");
        restaurant3.setBusiness(momKitchenLtd);


        // Persist restaurants to the database
        session.save(restaurant1);
        session.save(restaurant2);
        session.save(restaurant3);

        session.flush();





// Create Italian meals with the same image path
        Meal meal1 = new Meal("Margherita Pizza", "Classic pizza with fresh mozzarella and basil", "tuna, olive, corn, mushroom", 10.99 , "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal0.jpg", "special1");
        Meal meal2 = new Meal("Spaghetti Carbonara", "Pasta with pancetta, eggs, and Parmesan cheese", "pancetta, egg, parmesan", 13.99 , "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal1.jpg", "shared meal");
        Meal meal3 = new Meal("Lasagna", "Layered pasta with rich meat sauce and creamy béchamel", "beef, tomato sauce, béchamel", 14.99, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal2.jpg", "shared meal");
        Meal meal4 = new Meal("Risotto alla Milanese", "Creamy risotto with saffron and Parmesan", "saffron, parmesan, butter", 12.99, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal3.jpg", "shared meal");
        Meal meal5 = new Meal("Fettuccine Alfredo", "Pasta in a creamy Parmesan cheese sauce", "cream, parmesan, butter", 12.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal4.jpg", "shared meal");
        Meal meal6 = new Meal("Penne Arrabbiata", "Penne pasta with a spicy tomato and garlic sauce", "tomato, garlic, chili", 11.99, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal5.jpg", "special2");
        Meal meal7 = new Meal("Ravioli Ricotta e Spinaci", "Pasta pockets filled with ricotta cheese and spinach", "ricotta, spinach, nutmeg", 13.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal6.jpg", "special3");
        Meal meal8 = new Meal("Calzone Classico", "Folded pizza with ham, mozzarella, and mushrooms", "ham, mozzarella, mushroom", 13.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal7.jpg", "special1");
        Meal meal9 = new Meal("Arancini", "Fried risotto balls stuffed with cheese and peas", "cheese, peas, breadcrumbs", 8.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal8.jpg", "special2");
        Meal meal10 = new Meal("Polenta ai Funghi", "Creamy polenta topped with sautéed wild mushrooms", "mushrooms, garlic, butter", 11.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal9.jpg", "special3");

        // Associate meals with restaurants
        meal1.getRestaurants().add(restaurant1);

        meal2.getRestaurants().add(restaurant1);
        meal2.getRestaurants().add(restaurant2);
        meal2.getRestaurants().add(restaurant3);

        meal3.getRestaurants().add(restaurant1);
        meal3.getRestaurants().add(restaurant2);
        meal3.getRestaurants().add(restaurant3);

        meal4.getRestaurants().add(restaurant1);
        meal4.getRestaurants().add(restaurant2);
        meal4.getRestaurants().add(restaurant3);

        meal5.getRestaurants().add(restaurant1);
        meal5.getRestaurants().add(restaurant2);
        meal5.getRestaurants().add(restaurant3);

        meal6.getRestaurants().add(restaurant2);
        meal7.getRestaurants().add(restaurant3);
        meal8.getRestaurants().add(restaurant1);
        meal9.getRestaurants().add(restaurant2);
        meal10.getRestaurants().add(restaurant3);


        // Persist meals to the database
        session.save(meal1);
        session.save(meal2);
        session.save(meal3);
        session.save(meal4);
        session.save(meal5);
        session.save(meal6);
        session.save(meal7);
        session.save(meal8);
        session.save(meal9);
        session.save(meal10);

        session.flush();

        // Create authorized users
        //mnhelt resht
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

        //ditanit
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

        //30fedt kshre lko7ot
        AuthorizedUser user3 = new AuthorizedUser();
        user3.setUsername("Mohammed");
        user3.setPassword("password789");
        user3.setFirstname("Mohammed");
        user3.setLastname("Abu Saleh");
        user3.setIDNum("206538466");
        user3.setAge((short) 25);
        user3.setRestaurant(restaurant1);
        user3.setConnected(false);
        user3.setPermissionLevel((short) 2);

        //mnhlot snefem
        AuthorizedUser user4 = new AuthorizedUser();
        user4.setUsername("Lama");
        user4.setPassword("password777");
        user4.setFirstname("Lama");
        user4.setLastname("Hamoud");
        user4.setIDNum("222334455");
        user4.setAge((short) 22);
        user4.setRestaurant(restaurant1);
        user4.setConnected(false);
        user4.setPermissionLevel((short) 3);

        AuthorizedUser user6 = new AuthorizedUser();
        user6.setUsername("Malki");
        user6.setPassword("password111");
        user6.setFirstname("Malki");
        user6.setLastname("Grossman");
        user6.setIDNum("123456789");
        user6.setAge((short) 30);
        user6.setRestaurant(restaurant2);
        user6.setConnected(false);
        user6.setPermissionLevel((short) 3);

        AuthorizedUser user7 = new AuthorizedUser();
        user7.setUsername("Abedalftah");
        user7.setPassword("password222");
        user7.setFirstname("Abedalftah");
        user7.setLastname("Abedalftah");
        user7.setIDNum("987654321");
        user7.setAge((short) 30);
        user7.setRestaurant(restaurant3);
        user7.setConnected(false);
        user7.setPermissionLevel((short) 3);


        //30fdot
        AuthorizedUser user5 = new AuthorizedUser();
        user5.setUsername("Oraib");
        user5.setPassword("password888");
        user5.setFirstname("Oraib");
        user5.setLastname("Marzook");
        user5.setIDNum("111223344");
        user5.setAge((short) 29);
        user5.setRestaurant(restaurant1);
        user5.setConnected(false);
        user5.setPermissionLevel((short) 1);

        AuthorizedUser user8 = new AuthorizedUser();
        user8.setUsername("Rasha");
        user8.setPassword("password333");
        user8.setFirstname("Rasha");
        user8.setLastname("Odeh");
        user8.setIDNum("910229304");
        user8.setAge((short) 25);
        user8.setRestaurant(restaurant2);
        user8.setConnected(false);
        user8.setPermissionLevel((short) 1);

        AuthorizedUser user9 = new AuthorizedUser();
        user9.setUsername("Waleed");
        user9.setPassword("password666");
        user9.setFirstname("Waleed");
        user9.setLastname("Messi");
        user9.setIDNum("101010100");
        user9.setAge((short) 25);
        user9.setRestaurant(restaurant3);
        user9.setConnected(false);
        user9.setPermissionLevel((short) 1);

        //System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        //System.out.println(restaurant2.getId());
        // Save users in the database
        session.save(user1);
        session.save(user2);
        session.save(user3);
        session.save(user4);
        session.save(user5);
        session.save(user6);
        session.save(user7);
        session.save(user8);
        session.save(user9);
        //session.save(user10);

        //session.flush();
        //session.getTransaction().commit();

        // Insert tables for Haifa branch (13 tables)
        for (int i = 1; i <= 13; i++) {
            HostingTable table = new HostingTable();
            table.setTableNumber(i);
            table.setSeatsNumber((i % 4) + 2); // Seats between 2-5
            table.setReserved(false);
            table.setRestaurant(restaurant1);
            session.save(table);
        }

        // Insert tables for Tel Aviv branch (8 tables)
        for (int i = 1; i <= 8; i++) {
            HostingTable table = new HostingTable();
            table.setTableNumber(i);
            table.setSeatsNumber((i % 4) + 2); // Seats between 2-5
            table.setReserved(false);
            table.setRestaurant(restaurant2);
            session.save(table);
        }

        // Insert tables for Nahariya branch (14 tables)
        for (int i = 1; i <= 14; i++) {
            HostingTable table = new HostingTable();
            table.setTableNumber(i);
            table.setSeatsNumber((i % 4) + 2); // Seats between 2-5
            table.setReserved(false);
            table.setRestaurant(restaurant3);
            session.save(table);
        }
    }

    static Restaurant getRestaurant(String restaurantId){
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

    /*static List<Meal> requestHaifaMenu(){
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            return getMealsByRestaurantId(1);

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

    static List<Meal> requestTelAvivMenu(){
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            return getMealsByRestaurantId(2);

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

    static List<Meal> requestNahariyaMenu(){
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            return getMealsByRestaurantId(3);

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

*/

    public static void saveFeedback(String password, Feedback feedback) {
        Transaction tx = null;
        try (Session session = getSessionFactory(password).openSession()) {
            tx = session.beginTransaction();
            session.save(feedback);
            tx.commit();
            System.out.println("Feedback saved successfully.");
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }
    public static void saveComplaint(String password, Complaint complaint) {
        Transaction tx = null;
        try (Session session = getSessionFactory(password).openSession()) {
            tx = session.beginTransaction();
            session.save(complaint);
            tx.commit();
            System.out.println("Complaint saved successfully.");
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
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


    public static boolean removePriceConfirmation(int id) {
        try {
            System.out.println("Remove Confirmation");
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PriceConfirmation> query = builder.createQuery(PriceConfirmation.class);
            Root<PriceConfirmation> root = query.from(PriceConfirmation.class);
            query.select(root).where(builder.equal(root.get("id"), id));

            PriceConfirmation priceConfirmationToRemove = session.createQuery(query).uniqueResult();

            if (priceConfirmationToRemove != null) {
                session.remove(priceConfirmationToRemove);
                session.getTransaction().commit();
                return true;
            } else {
                session.getTransaction().rollback();
                System.out.println("No PriceConfirmation found for meal: " + id);
                return false;
            }

        } catch (Exception e) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static boolean removeDiscountConfirmation(int id) {
        try {
            System.out.println("Remove Conf");
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Discounts> query = builder.createQuery(Discounts.class);
            Root<Discounts> root = query.from(Discounts.class);
            query.select(root).where(builder.equal(root.get("id"), id));

            Discounts discountConfirmationToRemove = session.createQuery(query).uniqueResult();

            if (discountConfirmationToRemove != null) {
                session.remove(discountConfirmationToRemove);
                session.getTransaction().commit();
                return true;
            } else {
                session.getTransaction().rollback();
                System.out.println("No PriceConfirmation found for discount: " + id);
                return false;
            }

        } catch (Exception e) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
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


    static int makediscount(double discountPercentage , String category) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            // Fetch all meals
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            Root<Meal> root = query.from(Meal.class);
            query.select(root);

            List<Meal> meals = session.createQuery(query).getResultList();

            int count = 0;
            double discountFactor = (100.0 - discountPercentage) / 100.0;
            if(category.equals("haifa"))
            {
                category = "special1";
            }
            if(category.equals("tel-aviv"))
            {
                category = "special2";
            }
            if(category.equals("nahariya"))
            {
                category = "special3";
            }


            for (Meal meal : meals) {
                if(meal.getMealCategory().equals(category))
                {
                    double originalPrice = meal.getMealPrice();
                    double newPrice = originalPrice * discountFactor;

                    meal.setMealPrice(Math.round(newPrice * 100.0) / 100.0);
                    session.update(meal);
                    count++;
                }


            }
            session.getTransaction().commit();
            return 1;

        } catch (Exception e) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static int addMeal(String Name , String Description, String Preferences,  double Price , String Image , String Category) {
        try {
            System.out.println("Add meal method reached");
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();
            Meal newMeal = new Meal(Name, Description, Preferences, Price, Image, Category);
            if(Category.equals("special1"))
            {
                Restaurant haifaRestaurant = session.get(Restaurant.class, 1);
                newMeal.getRestaurants().add(haifaRestaurant);
            } else if (Category.equals("special2")) {
                Restaurant TelAvivRestaurant = session.get(Restaurant.class, 2);
                newMeal.getRestaurants().add(TelAvivRestaurant);
            } else if (Category.equals("special3")) {
                Restaurant NahariyaRestaurant = session.get(Restaurant.class, 3);
                newMeal.getRestaurants().add(NahariyaRestaurant);
            } else if (Category.equals("shared meal")) {
                Restaurant haifaRestaurant = session.get(Restaurant.class, 1);
                Restaurant TelAvivRestaurant = session.get(Restaurant.class, 2);
                Restaurant NahariyaRestaurant = session.get(Restaurant.class, 3);
                newMeal.getRestaurants().add(NahariyaRestaurant);
                newMeal.getRestaurants().add(haifaRestaurant);
                newMeal.getRestaurants().add(TelAvivRestaurant);
            }


            session.persist(newMeal);
            session.getTransaction().commit();

            return 1;
        } catch (Exception e) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static String removeMealByName(String mealName) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            Root<Meal> root = query.from(Meal.class);
            query.select(root).where(builder.equal(root.get("mealName"), mealName));

            Meal mealToRemove = session.createQuery(query).uniqueResult();

            if (mealToRemove != null) {
                String category = mealToRemove.getMealCategory();

                session.remove(mealToRemove);
                session.getTransaction().commit();
                System.out.println("Meal '" + mealName + "' removed successfully.");
                return category;
            } else {
                System.out.println("Meal not found: " + mealName);
                session.getTransaction().rollback();
                return null;
            }

        } catch (Exception e) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static String changeMealCategory(String mealName, String from, String to) {
        try {
            System.out.println("Changing category for meal: " + mealName + " from " + from + " to " + to);
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            Root<Meal> root = query.from(Meal.class);
            query.select(root).where(
                    builder.equal(root.get("mealName"), mealName)
            );

            Meal meal = session.createQuery(query).uniqueResult();

            if (meal != null) {
                // Step 1: update category
                meal.setMealCategory(to);

                // Step 2: clear existing restaurant links
                meal.getRestaurants().clear();

                // Step 3: reassign based on new category
                if (to.equals("special1")) {
                    meal.getRestaurants().add(session.get(Restaurant.class, 1));
                } else if (to.equals("special2")) {
                    meal.getRestaurants().add(session.get(Restaurant.class, 2));
                } else if (to.equals("special3")) {
                    meal.getRestaurants().add(session.get(Restaurant.class, 3));
                } else if (to.equals("shared meal")) {
                    meal.getRestaurants().add(session.get(Restaurant.class, 1));
                    meal.getRestaurants().add(session.get(Restaurant.class, 2));
                    meal.getRestaurants().add(session.get(Restaurant.class, 3));
                }

                session.update(meal);
                session.getTransaction().commit();
                return to;

            } else {
                session.getTransaction().rollback();
                System.out.println("Meal not found.");
                return null;
            }

        } catch (Exception e) {
            if (session != null && session.isOpen()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    static int updateMealIngredient(String mealName, String Ingredient) {
        try {
            System.out.println("update method reached");
            // Start a session and transaction
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            System.out.println("meal name in update function" + mealName);
            System.out.println("meal Ingredient in update function" + Ingredient);
            // Build the query to update the meal price
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Meal> updateQuery = builder.createCriteriaUpdate(Meal.class);
            Root<Meal> root = updateQuery.from(Meal.class);
            updateQuery.set("mealDescription", Ingredient)
                    .where(builder.equal(root.get("mealName"), mealName)); // Find the meal by name



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

    static int updateMealPref(String mealName, String pref) {
        try {
            System.out.println("update method reached");
            // Start a session and transaction
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            System.out.println("meal name in update function" + mealName);
            System.out.println("meal preferences in update function" + pref);
            // Build the query to update the meal price
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Meal> updateQuery = builder.createCriteriaUpdate(Meal.class);
            Root<Meal> root = updateQuery.from(Meal.class);
            updateQuery.set("mealPreferences", pref)
                    .where(builder.equal(root.get("mealName"), mealName)); // Find the meal by name



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


    public static double getCurrentMealPrice(String mealName) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            Root<Meal> root = query.from(Meal.class);
            query.select(root).where(builder.equal(root.get("mealName"), mealName));

            Meal meal = session.createQuery(query).uniqueResult();
            session.getTransaction().commit();

            return meal != null ? meal.getMealPrice() : -1;
        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
            return -1;
        } finally {
            if (session != null) session.close();
        }
    }


    public static List<PriceConfirmation> addPriceConfirmation(String mealName, double oldPrice, double newPrice) {
        List<PriceConfirmation> allConfirmations = new ArrayList<>();
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            PriceConfirmation pc = new PriceConfirmation(mealName, oldPrice, newPrice);
            session.save(pc);

            session.getTransaction().commit();

            // Fetch all current price confirmations from the DB
            session.beginTransaction();
            allConfirmations = session.createQuery("FROM PriceConfirmation", PriceConfirmation.class).getResultList();
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return allConfirmations;
    }


    public static List<Discounts> addDiscountConfirmation(double percentage,String category) {
        List<Discounts> allDiscounts = new ArrayList<>();
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            Discounts pc = new Discounts(percentage, category);
            session.save(pc);

            session.getTransaction().commit();

            // Fetch all current price confirmations from the DB
            session.beginTransaction();
            allDiscounts = session.createQuery("FROM Discounts", Discounts.class).getResultList();
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }

        return allDiscounts;
    }


    public static List<Discounts> getDiscountConfirmations() {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Discounts> query = builder.createQuery(Discounts.class);
            Root<Discounts> root = query.from(Discounts.class);
            query.select(root);

            List<Discounts> results = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return results;
        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }

    public static boolean mealExist(String mealName) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            Root<Meal> root = query.from(Meal.class);

            query.select(root).where(builder.equal(builder.lower(root.get("mealName")), mealName.toLowerCase()));
            List<Meal> results = session.createQuery(query).getResultList();

            session.getTransaction().commit();

            return !results.isEmpty(); // True if meal exists
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static String getMealCategoryByName(String mealName) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            Root<Meal> root = query.from(Meal.class);
            query.select(root).where(builder.equal(root.get("mealName"), mealName));

            Meal meal = session.createQuery(query).uniqueResult();
            session.getTransaction().commit();

            return (meal != null) ? meal.getMealCategory() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }


    public static List<PriceConfirmation> getPriceConfirmations() {
        List<PriceConfirmation> confirmations = new ArrayList<>();
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            List<PriceConfirmation> rawConfirmations = session.createQuery("FROM PriceConfirmation", PriceConfirmation.class).getResultList();

            for (PriceConfirmation pc : rawConfirmations) {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
                Root<Meal> root = query.from(Meal.class);
                query.select(root).where(builder.equal(root.get("mealName"), pc.getMealName()));

                Meal meal = session.createQuery(query).uniqueResult();
                if (meal != null) {
                    pc.setOldPrice(meal.getMealPrice());  // This overrides to latest price
                }

                confirmations.add(pc);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return confirmations;
    }


    public static List<Feedback> getManagerFeedback() {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Feedback> query = builder.createQuery(Feedback.class);
            Root<Feedback> root = query.from(Feedback.class);
            query.select(root);

            List<Feedback> results = session.createQuery(query).getResultList();
            session.getTransaction().commit();
            return results;
        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
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
}
