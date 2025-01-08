package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "menu")

public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String mealName;
    private String mealDescription;
    private String mealPreferences;
    private double mealPrice;

    public Meal() {
    }

    public Meal(String mealName, String mealDescription, String mealPreferences, double mealPrice) {
        this.mealName = mealName;
        this.mealDescription = mealDescription;
        this.mealPreferences = mealPreferences;
        this.mealPrice = mealPrice;
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

    public String getMealDescription() {
        return mealDescription;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public String getMealPreferences() {
        return mealPreferences;
    }

    public void setMealPreferences(String mealPreferences) {
        this.mealPreferences = mealPreferences;
    }

    public double getMealPrice() {
        return mealPrice;
    }

    public void setMealPrice(double mealPrice) {
        this.mealPrice = mealPrice;
    }

}
