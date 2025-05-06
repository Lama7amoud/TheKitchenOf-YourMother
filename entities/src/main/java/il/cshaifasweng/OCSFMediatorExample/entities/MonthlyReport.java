package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MonthlyReport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime generationTime;

    private int deliveryOrdersCount;

    private int customersServed;

    private int takeawayOrdersCount;

    private int restaurantCustomersCount;

    private int complaintCount;


    @ElementCollection
    private List<String> complaintSubjects = new ArrayList<>();

    @ManyToOne
    private Restaurant restaurant;

    public MonthlyReport() {}

    public MonthlyReport(LocalDateTime timestamp, int takeawayOrders, int customersServed,
                         int complaintCount, Restaurant restaurant) {
        this.generationTime = timestamp;
        this.takeawayOrdersCount = takeawayOrders;
        this.customersServed = customersServed;
        this.complaintCount = complaintCount;
        this.restaurant = restaurant;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getTakeawayOrdersCount() {
        return takeawayOrdersCount;
    }

    public void setTakeawayOrdersCount(int takeawayOrdersCount) {
        this.takeawayOrdersCount = takeawayOrdersCount;
    }

    public int getComplaintCount() {
        return complaintCount;
    }

    public void setComplaintCount(int complaintCount) {
        this.complaintCount = complaintCount;
    }

    public LocalDateTime getGenerationTime() { return generationTime; }
    public void setGenerationTime(LocalDateTime generationTime) { this.generationTime = generationTime; }

    public int getDeliveryOrdersCount() { return deliveryOrdersCount; }
    public void setDeliveryOrdersCount(int deliveryOrdersCount) { this.deliveryOrdersCount = deliveryOrdersCount; }

    public int getRestaurantCustomersCount() { return restaurantCustomersCount; }
    public void setRestaurantCustomersCount(int restaurantCustomersCount) { this.restaurantCustomersCount = restaurantCustomersCount; }

    public List<String> getComplaintSubjects() { return complaintSubjects; }
    public void setComplaintSubjects(List<String> complaintSubjects) { this.complaintSubjects = complaintSubjects; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public Object getTimestamp() {
        return generationTime;
    }

    public int getCustomersServed() {
        return customersServed;
    }


    public int getTakeawayOrders() {
        return takeawayOrdersCount;
    }
}

