package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import java.util.List;

public class ComplaintEvent {
    private List<Complaint> complaintList;

    public ComplaintEvent(List<Complaint> complaintList) {
        this.complaintList = complaintList;
    }

    public List<Complaint> getComplaintList() {
        return complaintList;
    }
}

