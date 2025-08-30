package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "monthly_reports")
public class MonthlyReport implements Serializable, IReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Each monthly report belongs to a specific restaurant
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "month", nullable = false)
    private LocalDateTime month;

    // Aggregated values
    private int totalCustomers;
    private int deliveryOrders;
    private int reservations;
    private int complaintsCount;

    // All daily reports that belong to this monthly report
    @OneToMany(mappedBy = "monthlyReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyReport> dailyReports;

    // ===== Getters and Setters =====
    public int getId() {
        return id;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getMonth() {
        return month;
    }

    public void setMonth(LocalDateTime month) {
        this.month = month;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public int getDeliveryOrders() {
        return deliveryOrders;
    }

    public void setDeliveryOrders(int deliveryOrders) {
        this.deliveryOrders = deliveryOrders;
    }

    public int getReservations() {
        return reservations;
    }

    public void setReservations(int reservations) {
        this.reservations = reservations;
    }

    public int getComplaintsCount() {
        return complaintsCount;
    }

    public void setComplaintsCount(int complaintsCount) {
        this.complaintsCount = complaintsCount;
    }

    public List<DailyReport> getDailyReports() {
        return dailyReports;
    }

    public void setDailyReports(List<DailyReport> dailyReports) {
        this.dailyReports = dailyReports;
    }

    public void updateFromDailyReports() {
        if (dailyReports == null || dailyReports.isEmpty()) {
            this.totalCustomers = 0;
            this.deliveryOrders = 0;
            this.reservations = 0;
            this.complaintsCount = 0;
            return;
        }

        this.totalCustomers = dailyReports.stream().mapToInt(DailyReport::getTotalCustomers).sum();
        this.deliveryOrders = dailyReports.stream().mapToInt(DailyReport::getDeliveryOrders).sum();
        this.reservations = dailyReports.stream().mapToInt(DailyReport::getReservations).sum();
        this.complaintsCount = dailyReports.stream().mapToInt(DailyReport::getComplaintsCount).sum();
    }

    @Override
    public String GetReportAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Monthly Report for Restaurant ID: ").append(restaurant.getId()).append("\n");
        sb.append("Month: ").append(month.getMonth()).append(" ").append(month.getYear()).append("\n");
        sb.append("Total Customers: ").append(totalCustomers).append("\n");
        sb.append("Reservations: ").append(reservations).append("\n");
        sb.append("Delivery Orders: ").append(deliveryOrders).append("\n");
        sb.append("Complaints: ").append(complaintsCount).append("\n");

        // summarize daily reports
        if (dailyReports != null && !dailyReports.isEmpty()) {
            sb.append("Daily Reports:\n");
            for (DailyReport dr : dailyReports) {
                sb.append(" - Date: ").append(dr.getDay().toLocalDate())
                        .append(", Customers: ").append(dr.getTotalCustomers())
                        .append(", Reservations: ").append(dr.getReservations())
                        .append(", Delivery Orders: ").append(dr.getDeliveryOrders())
                        .append(", Complaints: ").append(dr.getComplaintsCount())
                        .append("\n");
            }
        }

        return sb.toString();
    }

}
