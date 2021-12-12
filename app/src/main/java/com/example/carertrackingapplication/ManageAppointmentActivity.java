package com.example.carertrackingapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.firebase.auth.FirebaseAuth;

public class ManageAppointmentActivity extends AppCompatActivity {

    private Button makeAppointmentBtn,viewAppointmentBtn, carerManageAppointmentReq;
    private ImageButton backBtn;
    private TextView txt_ratingScore2,txt_username2, logout;

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


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageAppointmentActivity.this);
                builder.setTitle("Sign Out")
                        .setMessage("Do you want to sign out?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(ManageAppointmentActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
        txt_ratingScore2 = findViewById(R.id.txt_ratingScore2);
        txt_username2 = findViewById(R.id.txt_username2);
        logout = findViewById(R.id.logoutBtn2);

        backBtn = findViewById(R.id.backBtn);
        if(GlobalVar.user_type == "carer") {
            txt_username2.setText(GlobalVar.current_user);
            txt_ratingScore2.setText("Rating: " + GlobalVar.user_rating);
        }else{
            txt_username2.setText(GlobalVar.current_user);

        }
    }
}