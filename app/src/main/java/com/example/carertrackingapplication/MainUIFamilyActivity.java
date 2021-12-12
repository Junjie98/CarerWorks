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

import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class MainUIFamilyActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private ImageButton manageAppointment, mapTracker, settingPatient,historyLogBtn;
    TextView txt_username, txt_ratingScore,patientUserID,logout, upcomingAppointmentPatient,dateField,statusField,timeField,durationField,carerField,addressField,patientField,notesField;
    Button trackCarerBtn, callCarerBtn;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    Date smallestDate = new Date();
    static String smallestDateAppointmentID;
    String carerID, carerPhoneNum, trackAddress, trackPostcode, carerName;
    CardView cardViewPatientHome;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUserInformation();
        setupUpcomingAppointmentUI();
        System.out.println("IM BACK ALIVE");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_uifamily);

        MainUISetup();

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        GlobalVar.current_user_id = firebaseAuth.getCurrentUser().getUid();

        setupUserInformation();
        setupUpcomingAppointmentUI();
        historyLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUIFamilyActivity.this, ViewHistoryLog.class));
            }
        });


        settingPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUIFamilyActivity.this,settingActivity.class));
            }
        });


        manageAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUIFamilyActivity.this, ManageAppointmentActivity.class));
            }
        });

        callCarerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + carerPhoneNum));

                if (ActivityCompat.checkSelfPermission(MainUIFamilyActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("NOPE");
                    return;
                }
                startActivity(callIntent);
            }
        });

        trackCarerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsTrackerActivity m = new MapsTrackerActivity(trackAddress,trackPostcode,carerID,carerName);
                startActivity(new Intent(MainUIFamilyActivity.this, MapsTrackerActivity.class));

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainUIFamilyActivity.this);
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
                                Intent intent = new Intent(MainUIFamilyActivity.this, LoginActivity.class);
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
        txt_username = (TextView) findViewById(R.id.txt_Family_username);
        txt_ratingScore = (TextView) findViewById(R.id.txt_ratingScore);
        logout = (TextView) findViewById(R.id.logoutFamilyBtn);
        dateField = findViewById(R.id.viewAppointDatefamilyui);
        statusField = findViewById(R.id.familystatusTextHistory);
        timeField = findViewById(R.id.appointManageViewDatefamilyui);
        durationField = findViewById(R.id.endTimeFieldHistoryfamilyui);
        carerField = findViewById(R.id.carerAssignedFieldfamily);
        addressField = findViewById(R.id.viewAddressPostcodefamilyui);
        patientField = findViewById(R.id.PatientNameFieldHistoryfamilyui);
        notesField = findViewById(R.id.notesFieldfamilyui);
        trackCarerBtn = findViewById(R.id.trackHomePageAppointmentBtn);
        callCarerBtn = findViewById(R.id.callHomePageCarerBtn);
        cardViewPatientHome = findViewById(R.id.cardViewPatientFamilyMain);
        upcomingAppointmentPatient = findViewById(R.id.upcomingFamilyAppointmentLabel);
        settingPatient = findViewById(R.id.settingBtn);
        historyLogBtn = findViewById(R.id.historyLogBtn);
        patientUserID = findViewById(R.id.myID);


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
                        carerID = document.get("carer_id").toString();

                        trackAddress = document.get("address").toString();
                        trackPostcode = document.get("postcode").toString();
                        carerName = document.get("carer_name").toString();

                        DocumentReference docRef = fireStore.collection("users").document(carerID);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        carerPhoneNum = document.get("phone").toString();
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
        Query query = fireStore.collection("appointmentRequest").whereEqualTo("status","Assigned").whereEqualTo("user_id",GlobalVar.family_id); //where the user is my family id
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
                                    upcomingAppointmentPatient.setVisibility(View.VISIBLE);
                                    cardViewPatientHome.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else if(dateCheck.size() != 0) {
                        smallestDateAppointmentID = upcomingID.get(0);
                        fillCardView();
                        upcomingAppointmentPatient.setVisibility(View.VISIBLE);
                        cardViewPatientHome.setVisibility(View.VISIBLE);
                        System.out.println("aaaaaaaaaaaaaaaagggggggggggggggggggggg " + smallestDateAppointmentID);
                    } else
                        cardViewPatientHome.setVisibility(View.VISIBLE);
                    upcomingAppointmentPatient.setVisibility(View.GONE);

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
//                        Long typeNumber = (Long)document.get("user_type"); //use long instead of int. lol

                        GlobalVar.current_user = document.get("name").toString();
                        //String name = document.get("name").toString();
                        //String rating =  document.get("rating").toString();
                        txt_username.setText(GlobalVar.current_user);
                        patientUserID.setText(firebaseAuth.getCurrentUser().getUid());

                        // txt_ratingScore.setText("Rating: " + GlobalVar.user_rating);


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