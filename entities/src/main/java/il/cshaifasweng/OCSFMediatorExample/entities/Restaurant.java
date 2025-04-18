package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;
    private String location;
    private String phoneNumber;
    private String activityHours;
    private static Restaurant restaurant;

    public static Restaurant getRestaurant() {
        if (restaurant == null) {
            restaurant = new Restaurant();  // or fetch from DB
        }
        return restaurant;
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<HostingTable> hostingTables;  // Reference to the HostingTable entity

    @ManyToMany(mappedBy = "restaurants")
    private List<Meal> meals;  // Many-to-Many relationship with Meal

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    // Default constructor (required by Hibernate)
    public Restaurant() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getActivityHours() {
        return activityHours;
    }

    public void setActivityHours(String activityHours) {
        this.activityHours = activityHours;
    }

    public List<HostingTable> getHostingTables() {
        return hostingTables;
    }

    public void setHostingTables(List<HostingTable> hostingTables) {
        this.hostingTables = hostingTables;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public void addMeal(Meal meal) {
        this.meals.add(meal);
    }

    // Copy restaurant attributes from another restaurant
    public void copyRestaurant(Restaurant restaurant) {
        this.setId(restaurant.getId());
        this.setName(restaurant.getName());
        this.setAddress(restaurant.getAddress());
        this.setLocation(restaurant.getLocation());
        this.setPhoneNumber(restaurant.getPhoneNumber());
        this.setActivityHours(restaurant.getActivityHours());
        this.setHostingTables(restaurant.getHostingTables());
        this.setMeals(restaurant.getMeals());
        this.setBusiness(restaurant.getBusiness());
    }

    // Reset all attributes
    public void resetAttributes() {
        this.setId(0);
        this.setName(null);
        this.setAddress(null);
        this.setLocation(null);
        this.setPhoneNumber(null);
        this.setActivityHours(null);
        this.setHostingTables(null);
        this.setMeals(null);
        this.setBusiness(null);
    }
}