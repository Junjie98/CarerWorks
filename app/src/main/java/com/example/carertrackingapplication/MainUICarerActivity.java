package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainUICarerActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private ImageButton manageAppointment, mapTracker;
    Button trackPatientAddressBtn,callPatientBtn;
    TextView txt_username, txt_ratingScore, logout,dateField, statusField,timeField,durationField,carerField,addressField,patientField,notesField,upcomingAppointmentCarer;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    private static String smallestDateAppointmentID;
    String patientID, patientPhoneNum;
    CardView cardViewCarerHome;
    Date smallestDate = new Date();
    boolean hasUpcoming = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

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
        setupUpcomingAppointmentUI();
        System.out.println(smallestDateAppointmentID);

        if(hasUpcoming){
            upcomingAppointmentCarer.setVisibility(View.VISIBLE);
            cardViewCarerHome.setVisibility(View.VISIBLE);
        }
        //reload the upcoming if returned to page
        if(smallestDateAppointmentID != null){
            fillCardView();
            upcomingAppointmentCarer.setVisibility(View.VISIBLE);
            cardViewCarerHome.setVisibility(View.VISIBLE);
        }

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

        callPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + patientPhoneNum));
                System.out.println("tel:" + patientPhoneNum);


                if (ActivityCompat.checkSelfPermission(MainUICarerActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("NOPE");
                    return;
                }
                startActivity(callIntent);
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
        txt_ratingScore = (TextView) findViewById(R.id.txt_ratingScoreCarer);
        logout = (TextView) findViewById(R.id.logoutBtn);

        dateField = findViewById(R.id.viewAppointDatecarerui);
        statusField = findViewById(R.id.statusTextcarerui);
        timeField = findViewById(R.id.appointManageViewDatecarerui);
        durationField = findViewById(R.id.durationAreacarerui);
        carerField = findViewById(R.id.carerAssignedFieldcarerui);
        addressField = findViewById(R.id.viewAddressPostcodecarerui);
        patientField = findViewById(R.id.PatientNameFieldcarerui);
        notesField = findViewById(R.id.notesFieldcarerui);
        trackPatientAddressBtn = findViewById(R.id.trackHomePageAppointmentBtn);
        callPatientBtn = findViewById(R.id.callHomePageCarerBtn);
        cardViewCarerHome = findViewById(R.id.cardViewPatientMain);
        upcomingAppointmentCarer = findViewById(R.id.upcommingAppointmentLabel);
    }

    private void fillCardView(){
        DocumentReference getUpcoming = fireStore.collection("appointmentRequest").document(smallestDateAppointmentID);
        getUpcoming.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        dateField.setText(document.get("date").toString());
                        statusField.setText(document.get("status").toString());
                        timeField.setText(document.get("time").toString());
                        durationField.setText(document.get("duration").toString());
                        carerField.setText(document.get("carer_name").toString());
                        addressField.setText(document.get("address").toString() + ", " + document.get("postcode").toString());
                        patientField.setText(document.get("name").toString());
                        notesField.setText(document.get("notes").toString());
                        patientID = document.get("user_id").toString();

                        DocumentReference docRef = fireStore.collection("users").document(patientID);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        patientPhoneNum = document.get("phone").toString();
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
            }
        });
    }


    private void setupUpcomingAppointmentUI(){

        Query query = fireStore.collection("appointmentRequest").whereEqualTo("status","Assigned").whereEqualTo("carer_id",GlobalVar.current_user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<Date> dateCheck = new ArrayList<>();
                    ArrayList<String> upcomingID = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            Date d = new Date(); //check if it is overdue based on today's date and time. Else, ignore
                            Date b = new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(document.get("date").toString() +" "+ document.get("time").toString().replace(" ", ""));
                            System.out.println(b +  "  THIS IS DATE");
                            System.out.println(" this is today's date " + d);
                            if(d.before(new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(document.get("date").toString() +" "+ document.get("time").toString().replace(" ", "")))) {
                                System.out.println("HEREERERERERE " + document.getId() + "  === " + document.get("time").toString().replace(" ", ""));
                                dateCheck.add(new SimpleDateFormat("dd/MM/yyyy hh:mm").parse(document.get("date").toString() + " " + document.get("time").toString().replace(" ", "")));
                                upcomingID.add(document.getId());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("HERE IS THE SIZE " + dateCheck.size());
                    if(dateCheck.size()>1) {
                        for (int i = 0; i < dateCheck.size(); i++) { //find earliest date from patient's assigned appointment
                            for (int j = 0; j < dateCheck.size(); j++) {
                                if (dateCheck.get(i).before(dateCheck.get(j))) {
                                    smallestDate = dateCheck.get(i);
                                    smallestDateAppointmentID = upcomingID.get(i);
                                    System.out.println("aaaaaaaaaaaaaaaaglspglapslggggggggggggggggggg " + smallestDateAppointmentID);
                                    fillCardView();
                                    upcomingAppointmentCarer.setVisibility(View.VISIBLE);
                                    cardViewCarerHome.setVisibility(View.VISIBLE);
                                    hasUpcoming = true;
                                }
                            }
                        }
                    } else if(dateCheck.size() != 0) {
                        smallestDateAppointmentID = upcomingID.get(0);
                        fillCardView();
                        upcomingAppointmentCarer.setVisibility(View.VISIBLE);
                        cardViewCarerHome.setVisibility(View.VISIBLE);
                        hasUpcoming = true;
                        System.out.println("aaaaaaaaaaaaaaaagggggggggggggggggggggg " + smallestDateAppointmentID);
                    } else
                        cardViewCarerHome.setVisibility(View.GONE);
                        upcomingAppointmentCarer.setVisibility(View.GONE);
                    hasUpcoming = false;

                }
            }
        });
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