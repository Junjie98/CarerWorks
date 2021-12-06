package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class ViewAppointmentActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private Button deleteBtn,rescheduleBtn;
    private TextView date, time, duration, addressNpostcode,status,carerAssigned, noappointLabel,labelUserNameViewAppoint;
    FirebaseFirestore fireStore;
    String dateLoad,timeLoad,addressLoad,postcodeLoad,statusLoad,carerAssignedLoad,durationLoad;
    CardView cardview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);
        fireStore = FirebaseFirestore.getInstance();
        queryCurrentUserAppointment();
        setupUI();

        updateAppointmentText();




    }

    private void updateAppointmentText(){
        date.setText(dateLoad);
        time.setText(timeLoad);
        duration.setText(durationLoad);
        addressNpostcode.setText(addressLoad + ", " + postcodeLoad);
        status.setText(statusLoad);
        carerAssigned.setText(carerAssignedLoad);
    }

    private void setupUI(){
        date = (TextView) findViewById(R.id.dateViewTxt);
        time = findViewById(R.id.timeViewTxt);
        //tvDate = findViewById(R.id.dateText);
        duration = findViewById(R.id.durationTxt);
        //tvTime = findViewById(R.id.timeText);
        addressNpostcode = findViewById(R.id.addressNpostcodeTxt);
        status = findViewById(R.id.statusTxt);
        carerAssigned = findViewById(R.id.carerAssignedTxt);
        rescheduleBtn = findViewById(R.id.submitButton);
        deleteBtn = findViewById(R.id.deleteAppointment);
        cardview = findViewById(R.id.cardViewAppointment);
        noappointLabel = findViewById(R.id.noAppointmentLabel);
        labelUserNameViewAppoint = findViewById(R.id.txt_usernameViewAppoint);
        labelUserNameViewAppoint.setText(GlobalVar.current_user);
    }

//    private void databaseReadAppointmentStatus(){
//        DocumentReference docRef = fireStore.collection("users").document(GlobalVar.current_user_id);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        //queryUserType();
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        Long typeNumber = (Long)document.get("user_type"); //use long instead of int. lol
//
//                        GlobalVar.current_user = document.get("name").toString();
//                        String email = document.get("email").toString();
//                        String gender = document.get("gender").toString();
//                        String dob = document.get("dob").toString();
//                        String phone = document.get("phone").toString();
//
//
//                        //String name = document.get("name").toString();
//                        //String rating =  document.get("rating").toString();
//                        txt_username.setText(GlobalVar.current_user);
//                        // txt_ratingScore.setText("Rating: " + GlobalVar.user_rating);
//
//
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }

    private void queryCurrentUserAppointment(){
        CollectionReference appointment = fireStore.collection("appointmentRequest");
        Query query = appointment.whereEqualTo("user_id", GlobalVar.current_user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    cardview.setVisibility(View.VISIBLE);
                    noappointLabel.setVisibility(View.INVISIBLE);
                    for(QueryDocumentSnapshot document:task.getResult()){
                         dateLoad = document.get("date").toString();
                         timeLoad = document.get("time").toString();
                         durationLoad = document.get("duration").toString();
                         addressLoad = document.get("address").toString();
                         postcodeLoad = document.get("postcode").toString();
                         statusLoad = document.get("status").toString();
                         carerAssignedLoad = document.get("carer_assigned").toString();


                    }
                    updateAppointmentText();
                }else{
                    cardview.setVisibility(View.INVISIBLE);
                    noappointLabel.setVisibility(View.VISIBLE);
                    System.out.println("Query Failed. Something is wrong.");
                }
            }
        });
    }

}