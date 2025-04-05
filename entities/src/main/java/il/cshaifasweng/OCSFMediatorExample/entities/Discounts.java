package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Discounts")
public class Discounts implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double discount;

    private String category;





    public Discounts() {
    }

    public Discounts(double discount,String category) {
        this.discount = discount;
        this.category = category;
    }



    public int getId() {
        return id;
    }

   public double getDiscount() {
        return discount;
   }
   public void setId(int id) {
        this.id = id;
   }
   public void setDiscount(double discount) {
        this.discount = discount;
   }
   public String getCategory() {
        return category;
   }
   public void setCategory(String category) {
        this.category = category;
    }
    @Override
    public String toString() {
        return "Discounts{" +
                "id=" + id +
                ", discount='" + discount + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
