package il.cshaifasweng.OCSFMediatorExample.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderData {

    private static OrderData instance = null;

    private String preferredTime;
    private String sittingType;
    private int guestCount;
    private String generalNote;
    private LocalDate date;
    private Restaurant selectedRestaurant;

    // New fields for customer details
    private String fullName;
    private String phoneNumber;
    private String idNumber;
    private String address;
    private String email;
    private String visa;
    private List<MealOrder> mealOrders = new ArrayList<>();

    private int expirationMonth;
    private int expirationYear;
    private String cvv;


    private OrderData() {}

    public static OrderData getInstance() {
        if (instance == null) {
            instance = new OrderData();
        }
        return instance;
    }

    public void setDetails(String preferredTime, String sittingType, int guestCount, String generalNote) {
        this.preferredTime = preferredTime;
        this.sittingType = sittingType;
        this.guestCount = guestCount;
        this.generalNote = generalNote;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }  // ✅ Getter
    public void setEmail(String email) { this.email = email; }  // ✅ Setter

    public String getVisa() { return visa; }
    public void setVisa(String visa) { this.visa = visa; }

    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }

    public String getSittingType() { return sittingType; }
    public void setSittingType(String sittingType) { this.sittingType = sittingType; }

    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

    public String getGeneralNote() { return generalNote; }
    public void setGeneralNote(String generalNote) { this.generalNote = generalNote; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Restaurant getSelectedRestaurant() { return selectedRestaurant; }
    public void setSelectedRestaurant(Restaurant selectedRestaurant) { this.selectedRestaurant = selectedRestaurant; }

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


    public void addMealOrder(MealOrder order) {
        this.mealOrders.add(order);
    }

    public List<MealOrder> getMealOrders() {
        return mealOrders;
    }

    public void reset() {
        this.preferredTime = null;
        this.sittingType = null;
        this.guestCount = 0;
        this.generalNote = null;
        this.fullName = null;
        this.phoneNumber = null;
        this.idNumber = null;
        this.address = null;
        this.email = null;
        this.visa = null;
        this.expirationMonth = 0;
        this.expirationYear = 0;
        this.cvv = null;

    }

    @Override
    public String toString() {
        return "OrderData{" +
                "fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", preferredTime='" + preferredTime + '\'' +
                ", sittingType='" + sittingType + '\'' +
                ", guestCount=" + guestCount +
                ", generalNote='" + generalNote + '\'' +
                '}';
    }
}
