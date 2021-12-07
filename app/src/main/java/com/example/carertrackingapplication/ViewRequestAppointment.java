package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carertrackingapplication.appinfo.AdapterRecyclerView.MyAdapter;
import com.example.carertrackingapplication.appinfo.Appointment;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewRequestAppointment extends AppCompatActivity {
    boolean assigned = false;
    public static final String TAG = "TAG";
    private RecyclerView recyclerView; //mFirestoreList
    private FirebaseFirestore fireStore;
    //he uses DatabaseReference database; i suppose that is to dblive
    //FirebaseFirestore fireStore;
    //MyAdapter myAdapter;
    private FirestoreRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_appointment);

        ImageButton imgBtn = (ImageButton)findViewById(R.id.backBtnViewReq);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fireStore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.viewReqAppointManagement); //point towards activity_view_request_appointment.xml recycler view id.

        // to create firebaseFirestore adapter we need two things. 1: Query, 2:recycler options.

        //Query
        Query query = fireStore.collection("appointmentRequest").orderBy("date",Query.Direction.ASCENDING);

        //RecyclerOptions
        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();

        //now we are ready create recycler adapter.
         adapter = new FirestoreRecyclerAdapter<Appointment, AppointmentViewHolder>(options) {
            @NonNull
            @Override
            public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments,parent,false);
                return new AppointmentViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position, @NonNull Appointment model) {

                if(model.getStatus().equals("Assigned")){ //hide those that has already been assigned.
                    assigned = true;
                    holder.cardView.setVisibility(View.GONE);
                    //notifyDataSetChanged();
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                    holder.appointmentID = snapshot.getId();
                    System.out.println("HELLO JIALINGYI " + holder.appointmentID);
                    return;

                }else {
                    assigned = false;
                    holder.cardView.setVisibility(View.VISIBLE);
            //  notifyDataSetChanged();
                    holder.address = model.getAddress();
                    holder.postcode = model.getPostcode();
                    holder.date = model.getDate();
                    holder.time = model.getTime();
                    holder.duration = model.getDuration();
                    holder.name = model.getName();
                    holder.notes = model.getNotes();

                    holder.addressTV.setText(holder.address + ", " + holder.postcode);
                    holder.dateTV.setText(holder.date);
                    holder.timeTV.setText(holder.time);
                    holder.durationTV.setText(holder.duration);
                    holder.nameTV.setText(holder.name);
                    holder.notesTV.setText(holder.notes);

                    holder.position = holder.getAbsoluteAdapterPosition();
                    holder.userid = model.getUser_ID();
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                    holder.appointmentID = snapshot.getId();

                }
                //System.out.println(model.getUser_ID()+ " LOOOOOOOOOOOOOOOOOOOOOOOOOK");



            }
        };

         recyclerView.setHasFixedSize(false);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setAdapter(adapter);
                //view holder
    }

    //inner class
    private class AppointmentViewHolder extends RecyclerView.ViewHolder{

        TextView addressTV, dateTV, timeTV, durationTV, nameTV, notesTV;
        Button assignMeBtn;
        Appointment app;

        int position;
        //String userid;
        String address, date, duration, name, notes, postcode,status,time,userid;
        String appointmentID;
        CardView cardView;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
           // if(assigned==true){
                cardView = itemView.findViewById(R.id.appointmentRecyclerCardView);//.setVisibility(View.GONE);
            //}
           // else
            //    itemView.findViewById(R.id.appointmentRecyclerCardView).setVisibility(View.VISIBLE);

            addressTV = itemView.findViewById(R.id.appointManageViewAddress);
            dateTV = itemView.findViewById(R.id.appointManageViewDate);
            timeTV = itemView.findViewById(R.id.appointManageViewTime);
            durationTV = itemView.findViewById(R.id.appointManageViewDuration);
            nameTV = itemView.findViewById(R.id.appointManageViewName);
            notesTV = itemView.findViewById(R.id.appointManageViewNotes);

            //assignMeBtn = findViewById(R.id.acceptPatientAppointmentBtn);

            itemView.findViewById(R.id.acceptPatientAppointmentBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(appointmentID);

                    //DocumentReference docRef = fireStore.collection("appointmentRequest").document(appointmentID);


                    Map<String, Object> appointmentRequestUpdate = new HashMap<>();
                    appointmentRequestUpdate.put("carer_id", GlobalVar.current_user_id); //carer id.
                    appointmentRequestUpdate.put("carer_name", GlobalVar.current_user); //carer name
                    appointmentRequestUpdate.put("status", "Assigned");
                    fireStore.collection("appointmentRequest").document(appointmentID)
                            .update(appointmentRequestUpdate);
//
//                    appointmentRequestUpdate.put("user_id",nameTV);
//                    appointmentRequestUpdate.put("name",nameTV);
//                    appointmentRequestUpdate.put("address",addressSubmit);
//                    appointmentRequestUpdate.put("postcode",postcodeSubmit);
//                    appointmentRequestUpdate.put("date",dateSubmit);
//                    appointmentRequestUpdate.put("time",timeStoredSubmit);
//                    appointmentRequestUpdate.put("duration",careDurationSubmit);
//                    appointmentRequestUpdate.put("notes",notesSubmit);
//                    appointmentRequestUpdate.put("status","pending");
//                    appointmentRequestUpdate.put("carer_id",GlobalVar.current_user_id);
//                    appointmentRequestUpdate.put("carer_name",GlobalVar.current_user);
//                    appointmentRequestUpdate.put("status","Assigned");


//                    docRef.set(appointmentRequestUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            System.out.println(docRef);
//                            Log.d(TAG,"onSuccess: appointment is created by " + GlobalVar.current_user_id);
//                            Toast.makeText(ViewRequestAppointment.this, "Appointment has been requested successfully. Awaiting for approval by Administrator", Toast.LENGTH_SHORT).show();
//                            try{
//                                Toast.makeText(ViewRequestAppointment.this, "This appointment " + appointmentID + " has been assigned to you.", Toast.LENGTH_SHORT).show();
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            //startActivity(new Intent(ViewRequestAppointment.this, MainUIPatientActivity.class));
//
//                        }
//                    });
//                            collRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        //queryUserType();
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        document.get
//
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

//                    Query query = collRef.whereEqualTo("appointmentRequest",appointmentID);
//                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if(task.isSuccessful()) {
//
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    String Test = document.getId();
//                                    System.out.println("THIS IS IT NELSON! " + Test);
//                                }
//                            }
//                        }
//                    });
                }
            });
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

}