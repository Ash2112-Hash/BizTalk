package com.example.messagingapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.messagingapp.R;
import com.example.messagingapp.models.User;
import com.example.messagingapp.singleton.MainUser;

import javax.crypto.SecretKey;

import security.manager.CryptoMethods;
import security.manager.KDC;

public class HomePageActivity extends AppCompatActivity {

    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = MainUser.getInstance().getUserData();
        this.updateTheme();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        this.updateButtons();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        String userId = MainUser.getUserData().getWorkNumber();
        SecretKey key = CryptoMethods.generateKey();
        SecretKey key2 = CryptoMethods.generateKey();
        String str_key = CryptoMethods.SKeyToString(key);
        String str_key2 = CryptoMethods.SKeyToString(key2);
        KDC kdc = new KDC();
        kdc.updateUserPrivateKey(userId, str_key);
        kdc.updateMessagingSession(userId, str_key2);

        this.updateHeader();
        this.updateFontSize();
    }

    private void updateTheme() {
        switch (user.getProfilePreferences().getSystemTheme()){
            case "Blue": setTheme(R.style.BlueTheme); break;
            case "Red": setTheme(R.style.RedTheme); break;
            case "Green": setTheme(R.style.GreenTheme); break;
            default: setTheme(R.style.BlueTheme);
        }
    }

    private void updateButtons() {
        int[] homePageButtons = {R.id.navHomeBtn, R.id.navChatBtn, R.id.navAccountBtn, R.id.navPaymentBtn};
        int[] buttonBgColors = {R.color.navBlueTint, R.color.navRedTint, R.color.navGreenTint};
        int i;
        switch (user.getProfilePreferences().getSystemTheme()){
            case "Red": i=1; break;
            case "Green": i=2; break;
            default: i=0; break;
        }
        Log.d("DEBUG_HP", String.valueOf(i));
        for (int h : homePageButtons) {
            findViewById(h).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(buttonBgColors[i])));
        }
    }

    private void updateHeader() {
        TextView v = findViewById(R.id.homePageHeader);
        v.setText("Hello, "+user.getPreferredName().getFirst()+" "+user.getPreferredName().getLast());
    }

    public void navHomePressed(View v) {
        // ...
    }

    public void navChatPressed(View v) {
        Intent i = new Intent(this, ChooseRecipientActivity.class);
        startActivity(i);
    }

    public void navPaymentPressed(View v) {
        Intent i = new Intent(this, PaymentActivity.class);
        startActivity(i);
    }

    public void navAccountPressed(View v) {
        Intent i = new Intent(this, AccountActivity.class);
        startActivity(i);
    }
    private void updateFontSize(){
        float fontSizeHeader = 18;
        float fontSizeSmall = 15;
        switch(user.getProfilePreferences().getFontSize()) {
            case "Small":
                fontSizeSmall = 12;
                fontSizeHeader = 15;
                break;
            case "Medium":
                fontSizeSmall = 15;
                fontSizeHeader = 18;
                break;
            case "Large":
                fontSizeSmall = 18;
                fontSizeHeader = 21;
                break;
        }
        TextView header = findViewById(R.id.homePageHeader);
        TextView smaller = findViewById(R.id.homePageSecondaryMsg);


        header.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeHeader);
        smaller.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeSmall);

    }
}