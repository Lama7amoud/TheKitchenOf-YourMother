package il.cshaifasweng.OCSFMediatorExample.server;

import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.Scanner;

import il.cshaifasweng.OCSFMediatorExample.entities.HostingTable;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.AuthorizedUser;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class DataManager {

    private static Session session;
    private static String password = "";

    private static SessionFactory getSessionFactory(String password) throws HibernateException {

        Configuration configuration = new Configuration();
        // Override the password
        configuration.setProperty("hibernate.connection.password", password);
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(AuthorizedUser.class);
        configuration.addAnnotatedClass(Restaurant.class);
        configuration.addAnnotatedClass(HostingTable.class);
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


    private static void generateData() throws Exception {

        // Create Italian restaurants
        Restaurant restaurant1 = new Restaurant();
        restaurant1.setName("Mom Kitchen");
        restaurant1.setAddress("German Colony");
        restaurant1.setLocation("Haifa");
        restaurant1.setPhoneNumber("123-456-7890");
        restaurant1.setActivityHours("Monday-Saturday: 10:00 AM - 11:00 PM");

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Mom Kitchen");
        restaurant2.setAddress("Rothschild");
        restaurant2.setLocation("Tel-Aviv");
        restaurant2.setPhoneNumber("987-654-3210");
        restaurant2.setActivityHours("Monday-Sunday: 9:00 AM - 10:00 PM");

        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Mom Kitchen");
        restaurant3.setAddress("Weizman");
        restaurant3.setLocation("Nahariya");
        restaurant3.setPhoneNumber("555-123-4567");
        restaurant3.setActivityHours("Tuesday-Sunday: 11:00 AM - 12:00 AM");


        // Persist restaurants to the database
        session.save(restaurant1);
        session.save(restaurant2);
        session.save(restaurant3);

        session.flush();





// Create Italian meals with the same image path
        Meal meal1 = new Meal("Margherita Pizza", "Classic pizza with fresh mozzarella and basil", "Vegetarian", 10.99 , "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal0.jpg","special1");
        Meal meal2 = new Meal("Spaghetti Carbonara", "Pasta with pancetta, eggs, and Parmesan cheese", "Contains dairy", 13.99 , "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal1.jpg","shared meal");
        Meal meal3 = new Meal("Lasagna", "Layered pasta with rich meat sauce and creamy béchamel", "Contains dairy", 14.99,"/il/cshaifasweng/OCSFMediatorExample/client/meals/meal2.jpg","shared meal");
        Meal meal4 = new Meal("Risotto alla Milanese", "Creamy risotto with saffron and Parmesan", "Vegetarian", 12.99,"/il/cshaifasweng/OCSFMediatorExample/client/meals/meal3.jpg","shared meal");
        Meal meal5 = new Meal("Fettuccine Alfredo", "Pasta in a creamy Parmesan cheese sauce", "Vegetarian", 12.49,"/il/cshaifasweng/OCSFMediatorExample/client/meals/meal4.jpg","shared meal");
        Meal meal6 = new Meal("Penne Arrabbiata", "Penne pasta with a spicy tomato and garlic sauce", "Vegan, Spicy", 11.99, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal5.jpg","special2");
        Meal meal7 = new Meal("Ravioli Ricotta e Spinaci", "Pasta pockets filled with ricotta cheese and spinach", "Vegetarian", 13.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal6.jpg","special3");
        Meal meal8 = new Meal("Calzone Classico", "Folded pizza with ham, mozzarella, and mushrooms", "Contains pork and dairy", 13.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal7.jpg","special1");
        Meal meal9 = new Meal("Arancini", "Fried risotto balls stuffed with cheese and peas", "Contains dairy", 8.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal8.jpg","special2");
        Meal meal10 = new Meal("Polenta ai Funghi", "Creamy polenta topped with sautéed wild mushrooms", "Vegetarian", 11.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal9.jpg","special3");




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
}
