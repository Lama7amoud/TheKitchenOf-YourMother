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
    private String phoneNumber;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<HostingTable> hostingTables;  // Reference to the HostingTable entity

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<HostingTable> getHostingTables() {
        return hostingTables;
    }

    public void setHostingTables(List<HostingTable> hostingTables) {
        this.hostingTables = hostingTables;
    }

    // Copy restaurant attributes from another restaurant
    public void copyRestaurant(Restaurant restaurant) {
        this.setId(restaurant.getId());
        this.setName(restaurant.getName());
        this.setAddress(restaurant.getAddress());
        this.setPhoneNumber(restaurant.getPhoneNumber());
        this.setHostingTables(restaurant.getHostingTables());
    }

    // Reset all attributes
    public void resetAttributes() {
        this.setId(0);
        this.setName(null);
        this.setAddress(null);
        this.setPhoneNumber(null);
        this.setHostingTables(null);
    }
}
