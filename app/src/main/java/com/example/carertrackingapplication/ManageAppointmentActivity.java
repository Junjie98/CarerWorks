package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ManageAppointmentActivity extends AppCompatActivity {

    private Button makeAppointmentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointment);
        setupManageAppointment();

        makeAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageAppointmentActivity.this, MakeAppointment.class));
            }
        });

    }

    private void setupManageAppointment(){
        makeAppointmentBtn = findViewById(R.id.makeAppointmentBtn);
    }
}