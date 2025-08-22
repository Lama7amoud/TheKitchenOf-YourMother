package il.cshaifasweng.OCSFMediatorExample.entities;

public class Dietitian extends Employee {
    public Dietitian(int id, String fullName, String username, String password, int restaurantId) {
        super(id, fullName, username, password, 5, 0);
    }

    public void updateMeal() {
        System.out.println("Dietitian updated a meal at restaurant ID: " + getRestaurantId());
    }

    public void suggestPriceChange() {
        System.out.println("Dietitian suggested a price change at restaurant ID: " + getRestaurantId());
    }

    public void suggestDiscountChange() {
        System.out.println("Dietitian suggested a Discount change at restaurant ID: " + getRestaurantId());
    }
}