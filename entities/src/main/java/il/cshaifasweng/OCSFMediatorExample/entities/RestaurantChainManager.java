package il.cshaifasweng.OCSFMediatorExample.entities;

public abstract class RestaurantChainManager extends BranchManager {
    @Override
    public void generateReport() {
        System.out.println("Branch Manager generates reports for restaurant ID: " + getRestaurantId());
    }

    @Override
    public  void generateFeedback(){
        System.out.println("Branch Manager generates feedbacks for restaurants ID: " + getRestaurantId());
    }


}