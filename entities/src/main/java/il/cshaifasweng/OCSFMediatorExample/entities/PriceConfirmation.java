package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PriceConfirmation")
public class PriceConfirmation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String mealName;
    private double oldPrice;
    private double newPrice;




    public PriceConfirmation() {
    }

    public PriceConfirmation(String mealName,  double oldPrice ,double newPrice) {
        this.mealName = mealName;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }



    public int getId() {
        return id;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public double getOldPrice() {
        return oldPrice;
    }
    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }
    public double getNewPrice() {
        return newPrice;
    }
    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", mealName='" + mealName + '\'' +
                ", mealDescription='" + oldPrice + '\'' +
                ", mealPreferences='" + newPrice +
                '}';
    }
}
