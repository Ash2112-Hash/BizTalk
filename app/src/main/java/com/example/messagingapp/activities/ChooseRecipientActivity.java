package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messagingapp.R;
import com.example.messagingapp.models.User;
import com.example.messagingapp.singleton.MainUser;
import com.example.messagingapp.utils.Firebase_CollectionFields;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ChooseRecipientActivity extends AppCompatActivity {

    ArrayList<User> recipientsList = new ArrayList<>();
    LinearLayout layoutList;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = MainUser.getInstance().getUserData();

        EdgeToEdge.enable(this);
        updateTheme();
        setContentView(R.layout.activity_choose_recipient);

        layoutList = findViewById(R.id.recipientsLayout);

        this.getRecipientsFromDB();

    }

    private void getRecipientsFromDB() {
        String userEmail = user.getEmail();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Firebase_CollectionFields.ATTR_COLLECTION_USER_PROFILE)
                // exclude the sender from recipient query
                .whereNotEqualTo(Firebase_CollectionFields.ATTR_EMAIL, userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {

                        List<DocumentSnapshot> recipientsQueryData = task.getResult().getDocuments();
                        for (DocumentSnapshot docSnap : recipientsQueryData) {
                            User recipient = docSnap.toObject(User.class);
                            recipientsList.add(recipient);
                        }
                        // now that recipients are found from query, update view
                        this.setRecipientButtons();
                    }
                });
    }

    private void setRecipientButtons() {
        Log.d("DEBUG_CHOOSE_RECIP",recipientsList.toString());
        for (User recipient: recipientsList) {
            // using preferred names here for display
            String recipFirst = recipient.getPreferredName().getFirst();
            String recipLast = recipient.getPreferredName().getLast();
            Log.d("DEBUG_CHOOSE_RECIP",recipFirst);
            Log.d("DEBUG_CHOOSE_RECIP",recipLast);
            View v = getLayoutInflater().inflate(R.layout.recipient,null,false);
            Button b = v.findViewById(R.id.recipientButton);
            this.updateFontSize(b);
            b.setText(recipFirst+" "+recipLast);
            layoutList.addView(v);
        }
    }

    public void launchMessaging(View v) {
        Intent i = new Intent(this, MessagingActivity.class);
        Button b = (Button) v;
        String chosenName = b.getText().toString();
        String first = chosenName.split(" ")[0];
        String last = chosenName.split(" ")[1];

        boolean found = false;
        for (User r : recipientsList) {
            // find the matching recipient obj
            if (r.getPreferredName().getFirst().equals(first)
                    && r.getPreferredName().getLast().equals(last)) {
                found = true;
                // put recipient so messaging activity can refer to it
                i.putExtra("recipient", r);
                Log.d("DEBUG_CHOOSE_RECIP", "found recip, adding to intent");
            }
        }
        if (!found) {
            Log.d("DEBUG_CHOOSE_RECIP", "recipient mismatch on button press");
        }
        else {
            // proceed to messaging the chosen recipient
            startActivity(i);
        }
    }

    private void updateTheme() {
        switch (user.getProfilePreferences().getSystemTheme()){
            case "Blue": setTheme(R.style.BlueTheme); break;
            case "Red": setTheme(R.style.RedTheme); break;
            case "Green": setTheme(R.style.GreenTheme); break;
            default: setTheme(R.style.BlueTheme);
        }
    }
    private void updateFontSize(Button b){
        float fontSize = 15;
        switch(user.getProfilePreferences().getFontSize()){
            case "Small": fontSize = 12; break;
            case "Medium": fontSize = 15; break;
            case "Large": fontSize= 18; break;
        }
        b.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);

    }
}