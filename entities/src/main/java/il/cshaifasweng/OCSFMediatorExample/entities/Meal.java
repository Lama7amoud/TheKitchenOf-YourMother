package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
public class Meal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String mealName;
    private String mealDescription;
    private String mealPreferences;
    private double mealPrice;
    private String  imagePath;
    private String mealCategory;


    @ManyToMany
    @JoinTable(
            name = "restaurant_meals",
            joinColumns = @JoinColumn(name = "meal_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id")
    )
    private List<Restaurant> restaurants = new ArrayList<>();

    public Meal() {
    }

    public Meal(String mealName, String mealDescription, String mealPreferences, double mealPrice , String imagePath , String mealCategory) {
        this.mealName = mealName;
        this.mealDescription = mealDescription;
        this.mealPreferences = mealPreferences;
        this.mealPrice = mealPrice;
        this.imagePath = imagePath;
        this.mealCategory = mealCategory;

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

    public String getImagePath() {return imagePath;}
    public void setImagePath(String imagePath) {this.imagePath = imagePath;}

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void addRestaurant(Restaurant restaurant) {
        this.restaurants.add(restaurant);
    }

    public void setMealCategory(String mealCategory) {
        this.mealCategory = mealCategory;
    }
    public String getMealCategory() {return mealCategory;}

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", mealName='" + mealName + '\'' +
                ", mealDescription='" + mealDescription + '\'' +
                ", mealPreferences='" + mealPreferences + '\'' +
                ", mealPrice=" + mealPrice +
                ", imagePath='" + imagePath + '\'' +
                ", restaurants=" + restaurants +
                '}';
    }
}
