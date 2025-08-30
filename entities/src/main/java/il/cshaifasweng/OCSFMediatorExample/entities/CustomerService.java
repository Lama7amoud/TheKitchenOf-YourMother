package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("CUSTOMER_SERVICE")
public class CustomerService extends AuthorizedUser {

}
