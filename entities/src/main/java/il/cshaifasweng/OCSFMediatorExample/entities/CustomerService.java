package il.cshaifasweng.OCSFMediatorExample.entities;

public class CustomerService extends Employee {
    public CustomerService(int id, String fullName, String username, String password, int restaurantId) {
        super(id, fullName, username, password, 2, 0);
    }

    public void handleComplaint() {
        System.out.println("Customer service is handling a complaint at restaurant ID: " + getRestaurantId());
    }
}
