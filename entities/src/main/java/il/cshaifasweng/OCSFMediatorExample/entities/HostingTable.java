package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "hosting_tables")  // Renamed table name for the class HostingTable
public class HostingTable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int tableNumber;
    private int seatsNumber;
    private boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant; // The restaurant this hosting table belongs to

    // Default constructor (required by Hibernate)
    public HostingTable() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getSeatsNumber() {
        return seatsNumber;
    }

    public void setSeatsNumber(int seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    // Copy hosting table attributes from another hosting table
    public void copyTable(HostingTable table) {
        this.setId(table.getId());
        this.setTableNumber(table.getTableNumber());
        this.setSeatsNumber(table.getSeatsNumber());
        this.setReserved(table.isReserved());
        this.setRestaurant(table.getRestaurant());
    }

    // Reset all attributes
    public void resetAttributes() {
        this.setId(0);
        this.setTableNumber(0);
        this.setSeatsNumber(0);
        this.setReserved(false);
        this.setRestaurant(null);
    }
}
