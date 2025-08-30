package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User{

    public static String requestBranchDetailsMsg(){
        return "Get branch details;";
    }
}
