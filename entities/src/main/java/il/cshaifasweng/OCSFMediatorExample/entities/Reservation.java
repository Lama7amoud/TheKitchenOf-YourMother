package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Reservation implements Serializable {

    private static Reservation instance = null;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // reservation ID

    private String name;
    private String idNumber;
    private String address;
    private String phoneNumber;
    private String timeSlot;
    private int totalGuests;
    private String sittingType;
    private String visa; // 16 digit number
    private LocalDateTime orderingTime;
    private boolean isPayed;
    private int restaurantId;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany
    private List<HostingTable> reservedTables;

    private LocalDate date;

    // Default constructor for Hibernate only
    public Reservation() {}

    // Singleton accessor
    public static Reservation getReservation() {
        if (instance == null) {
            instance = new Reservation();
        }
        return instance;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public int getTotalGuests() { return totalGuests; }
    public void setTotalGuests(int totalGuests) { this.totalGuests = totalGuests; }

    public String getSittingType() { return sittingType; }
    public void setSittingType(String sittingType) { this.sittingType = sittingType; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public List<HostingTable> getReservedTables() { return reservedTables; }
    public void setReservedTables(List<HostingTable> reservedTables) { this.reservedTables = reservedTables; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getVisa() { return visa; }
    public void setVisa(String visa) { this.visa = visa; }

    public LocalDateTime getOrderingTime() { return orderingTime; }
    public void setOrderingTime(LocalDateTime orderingTime) { this.orderingTime = orderingTime; }

    public boolean isPayed() { return isPayed; }
    public void setPayed(boolean payed) { isPayed = payed; }
}
