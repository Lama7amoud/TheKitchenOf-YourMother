package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String idNumber;
    private String address;
    private String phoneNumber;
/*    private String timeSlot;
    private LocalDate date;*/
    private int totalGuests;
    private String sittingType;
    private String visa;
    private LocalDateTime reservationTime;
    private boolean isPayed;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany
    private List<HostingTable> reservedTables;

    public Reservation() {}

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
/*
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }*/

    public int getTotalGuests() { return totalGuests; }
    public void setTotalGuests(int totalGuests) { this.totalGuests = totalGuests; }

    public String getSittingType() { return sittingType; }
    public void setSittingType(String sittingType) { this.sittingType = sittingType; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public List<HostingTable> getReservedTables() { return reservedTables; }
    public void setReservedTables(List<HostingTable> reservedTables) { this.reservedTables = reservedTables; }


    public String getVisa() { return visa; }
    public void setVisa(String visa) { this.visa = visa; }

    public LocalDateTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalDateTime reservationTime) { this.reservationTime = reservationTime; }

    public boolean isPayed() { return isPayed; }
    public void setPayed(boolean payed) { isPayed = payed; }
}
