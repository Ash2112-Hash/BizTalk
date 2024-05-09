package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.messagingapp.R;
import com.example.messagingapp.models.Message;
import com.example.messagingapp.models.User;
import com.example.messagingapp.singleton.MainUser;
import com.example.messagingapp.utils.Firebase_CollectionFields;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.UUID;

import javax.crypto.SecretKey;

import security.manager.AppClient;
import security.manager.AuthenticationServer;
import security.manager.CryptoMethods;
import security.manager.KDC;


//then upon message user authenticate then get chat with messaging key


public class MessagingActivity extends AppCompatActivity {

    ArrayList<Message> messageList = new ArrayList<>();
    LinearLayout messageLayoutList;
    User recipient;
    public static final User user = MainUser.getInstance().getUserData();;
    String pairID;

    public static final KDC kdc = new KDC();
    public static final AuthenticationServer AS = new AuthenticationServer(kdc);

    public static final AppClient AC = new AppClient(user.getWorkNumber(), "", 1234, kdc, user);


    //TODO:ideally, this shouldn't be here, such Authentication Server should be a actual server but we don't ahve time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getting info passed from choose recipient activity
        Intent i = getIntent();
        recipient = (User) i.getSerializableExtra("recipient");
        pairID = generatePairID();

        String recipientName = recipient.getPreferredName().getFirst()
                                + " " + recipient.getPreferredName().getLast();
        Log.d("DEBUG_MESSAGING","Recipient Display Name: "+recipientName);

        setTitle("Messaging "+recipientName);
        updateTheme();
        setContentView(R.layout.activity_messaging);

        messageLayoutList = findViewById(R.id.messageLayout);

        updateHeader(recipientName);

        // start event listener for changes in messages between
        // this main user and selected recipient
        startMessageLogListener();

        // start event listener for typing in msg field
        startEnterMessageListener();
        if(!AS.serverStart){
            AS.start();
        }
        while(!AC.connected.get()){
            AC.connect();
            try{
                Thread.sleep(3000);
            }
            catch(Exception e){

            }
        }

    }

    // create message object
    private Message createMessage(String messageContents) {
        Message m = new Message();
        m.setPairID(pairID);
        m.setMessage(messageContents);
        m.setDate(Timestamp.now());
        m.setSenderEmail(user.getEmail());
        return m;
    }

    // the key for communication between these two users
    // chosen by the lexicographic ordering
    private String generatePairID() {
        String pairID;
        String e1 = user.getEmail();
        String e2 = recipient.getEmail();
        if (e1.compareTo(e2) < 0) {
            pairID = e1+"-"+e2;
        }
        else {
            pairID = e2+"-"+e1;
        }
        Log.d("DEBUG_MESSAGING","Generated Pair ID: "+pairID);
        return pairID;
    }
    private void updateTheme() {
        switch (user.getProfilePreferences().getSystemTheme()){
            case "Blue": setTheme(R.style.BlueTheme); break;
            case "Red": setTheme(R.style.RedTheme); break;
            case "Green": setTheme(R.style.GreenTheme); break;
            default: setTheme(R.style.BlueTheme);
        }
    }
    private void updateFontSize(){
        float fontSize = 15;
        switch(user.getProfilePreferences().getFontSize()){
            case "Small": fontSize = 12; break;
            case "Medium": fontSize = 15; break;
            case "Large": fontSize= 18; break;
        }

        TextView recipientBox = findViewById(R.id.recipientButton);

        recipientBox.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);

    }

    // OLD
    /*private void retrieveMessages() {
        Log.d("DEBUG_MESSAGING","Retrieving messages from log with pair ID: "+pairID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Firebase_CollectionFields.ATTR_COLLECTION_MESSAGING)
                // uses the generated key for user, recipient pair
                .whereEqualTo(Firebase_CollectionFields.ATTR_PAIR_ID, pairID)
                // order by timestamp
                .orderBy(Firebase_CollectionFields.ATTR_DATE, Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        messageList.clear();
                        List<DocumentSnapshot> messagesQueryData = task.getResult().getDocuments();
                        for (DocumentSnapshot docSnap : messagesQueryData) {
                            Message message = docSnap.toObject(Message.class);
                            messageList.add(message);
                            Log.d("DEBUG_MESSAGING2","Added to local msg list: "+message.getMessage());
                        }
                    }
                    // messages have been retrieved from DB, now display
                    updateMessageLayout();
                });
    }*/

    public void saveMessage(View v) {
        // extract current message input
        EditText e = findViewById(R.id.messageField);
        String s = e.getText().toString();
        SecretKey key = CryptoMethods.StringToSKey(user.getSecretKey());
        SecretKey key2 = CryptoMethods.StringToSKey(user.getMessagingKey());
        e.setText("");
        try {
            String encrptedS = CryptoMethods.encryption(CryptoMethods.encryption(s, key),key2);
            // save as message obj
            Message msg = createMessage(encrptedS);
            // reset the text field
            if(!AC.sendMessage(msg, pairID)){
                Log.d("MessagingActivity", "Exception on AC sending Message");
            }
        }
        catch (Exception ex){
            Log.d("MessagingActivity", "Message failed to send");
        }
    }

    // TODO need to make this layout nicer
    // separate xml files for sender and recipient messages
    private void updateMessageLayout() {
        messageLayoutList.removeAllViews();
        for (Message msg : messageList) {
            View v = getLayoutInflater().inflate(R.layout.message,null,false);
            TextView tv = v.findViewById(R.id.messageFromSender);

            if (msg.getSenderEmail().equals(user.getEmail())) {
                tv.setText(user.getPreferredName().getFirst() +
                        ": "+msg.getMessage()+"\n"+msg.getDate().toDate());
            }
            else {
                tv.setText(recipient.getPreferredName().getFirst() +
                        ": "+msg.getMessage()+"\n"+msg.getDate().toDate());
            }
            messageLayoutList.addView(v);
        }
    }

    private void updateHeader(String recipName) {
        TextView t = findViewById(R.id.msgHeader);
        t.setText(recipName);
    }

    // snapshot listener for changes to db relevant to query
    private void startMessageLogListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // will run every time DB for relevant entries are modified
        db.collection(Firebase_CollectionFields.ATTR_COLLECTION_MESSAGING)
                // uses the generated key for user, recipient pair
                .whereEqualTo(Firebase_CollectionFields.ATTR_PAIR_ID, pairID)
                .orderBy(Firebase_CollectionFields.ATTR_DATE, Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d("DEBUG_MESSAGING", "DB listener failed: ", error);
                    }
                    if (value != null) {
                        // clear existing list to add new entries
                        messageList.clear();
                        for (DocumentSnapshot docSnap : value) {
                            Message message = docSnap.toObject(Message.class);
                            messageList.add(message);

                        }
                    }
                    // messages have been retrieved from DB, now display
                    updateMessageLayout();
                });
    }

    private void startEnterMessageListener() {
        EditText e = findViewById(R.id.messageField);
        e.addTextChangedListener(new TextWatcher() {
            Button send = findViewById(R.id.sendMsgButton);
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    send.setEnabled(true);
                    send.setAlpha(1);
                }
                else {
                    send.setEnabled(false);
                    send.setAlpha(0.5F);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}