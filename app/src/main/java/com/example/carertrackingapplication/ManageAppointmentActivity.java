package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.carertrackingapplication.variable.GlobalVar;

public class ManageAppointmentActivity extends AppCompatActivity {

    private Button makeAppointmentBtn,viewAppointmentBtn, carerManageAppointmentReq;
    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointment);
        setupManageAppointment();

        if(GlobalVar.user_type == "carer"){
            carerManageAppointmentReq.setVisibility(View.VISIBLE);
            makeAppointmentBtn.setVisibility(View.GONE);
        }
        carerManageAppointmentReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageAppointmentActivity.this, ViewRequestAppointment.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        makeAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageAppointmentActivity.this, MakeAppointment.class));
            }
        });

        viewAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageAppointmentActivity.this, viewAppointmentAll.class));


            }
        });

    }

    private void setupManageAppointment(){
        makeAppointmentBtn = findViewById(R.id.makeAppointmentBtn);
        viewAppointmentBtn = findViewById(R.id.viewAppointmentBtn);
        carerManageAppointmentReq = findViewById(R.id.ManageAppointmentRequest);
        backBtn = findViewById(R.id.backBtn);
    }
}