package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "MonthlyReports")
public class MonthlyReport implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate reportDate;
    private int totalDeliveryOrders;

    @ElementCollection
    private Map<LocalDate, Integer> customersPerDay;

    @ElementCollection
    private Map<String, Integer> complaintsHistogram;

    public MonthlyReport() {}

    public MonthlyReport(LocalDate reportDate, int totalDeliveryOrders, Map<LocalDate, Integer> customersPerDay, Map<String, Integer> complaintsHistogram) {
        this.reportDate = reportDate;
        this.totalDeliveryOrders = totalDeliveryOrders;
        this.customersPerDay = customersPerDay;
        this.complaintsHistogram = complaintsHistogram;
    }

    // Getter for reportDate
    public LocalDate getReportDate() {
        return reportDate;
    }

    // Getter for totalDeliveryOrders
    public int getTotalDeliveryOrders() {
        return totalDeliveryOrders;
    }

    // Getter for customersPerDay (if needed)
    public Map<LocalDate, Integer> getCustomersPerDay() {
        return customersPerDay;
    }

    // Getter for complaintsHistogram (if needed)
    public Map<String, Integer> getComplaintsHistogram() {
        return complaintsHistogram;
    }
}
