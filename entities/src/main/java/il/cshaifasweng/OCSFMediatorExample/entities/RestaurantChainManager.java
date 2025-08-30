package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("CHAIN_MANAGER")
public class RestaurantChainManager extends BranchManager {

    public static String reportRestaurantMsg(String restaurantName){
        switch(restaurantName){
            case("Haifa Branch"): return "Haifa-Mom Kitchen";
            case("Tel-Aviv Branch"): return "Tel-Aviv-Mom Kitchen";
            case ("Nahariya Branch"): return "Nahariya-Mom Kitchen";
            case ("All") : return "All";
            default: return "";
        }
    }

}