package il.cshaifasweng.OCSFMediatorExample.entities;

public abstract class Manager extends Employee {
    public Manager(int id, String fullName, String username, String password, Integer role, Integer restaurantId) {
        super(id, fullName, username, password, role, restaurantId);
    }

    public abstract void generateReport();
    public abstract void generateFeedback();
}