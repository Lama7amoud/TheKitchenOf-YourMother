package il.cshaifasweng.OCSFMediatorExample.entities;

public class BranchManager extends Manager {
    public BranchManager(int id, String fullName, String username, String password, int restaurantId) {
        super(id, fullName, username, password, 3, restaurantId);
    }

    @Override
    public void generateReport() {
        System.out.println("Branch Manager generates reports for restaurant ID: " + getRestaurantId());
    }

    @Override
    public  void generateFeedback(){
        System.out.println("Branch Manager generates feedbacks for restaurants ID: " + getRestaurantId());
    }

}
