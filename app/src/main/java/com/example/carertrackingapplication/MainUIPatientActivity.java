package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.window.SplashScreen;

import com.example.carertrackingapplication.appinfo.User;
import com.example.carertrackingapplication.variable.GlobalVar;
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
    TextView txt_username, txt_ratingScore, logout;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui_patient);
        MainUISetup();
//        User user = new User();
//        GlobalVar.current_user = user.getName();
//
//        try{
//            GlobalVar.current_user = user.getName();
//            Thread.sleep(3000);
//        }catch(Exception e){
//
//        }

       // System.out.println("LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOK AT ME! " + GlobalVar.current_user);
        //txt_username.setText(user.getName());

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        setupUserInformation();

        mapTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUIPatientActivity.this,MapsTrackerActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainUIPatientActivity.this);
                builder.setTitle("Sign Out")
                        .setMessage("Do you want to sign out?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainUIPatientActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(dialog1 -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(android.R.color.holo_red_light));
                });

                dialog.show();
            }
        });
    }


    protected void MainUISetup(){
        manageAppointment = (ImageButton)findViewById(R.id.appointmentBtn);
        mapTracker = (ImageButton)findViewById(R.id.mapTrackerBtn);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_ratingScore = (TextView) findViewById(R.id.txt_ratingScore);
        logout = (TextView) findViewById(R.id.logoutBtn);
    }

    private void setupUserInformation(){

        DocumentReference docRef = fireStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = new User();
                        user.initializeUserInfo();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        GlobalVar.current_user = document.get("name").toString();
                        GlobalVar.user_rating = document.get("rating").toString();
                        //String name = document.get("name").toString();
                        //String rating =  document.get("rating").toString();
                        txt_username.setText(GlobalVar.current_user);
                        txt_ratingScore.setText("Rating: " + GlobalVar.user_rating);


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