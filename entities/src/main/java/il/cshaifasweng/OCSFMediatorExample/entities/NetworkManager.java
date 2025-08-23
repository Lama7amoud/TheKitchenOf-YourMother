package il.cshaifasweng.OCSFMediatorExample.entities;

public class NetworkManager extends Manager {
    public NetworkManager(int id, String fullName, String username, String password) {
        super(id, fullName, username, password, 4, 0); // null = all restaurants
    }

    @Override
    public void generateReport() {
        System.out.println("Network Manager generates global reports for ALL restaurants.");
    }
    @Override
    public  void generateFeedback(){
        System.out.println("Network Manager generates global feedbacks for ALL restaurants.");
    }

    public void approvePriceUpdate() {
        System.out.println("Network Manager approved a price update.");
    }

    public void approveDiscount() {
        System.out.println("Network Manager approved a discount.");
    }
}
