package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MakeAppointment extends AppCompatActivity {

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
                    DocumentReference docRef = fireStore.collection("users").document(GlobalVar.current_user_id);
                }
            }
        });
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
                                    etTime.setText(f12Hours.format(date));
                                    timeStored = f12Hours.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        },12,0,false
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