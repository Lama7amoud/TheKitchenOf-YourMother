package il.cshaifasweng.OCSFMediatorExample.entities;

public class Hostess extends Employee {
    public Hostess(int id, String fullName, String username, String password, int restaurantId) {
        super(id, fullName, username, password, 1, restaurantId);
    }

    public void manageReservation() {
        System.out.println("Hostess is managing a reservation at restaurant ID: " + getRestaurantId());
    }

    public void allocateTable() {
        System.out.println("Hostess is allocating a table at restaurant ID: " + getRestaurantId());
    }
}
