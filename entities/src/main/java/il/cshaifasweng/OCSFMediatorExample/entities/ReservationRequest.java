package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class ReservationRequest implements Serializable {
    private Reservation reservation;
    private boolean shouldSave;

    public ReservationRequest(Reservation reservation, boolean shouldSave) {
        this.reservation = reservation;
        this.shouldSave = shouldSave;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public boolean isShouldSave() {
        return shouldSave;
    }

    public void setShouldSave(boolean shouldSave) {
        this.shouldSave = shouldSave;
    }
}
