package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MakeAppointment extends AppCompatActivity {

    public static final String TAG = "TAG";
    private TextView tvDate,tvTime;
    private EditText etDate,etTime,address,postcode,careDuration,toCarerNotes;
    private int timeHour, timeMinute;
    private DatePickerDialog.OnDateSetListener setDateListener;
    private TimePickerDialog.OnTimeSetListener setTimeListener;
    private String date, timeStored;
    private Button submitBtn;
    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_appointment);
        fireStore = FirebaseFirestore.getInstance();
        setupMakeAppointment();
        dateFieldSetup();
        timeFieldSetup();
        validationField();
        submitData();


    }

    private void submitData(){
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validationField()){
                    String addressSubmit = address.getText().toString().trim();
                    String postcodeSubmit = postcode.getText().toString().trim();
                    String dateSubmit = etDate.getText().toString().trim();
                    String timeStoredSubmit = etTime.getText().toString().trim().replaceAll(" ", ""); //in order to remove space between AM PM. Else quicksort wont work accurately
                    String careDurationSubmit = careDuration.getText().toString().trim();
                    String notesSubmit = toCarerNotes.getText().toString().trim();

                    geoLocation();
                    DocumentReference docRef = fireStore.collection("appointmentRequest").document();
                    Map<String, Object> appointmentRequest = new HashMap<>();
                    appointmentRequest.put("user_id",GlobalVar.current_user_id);
                    appointmentRequest.put("name",GlobalVar.current_user);
                    appointmentRequest.put("address",addressSubmit);
                    appointmentRequest.put("postcode",postcodeSubmit);
                    appointmentRequest.put("date",dateSubmit);
                    appointmentRequest.put("time",timeStoredSubmit);
                    appointmentRequest.put("duration",careDurationSubmit);
                    appointmentRequest.put("notes",notesSubmit);
                    appointmentRequest.put("status","pending");
                    docRef.set(appointmentRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println(docRef);
                            Log.d(TAG,"onSuccess: appointment is created by " + GlobalVar.current_user_id);
                            Toast.makeText(MakeAppointment.this, "Appointment has been requested successfully. Awaiting for approval by Administrator", Toast.LENGTH_SHORT).show();
                            try{
                                Toast.makeText(MakeAppointment.this, "Appointment has been requested successfully. Awaiting for approval by Administrator", Toast.LENGTH_SHORT).show();
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            startActivity(new Intent(MakeAppointment.this, MainUIPatientActivity.class));

                        }
                    });
                }
            }
        });
    }

    private void geoLocation(){
        String locationName = address.getText().toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);

            if(addressList.size()>0){ //if addressList is not empty, get the address of index 0
                Address address = addressList.get(0);
                System.out.println(address.getLatitude() + "," + address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validationField(){
        boolean result = false;
        String addressField = address.getText().toString();
        String postcodeField = postcode.getText().toString();
        String dateField = etDate.getText().toString();
        String timeStoredField = etTime.getText().toString();
        String careDurationField = careDuration.getText().toString();
        String notesField = toCarerNotes.getText().toString();

        if (addressField.isEmpty() || postcodeField.isEmpty() || dateField.isEmpty() || timeStoredField.isEmpty() || careDurationField.isEmpty() || notesField.isEmpty()) {
            Toast.makeText(this, "Please fill up all the fields before retying.",
                    Toast.LENGTH_SHORT).show(); //error message. toast lenth short = time to be displayed
            result = false;

        }
        else{
            result = true;
        }
        return result;
    }

    private void setupMakeAppointment(){
        address = findViewById(R.id.addressField);
        postcode = findViewById(R.id.postcodeField);
        //tvDate = findViewById(R.id.dateText);
        etDate = findViewById(R.id.dateField);
        //tvTime = findViewById(R.id.timeText);
        etTime = findViewById(R.id.timeField);
        careDuration = findViewById(R.id.durationField);
        toCarerNotes = findViewById(R.id.noteField);
        submitBtn = findViewById(R.id.submitButton);

    }

    private void timeFieldSetup(){
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MakeAppointment.this,
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
                                    etTime.setText(f24Hours.format(date));
                                    timeStored = f24Hours.format(date);
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
    }

    private void dateFieldSetup(){
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MakeAppointment.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,setDateListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setDateListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                date = day+"/"+month+"/"+year;
                etDate.setText(date);
            }
        };
    }


}