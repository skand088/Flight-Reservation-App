package com.flightreservation.model.entities;


import java.time.LocalDateTime;

// subject for newsletter observer pattern used by customers
public class Newsletter {
    private int newsletterId;
    private String subject;
    private String message;
    private LocalDateTime sentDate;

    public Newsletter() {
    }

    public Newsletter(String subject, String message) {
        this.subject = subject;
        this.message = message;
        this.sentDate = LocalDateTime.now();
    }

    public int getNewsletterId() {
        return newsletterId;
    }

    public void setNewsletterId(int newsletterId) {
        this.newsletterId = newsletterId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }
}
