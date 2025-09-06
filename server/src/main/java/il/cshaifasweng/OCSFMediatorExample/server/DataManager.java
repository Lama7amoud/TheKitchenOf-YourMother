package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Reservation;


import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.*;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import il.cshaifasweng.OCSFMediatorExample.entities.*;

import il.cshaifasweng.OCSFMediatorExample.entities.HostingTable;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.ReservedTime;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.Transaction;
import java.util.stream.Collectors;


public class DataManager {

    private static Session session;
    private static String password = "";
    public static String getPassword(){
        return password;
    }
    private static SessionFactory sessionFactory;
    private static SessionFactory getSessionFactory(String password) throws HibernateException {


        Configuration configuration = new Configuration();
        // Override the password
        configuration.setProperty("hibernate.connection.password", password);
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(PriceConfirmation.class);
        configuration.addAnnotatedClass(Discounts.class);
        configuration.addAnnotatedClass(Feedback.class);
        configuration.addAnnotatedClass(Complaint.class);
        configuration.addAnnotatedClass(DailyReport.class);
        configuration.addAnnotatedClass(MonthlyReport.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Restaurant.class);
        configuration.addAnnotatedClass(HostingTable.class);
        configuration.addAnnotatedClass(Business.class);
        configuration.addAnnotatedClass(Reservation.class);
        configuration.addAnnotatedClass(ReservedTime.class);
        configuration.addAnnotatedClass(MealOrder.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static boolean hasActiveReservationAround(String idNumber,
                                                     LocalDateTime candidateTime,
                                                     int minutesEitherSide) {
        SessionFactory sf = getSessionFactory(password);
        Session s = null;
        try {
            s = sf.openSession();
            s.beginTransaction();

            LocalDateTime start = candidateTime.minusMinutes(minutesEitherSide);
            LocalDateTime end   = candidateTime.plusMinutes(minutesEitherSide);

            Long count = s.createQuery(
                            "SELECT COUNT(r) FROM Reservation r " +
                                    "WHERE r.idNumber = :id " +
                                    "  AND lower(r.status) = 'on' " +
                                    "  AND r.reservationTime BETWEEN :start AND :end", Long.class)
                    .setParameter("id", idNumber)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getSingleResult();

            s.getTransaction().commit();
            return (count != null && count > 0);
        } catch (Exception e) {
            if (s != null && s.getTransaction().isActive()) s.getTransaction().rollback();
            e.printStackTrace();
            // On error, be conservative and say there's a conflict
            return true;
        } finally {
            if (s != null) s.close();
        }
    }



    // called by a function that already opened the session
    private static List<Meal> getMenu() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
        query.from(Meal.class);

        List<Meal> data = session.createQuery(query).getResultList();
        return data;
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
        restaurant1.setName("Haifa-Mom Kitchen");
        restaurant1.setAddress("German Colony");
        restaurant1.setLocation("Haifa");
        restaurant1.setPhoneNumber("123-456-7890");
        restaurant1.setOpeningTime(10.00);  // 10:00 AM
        restaurant1.setClosingTime(23.00);  // 11:00 PM
        restaurant1.setHolidays("Sunday");
        restaurant1.setBusiness(momKitchenLtd);

        Restaurant restaurant2 = new Restaurant();
        restaurant2.setName("Tel-Aviv-Mom Kitchen");
        restaurant2.setAddress("Rothschild");
        restaurant2.setLocation("Tel-Aviv");
        restaurant2.setPhoneNumber("987-654-3210");
        restaurant2.setOpeningTime(9.00);   // 9:00 AM
        restaurant2.setClosingTime(22.00);  // 10:00 PM
        restaurant2.setHolidays("Saturday");
        restaurant2.setBusiness(momKitchenLtd);


        Restaurant restaurant3 = new Restaurant();
        restaurant3.setName("Nahariya-Mom Kitchen");
        restaurant3.setAddress("Weizman");
        restaurant3.setLocation("Nahariya");
        restaurant3.setPhoneNumber("555-123-4567");
        restaurant3.setOpeningTime(11.00);  // 11:00 AM
        restaurant3.setClosingTime(23.00);  // 11:00 PM
        restaurant3.setHolidays("Monday");
        restaurant3.setBusiness(momKitchenLtd);

        session.save(restaurant1);
        session.save(restaurant2);
        session.save(restaurant3);
        session.flush();


        // Create Italian meals with the same image path
        Meal meal1 = new Meal("Margherita Pizza", "Classic pizza with fresh mozzarella and basil", "tuna, olive, corn, mushroom", 10.99 , "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal0.jpg", "special1");
        Meal meal2 = new Meal("Spaghetti Carbonara", "Pasta with pancetta, eggs, and Parmesan cheese", "pancetta, egg, parmesan", 13.99 , "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal1.jpg", "shared meal");
        Meal meal3 = new Meal("Lasagna", "Layered pasta with rich meat sauce and creamy bÃ©chamel", "beef, tomato sauce, bÃ©chamel", 14.99, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal2.jpg", "shared meal");
        Meal meal4 = new Meal("Risotto alla Milanese", "Creamy risotto with saffron and Parmesan", "saffron, parmesan, butter", 12.99, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal3.jpg", "shared meal");
        Meal meal5 = new Meal("Fettuccine Alfredo", "Pasta in a creamy Parmesan cheese sauce", "cream, parmesan, butter", 12.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal4.jpg", "shared meal");
        Meal meal6 = new Meal("Penne Arrabbiata", "Penne pasta with a spicy tomato and garlic sauce", "tomato, garlic, chili", 11.99, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal5.jpg", "special2");
        Meal meal7 = new Meal("Ravioli Ricotta e Spinaci", "Pasta pockets filled with ricotta cheese and spinach", "ricotta, spinach, nutmeg", 13.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal6.jpg", "special3");
        Meal meal8 = new Meal("Calzone Classico", "Folded pizza with ham, mozzarella, and mushrooms", "ham, mozzarella, mushroom", 13.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal7.jpg", "special1");
        Meal meal9 = new Meal("Arancini", "Fried risotto balls stuffed with cheese and peas", "cheese, peas, breadcrumbs", 8.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal8.jpg", "special2");
        Meal meal10 = new Meal("Polenta ai Funghi", "Creamy polenta topped with sautÃ©ed wild mushrooms", "mushrooms, garlic, butter", 11.49, "/il/cshaifasweng/OCSFMediatorExample/client/meals/meal9.jpg", "special3");

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
        User user1 = new User();
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
        User user2 = new User();
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
        User user3 = new User();
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
        User user4 = new User();
        user4.setUsername("Lama");
        user4.setPassword("password777");
        user4.setFirstname("Lama");
        user4.setLastname("Hamoud");
        user4.setIDNum("222334455");
        user4.setAge((short) 22);
        user4.setRestaurant(restaurant1);
        user4.setConnected(false);
        user4.setPermissionLevel((short) 3);

        User user6 = new User();
        user6.setUsername("Malki");
        user6.setPassword("password111");
        user6.setFirstname("Malki");
        user6.setLastname("Grossman");
        user6.setIDNum("123456789");
        user6.setAge((short) 30);
        user6.setRestaurant(restaurant2);
        user6.setConnected(false);
        user6.setPermissionLevel((short) 3);

        User user7 = new User();
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
        User user5 = new User();
        user5.setUsername("Oraib");
        user5.setPassword("password888");
        user5.setFirstname("Oraib");
        user5.setLastname("Marzook");
        user5.setIDNum("111223344");
        user5.setAge((short) 29);
        user5.setRestaurant(restaurant1);
        user5.setConnected(false);
        user5.setPermissionLevel((short) 1);

        User user8 = new User();
        user8.setUsername("Rasha");
        user8.setPassword("password333");
        user8.setFirstname("Rasha");
        user8.setLastname("Odeh");
        user8.setIDNum("910229304");
        user8.setAge((short) 25);
        user8.setRestaurant(restaurant2);
        user8.setConnected(false);
        user8.setPermissionLevel((short) 1);

        User user9 = new User();
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
            table.setSeatsNumber((i % 3) + 2);  // Seats: 2–4
            table.setRestaurant(restaurant1);
            table.setInside(i <= 8);
            table.setReservedTimes(new ArrayList<>());
            session.save(table);
        }


        // Insert tables for Tel Aviv branch (8 tables)
        for (int i = 1; i <= 8; i++) {
            HostingTable table1 = new HostingTable();
            table1.setTableNumber(i);
            table1.setSeatsNumber((i % 3) + 2);  // Seats: 2–4
            table1.setRestaurant(restaurant2);
            table1.setInside(i <= 6);
            table1.setReservedTimes(new ArrayList<>());
            session.save(table1);
        }


        // Insert tables for Nahariya branch (14 tables)
        for (int i = 1; i <= 14; i++) {
            HostingTable table2 = new HostingTable();
            table2.setTableNumber(i);
            table2.setSeatsNumber((i % 3) + 2);  // Seats: 2–4
            table2.setRestaurant(restaurant3);
            table2.setInside(i <= 10);
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

    static boolean saveFeedback(String feedbackMsg, int rating, int restaurantId,String idNumber,String name) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            // Load the restaurant from DB
            Restaurant restaurant = session.get(Restaurant.class, restaurantId);
            if (restaurant == null) {
                System.err.println("Restaurant not found with ID: " + restaurantId);
                session.getTransaction().rollback();
                return false;
            }

            // Create and fill the feedback
            Feedback feedback = new Feedback();
            feedback.setMessage(feedbackMsg);
            feedback.setRating(rating);
            feedback.setSubmittedAt(LocalDateTime.now());
            feedback.setRestaurant(restaurant);
            feedback.setUserId(idNumber);
            feedback.setName(name);


            // Save to DB
            session.save(feedback);
            session.getTransaction().commit();

            return true;

        } catch (Exception e) {
            System.err.println("Error saving feedback:");
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
                System.out.println("Session closed");
            }
        }
    }


    public static boolean userMatchesActiveReservationForFeedback(String idNumber, String name, int restaurantId) {
        SessionFactory sf = getSessionFactory(password);
        Session session = null;
        try {
            session = sf.openSession();
            session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Reservation> root = cq.from(Reservation.class);

            List<Predicate> preds = new ArrayList<>();

            if (idNumber != null && !idNumber.isBlank()) {
                preds.add(cb.equal(root.get("idNumber"), idNumber.trim()));
            } else {
                session.getTransaction().rollback();
                return false;
            }

            if (name != null && !name.isBlank()) {
                preds.add(cb.equal(cb.lower(root.get("name")), name.trim().toLowerCase()));
            } else {
                session.getTransaction().rollback();
                return false;
            }
            preds.add(cb.equal(root.get("restaurant").get("id"), restaurantId));
            preds.add(cb.equal(cb.lower(root.get("status")), "on"));

            cq.select(cb.count(root)).where(preds.toArray(new Predicate[0]));
            Long count = session.createQuery(cq).getSingleResult();

            session.getTransaction().commit();
            return count != null && count > 0;

        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) session.close();
        }
    }



    public static Complaint saveComplaintReturn(String text, int restaurantId, String idNumber, String name, String email) {
        Session session = getSessionFactory(password).openSession();
        Transaction tx = null;
        Complaint c = null;

        try {
            tx = session.beginTransaction();

            Restaurant res = session.get(Restaurant.class, restaurantId);

            c = new Complaint();
            c.setComplaint(text);
            c.setRestaurant(res);
            c.setUserId(idNumber);
            c.setName(name);
            c.setEmail(email);
            c.setSubmittedAt(LocalDateTime.now());
            c.setStatus(false); // unresolved by default
            c.setAutoresponse(false); // default
            c.setResponse("");      // empty response by default

            session.save(c);
            tx.commit();

            // -------------------------
            // Scheduler for auto-response after 24 hours
            // -------------------------
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            LocalDateTime now = LocalDateTime.now();
                LocalDateTime targetTime = c.getSubmittedAt().plusHours(24);

            long delay = Duration.between(now, targetTime).toMillis();
            final int complaintId = c.getId(); // effectively final for lambda

            scheduler.schedule(() -> {
                try (Session taskSession = getSessionFactory(password).openSession()) {
                    taskSession.beginTransaction();
                    Complaint complaint = taskSession.get(Complaint.class, complaintId);
                    if (complaint != null
                            && !complaint.getAutoresponse()
                            && (complaint.getResponse() == null || complaint.getResponse().isEmpty())) {

                        // Send email before setting autoresponse
                        EmailSender.sendEmail(
                                complaint.getEmail(),
                                "Auto Response - Complaint Follow-up",
                                "Hello " + complaint.getName() + ",\n\nThis is an automatic response regarding your complaint submitted on "
                                        + complaint.getSubmittedAt() + ". Our team will review it shortly."
                        );

                        complaint.setAutoresponse(true);
                        taskSession.update(complaint);
                    }
                    taskSession.getTransaction().commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }, delay, TimeUnit.MILLISECONDS);
            // -------------------------

            // Update daily report if needed
            updateDailyReportComplaints(c);
            return c;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }





    public static Complaint updateComplaint(int id, String response, double refund) {
        Session session = getSessionFactory(password).openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Complaint c = session.get(Complaint.class, id);
            if (c != null) {
                c.setResponse(response);
                c.setRefund(refund);
                c.setStatus(true);
                session.update(c);
                EmailSender.sendEmail(c.getEmail(),
                        "Customer Service Response - Complaint Follow-up",
                        "Hello " + c.getName() + "\n\n" + c.getResponse()+ "\n\n"+"Refund = "+refund);
            }
            tx.commit();
            return c;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }



    public static void updateDailyReportComplaints(Complaint complaint) {
        Restaurant restaurant = complaint.getRestaurant();
        LocalDate complaintDate = complaint.getSubmittedAt().toLocalDate();

        LocalDateTime startOfDay = complaintDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            DailyReport report = session.createQuery(
                            "FROM DailyReport WHERE restaurant = :restaurant AND day = :startOfDay",
                            DailyReport.class)
                    .setParameter("restaurant", restaurant)
                    .setParameter("startOfDay", startOfDay)
                    .uniqueResult();

            Long complaintCount = session.createQuery(
                            "SELECT COUNT(c) FROM Complaint c WHERE c.restaurant = :restaurant " +
                                    "AND c.submittedAt >= :startOfDay AND c.submittedAt < :endOfDay", Long.class)
                    .setParameter("restaurant", restaurant)
                    .setParameter("startOfDay", startOfDay)
                    .setParameter("endOfDay", endOfDay)
                    .uniqueResult();

            if (report == null) {
                report = new DailyReport();
                report.setRestaurant(restaurant);
                report.setDay(startOfDay);
                report.setComplaintsCount(complaintCount != null ? complaintCount.intValue() : 1);
                report.setTotalCustomers(0);
                report.setDeliveryOrders(0);
                report.setReservations(0);
                session.save(report);
            } else {
                report.setComplaintsCount(complaintCount != null ? complaintCount.intValue() : 0);
                session.update(report);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }



    public static void updateDailyReport(Reservation reservation) {
        Restaurant restaurant = reservation.getRestaurant();
        LocalDate reservationDate = reservation.getReservationTime().toLocalDate();

        LocalDateTime startOfDay = reservationDate.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            DailyReport report = session.createQuery(
                            "FROM DailyReport WHERE restaurant = :restaurant AND day = :startOfDay",
                            DailyReport.class)
                    .setParameter("restaurant", restaurant)
                    .setParameter("startOfDay", startOfDay)
                    .uniqueResult();


            if (report == null) {
                report = new DailyReport();
                report.setRestaurant(restaurant);
                report.setDay(startOfDay);

                report.setTotalCustomers(reservation.isTakeAway()? 0 : reservation.getTotalGuests());
                report.setReservations(reservation.isTakeAway() ? 0 : 1);
                report.setDeliveryOrders(reservation.isTakeAway() ? 1 : 0);
                report.setComplaintsCount(0);  // complaints not updated here
            } else {
                Long totalCustomers = session.createQuery(
                                "SELECT COALESCE(SUM(r.totalGuests), 0) FROM Reservation r " +
                                        "WHERE r.restaurant = :restaurant " +
                                        "AND r.isTakeAway = false " +
                                        "AND r.cancellationStatus = 'on' " +
                                        "AND r.reservationTime >= :startOfDay " +
                                        "AND r.reservationTime < :endOfDay", Long.class)
                        .setParameter("restaurant", restaurant)
                        .setParameter("startOfDay", startOfDay)
                        .setParameter("endOfDay", endOfDay)
                        .uniqueResult();


                Long deliveryOrders = session.createQuery(
                                "SELECT COUNT(r) FROM Reservation r " +
                                        "WHERE r.restaurant = :restaurant " +
                                        "AND r.isTakeAway = true " +
                                        "AND r.cancellationStatus = 'on' " +
                                        "AND r.reservationTime >= :startOfDay " +
                                        "AND r.reservationTime < :endOfDay", Long.class)
                        .setParameter("restaurant", restaurant)
                        .setParameter("startOfDay", startOfDay)
                        .setParameter("endOfDay", endOfDay)
                        .uniqueResult();


                Long reservations = session.createQuery(
                                "SELECT COUNT(r) FROM Reservation r " +
                                        "WHERE r.restaurant = :restaurant " +
                                        "AND r.isTakeAway = false " +
                                        "AND r.cancellationStatus = 'on' " +
                                        "AND r.reservationTime >= :startOfDay " +
                                        "AND r.reservationTime < :endOfDay", Long.class)
                        .setParameter("restaurant", restaurant)
                        .setParameter("startOfDay", startOfDay)
                        .setParameter("endOfDay", endOfDay)
                        .uniqueResult();


                report.setTotalCustomers(totalCustomers.intValue());
                report.setDeliveryOrders(deliveryOrders.intValue());
                report.setReservations(reservations.intValue());
            }

            // Link reservation to report
            reservation.setDailyReport(report);
            session.saveOrUpdate(reservation);
            session.saveOrUpdate(report);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }



    static User checkPermission(String details) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            int spaceIndex = details.indexOf(" ");
            details = details.substring(spaceIndex + 1);

            String[] parts = details.split(";");
            System.out.println(Arrays.toString(parts));
            if (parts.length < 2) {
                User errorUser = new User();
                errorUser.setMessageToServer("Invalid input format. Expected 'username;password'");
                return errorUser;
            }

            String username = parts[0].trim();
            String password = parts[1].trim();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);

            // Search for user by username
            query.select(root).where(builder.equal(root.get("username"), username));
            User user = session.createQuery(query).uniqueResult();

            if (user == null) {
                User errorUser = new User();
                errorUser.setMessageToServer("User does not exist");
                return errorUser;
            }

            // Verify password
            if (!user.getPassword().equals(password)) {
                User errorUser = new User();
                errorUser.setMessageToServer("Incorrect password");
                return errorUser;
            }

            // Check if the user is already connected
            if (user.isConnected()) {
                User errorUser = new User();
                errorUser.setMessageToServer("You are already connected with this user.");
                return errorUser;
            }

            user.setConnected(true);
            session.update(user);
            session.getTransaction().commit();

            User currentUser = new User();
            currentUser.copyUser(user);
            currentUser.setMessageToServer("Login successful");

            return currentUser;

        } catch (Exception exception) {
            System.err.println("An error occurred");
            exception.printStackTrace();
            User errorUser = new User();
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
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root = query.from(User.class);

            // Search for user by username
            query.select(root).where(builder.equal(root.get("username"), username));

            // Get the user object from the query result
            User user = session.createQuery(query).uniqueResult();

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

                meal.setMealCategory(to);

                meal.getRestaurants().clear();

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

    public static List<Complaint> getComplaint() {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Complaint> query = builder.createQuery(Complaint.class);
            Root<Complaint> root = query.from(Complaint.class);
            query.select(root);

            List<Complaint> results = session.createQuery(query).getResultList();
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

        if(!requestMenu().isEmpty()) {
            try {
                SessionFactory sessionFactory = getSessionFactory(password);
                session = sessionFactory.openSession();
                session.beginTransaction();

                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                // Query reservations with status = on
                List<Reservation> reservations = session.createQuery(
                        "FROM Reservation r WHERE r.status = 'on'", Reservation.class).list();

                for (Reservation r : reservations) {
                    LocalDateTime reservationTime = r.getReservationTime();
                    LocalDateTime now = LocalDateTime.now();

                    if (r.isTakeAway()) {
                        // TakeAway: turn off immediately if time passed
                        if (!now.isBefore(reservationTime)) {
                            r.setStatus("off");
                            session.update(r);
                        } else {
                            // Schedule to turn off exactly at reservationTime
                            long delay = Duration.between(now, reservationTime).toMillis();
                            scheduler.schedule(() -> {
                                try (Session taskSession = sessionFactory.openSession()) {
                                    taskSession.beginTransaction();
                                    Reservation res = taskSession.get(Reservation.class, r.getId());
                                    if (res != null && "on".equals(res.getStatus())) {
                                        res.setStatus("off");
                                        taskSession.update(res);
                                    }
                                    taskSession.getTransaction().commit();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }, delay, TimeUnit.MILLISECONDS);
                        }
                    } else {
                        // Normal reservation: expiry after 90 minutes
                        LocalDateTime expiryTime = reservationTime.plusMinutes(90);

                        if (!now.isBefore(expiryTime)) {
                            r.setStatus("off");
                            session.update(r);
                        } else {
                            // Schedule to turn off after 90 minutes from reservationTime
                            long delay = Duration.between(now, expiryTime).toMillis();
                            scheduler.schedule(() -> {
                                try (Session taskSession = sessionFactory.openSession()) {
                                    taskSession.beginTransaction();
                                    Reservation res = taskSession.get(Reservation.class, r.getId());
                                    if (res != null && "on".equals(res.getStatus())) {
                                        res.setStatus("off");
                                        taskSession.update(res);
                                    }
                                    taskSession.getTransaction().commit();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }, delay, TimeUnit.MILLISECONDS);
                        }
                    }
                }

                // Handle Complaints Auto-response
                LocalDateTime now = LocalDateTime.now();
                List<Complaint> complaints = session.createQuery(
                        "FROM Complaint c WHERE c.autoresponse = false", Complaint.class).list();

                for (Complaint c : complaints) {
                    LocalDateTime targetTime = c.getSubmittedAt().plusHours(24);

                    if (!now.isBefore(targetTime)) {
                        // 24 hours have already passed → set autoresponse immediately
                        if (!c.getAutoresponse() && (c.getResponse() == null || c.getResponse().isEmpty())) {
                            // Send email first
                            EmailSender.sendEmail(
                                    c.getEmail(),
                                    "Auto Response - Complaint Follow-up",
                                    "Hello " + c.getName() + ",\n\nThis is an automatic response regarding your complaint submitted on "
                                            + c.getSubmittedAt() + ". Our team will review it shortly."
                            );

                            // Then set autoresponse
                            c.setAutoresponse(true);
                            session.update(c);
                        }
                    } else {
                        // Schedule autoresponse for exactly 24 hours after submittedAt
                        long delay = Duration.between(now, targetTime).toMillis();
                        final int complaintId = c.getId(); // make id effectively final for lambda

                        scheduler.schedule(() -> {
                            try (Session taskSession = sessionFactory.openSession()) {
                                taskSession.beginTransaction();
                                Complaint complaint = taskSession.get(Complaint.class, complaintId);
                                if (complaint != null
                                        && !complaint.getAutoresponse()
                                        && (complaint.getResponse() == null || complaint.getResponse().isEmpty())) {

                                    // Send email
                                    EmailSender.sendEmail(
                                            complaint.getEmail(),
                                            "Auto Response - Complaint Follow-up",
                                            "Hello " + complaint.getName() + ",\n\nThis is an automatic response regarding your complaint submitted on "
                                                    + complaint.getSubmittedAt() + ". Our team will review it shortly."
                                    );

                                    // Then set autoresponse
                                    complaint.setAutoresponse(true);
                                    taskSession.update(complaint);
                                }
                                taskSession.getTransaction().commit();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }, delay, TimeUnit.MILLISECONDS);
                    }
                }

                // ===== Monthly Reports handled here =====

                // 1) Backfill missing monthly reports for past months
                ZoneId tz = ZoneId.of("Asia/Jerusalem");
                LocalDateTime firstDayCurrentMonth = LocalDate.now(tz).withDayOfMonth(1).atStartOfDay();
                List<Restaurant> allRestaurants = session.createQuery("FROM Restaurant", Restaurant.class).list();

                for (Restaurant r : allRestaurants) {
                    List<DailyReport> pastDRs = session.createQuery(
                                    "FROM DailyReport dr WHERE dr.restaurant = :r AND dr.day < :start",
                                    DailyReport.class)
                            .setParameter("r", r)
                            .setParameter("start", firstDayCurrentMonth)
                            .list();

                    Map<YearMonth, List<DailyReport>> byYM = pastDRs.stream()
                            .collect(Collectors.groupingBy(dr -> YearMonth.from(dr.getDay())));

                    for (Map.Entry<YearMonth, List<DailyReport>> e : byYM.entrySet()) {
                        YearMonth ym = e.getKey();
                        LocalDateTime monthStart = ym.atDay(1).atStartOfDay();
                        LocalDateTime monthEnd = ym.plusMonths(1).atDay(1).atStartOfDay();

                        if (!monthStart.isBefore(firstDayCurrentMonth)) {
                            System.out.println("continued");
                            continue;
                        }
                        MonthlyReport existing = session.createQuery(
                                        "FROM MonthlyReport mr WHERE mr.restaurant = :r AND mr.month = :m",
                                        MonthlyReport.class)
                                .setParameter("r", r)
                                .setParameter("m", monthStart)
                                .uniqueResult();

                        if (existing == null) {
                            List<DailyReport> monthDRs = session.createQuery(
                                            "FROM DailyReport dr WHERE dr.restaurant = :r AND dr.day >= :start AND dr.day < :end",
                                            DailyReport.class)
                                    .setParameter("r", r)
                                    .setParameter("start", monthStart)
                                    .setParameter("end", monthEnd)
                                    .list();

                            if (!monthDRs.isEmpty()) {
                                MonthlyReport mr = new MonthlyReport();
                                mr.setRestaurant(r);
                                mr.setMonth(monthStart);
                                mr.setDailyReports(monthDRs);
                                for (DailyReport dr : monthDRs) { dr.setMonthlyReport(mr); session.merge(dr); }
                                mr.updateFromDailyReports();
                                session.save(mr);
                            }
                        }
                    }
                }

                // 2) Schedule monthly task at next month boundary
                ZonedDateTime nowZ = ZonedDateTime.now(tz);
                ZonedDateTime nextMonthStart = nowZ.plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
                long delayMs = Duration.between(nowZ, nextMonthStart).toMillis();

                scheduler.schedule(new MonthBoundaryTask(sessionFactory, scheduler), delayMs, TimeUnit.MILLISECONDS);


                session.getTransaction().commit();
            }catch (Exception exception) {
                if (session != null && session.isOpen()) {
                    session.getTransaction().rollback();
                }
                System.err.println("An error occured, changes have been rolled back.");
                exception.printStackTrace();
            }
            return;
        }

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

    public static boolean hasReservationForUser(String idNumber, int restaurantId, String name, String email) {
        SessionFactory sf = getSessionFactory(password);
        Session session = null;
        try {
            session = sf.openSession();
            session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Reservation> root = cq.from(Reservation.class);
            List<Predicate> preds = new ArrayList<>();
            if (idNumber != null && !idNumber.isBlank()) {
                preds.add(cb.equal(root.get("idNumber"), idNumber.trim()));
            }
            if (name != null && !name.isBlank()) {
                preds.add(cb.equal(cb.lower(root.get("name")), name.trim().toLowerCase()));
            }
            if (email != null && !email.isBlank()) {
                preds.add(cb.equal(cb.lower(root.get("email")), email.trim().toLowerCase()));
            }
            if (restaurantId > 0) {
                preds.add(cb.equal(root.get("restaurant").get("id"), restaurantId));
            }
            cq.select(cb.count(root)).where(preds.toArray(new Predicate[0]));
            Long count = session.createQuery(cq).getSingleResult();
            session.getTransaction().commit();

            return count != null && count > 0;
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

    public static Restaurant getRestaurantById(int resId) {
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            return session.get(Restaurant.class, resId);

        } catch (Exception exception) {
            exception.printStackTrace();
            return null; // Restaurant not found or error
        } finally {
            if (session != null) {
                session.close();
                System.out.println("Session closed");
            }
        }
    }

    /*public static void saveReservation(Reservation reservation) {

        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            // Do not save if ID already exists
            boolean timeClash = hasActiveReservationAround(
                    reservation.getIdNumber(),
                    reservation.getReservationTime(),
                    90
            );
            if (timeClash) {
                System.out.println("User has another active reservation within ±90 minutes. Rejecting.");
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

            // ------------------------
            // Scheduler logic starts here
            // ------------------------
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetTime;

            if (reservation.isTakeAway()) {
                // TakeAway → turn off at reservationTime
                targetTime = reservation.getReservationTime();
            } else {
                // Normal → turn off 90 minutes after reservationTime
                targetTime = reservation.getReservationTime().plusMinutes(90);
            }

            if (!now.isBefore(targetTime)) {
                // Time already passed → turn off immediately
                try (Session taskSession = sessionFactory.openSession()) {
                    taskSession.beginTransaction();
                    Reservation res = taskSession.get(Reservation.class, reservation.getId());
                    if (res != null && "on".equals(res.getStatus())) {
                        res.setStatus("off");
                        taskSession.update(res);
                    }
                    taskSession.getTransaction().commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // Schedule to turn off at targetTime
                long delay = Duration.between(now, targetTime).toMillis();
                scheduler.schedule(() -> {
                    try (Session taskSession = sessionFactory.openSession()) {
                        taskSession.beginTransaction();
                        Reservation res = taskSession.get(Reservation.class, reservation.getId());
                        if (res != null && "on".equals(res.getStatus())) {
                            res.setStatus("off");
                            taskSession.update(res);
                        }
                        taskSession.getTransaction().commit();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
            // ------------------------
            // Scheduler logic ends here
            // ------------------------

        } catch (Exception e) {
            session.getTransaction().rollback();
            System.err.println(">>> Error during reservation save:");
            e.printStackTrace();
        } finally {
            session.close();
        }
    }*/

    public static boolean saveReservation(Reservation reservation) {

        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        if (reservation.getCancellationStatus() == null) reservation.setCancellationStatus("on");
        if (reservation.getStatus() == null) reservation.setStatus("on");

        try {

            if (!reservation.isTakeAway()) {
                // Reject if there is another ACTIVE reservation for this ID within ±90 minutes
                boolean timeClash = hasActiveReservationAround(
                        reservation.getIdNumber(),
                        reservation.getReservationTime(),
                        90
                );
                if (timeClash) {
                    System.out.println("User has another active reservation within ±90 minutes. Rejecting.");
                    session.getTransaction().rollback();
                    return false;
                }

                //Bagin of update for double click for reservation
                LocalDateTime start = reservation.getReservationTime();
                LocalDateTime windowStart = start.minusMinutes(60);
                LocalDateTime windowEnd   = start.plusMinutes(60);

                List<HostingTable> picked = reservation.getReservedTables();
                if (picked == null || picked.isEmpty()) {
                    // server-side fallback: compute a combo now
                    List<HostingTable> freeNow = getAvailableTables(reservation);
                    picked = pickMinimalTablesServer(freeNow, reservation.getTotalGuests());
                    if (picked == null) picked = new ArrayList<>();
                    reservation.setReservedTables(picked);
                    if (picked.isEmpty()) {
                        session.getTransaction().rollback();
                        return false; // genuinely no capacity
                    }
                }

                List<Integer> ids = picked.stream()
                        .map(t -> Integer.valueOf(t.getId()))
                        .collect(java.util.stream.Collectors.toList());

                // Lock the selected tables
                session.createQuery(
                                "select t from HostingTable t where t.id in :ids", HostingTable.class)
                        .setParameter("ids", ids)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .getResultList();

                // Re-check overlap
                Long conflicts = session.createQuery(
                                "select count(rt.id) from ReservedTime rt " +
                                        "where rt.table.id in :ids " +
                                        "and rt.reservedTime between :ws and :we", Long.class)
                        .setParameter("ids", ids)
                        .setParameter("ws", windowStart)
                        .setParameter("we", windowEnd)
                        .getSingleResult();

                if (conflicts != null && conflicts > 0) {
                    session.getTransaction().rollback();
                    return false;
                }
            }

            // Save the reservation FIRST to assign its ID
            session.save(reservation);

            // Save ReservedTime only for sit-in reservations
            if (!reservation.isTakeAway() && reservation.getReservedTables() != null) {
                LocalDateTime startTime = reservation.getReservationTime();
                for (HostingTable table : reservation.getReservedTables()) {
                    ReservedTime rt = new ReservedTime(table, startTime, reservation);
                    session.save(rt);
                }
            }


            session.getTransaction().commit();
            System.out.println(">>> Reservation and reserved times committed");

        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.err.println(">>> Error during reservation save:");
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }

        // ------------------------
        // Scheduler logic (outside the transaction)
        // ------------------------
        try {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime targetTime = reservation.isTakeAway()
                    ? reservation.getReservationTime()
                    : reservation.getReservationTime().plusMinutes(90);

            if (!now.isBefore(targetTime)) {
                // Time already passed → turn off immediately
                try (Session taskSession = sessionFactory.openSession()) {
                    taskSession.beginTransaction();
                    Reservation res = taskSession.get(Reservation.class, reservation.getId());
                    if (res != null && "on".equals(res.getStatus())) {
                        res.setStatus("off");
                        taskSession.update(res);
                    }
                    taskSession.getTransaction().commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                // Schedule to turn off at targetTime
                long delay = Duration.between(now, targetTime).toMillis();
                final long savedId = reservation.getId();
                scheduler.schedule(() -> {
                    try (Session taskSession = sessionFactory.openSession()) {
                        taskSession.beginTransaction();
                        Reservation res = taskSession.get(Reservation.class, savedId);
                        if (res != null && "on".equals(res.getStatus())) {
                            res.setStatus("off");
                            taskSession.update(res);
                        }
                        taskSession.getTransaction().commit();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // ------------------------

        return true;
    }

    private static List<HostingTable> pickMinimalTablesServer(List<HostingTable> free, int need) {
        free.sort(java.util.Comparator.comparingInt(HostingTable::getSeatsNumber).reversed());
        List<HostingTable> best = new ArrayList<>();
        backtrackPickServer(free, 0, new ArrayList<>(), 0, need, best);
        return best;
    }

    private static void backtrackPickServer(List<HostingTable> arr, int i,
                                            List<HostingTable> cur, int seats, int need,
                                            List<HostingTable> best) {
        if (seats >= need) {
            if (best.isEmpty() || cur.size() < best.size()) {
                best.clear();
                best.addAll(cur);
            }
            return;
        }
        if (i >= arr.size()) return;
        if (!best.isEmpty() && cur.size() >= best.size()) return; // prune

        cur.add(arr.get(i));
        backtrackPickServer(arr, i + 1, cur, seats + arr.get(i).getSeatsNumber(), need, best);
        cur.remove(cur.size() - 1);

        backtrackPickServer(arr, i + 1, cur, seats, need, best);
    }




    public static List<String[]> findOverlappingReservations(String[] details) {
        Session session = null;
        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Reservation> query = builder.createQuery(Reservation.class);
            Root<Reservation> root = query.from(Reservation.class);

            // Parse the details
            Long restaurantId = Long.parseLong(details[0]);
            LocalDate date = LocalDate.parse(details[1]);      // e.g., "2025-06-15"
            LocalTime time = LocalTime.parse(details[2]);      // e.g., "18:30"
            LocalDateTime startTime = LocalDateTime.of(date, time);
            LocalDateTime endTime = startTime.plusMinutes(90); // Add 1.5 hours

            // Build predicates
            Predicate byRestaurant = builder.equal(root.get("restaurant").get("id"), restaurantId);
            Predicate overlaps = builder.and(
                    builder.lessThan(root.get("reservationTime"), endTime),
                    builder.greaterThan(root.get("reservationTime"), startTime.minusMinutes(90))
            );

            // Execute reservation query
            query.select(root).where(builder.and(byRestaurant, overlaps));
            List<Reservation> reservations = session.createQuery(query).getResultList();

            // Get reserved times using reservation IDs
            List<Long> reservationIds = reservations.stream()
                    .map(Reservation::getId)
                    .collect(Collectors.toList());

            if (reservationIds.isEmpty()) {
                return new ArrayList<>();
            }

            CriteriaQuery<ReservedTime> rtQuery = builder.createQuery(ReservedTime.class);
            Root<ReservedTime> rtRoot = rtQuery.from(ReservedTime.class);
            rtQuery.select(rtRoot).where(rtRoot.get("reservation").get("id").in(reservationIds));
            List<ReservedTime> reservedTimes = session.createQuery(rtQuery).getResultList();

            List<String[]> results = new ArrayList<>();
            for (ReservedTime rt : reservedTimes) {
                String[] row = new String[4];  // Changed size to 4
                row[0] = String.valueOf(rt.getTable().getId());       // Table ID
                row[1] = rt.getReservedTime().toLocalTime().toString(); // Only hour part
                row[2] = rt.getReservation().getName();               // Reservation name
                row[3] = String.valueOf(rt.getReservation().getTotalGuests());  // totalGuests as string
                results.add(row);
            }

            session.getTransaction().commit();
            return results;

        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (session != null) {
                session.close();
                System.out.println("Session closed");
            }
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
            CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
            Root<Reservation> root = query.from(Reservation.class);

            query.select(root).where(cb.equal(root.get("idNumber"), idNumber));

            List<Reservation> reservations = session.createQuery(query).getResultList();

            // Return true if any reservation has status "on"
            for (Reservation res : reservations) {
                if (res.getStatus().equalsIgnoreCase("on")) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }


    public static boolean cancelReservationById(String idNumber) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            // Fetch all reservations with this ID
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Reservation> query = cb.createQuery(Reservation.class);
            Root<Reservation> root = query.from(Reservation.class);
            query.select(root).where(cb.equal(root.get("idNumber"), idNumber));

            List<Reservation> reservations = session.createQuery(query).getResultList();

            // Find the first with status "on"
            for (Reservation res : reservations) {
                if (res.getStatus().equalsIgnoreCase("on")) {
                    res.setStatus("off");
                    res.setCancellationStatus("cancelled");
                    session.update(res);

                    // Delete all related ReservedTime entries
                    CriteriaDelete<ReservedTime> deleteQuery = cb.createCriteriaDelete(ReservedTime.class);
                    Root<ReservedTime> rtRoot = deleteQuery.from(ReservedTime.class);
                    deleteQuery.where(cb.equal(rtRoot.get("reservation").get("id"), res.getId()));
                    session.createQuery(deleteQuery).executeUpdate();

                    session.getTransaction().commit();
                    return true;
                }

            }

            // No "on" reservation found
            session.getTransaction().rollback();
            return false;

        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

//addeddddd "order"
    /**
     * Given a reservation ID, look up all MealOrder rows whose reservationId matches,
     * sum their linePrice, and return the total.
     */
    public static double getTotalOrderPriceForReservation(long reservationId) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        double total = 0.0;
        try {
            session.beginTransaction();

            @SuppressWarnings("unchecked")
            List<MealOrder> orders = session
                    .createQuery("FROM MealOrder mo WHERE mo.reservationId = :resId")
                    .setParameter("resId", reservationId)
                    .getResultList();

            for (MealOrder mo : orders) {
                total += mo.getTotalPrice();
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return total;
    }

    public static boolean deleteMealOrdersByReservationId(long reservationId) {
        Session session = getSessionFactory(password).openSession();
        try {
            session.beginTransaction();
            int deletedCount = session.createQuery(
                            "DELETE FROM MealOrder mo WHERE mo.reservationId = :rid")
                    .setParameter("rid", reservationId)
                    .executeUpdate();
            session.getTransaction().commit();
            return (deletedCount > 0);
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    //addedddd
// **NEW** Hibernate-based:
public static Reservation getActiveReservationById(String idNumber, int restaurantId) {
    // Use the same SessionFactory you already have
    SessionFactory sessionFactory = getSessionFactory(password);
    Session session = sessionFactory.openSession();
    try{
        // Start a transaction if you want, although here read-only is fine
        session.beginTransaction();

        // Equivalent HQL: select a reservation whose idNumber matches and status = 'on'
        Reservation res;

        if (restaurantId == -1) {
            res = session.createQuery(
                            "FROM Reservation r WHERE r.idNumber = :idNum AND lower(r.status) = 'on'",
                            Reservation.class
                    )
                    .setParameter("idNum", idNumber)
                    .uniqueResult();
        } else {
            res = session.createQuery(
                            "FROM Reservation r WHERE r.idNumber = :idNum AND r.restaurant.id = :resId AND lower(r.status) = 'on'",
                            Reservation.class
                    )
                    .setParameter("idNum", idNumber)
                    .setParameter("resId", restaurantId)
                    .uniqueResult();
        }

        session.getTransaction().commit();
        return res;  // either a Reservation or null
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
    finally {
        if (session != null) {
            session.close();
            System.out.println("Session closed");
        }
    }
}

    //addedddd
    public static void updateReservation(Reservation reservation) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        try{
            Transaction tx = session.beginTransaction();
            session.update(reservation);   // or session.merge(reservation) if detached
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (session != null) {
                session.close();
                System.out.println("Session closed");
                updateDailyReport(reservation);
            }
        }
    }

    static List<DailyReport> getReportsByMonth(String message) {
        List<DailyReport> reports = new ArrayList<>();
        String[] parts = message.split(";");
        String restaurantName = parts[1];
        int month = Integer.parseInt(parts[2]);
        int year = Integer.parseInt(parts[3]);

        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<DailyReport> query = builder.createQuery(DailyReport.class);
            Root<DailyReport> root = query.from(DailyReport.class);

            // Eager fetch reservations list
            root.fetch("reservationsList", JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            // Filter by date range
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.withDayOfMonth(startDate.toLocalDate().lengthOfMonth())
                    .plusDays(1).minusSeconds(1);
            predicates.add(builder.between(root.get("day"), startDate, endDate));

            // Filter by restaurant name if not "All"
            if (!restaurantName.equalsIgnoreCase("All")) {
                Join<DailyReport, Restaurant> restaurantJoin = root.join("restaurant");
                predicates.add(builder.like(
                        builder.lower(restaurantJoin.get("name")),
                        "%" + restaurantName.toLowerCase() + "%"
                ));
            }

            query.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));

            reports = session.createQuery(query).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
                System.out.println("session closed");
            }
        }

        return reports;
    }


    static List<MonthlyReport> getPrevMonthlyReport(int restaurantId, int month, int year) {
        List<MonthlyReport> reports = new ArrayList<>();
        Session session = null;

        try {
            SessionFactory sessionFactory = getSessionFactory(password);
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MonthlyReport> query = builder.createQuery(MonthlyReport.class);
            Root<MonthlyReport> root = query.from(MonthlyReport.class);

            List<Predicate> predicates = new ArrayList<>();

            // Create range for the month
            LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

            predicates.add(builder.greaterThanOrEqualTo(root.get("month"), startOfMonth));
            predicates.add(builder.lessThan(root.get("month"), startOfNextMonth));

            // Filter by restaurant if needed
            if (restaurantId != 0) {
                Join<MonthlyReport, Restaurant> restaurantJoin = root.join("restaurant");
                predicates.add(builder.equal(restaurantJoin.get("id"), restaurantId));
            }

            query.select(root)
                    .where(predicates.toArray(new Predicate[0]))
                    .distinct(true);

            // Get all matching reports
            reports = session.createQuery(query).getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
                System.out.println("session closed");
            }
        }

        return reports;
    }





    //    visa payment
    public static void markPaymentAsPaidInDatabase(String customerId) {
        SessionFactory sessionFactory = getSessionFactory(password);
        session = sessionFactory.openSession();
        session.beginTransaction();

        try {

            TypedQuery<Reservation> query = session.createQuery(
                    "SELECT r FROM Reservation r WHERE r.idNumber = :customerId AND r.isPayed = false",
                    Reservation.class
            );
            query.setParameter("customerId", customerId);

            List<Reservation> results = query.getResultList();

            if (!results.isEmpty()) {
                Reservation reservation = results.get(0);
                reservation.setPayed(true);
                session.merge(reservation);
                System.out.println("Reservation marked as paid for customer ID: " + customerId);
            } else {
                System.out.println("No unpaid reservation found for customer ID: " + customerId);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
                System.out.println("session closed");
            }
        }
    }

    public static List<Restaurant> getAllRestaurants() {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            List<Restaurant> restaurants = session.createQuery("FROM Restaurant", Restaurant.class).getResultList();
            session.getTransaction().commit();

            // Log each restaurant name to verify
            System.out.println("Fetched " + restaurants.size() + " restaurants from database:");
            for (Restaurant restaurant : restaurants) {
                System.out.println("Restaurant: " + restaurant.getName());
            }

            return restaurants;
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }




    public static List<MealOrder> mergeDuplicateOrders(List<MealOrder> orders) {
        Map<String, MealOrder> mergedMap = new HashMap<>();

        for (MealOrder order : orders) {
            String key = order.getReservationId() + "||" + order.getMealName() + "||" + order.getPreferences();

            if (!mergedMap.containsKey(key)) {
                // Add a copy of the order to the map to avoid modifying the original list
                MealOrder copy = new MealOrder(
                        order.getReservationId(),
                        order.getMealName(),
                        order.getPreferences(),
                        order.getQuantity(),
                        order.getTotalPrice()
                );
                mergedMap.put(key, copy);
            } else {
                MealOrder existing = mergedMap.get(key);
                existing.setQuantity(existing.getQuantity() + order.getQuantity());
                existing.setTotalPrice(existing.getTotalPrice() + order.getTotalPrice());
            }
        }

        return new ArrayList<>(mergedMap.values());
    }


    public static void saveMealOrders(List<MealOrder> orders) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();

            for (MealOrder order : orders) {
                session.save(order);
            }

            session.getTransaction().commit();
            System.out.println("Meal orders saved successfully.");
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    public static boolean cancelReservationByIdAndReservationId(String idNumber, long reservationId) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Reservation res = session.createQuery(
                            "FROM Reservation r WHERE r.id = :rid AND r.idNumber = :idnum AND lower(r.status) = 'on'",
                            Reservation.class)
                    .setParameter("rid", reservationId)   // Reservation.id property
                    .setParameter("idnum", idNumber)
                    .uniqueResult();

            if (res == null) {
                session.getTransaction().rollback();
                return false;
            }

            res.setStatus("off");
            res.setCancellationStatus("cancelled");
            session.update(res);

            // Delete ReservedTime rows linked to this reservation
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<ReservedTime> del = cb.createCriteriaDelete(ReservedTime.class);
            Root<ReservedTime> rt = del.from(ReservedTime.class);
            del.where(cb.equal(rt.get("reservation").get("id"), res.getId()));
            session.createQuery(del).executeUpdate();

            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public static boolean cancelOrderByIdAndReservationId(String idNumber, long reservationId) {
        SessionFactory sessionFactory = getSessionFactory(password);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Reservation res = session.createQuery(
                            "FROM Reservation r WHERE r.id = :rid AND r.idNumber = :idnum AND r.isTakeAway = true AND lower(r.status) = 'on'",
                            Reservation.class)
                    .setParameter("rid", reservationId)   // Reservation.id property
                    .setParameter("idnum", idNumber)
                    .uniqueResult();

            if (res == null) {
                session.getTransaction().rollback();
                return false;
            }

            res.setStatus("off");
            res.setCancellationStatus("cancelled");
            session.update(res);

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaDelete<ReservedTime> del = cb.createCriteriaDelete(ReservedTime.class);
            Root<ReservedTime> rt = del.from(ReservedTime.class);
            del.where(cb.equal(rt.get("reservation").get("id"), res.getId()));
            session.createQuery(del).executeUpdate();

            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (session.getTransaction().isActive()) session.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
    public static Reservation getActiveReservationByIdAndReservationId(String idNumber, long reservationId) {
        SessionFactory sf = getSessionFactory(password);
        Session s = sf.openSession();
        try {
            return s.createQuery(
                            "FROM Reservation r " +
                                    "WHERE r.id = :rid " +
                                    "AND r.idNumber = :idnum " +
                                    "AND lower(r.status) = 'on'",
                            Reservation.class)
                    .setParameter("rid", reservationId)
                    .setParameter("idnum", idNumber)
                    .uniqueResult();
        } finally {
            s.close();
        }
    }




}
