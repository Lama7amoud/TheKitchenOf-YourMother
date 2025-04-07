package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Feedback;
import java.util.List;

public class FeedbackEvent {
    private List<Feedback> feedbackList;

    public FeedbackEvent(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public List<Feedback> getFeedbackList() {
        return feedbackList;
    }
}