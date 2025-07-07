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
    private int totalGuests;
    private String sittingType;
    private String visa;
    private LocalDateTime reservationTime;
    private boolean isPayed;
    private LocalDateTime receivingTime;
    private String status = "on";
    private boolean isTakeAway;
    private int expirationMonth;
    private int expirationYear;
    private String cvv;
    private String email;
    private double amountDue = 0.0;   //addedddd

    @Transient  // If using JPA
    private String senderId;



    @ManyToOne
    private Restaurant restaurant;

    @ManyToMany
    private List<HostingTable> reservedTables;

    public Reservation() {}

    public Reservation(String name, String phoneNumber, String idNumber, String address, LocalDateTime reservationTime) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.idNumber = idNumber;
        this.address = address;
        this.reservationTime = reservationTime;
        this.status = "on"; // default status
        this.isTakeAway = true; // since it's from the TakeAway page
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }
    public String getStatus() {
        return status;
    }

    public boolean isTakeAway() { return isTakeAway; }
    public void setTakeAway(boolean takeAway) { isTakeAway = takeAway; }

    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDateTime getReceivingTime() { return receivingTime; }
    public void setReceivingTime(LocalDateTime receivingTime) { this.receivingTime = receivingTime; }


    public double getAmountDue() {   //addedddd
        return amountDue;
    }

    public void setAmountDue(double amountDue) {//addeddddd
        this.amountDue = amountDue;
    }
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


    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getReservationTime() { return reservationTime; }
    public void setReservationTime(LocalDateTime reservationTime) { this.reservationTime = reservationTime; }

    public boolean isPayed() { return isPayed; }
    public void setPayed(boolean payed) { isPayed = payed; }
}
