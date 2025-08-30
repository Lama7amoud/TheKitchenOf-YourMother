package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@DiscriminatorValue("BRANCH_MANAGER")
public class BranchManager extends RestaurantEmployee {

    public static String requestReportMsg(){
        return "request_reports_daily_as_monthly;";
    }
    public static String requestFeedbackMsg(){
        return "Get Manager feedback";
    }

    public static String requestPrevReportsMsg(){
        return "request_prev_only_monthly_reports;";
    }

}
