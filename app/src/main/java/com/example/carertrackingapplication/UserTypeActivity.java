package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.carertrackingapplication.variable.GlobalVar;

public class UserTypeActivity extends AppCompatActivity {

    private Button patientUserBtn, patientFamilyBtn, carerBtn;
    private static boolean carer = false;
    private static boolean patient = false;
    private static boolean patientFamily = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        setupUserTypeUI();

        patientUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set btnValue
                patient = true;
                //reset others in-case user select between it.
                carer = false;
                patientFamily = false;
                GlobalVar.user_type = "patient";
                startActivity(new Intent(UserTypeActivity.this, RegisterActivity.class));
            }
        });

        patientFamilyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set btnValue
                patientFamily = true;
                //reset others in-case user select between it.
                carer = false;
                patient = false;
                GlobalVar.user_type = "patientFamily";
                startActivity(new Intent(UserTypeActivity.this, RegisterActivity.class));
            }
        });

        carerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set btnValue
                carer = true;
                //reset others in-case user select between it.
                patientFamily = false;
                patient = false;
                GlobalVar.user_type = "carer";
                startActivity(new Intent(UserTypeActivity.this, RegisterActivity.class));
            }
        });
    }

    private void setupUserTypeUI(){
        patientUserBtn = (Button)findViewById(R.id.type_patient_btn);
        patientFamilyBtn = (Button)findViewById(R.id.type_patient_family);
        carerBtn = (Button)findViewById(R.id.type_carer);
    }

    public boolean getCarerBool(){
        return this.carer;
    }
    public boolean getPatientBool() { return this.patient; }
    public boolean getPatientFamilyBool() { return this.patientFamily; }
}