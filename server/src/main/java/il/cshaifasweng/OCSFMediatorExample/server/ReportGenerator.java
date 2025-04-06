package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.MonthlyReport;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class ReportGenerator {

    private static Timer timer = new Timer();
    private static long intervalMillis = TimeUnit.SECONDS.toMillis(30); // default: 30 days

    public static void setIntervalDays(int secounds) {
        intervalMillis = TimeUnit.SECONDS.toMillis(secounds);
        restartTimer();
    }

    public static void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                generateAndSaveReport();
            }
        }, 0, intervalMillis);
    }

    private static void restartTimer() {
        timer.cancel();
        timer = new Timer();
        start();
    }

    private static void generateAndSaveReport() {
        System.out.println("Generating report...");

        LocalDate now = LocalDate.now();
        LocalDate start = now.minusDays(30);

        // Get password from DataManager
        String password = DataManager.getPassword();

        // Pass the password to the DataManager methods
        int totalDeliveryOrders = DataManager.getDeliveryOrdersCount(password, start, now);
        Map<LocalDate, Integer> customersPerDay = DataManager.getCustomerCountPerDay(password, start, now);
        Map<String, Integer> complaintsHistogram = DataManager.getComplaintHistogram(password, start, now);

        MonthlyReport report = new MonthlyReport(now, totalDeliveryOrders, customersPerDay, complaintsHistogram);
        DataManager.saveMonthlyReport(report);

        System.out.println("Report generated for: " + now);
    }




}
