package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MonthlyReport;
import java.util.List;

public class MonthlyReportsEvent {
    private List<MonthlyReport> reports;

    public MonthlyReportsEvent(List<MonthlyReport> reports) {
        this.reports = reports;
    }

    public List<MonthlyReport> getReports() {
        return reports;
    }
}
