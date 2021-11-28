package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainUIPatientActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private ImageButton manageAppointment, mapTracker;
    TextView txt_username, txt_ratingScore;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui_patient);
        MainUISetup();
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        setupUserInformation();

        mapTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUIPatientActivity.this,MapsTrackerActivity.class));
            }
        });
    }


    protected void MainUISetup(){
        manageAppointment = (ImageButton)findViewById(R.id.appointmentBtn);
        mapTracker = (ImageButton)findViewById(R.id.mapTrackerBtn);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_ratingScore = (TextView) findViewById(R.id.txt_ratingScore);
    }

    private void setupUserInformation(){

        DocumentReference docRef = fireStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String name = (String) document.get("name");
                        String rating = (String) document.get("rating");
                        txt_username.setText(name);
                        txt_ratingScore.setText("Rating: " + rating);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }
}