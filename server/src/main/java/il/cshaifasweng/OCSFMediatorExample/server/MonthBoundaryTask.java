package il.cshaifasweng.OCSFMediatorExample.server;


import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import il.cshaifasweng.OCSFMediatorExample.entities.*;

import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

class MonthBoundaryTask implements Runnable {
    private final SessionFactory sessionFactory;
    private final ScheduledExecutorService scheduler;
    private final ZoneId tz = ZoneId.of("Asia/Jerusalem");

    MonthBoundaryTask(SessionFactory sf, ScheduledExecutorService sched) {
        this.sessionFactory = sf;
        this.scheduler = sched;
    }

    @Override
    public void run() {
        try (Session s = sessionFactory.openSession()) {
            s.beginTransaction();

            YearMonth prev = YearMonth.now(tz).minusMonths(1);
            LocalDateTime start = prev.atDay(1).atStartOfDay();
            LocalDateTime end   = prev.plusMonths(1).atDay(1).atStartOfDay();

            List<Restaurant> restaurants = s.createQuery("FROM Restaurant", Restaurant.class).list();
            for (Restaurant r : restaurants) {
                MonthlyReport existing = s.createQuery(
                                "FROM MonthlyReport mr WHERE mr.restaurant = :r AND mr.month = :m",
                                MonthlyReport.class)
                        .setParameter("r", r)
                        .setParameter("m", start)
                        .uniqueResult();

                if (existing == null) {
                    List<DailyReport> drs = s.createQuery(
                                    "FROM DailyReport dr WHERE dr.restaurant = :r " +
                                            "AND dr.day >= :start AND dr.day < :end",
                                    DailyReport.class)
                            .setParameter("r", r)
                            .setParameter("start", start)
                            .setParameter("end", end)
                            .list();

                    if (!drs.isEmpty()) {
                        MonthlyReport mr = new MonthlyReport();
                        mr.setRestaurant(r);
                        mr.setMonth(start);
                        mr.setDailyReports(drs);
                        for (DailyReport dr : drs) { dr.setMonthlyReport(mr); s.merge(dr); }
                        mr.updateFromDailyReports();
                        s.save(mr);
                    }
                }
            }
            s.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // schedule again for next month boundary
        ZonedDateTime now = ZonedDateTime.now(tz);
        ZonedDateTime next = now.plusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        long delay = Duration.between(now, next).toMillis();
        scheduler.schedule(new MonthBoundaryTask(sessionFactory, scheduler), delay, TimeUnit.MILLISECONDS);
    }
}
