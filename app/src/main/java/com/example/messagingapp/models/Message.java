package com.example.messagingapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

public class Message implements Serializable {
    private String pairID;
    private String message;
    private String senderEmail;
    @ServerTimestamp
    private Timestamp date;

    public String getPairID() {
        return pairID;
    }

    public void setPairID(String pairID) {
        this.pairID = pairID;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
