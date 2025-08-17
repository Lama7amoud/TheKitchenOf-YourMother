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

    private String userId;
    private String name;
    private boolean autoresponse;
    private String email;

    private String response;
    private double Refund;

    private String complaint;

    private boolean status; // responded or not

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public Complaint() {}

    public Complaint(String complaint, LocalDateTime submittedAt, Restaurant restaurant,String userId,String name, String email) {
        this.complaint = complaint;
        this.status=false;
        this.submittedAt = submittedAt;
        this.restaurant = restaurant;
        this.userId=userId;
        this.name = name;
        this.email = email;
        this.autoresponse = false;
        this.response = "";
        this.Refund = 0;
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

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }
    public double getRefund() {
        return Refund;
    }
    public void setRefund(double Refund) {
        this.Refund = Refund;
    }
    public boolean getAutoresponse() { return autoresponse; }
    public void setAutoresponse(boolean autoresponse) {
        this.autoresponse = autoresponse;
    }


}