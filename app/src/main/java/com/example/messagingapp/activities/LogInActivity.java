package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.messagingapp.databinding.ActivityLogInBinding;
import com.example.messagingapp.models.User;
import com.example.messagingapp.singleton.MainUser;
import com.example.messagingapp.utils.Firebase_CollectionFields;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.crypto.SecretKey;

import security.manager.CryptoMethods;
import security.manager.KDC;
//upon login, KDC give user a key,
public class LogInActivity extends AppCompatActivity {

    private ActivityLogInBinding binding;
    private static final KDC kdc = new KDC();
    private String userId;

    private MainUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    public void loginPressed(View v) throws Exception{

        if (!this.validLogInCredFormat()) {
        }
        else {
            Button b = (Button) v;
            user_signIn();
            //startActivity(i);
        }
    }

    public void user_signIn(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Firebase_CollectionFields.ATTR_COLLECTION_USER_PROFILE)
                .whereEqualTo(Firebase_CollectionFields.ATTR_EMAIL, binding.EmailInput.getText().toString())
                .whereEqualTo(Firebase_CollectionFields.ATTR_PASSWORD, binding.pwdInput.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot docSnap = task.getResult().getDocuments().get(0);

                        // save query result as user object
                        User user = docSnap.toObject(User.class);
                        Intent home_page = new Intent(this, HomePageActivity.class);
                        displayHelpText("Company Login Successful");
                        String clientID = docSnap.getId();

                        MainUser.getInstance().setUserData(user);
                        startActivity(home_page);
                    }
                    else {
                        displayHelpText("User does not exist!");
                    }

                });

    }

    private void displayHelpText(String txt){
        Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
    }

    private boolean validLogInCredFormat() {
        if(binding.EmailInput.getText().toString().trim().isEmpty()){
            displayHelpText("Username Required");
            return false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.EmailInput.getText().toString()).matches()){
            displayHelpText("Please Enter a Valid Username");
            return false;
        }

        else if(binding.pwdInput.getText().toString().trim().isEmpty()){
            displayHelpText("Password Required");
            return false;
        }

        else {
            return true;
        }
    }

    private String getUserId(String email){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AtomicReference<String> workNumber = new AtomicReference<>();
        db.collection("userProfile")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // Assuming 'workNumber' is the field you're looking for
                            workNumber.set(querySnapshot.getDocuments().get(0).getString("workNumber"));

                            Log.d("WorkNumberFound", "Work number for email " + email + " is: " + workNumber);

                            // Do something with the workNumber, like updating UI
                        } else {
                            Log.d("NoDocumentFound", "No document found with the email " + email);
                            // Handle the case where no document was found
                        }
                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                        // Handle the error
                    }
                });

        return workNumber.toString();
    }


}