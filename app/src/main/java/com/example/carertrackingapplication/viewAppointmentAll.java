package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.carertrackingapplication.appinfo.Appointment;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class viewAppointmentAll extends AppCompatActivity {

//    boolean assigned = false;
    public static final String TAG = "TAG";
    private RecyclerView recyclerView; //mFirestoreList
    private FirebaseFirestore fireStore;

    private FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_all);

        ImageButton imgBtn = (ImageButton)findViewById(R.id.backBtnViewAppointment);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fireStore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.viewAppointmentRecyclerView); //point towards activity_view_request_appointment.xml recycler view id.

        // to create firebaseFirestore adapter we need two things. 1: Query, 2:recycler options.

        //Query
        Query query = fireStore.collection("appointmentRequest").orderBy("date",Query.Direction.ASCENDING);

        //RecyclerOptions
        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();


        //now we are ready create recycler adapter.
        adapter = new FirestoreRecyclerAdapter<Appointment, AllAppointmentViewHolder>(options) {
            @NonNull
            @Override
            public AllAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_appointment,parent,false);
                return new AllAppointmentViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull AllAppointmentViewHolder holder, int position, @NonNull Appointment model) {
                if(GlobalVar.user_type == "carer") {
                    holder.routeBtn.setVisibility(View.VISIBLE);
                    if (model.getStatus().equals("Assigned") && model.getCarer_id().equals(GlobalVar.current_user_id)) { //hide those that has already been assigned.
                        // assigned = true;
                        System.out.println("Validate the ID HERE " + model.getCarer_id() + " ACTUAL ID =" + GlobalVar.current_user_id);
                        holder.cardView.setVisibility(View.VISIBLE);
                        //notifyDataSetChanged();
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        holder.appointmentID = snapshot.getId();
                        System.out.println("HELLO JIALINGYI " + holder.appointmentID);

                        holder.address = model.getAddress();
                        holder.postcode = model.getPostcode();
                        holder.date = model.getDate();
                        holder.time = model.getTime();
                        holder.duration = model.getDuration();
                        holder.name = model.getName();
                        holder.notes = model.getNotes();
                        holder.status = model.getStatus();
                        holder.carerName = model.getCarer_name();

                        holder.addressTV.setText(holder.address + ", " + holder.postcode);
                        holder.dateTV.setText(holder.date);
                        holder.timeTV.setText(holder.time);
                        holder.durationTV.setText(holder.duration);
                        holder.namePatient.setText(holder.name);
                        holder.notesTV.setText(holder.notes);
                        holder.statusTV.setText(holder.status);
                        holder.nameCarerTV.setText(holder.carerName);

                        holder.position = holder.getAbsoluteAdapterPosition();
                        holder.userid = model.getUser_ID();

                        return;

                    } else {
//                    assigned = false;
                        holder.cardView.setVisibility(View.GONE);
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        holder.appointmentID = snapshot.getId();

                    }
                    //System.out.println(model.getUser_ID()+ " LOOOOOOOOOOOOOOOOOOOOOOOOOK");


                }

                if(GlobalVar.user_type == "patient") {
                    holder.routeBtn.setVisibility(View.VISIBLE);
                    if (model.getUser_ID().equals(GlobalVar.current_user_id)) { //hide those that has already been assigned.
                        // assigned = true;
                        holder.cardView.setVisibility(View.VISIBLE);
                        holder.routeBtn.setText("Track");
                        //notifyDataSetChanged();
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        holder.appointmentID = snapshot.getId();
                        System.out.println("HELLO HERE " + holder.appointmentID);

                        holder.address = model.getAddress();
                        holder.postcode = model.getPostcode();
                        holder.date = model.getDate();
                        holder.time = model.getTime();
                        holder.duration = model.getDuration();
                        holder.name = model.getName();
                        holder.notes = model.getNotes();
                        holder.status = model.getStatus();
                        holder.carerName = model.getCarer_name();
                        holder.carerID = model.getCarer_id();

                        holder.addressTV.setText(holder.address + ", " + holder.postcode);
                        holder.dateTV.setText(holder.date);
                        holder.timeTV.setText(holder.time);
                        holder.durationTV.setText(holder.duration);
                        holder.namePatient.setText(holder.name);
                        holder.notesTV.setText(holder.notes);
                        holder.statusTV.setText(holder.status);
                        holder.nameCarerTV.setText(holder.carerName);

                        holder.position = holder.getAbsoluteAdapterPosition();
                        holder.userid = model.getUser_ID();

                        return;

                    } else {
//                    assigned = false;
                        holder.cardView.setVisibility(View.GONE);
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        holder.appointmentID = snapshot.getId();

                    }
                    //System.out.println(model.getUser_ID()+ " LOOOOOOOOOOOOOOOOOOOOOOOOOK");


                }
            }
        };

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //view holder
    }

    //inner class
    private class AllAppointmentViewHolder extends RecyclerView.ViewHolder{

        TextView addressTV, dateTV, timeTV, durationTV, nameCarerTV, notesTV, namePatient, statusTV;
        Button assignMeBtn,routeBtn;

        int position;
        String address, date, duration, name, notes, postcode,status,time,userid,carerName,carerID;
        String appointmentID;
        CardView cardView;

        public AllAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            // if(assigned==true){
            cardView = itemView.findViewById(R.id.appointmentViewRecyclerCardView);//.setVisibility(View.GONE);
            //}
            // else
            //    itemView.findViewById(R.id.appointmentRecyclerCardView).setVisibility(View.VISIBLE);

            addressTV = itemView.findViewById(R.id.viewAddressPostcode);
            dateTV = itemView.findViewById(R.id.viewAppointDate);
            timeTV = itemView.findViewById(R.id.appointManageViewDate);
            durationTV = itemView.findViewById(R.id.durationArea);
            nameCarerTV = itemView.findViewById(R.id.carerAssignedField);
            notesTV = itemView.findViewById(R.id.notesField);
            namePatient = itemView.findViewById(R.id.PatientNameField);
            statusTV = itemView.findViewById(R.id.statusText);
            routeBtn = itemView.findViewById(R.id.routeBtn);

                routeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(GlobalVar.user_type == "carer") {
                            CarerMapTrackerActivity c = new CarerMapTrackerActivity(address, postcode, userid, name);
                            startActivity(new Intent(viewAppointmentAll.this, CarerMapTrackerActivity.class));
                        }
                        if(GlobalVar.user_type == "patient"){
                            MapsTrackerActivity m = new MapsTrackerActivity(address,postcode,carerID,carerName);
                            startActivity(new Intent(viewAppointmentAll.this, MapsTrackerActivity.class));
                        }

                    }
                });


//            itemView.findViewById(R.id.acceptPatientAppointmentBtn).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    System.out.println(appointmentID);
//
//                    //DocumentReference docRef = fireStore.collection("appointmentRequest").document(appointmentID);
//
//
//                    Map<String, Object> appointmentRequestUpdate = new HashMap<>();
//                    appointmentRequestUpdate.put("carer_id", GlobalVar.current_user_id); //carer id.
//                    appointmentRequestUpdate.put("carer_name", GlobalVar.current_user); //carer name
//                    appointmentRequestUpdate.put("status", "Assigned");
//                    fireStore.collection("appointmentRequest").document(appointmentID)
//                            .update(appointmentRequestUpdate);
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
//                            Toast.makeText(viewAppointmentAll.this, "Appointment has been requested successfully. Awaiting for approval by Administrator", Toast.LENGTH_SHORT).show();
//                            try{
//                                Toast.makeText(viewAppointmentAll.this, "This appointment " + appointmentID + " has been assigned to you.", Toast.LENGTH_SHORT).show();
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            //startActivity(new Intent(viewAppointmentAll.this, MainUIPatientActivity.class));
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
              //  }
           // });
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
