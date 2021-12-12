package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        ImageButton imgBtn = (ImageButton)findViewById(R.id.backBtnViewAppointment);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fireStore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.viewReqAppointmentRecyclerView); //point towards activity_view_request_appointment.xml recycler view id.

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
            dateTV = itemView.findViewById(R.id.HistoryViewTimeField);
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