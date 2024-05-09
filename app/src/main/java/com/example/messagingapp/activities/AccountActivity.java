package com.example.messagingapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.messagingapp.R;
import com.example.messagingapp.models.User;
import com.example.messagingapp.singleton.MainUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

public class AccountActivity extends AppCompatActivity {
    User user;
    private String selectedColor;
    private String selectedFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        user = MainUser.getInstance().getUserData();
        selectedColor = user.getProfilePreferences().getSystemTheme();
        selectedFontSize = user.getProfilePreferences().getFontSize();

        this.updateTheme();

        setContentView(R.layout.activity_account);
        this.updateFontSize();
        this.updateText();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startEventColorSpinnerListener();
        startEventTextSizeSpinnerListener();

    }
    public void updateButtonPress(View v){
        EditText fullName = findViewById(R.id.prefNameResult);
        String fullNameStr = fullName.getText().toString();
        String[] firstLast = fullNameStr.split(" ");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userProfileCollection = db.collection("userProfile");
        Query query = userProfileCollection.whereEqualTo("email", user.getEmail());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Handle the document here
                    Log.d("DB", document.getId() + " => " + document.getData());
                    DocumentReference docRef = userProfileCollection.document(document.getId());
                    user.getProfilePreferences().setFontSize(selectedFontSize);
                    user.getProfilePreferences().setSystemTheme(selectedColor);
                    user.getPreferredName().setFirst(firstLast[0]);
                    user.getPreferredName().setLast(firstLast[1]);
                    docRef.update("preferredName.first", firstLast[0], "preferredName.last", firstLast[1], "profilePreferences.fontSize", selectedFontSize, "profilePreferences.systemTheme", selectedColor)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("DB Update", "DocumentSnapshot successfully updated!");
                                recreate();
                            })
                            .addOnFailureListener(e -> Log.w("DB Update", "Error updating document", e));
                }
            } else {
                Log.d("DB", "Error getting documents: ", task.getException());
            }
        });
    }
    private void startEventColorSpinnerListener() {
        Spinner spinner = findViewById(R.id.sysColorSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.colorsArray,
                R.layout.spinner_list
        );
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);

        String desiredTheme = user.getProfilePreferences().getSystemTheme(); // Change "YourDesiredValue" to the value you want to find the index for
        // Find the index of the desired value
        int index = getIndex(adapter, desiredTheme);
        if (index != -1) {
            spinner.setSelection(index); // Set the selection to the index if found
        }
        // Set an item selected listener to perform actions when an item is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected item from the spinner
                selectedColor = parent.getItemAtPosition(position).toString();

                // Display a toast with the selected color
                //Toast.makeText(AccountActivity.this, "Selected color: " + selectedColor, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });
    }


    private void startEventTextSizeSpinnerListener() {
        Spinner spinner = findViewById(R.id.fontSizeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.fontSizeArray,
                R.layout.spinner_list
        );
        adapter.setDropDownViewResource(R.layout.spinner_list);
        spinner.setAdapter(adapter);

        String desiredSize = user.getProfilePreferences().getFontSize();
        // Find the index of the desired value
        int index = getIndex(adapter, desiredSize);
        if (index != -1) {
            spinner.setSelection(index); // Set the selection to the index if found
        }
        // Set an item selected listener to perform actions when an item is selected
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected item from the spinner
                selectedFontSize = parent.getItemAtPosition(position).toString();

                // Display a toast with the selected color
                //Toast.makeText(AccountActivity.this, "Selected color: " + selectedColor, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });
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

        TextView workRole = findViewById(R.id.workRole);
        TextView workRoleR = findViewById(R.id.workRoleResult);
        TextView phoneNumber = findViewById(R.id.phoneNumber);
        TextView phoneNumberR = findViewById(R.id.phoneNumberResult);
        TextView prefName = findViewById(R.id.prefName);
        EditText prefNameR = findViewById(R.id.prefNameResult);
        TextView fSize = findViewById(R.id.fontSize);
        TextView sysColor = findViewById(R.id.sysColor);

        workRole.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        workRoleR.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        phoneNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        phoneNumberR.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        prefName.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        prefNameR.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        fSize.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
        sysColor.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSize);
    }
    private void updateText(){
        TextView prefName = findViewById(R.id.textView4);
        TextView accName = findViewById(R.id.textView);
        TextView email = findViewById(R.id.textView3);
        TextView phoneNumber = findViewById(R.id.phoneNumberResult);
        EditText prefNameEditable = findViewById(R.id.prefNameResult);

        String firstLastPref = user.getPreferredName().getFirst()+" "+user.getPreferredName().getLast();
        prefName.setText(firstLastPref);
        prefNameEditable.setText(firstLastPref);

        String firstLastAcc = user.getEmployeeName().getFirst()+" "+user.getEmployeeName().getLast();
        accName.setText(firstLastAcc);

        email.setText(user.getEmail());
        phoneNumber.setText(user.getWorkNumber());


    }
    private static int getIndex(ArrayAdapter<CharSequence> adapter, String desiredItem) {
        int index = -1; // Initialize the index to -1
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(desiredItem)) {
                index = i; // Found the index
                break;
            }
        }
        return index;
    }
}