package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.carertrackingapplication.appinfo.CarerInfo;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainUICarerActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main_uicarer);
        MainUISetup();

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        setupUserInformation();

        mapTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUICarerActivity.this,CarerMapTrackerActivity.class));
            }
        });


        manageAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUICarerActivity.this, ManageAppointmentActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainUICarerActivity.this);
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
                                Intent intent = new Intent(MainUICarerActivity.this, LoginActivity.class);
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
                            .setTextColor(getResources().getColor(android.R.color.holo_blue_light));
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
                        //queryUserType();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Long typeNumber = (Long)document.get("user_type"); //use long instead of int. lol

                        GlobalVar.current_user = document.get("name").toString();
                        String email = document.get("email").toString();
                        String gender = document.get("gender").toString();
                        String dob = document.get("dob").toString();
                        String phone = document.get("phone").toString();
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

    private void queryUserType(int num){
        CollectionReference carerRef = fireStore.collection("users");
        Query query = carerRef.whereEqualTo("user_type", num);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document:task.getResult()){
                        System.out.println(document.get("name"));
                    }
                }else{
                    System.out.println("Query Failed. Something is wrong.");
                }
            }
        });
    }

}