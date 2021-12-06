package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.carertrackingapplication.variable.GlobalVar;

public class ManageAppointmentActivity extends AppCompatActivity {

    private Button makeAppointmentBtn,viewAppointmentBtn;

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

        viewAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("HERE I AM YOOOHOOOOO " + GlobalVar.user_type);
                if(GlobalVar.user_type == "patient") {
                    startActivity(new Intent(ManageAppointmentActivity.this, ViewAppointmentActivity.class));
                }
                if(GlobalVar.user_type == "carer"){
                    startActivity(new Intent(ManageAppointmentActivity.this, ViewRequestAppointment.class));
                }
            }
        });

    }

    private void setupManageAppointment(){
        makeAppointmentBtn = findViewById(R.id.makeAppointmentBtn);
        viewAppointmentBtn = findViewById(R.id.viewAppointmentBtn);
    }
}