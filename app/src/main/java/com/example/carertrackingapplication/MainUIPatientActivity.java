package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainUIPatientActivity extends AppCompatActivity {

    private ImageButton manageAppointment, mapTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui_patient);
        MainUISetup();

        mapTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUIPatientActivity.this,MapsTrackerActivity.class));
            }
        });
    }


    protected void MainUISetup(){
        manageAppointment = (ImageButton)findViewById(R.id.appointmentBtn);
        mapTracker = (ImageButton)findViewById(R.id.mapTrackerBtn);
    }
}