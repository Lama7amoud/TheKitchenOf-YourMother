package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "authorized_users")
public class AuthorizedUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String IDNum;
    private short age;
    private boolean isConnected;
    private short permissionLevel;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;  // The restaurant this user is associated with

    @Transient
    private String messageToServer;

    @Transient
    private short restaurantInterest;

    // Default constructor (required by Hibernate!!)
    public AuthorizedUser() {}

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getIDNum() {
        return IDNum;
    }

    public void setIDNum(String IDNum) {
        this.IDNum = IDNum;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public short getRestaurantInterest() {
        return restaurantInterest;
    }

    public void setRestaurantInterest(short restaurantInterest) {
        this.restaurantInterest = restaurantInterest;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getMessageToServer() {
        return messageToServer;
    }

    public void setMessageToServer(String messageToServer) {
        this.messageToServer = messageToServer;
    }

    public short getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(short permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public short getRestaurantId() {
        return (restaurant != null) ? (short) restaurant.getId() : 0;
    }

    // Copy user attributes from another user
    public void copyUser(AuthorizedUser user) {
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setFirstname(user.getFirstname());
        this.setLastname(user.getLastname());
        this.setIDNum(user.getIDNum());
        this.setAge(user.getAge());
        this.setRestaurant(user.getRestaurant());
        this.setRestaurantInterest(user.getRestaurantInterest());
        this.setConnected(user.isConnected());
        this.setMessageToServer(user.getMessageToServer());
        this.setPermissionLevel(user.getPermissionLevel());
    }

    public void resetAttributes() {
        this.setId(0);
        this.setUsername(null);
        this.setPassword(null);
        this.setFirstname(null);
        this.setLastname(null);
        this.setIDNum(null);
        this.setAge((short) 0);
        this.setRestaurant(null);
        this.setRestaurantInterest((short) 0);
        this.setConnected(false);
        this.setMessageToServer(null);
        this.setPermissionLevel((short) 0);
    }
}
