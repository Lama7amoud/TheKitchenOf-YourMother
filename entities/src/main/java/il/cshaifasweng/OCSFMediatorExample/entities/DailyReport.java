package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "daily_reports")
public class DailyReport implements Serializable, IReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private LocalDateTime day;

    private int totalCustomers;

    private int deliveryOrders;

    private int reservations;

    private int complaintsCount;

    // Bidirectional mapping: all reservations for this report (no filtering here)
    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservationsList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_report_id")
    private MonthlyReport monthlyReport;

    public MonthlyReport getMonthlyReport() {return monthlyReport;}

    public void setMonthlyReport(MonthlyReport monthlyReport) {this.monthlyReport = monthlyReport;}

    public int getId() {
        return id;
    }

    // Getters and setters
    public List<Reservation> getReservationsList() {
        return reservationsList;
    }

    public void setReservationsList(List<Reservation> reservationsList) {
        this.reservationsList = reservationsList;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public LocalDateTime getDay() {
        return day;
    }

    public void setDay(LocalDateTime day) {
        this.day = day;
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

    @Override
    public String GetReportAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Daily Report for Restaurant ID: ").append(restaurant.getId()).append("\n");
        sb.append("Date: ").append(day.toLocalDate()).append("\n");
        sb.append("Total Customers: ").append(totalCustomers).append("\n");
        sb.append("Reservations: ").append(reservations).append("\n");
        sb.append("Delivery Orders: ").append(deliveryOrders).append("\n");
        sb.append("Complaints: ").append(complaintsCount).append("\n");

        // list reservations
        if (reservationsList != null && !reservationsList.isEmpty()) {
            sb.append("Reservations Details:\n");
            for (Reservation r : reservationsList) {
                sb.append(" - Reservation ID: ").append(r.getId())
                        .append(", Guests: ").append(r.getTotalGuests())
                        .append(", TakeAway: ").append(r.isTakeAway())
                        .append("\n");
            }
        }

        return sb.toString();
    }



}
