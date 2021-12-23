package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carertrackingapplication.appinfo.VisitLog;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ViewHistoryLog extends AppCompatActivity {

    public static final String TAG = "TAG";
    private RecyclerView recyclerView;
    private FirebaseFirestore fireStore;
    private FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history_log);


        ImageButton imgBtn = (ImageButton) findViewById(R.id.historyBackBtn);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fireStore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.viewHistoryLogRecyclerView);

        // to create firebaseFirestore adapter we need two things. 1: Query, 2:recycler options.

        //Query
        Query query = fireStore.collection("appointmentLog").orderBy("date", Query.Direction.ASCENDING);


        //RecyclerOptions
        FirestoreRecyclerOptions<VisitLog> options = new FirestoreRecyclerOptions.Builder<VisitLog>()
                .setQuery(query, VisitLog.class)
                .build();

        //now we are ready create recycler adapter.
        adapter = new FirestoreRecyclerAdapter<VisitLog, HistoryViewHolder>(options) {

            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.historylog, parent, false);
                return new HistoryViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull VisitLog model) {

                if (model.getUser_id().equals(GlobalVar.current_user_id) || model.getCarer_id().equals(GlobalVar.current_user_id) ||model.getUser_id().equals(GlobalVar.family_id) ) {

                        holder.cardView.setVisibility(View.VISIBLE);
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        holder.historyLogID = snapshot.getId();

                        holder.date = model.getDate();
                        holder.status = model.getStatus();
                        holder.time = model.getTime();
                        holder.end_time = model.getEnd_time();
                        holder.carer = model.getCarer_name();
                        holder.carer_id = model.getCarer_id();
                        holder.name = model.getName();
                        holder.carer_notes = model.getCarerNotes();
                        holder.userid = model.getUser_id();
                        holder.rated = model.getRated();

                        holder.dateTV.setText(holder.date);
                        holder.statusTV.setText(holder.status);
                        holder.timeTV.setText(holder.time);
                        holder.endTimeTV.setText(holder.end_time);
                        holder.carerTV.setText(holder.carer);
                        holder.nameTV.setText(holder.name);
                        holder.notesTV.setText(holder.carer_notes);

                        if(holder.rated.equals("false") && GlobalVar.user_type == "patient"){
                            holder.rateBtn.setVisibility(View.VISIBLE);
                        }
                        if(holder.rated.equals("false") && GlobalVar.user_type == "patientFamily"){
                            holder.rateBtn.setVisibility(View.VISIBLE);
                        }
                        return;


                } else {
                    System.out.println("Im here in else lol");
                    holder.cardView.setVisibility(View.GONE);

                }
            }
        };
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //view holder
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder{

        TextView dateTV, timeTV, durationTV, nameTV, notesTV, statusTV,endTimeTV,carerTV;
        Button rateBtn,callBtn;

        int position;
        //String userid;
        String address, date, duration, name, notes, postcode,status,time,userid, end_time, carer, carer_id,carer_notes,rated;
        String historyLogID;
        CardView cardView;
        double carerRating;
        String carerPhoneNum, patientPhoneNum;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.historyLogViewCardView);//


//            addressTV = itemView.findViewById(R.id.appointManageViewAddress);
            dateTV = itemView.findViewById(R.id.HistoryViewTimeField);
            statusTV = itemView.findViewById(R.id.statusTextHistory);
            timeTV = itemView.findViewById(R.id.HistoryViewTimeField);
            endTimeTV = itemView.findViewById(R.id.endTimeFieldHistory);
            //durationTV = itemView.findViewById(R.id.appointManageViewDuration);
            nameTV = itemView.findViewById(R.id.PatientNameFieldHistory);
            notesTV = itemView.findViewById(R.id.notesFieldHistory);
            carerTV = itemView.findViewById(R.id.carerAssignedFieldHistory);
            rateBtn = itemView.findViewById(R.id.completeBtn);
            callBtn = itemView.findViewById(R.id.contactBtn);

            Intent callIntent = new Intent(Intent.ACTION_CALL);

            callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalVar.user_type == "carer") {

                    DocumentReference docRef = fireStore.collection("users").document(userid);
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
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + patientPhoneNum));

                    if (ActivityCompat.checkSelfPermission(ViewHistoryLog.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        System.out.println("NOPE");
                        return;
                    }
                    startActivity(callIntent);
                }else{ //else it will be patient family and patient. Which they will only want to contact carer if required.
                    DocumentReference docRef = fireStore.collection("users").document(carer_id);
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

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + carerPhoneNum));

                if (ActivityCompat.checkSelfPermission(ViewHistoryLog.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("NOPE");
                    return;
                }
                startActivity(callIntent);
            }
        });

            rateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertRatingDialog = new AlertDialog.Builder(ViewHistoryLog.this);
                    alertRatingDialog.setTitle("Add rating to carer");
                    final RatingBar ratingBar = new RatingBar(ViewHistoryLog.this);
                    LinearLayout layout = new LinearLayout(ViewHistoryLog.this);

                    LinearLayout.LayoutParams layParam = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    ratingBar.setLayoutParams(layParam);
                    ratingBar.setNumStars(5);
                    ratingBar.setStepSize(1);
                    ratingBar.setRating(0);
                    layout.addView(ratingBar);
                    alertRatingDialog.setView(layout);

                    //get rating score to calculate average. Instead of storing and getting all 1-5 start and * votes and / sum, ill just do ("carerRating" + "userRating") /2
                    DocumentReference docRef = fireStore.collection("users").document(carer_id);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String strRating = document.get("rating").toString();
                                    System.out.println(Double.parseDouble(strRating));
                                    carerRating = Double.parseDouble(strRating);

                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    alertRatingDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertRatingDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ratingBar = 0.0;
                            if(ratingBar.getRating() != 0.0){
                            System.out.println(ratingBar.getRating());
                            double userRating = ratingBar.getRating();
                            double finalRating = (userRating + carerRating) / 2;
                            String submitFinalRating = Double.toString(finalRating);

                            //add rating to carer database
                            Map<String, Object> updateCarerRating = new HashMap<>();
                            updateCarerRating.put("rating", finalRating);

                            //change visit has been rated.
                            Map<String, Object> updateLogHasBeenRated = new HashMap<>();
                            updateLogHasBeenRated.put("rated", "true");

                            fireStore.collection("users").document(carer_id)
                                    .update(updateCarerRating);

                            fireStore.collection("appointmentLog").document(historyLogID)
                                    .update(updateLogHasBeenRated);


                            }else {
                                System.out.println("Stars are not filled");
                                Toast.makeText(ViewHistoryLog.this, "Please fill up all the fields before retying.",
                                        Toast.LENGTH_SHORT).show();
                            }
//
                        }
                    })
                            .setCancelable(false);
                    AlertDialog dialog = alertRatingDialog.create();
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