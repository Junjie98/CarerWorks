package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.carertrackingapplication.appinfo.Appointment;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class viewAppointmentAll extends AppCompatActivity {

//    boolean assigned = false;
    public static final String TAG = "TAG";
    private RecyclerView recyclerView; //mFirestoreList
    private FirebaseFirestore fireStore;
    private int timeHour, timeMinute;
    private String timeStore;
    private String timeStoredReschedule, dateStoredReschedule,addressStoredReschedule, postcodeStoredReschedule, durationStoredReschedule;
    private DatePickerDialog.OnDateSetListener setDateListener;

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
                    holder.completeBtn.setVisibility(View.VISIBLE);
                    if (model.getStatus().equals("Assigned") && model.getCarer_id().equals(GlobalVar.current_user_id)) { //hide those that has already been assigned.
                        // assigned = true;
                        System.out.println("Validate the ID HERE " + model.getCarer_id() + " ACTUAL ID =" + GlobalVar.current_user_id);
                        holder.cardView.setVisibility(View.VISIBLE);
                        //notifyDataSetChanged();
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAbsoluteAdapterPosition());
                        holder.appointmentID = snapshot.getId();
                        System.out.println("HELLO JIALINGYI " + holder.appointmentID + " THIS IS THE POSITION " );

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

                        //allow changes to date and time if not assigned
                        if(!holder.status.equals("Assigned")){
                            holder.rescheduleBtn.setVisibility(View.VISIBLE);
                        }
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
        Button rescheduleBtn,routeBtn, completeBtn;

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
            durationTV = itemView.findViewById(R.id.durationAreaPatientUi);
            nameCarerTV = itemView.findViewById(R.id.carerAssignedField);
            notesTV = itemView.findViewById(R.id.notesField);
            namePatient = itemView.findViewById(R.id.PatientNameFieldpatientui);
            statusTV = itemView.findViewById(R.id.statusTextpatientui);
            routeBtn = itemView.findViewById(R.id.routeBtn);
            completeBtn = itemView.findViewById(R.id.completeAppBtn);
            rescheduleBtn = itemView.findViewById(R.id.rescheduleAppBtn);


                rescheduleBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder rescheduleBuilder = new AlertDialog.Builder(viewAppointmentAll.this);
                        rescheduleBuilder.setTitle("Reschedule");
                        LinearLayout layout = new LinearLayout(viewAppointmentAll.this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        /**time and date here**/
                        final EditText editTime = new EditText(viewAppointmentAll.this);
                        editTime.setText(time);
                        final EditText editDate = new EditText(viewAppointmentAll.this);
                        editDate.setText(date);
                        final TextView getTimeLabel = new TextView(viewAppointmentAll.this);
                        getTimeLabel.setText("Reschedule Time:");
                        final TextView getDateLabel = new TextView(viewAppointmentAll.this);
                        getDateLabel.setText("Reschedule Date:");

                        /**address postcode and duration here**/
                        final EditText editAddress = new EditText(viewAppointmentAll.this);
                        editAddress.setText(address);
                        final EditText editPostcode = new EditText(viewAppointmentAll.this);
                        editPostcode.setText(postcode);
                        final EditText editDuration = new EditText(viewAppointmentAll.this);
                        editDuration.setText(duration);
                        final TextView getAddressLabel = new TextView(viewAppointmentAll.this);
                        getAddressLabel.setText("Change Address:");
                        final TextView getPostcodeLabel = new TextView(viewAppointmentAll.this);
                        getPostcodeLabel.setText("Change Postcode:");
                        final TextView getDuration = new TextView(viewAppointmentAll.this);
                        getDuration.setText("Change Duration(Hr):");


                        rescheduleBuilder.setMessage("Reschedule time and date. Leave it as it is if unchanged.");

                        //add everything in layout before prompting dialog.
                        layout.addView(getTimeLabel);
                        layout.addView(editTime);
                        layout.addView(getDateLabel);
                        layout.addView(editDate);

                        layout.addView(getAddressLabel);
                        layout.addView(editAddress);
                        layout.addView(getPostcodeLabel);
                        layout.addView(editPostcode);
                        layout.addView(getDuration);
                        layout.addView(editDuration);

                        rescheduleBuilder.setView(layout);

                        editTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TimePickerDialog timePickerDialog = new TimePickerDialog(viewAppointmentAll.this,
                                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                        new TimePickerDialog.OnTimeSetListener(){

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                timeHour = hourOfDay;
                                                timeMinute = minute;

                                                String time = timeHour + ":" + timeMinute;

                                                //initialise 24h format
                                                SimpleDateFormat f24Hours = new SimpleDateFormat(
                                                        "HH:mm"
                                                );
                                                try {
                                                    Date date = f24Hours.parse(time);
                                                    //initialise 12h format
                                                    SimpleDateFormat f12Hours = new SimpleDateFormat(
                                                            "hh:mm aa"
                                                    );
                                                    //Set selected time on editView
                                                    editTime.setText(f24Hours.format(date));
                                                    timeStore = f24Hours.format(date);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },24,0,true
                                );
                                //set transparent background here
                                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                //display previous selected time
                                timePickerDialog.updateTime(timeHour,timeMinute);
                                //show
                                timePickerDialog.show();
                            }
                        });

                        timeStoredReschedule = editTime.getText().toString().trim().replaceAll(" ", "");

                        //prompt for date
                        Calendar calendar = Calendar.getInstance();
                        final int day = calendar.get(Calendar.DAY_OF_MONTH);
                        final int month = calendar.get(Calendar.MONTH);
                        final int year = calendar.get(Calendar.YEAR);

                        editDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatePickerDialog datePickerDialog = new DatePickerDialog(
                                        viewAppointmentAll.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,setDateListener,year,month,day);
                                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                datePickerDialog.show();
                            }
                        });

                        setDateListener = new DatePickerDialog.OnDateSetListener(){

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month = month+1;
                                date = day+"/"+month+"/"+year;
                                if(!date.equals(null)){ //if user has entered new date, get it and store.
                                editDate.setText(date);
                                dateStoredReschedule = editDate.getText().toString().trim();
                                }
                            }


                        };
                        //else, get default value set during initialised.
                        dateStoredReschedule = editDate.getText().toString().trim();
                        ///
                        rescheduleBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        rescheduleBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addressStoredReschedule = editAddress.getText().toString();
                                postcodeStoredReschedule = editPostcode.getText().toString();
                                durationStoredReschedule = editDuration.getText().toString();
                                if(!addressStoredReschedule.isEmpty() || !postcodeStoredReschedule.isEmpty() || !durationStoredReschedule.isEmpty() || !dateStoredReschedule.isEmpty() || !timeStoredReschedule.isEmpty()) {
                                    System.out.println("new Time: " + timeStoredReschedule + " new Date: " + dateStoredReschedule);
                                    Map<String, Object> appointmentRescheduleUpdate = new HashMap<>();
                                    appointmentRescheduleUpdate.put("date", dateStoredReschedule); //carer id.
                                    appointmentRescheduleUpdate.put("time", timeStoredReschedule); //carer name
                                    appointmentRescheduleUpdate.put("address", addressStoredReschedule);
                                    appointmentRescheduleUpdate.put("postcode", addressStoredReschedule);
                                    appointmentRescheduleUpdate.put("duration", durationStoredReschedule);
                                    fireStore.collection("appointmentRequest").document(appointmentID)
                                            .update(appointmentRescheduleUpdate);
                                }else {
                                    Toast.makeText(viewAppointmentAll.this, "Please fill up all the fields before retying.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                                .setCancelable(false);
                        AlertDialog dialog = rescheduleBuilder.create();
                        dialog.setOnShowListener(dialog1 -> {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                    .setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                        });

                        dialog.show();



                    }
                });


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

                completeBtn.setOnClickListener(new View.OnClickListener() {
                    Date getDate = new Date();
                    DateFormat dateFormatting = new SimpleDateFormat("HH:mm:ss");
                    String currentTime = dateFormatting.format(getDate);

                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(viewAppointmentAll.this);
                        builder.setTitle("Finish visit trip")
                                .setMessage("Leave a notes and Complete Trip");
                        final EditText carerInput = new EditText(viewAppointmentAll.this);
                        builder.setView(carerInput);

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.setPositiveButton("Submit and Complete.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.out.println(address+date+duration+name+notes+postcode+status+time+userid+carerName+carerID);
                                        DocumentReference docRefLog = fireStore.collection("appointmentLog").document(appointmentID);
                                        Map<String, Object> appointmentLog = new HashMap<>();
                                        appointmentLog.put("user_id",userid); //user = patient.
                                        appointmentLog.put("name",name);
                                        appointmentLog.put("carer_name",carerName);
                                        appointmentLog.put("carer_id",carerID);
                                        appointmentLog.put("address",address);
                                        appointmentLog.put("postcode",postcode);
                                        appointmentLog.put("date",date);
                                        appointmentLog.put("time",time);
                                        appointmentLog.put("end_time", currentTime);
                                        appointmentLog.put("duration",duration);
                                        appointmentLog.put("notes",notes);
                                        appointmentLog.put("status","Completed!");
                                        appointmentLog.put("carerNotes",carerInput.getText().toString());
                                        docRefLog.set(appointmentLog).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                System.out.println(docRefLog);
                                                Log.d(TAG,"onSuccess: appointment is created by " + GlobalVar.current_user_id);
                                                Toast.makeText(viewAppointmentAll.this, "Trip + " + appointmentID + " has been completed!", Toast.LENGTH_SHORT).show();
                                                try{

                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                //startActivity(new Intent(viewAppointmentAll.this, MainUIPatientActivity.class));

                                            }
                                        });

                                        //after copied to log database, delete it from appointmentView
                                        fireStore.collection("appointmentRequest").document(appointmentID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                        finish();
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        //startActivity(intent);
                                        //finish();
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
                        ////////////////////////////
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
