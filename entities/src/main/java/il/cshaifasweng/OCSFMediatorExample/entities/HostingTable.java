package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
    private boolean isInside;

    @ElementCollection
    @CollectionTable(name = "reserved_times", joinColumns = @JoinColumn(name = "table_id"))
    @Column(name = "reserved_time")
    private List<LocalDateTime> reservedTimes;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant; // The restaurant this hosting table belongs to



    public String getSittingType() {
        return isInside ? "Inside" : "Outside";
    }

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

    public boolean isInside() {
        return isInside;
    }

    public void setInside(boolean isInside) {
        this.isInside = isInside;
    }

    public void setSeatsNumber(int seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<LocalDateTime> getReservedTimes() {
        return reservedTimes;
    }

    public void setReservedTimes(List<LocalDateTime> reservedTimes) {
        this.reservedTimes = reservedTimes;
    }

    // Copy hosting table attributes from another hosting table
    public void copyTable(HostingTable table) {
        this.setId(table.getId());
        this.setTableNumber(table.getTableNumber());
        this.setSeatsNumber(table.getSeatsNumber());
        this.setRestaurant(table.getRestaurant());
    }

    // Reset all attributes
    public void resetAttributes() {
        this.setTableNumber(0);
        this.setSeatsNumber(0);
        this.setRestaurant(null);
        this.setReservedTimes(null);
    }

}
