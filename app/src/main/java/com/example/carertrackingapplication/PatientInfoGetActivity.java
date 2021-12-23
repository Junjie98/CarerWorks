package com.example.carertrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.carertrackingapplication.variable.GlobalVar;

public class PatientInfoGetActivity extends AppCompatActivity {

    EditText illnessConditionField, allergicIntolerancesField, dailyPreferenceField, personalDifficultiesField;
    Button submit;
    String currentID = GlobalVar.current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info_get);
        setupGetInfoPatientUI();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }


    private void setupGetInfoPatientUI(){
        illnessConditionField = findViewById(R.id.illnessGetField);
        allergicIntolerancesField = findViewById(R.id.allergicGetField);
        dailyPreferenceField = findViewById(R.id.getPreferencesField);
        personalDifficultiesField = findViewById(R.id.personalDifficultiesField);
        submit = findViewById(R.id.submitPersonalInfo);

    }
}