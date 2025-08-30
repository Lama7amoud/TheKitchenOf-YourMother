package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DIETITIAN")
public class Dietitian extends AuthorizedUser {

}