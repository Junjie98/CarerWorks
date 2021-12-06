package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.carertrackingapplication.appinfo.AdapterRecyclerView.MyAdapter;
import com.example.carertrackingapplication.appinfo.Appointment;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewRequestAppointment extends AppCompatActivity {

    RecyclerView recyclerView;
    //he uses DatabaseReference database; i suppose that is to dblive
    FirebaseFirestore fireStore;
    MyAdapter myAdapter;
    ArrayList<Appointment> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_appointment);

        recyclerView = findViewById(R.id.viewReqAppointManagement); //point towards activity_view_request_appointment.xml recycler view id.
        fireStore = FirebaseFirestore.getInstance();
//        FirebaseFirestore appointment = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this,list);
        recyclerView.setAdapter(myAdapter);

        Query query = fireStore
                .collection("appointmentReference");

        //recyclerOption
        FirestoreRecyclerOptions<Appointment> appointment = new FirestoreRecyclerOptions<Appointment, MyAdapter.MyViewHolder>(){

        }

//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot document : task.getResult()){
//                                Appointment appointment = (Appointment) document.get(Appointment.class);
//                                list.add(appointment);
//                            }
//                        }
//                    }
//                });

    }

    private void queryCurrentUserAppointment(){
        CollectionReference appointment = fireStore.collection("appointmentRequest");
        Query query = appointment.whereEqualTo("user_id", GlobalVar.current_user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
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