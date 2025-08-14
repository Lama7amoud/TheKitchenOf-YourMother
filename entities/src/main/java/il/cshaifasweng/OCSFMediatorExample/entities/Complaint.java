package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "complaints")
public class Complaint implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String complaint;

    private boolean status; // responded or not

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public Complaint() {}

    public Complaint(String complaint, Boolean status, LocalDateTime submittedAt, Restaurant restaurant) {
        this.complaint = complaint;
        this.status = status;
        this.submittedAt = submittedAt;
        this.restaurant = restaurant;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }


    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}