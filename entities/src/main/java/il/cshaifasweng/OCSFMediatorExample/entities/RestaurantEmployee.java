package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("RESTAURANT_EMPLOYEE")
public abstract class RestaurantEmployee extends AuthorizedUser{

}
