package com.example.messagingapp.activities;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentAPI {

    public static JSONObject sendPayment(String recipientName, double amount) {
        JSONObject jsonResponse = new JSONObject();
        // simulate sending payment details to API
        // this is a verrrrrry barebones situation
        if (amount > 0) {
            // payment transaction successful
            try {
                jsonResponse.put("status", 1);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            // payment transaction failed
            try {
                jsonResponse.put("status", 0);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonResponse;
    }
}
