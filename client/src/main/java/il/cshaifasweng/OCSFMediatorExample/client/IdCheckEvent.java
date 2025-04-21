package il.cshaifasweng.OCSFMediatorExample.client;

public class IdCheckEvent {
    private final boolean exists;

    public IdCheckEvent(boolean exists) {
        this.exists = exists;
    }

    public boolean doesExist() {
        return exists;
    }
}
