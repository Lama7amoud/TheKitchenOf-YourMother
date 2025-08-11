package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class MonthlyReport implements Serializable {
    private String restaurantName;
    private LocalDateTime generatedTime;
    private int totalCustomers;
    private int deliveryOrders;
    private Map<String, Integer> complaintsData; // category -> count

    public MonthlyReport(String restaurantName, LocalDateTime generatedTime,
                         int totalCustomers, int deliveryOrders,
                         Map<String, Integer> complaintsData) {
        this.restaurantName = restaurantName;
        this.generatedTime = generatedTime;
        this.totalCustomers = totalCustomers;
        this.deliveryOrders = deliveryOrders;
        this.complaintsData = complaintsData;
    }

    public String getRestaurantName() { return restaurantName; }
    public LocalDateTime getGeneratedTime() { return generatedTime; }
    public int getTotalCustomers() { return totalCustomers; }
    public int getDeliveryOrders() { return deliveryOrders; }
    public Map<String, Integer> getComplaintsData() { return complaintsData; }
}
