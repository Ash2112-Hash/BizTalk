package com.example.messagingapp.models;

import java.io.Serializable;

public class User implements Serializable {
    private String email; // treat email as primary key for User
    private EmployeeName employeeName;
    private String messagingKey;
    private String password;
    private String paymentSelection; // probably replace with enum {"Paypal", "provider2", ...}
    private EmployeeName preferredName;
    private ProfilePreferences profilePreferences;
    private String secretKey;
    private String workNumber;

    private String sessionKey;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeName getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(EmployeeName employeeName) {
        this.employeeName = employeeName;
    }

    public String getMessagingKey() {
        return messagingKey;
    }

    public void setMessagingKey(String messagingKey) {
        this.messagingKey = messagingKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPaymentSelection() {
        return paymentSelection;
    }

    public void setPaymentSelection(String paymentSelection) {
        this.paymentSelection = paymentSelection;
    }

    public EmployeeName getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(EmployeeName preferredName) {
        this.preferredName = preferredName;
    }

    public ProfilePreferences getProfilePreferences() {
        return profilePreferences;
    }

    public void setProfilePreferences(ProfilePreferences profilePreferences) {
        this.profilePreferences = profilePreferences;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }
}
