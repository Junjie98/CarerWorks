package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.carertrackingapplication.appinfo.AdapterRecyclerView.MyAdapter;
import com.example.carertrackingapplication.appinfo.Appointment;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
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

    private RecyclerView recyclerView; //mFirestoreList
    private FirebaseFirestore fireStore;
    //he uses DatabaseReference database; i suppose that is to dblive
    //FirebaseFirestore fireStore;
    //MyAdapter myAdapter;
    //ArrayList<Appointment> list;
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
                //Appointment appointment = list.get(position);
                holder.addressTV.setText(model.getAddress() + ", " + model.getPostcode());
                holder.dateTV.setText(model.getDate());
                holder.timeTV.setText(model.getTime());
                holder.durationTV.setText(model.getDuration());
                holder.nameTV.setText(model.getName());
                holder.notesTV.setText(model.getNotes());
            }
        };

         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setAdapter(adapter);
                //view holder
    }

    //inner class
    private class AppointmentViewHolder extends RecyclerView.ViewHolder{

        TextView addressTV, dateTV, timeTV, durationTV, nameTV, notesTV;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            addressTV = itemView.findViewById(R.id.appointManageViewAddress);
            dateTV = itemView.findViewById(R.id.appointManageViewDate);
            timeTV = itemView.findViewById(R.id.appointManageViewTime);
            durationTV = itemView.findViewById(R.id.appointManageViewDuration);
            nameTV = itemView.findViewById(R.id.appointManageViewName);
            notesTV = itemView.findViewById(R.id.appointManageViewNotes);
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