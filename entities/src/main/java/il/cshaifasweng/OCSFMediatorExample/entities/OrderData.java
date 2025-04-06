package il.cshaifasweng.OCSFMediatorExample.entities;


import java.time.LocalDate;

public class OrderData {

    private static OrderData instance = null;

    private String preferredTime;
    private String sittingType;
    private int guestCount;
    private String generalNote;
    private LocalDate date;


    // Additional future fields (optional)
    // private List<HostingTable> selectedTables;
    // private boolean paymentConfirmed;

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

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getSittingType() {
        return sittingType;
    }

    public void setSittingType(String sittingType) {
        this.sittingType = sittingType;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getGeneralNote() {
        return generalNote;
    }

    public void setGeneralNote(String generalNote) {
        this.generalNote = generalNote;
    }

    public void reset() {
        this.preferredTime = null;
        this.sittingType = null;
        this.guestCount = 0;
        this.generalNote = null;
        // If more fields added in future, reset them here too
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "OrderData{" +
                "preferredTime='" + preferredTime + '\'' +
                ", sittingType='" + sittingType + '\'' +
                ", guestCount=" + guestCount +
                ", generalNote='" + generalNote + '\'' +
                '}';
    }
}
