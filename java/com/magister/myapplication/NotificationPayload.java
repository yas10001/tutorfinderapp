package com.magister.myapplication;

public class NotificationPayload {
    private String title;
    private String message;
    private String startDate;

    public NotificationPayload() {
        // Empty constructor required for Firebase
    }

    public NotificationPayload(String title, String message, String startDate) {
        this.title = title;
        this.message = message;
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
