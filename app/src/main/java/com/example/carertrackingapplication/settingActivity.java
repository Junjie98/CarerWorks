package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class settingActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    Switch drivingModeSwitch;
    Button changeContactNum,updateMedicalRecord;
    ImageButton backBtn;
    TextView travelModeLabel;
    //constants
    public static final String SHARED_PREFERENCES = "sharedPrefs";
    //shared preferences for the text
    //public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";
    public static boolean switchOnOff;
    private FirebaseUser currentUser;
    private FirebaseFirestore fireStore;
    private String condition,allergic,daily_pref,disability;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setupSettingUI();
        checkPatientType(); //if patient, take value from database
        if(GlobalVar.user_type != "carer"){
            drivingModeSwitch.setVisibility(View.GONE);
            travelModeLabel.setVisibility(View.GONE);

        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder updateRecord = new AlertDialog.Builder(settingActivity.this);
                updateRecord.setTitle("Update Medical Record");
                updateRecord.setMessage("\n");
                LinearLayout layout = new LinearLayout(settingActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layParam = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                LinearLayout.LayoutParams layParamLabel = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layParam.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin); //take value from dimen
                layParam.topMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                layParam.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                layParamLabel.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_marginlabel);

                final TextView conditionLabel = new TextView(settingActivity.this);
                final EditText conditionField = new EditText(settingActivity.this);
                conditionLabel.setText("Any Condition, Illness or Disability:");
                conditionField.setText(condition);
                final TextView allergicLabel = new TextView(settingActivity.this);
                final EditText allergicField = new EditText(settingActivity.this);
                allergicLabel.setText("Any allergic or intolerant:");
                allergicField.setText(allergic);
                final TextView dailyPrefLabel = new TextView(settingActivity.this);
                final EditText dailyPrefField = new EditText(settingActivity.this);
                dailyPrefLabel.setText("Daily Preferences:");
                dailyPrefField.setText(daily_pref);
                final TextView disabilityLabel = new TextView(settingActivity.this);
                final EditText disabilityField = new EditText(settingActivity.this);
                disabilityLabel.setText("Any personal difficulties / disability:");
                disabilityField.setText(disability);
                //set the margin configured earlier
                conditionField.setLayoutParams(layParam);
                allergicField.setLayoutParams(layParam);
                dailyPrefField.setLayoutParams(layParam);
                disabilityField.setLayoutParams(layParam);

                conditionLabel.setLayoutParams(layParamLabel);
                allergicLabel.setLayoutParams(layParamLabel);
                dailyPrefLabel.setLayoutParams(layParamLabel);
                disabilityLabel.setLayoutParams(layParamLabel);

                layout.addView(conditionLabel);
                layout.addView(conditionField);
                layout.addView(allergicLabel);
                layout.addView(allergicField);
                layout.addView(dailyPrefLabel);
                layout.addView(dailyPrefField);
                layout.addView(disabilityLabel);
                layout.addView(disabilityField);
                updateRecord.setView(layout);

                updateRecord.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                updateRecord.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        String newPhone =  editPhoneNum.getText().toString();
                        //set default value;
                        String newCondition = "none";
                        String newAllergic = "none";
                        String newDailyPref = "none";
                        String newDisability = "none";
                        ////

                        newCondition = conditionField.getText().toString();
                        newAllergic = allergicField.getText().toString();
                        newDailyPref = dailyPrefField.getText().toString();
                        newDisability = disabilityField.getText().toString();

                        fireStore = FirebaseFirestore.getInstance();
                        Map<String, Object> newUpdate = new HashMap<>();
                        newUpdate.put("allergic", newAllergic);
                        newUpdate.put("condition", newCondition);
                        newUpdate.put("daily_pref", newDailyPref);
                        newUpdate.put("disability", newDisability);
                        fireStore.collection("medical_record").document(GlobalVar.current_user_id).update(newUpdate);
                        Toast.makeText(settingActivity.this, "Medical Record has been updated", Toast.LENGTH_SHORT).show();

                    }
                }).setCancelable(false);
                AlertDialog dialog = updateRecord.create();
                dialog.setOnShowListener(dialog1 -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                });
                dialog.show();


            }
        });

        changeContactNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder changeNumberBuilder = new AlertDialog.Builder(settingActivity.this);
                changeNumberBuilder.setTitle("Change Contact Number");
                final EditText editPhoneNum = new EditText(settingActivity.this);
                changeNumberBuilder.setMessage("Please enter your new contact number");
                changeNumberBuilder.setView(editPhoneNum);
                changeNumberBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                changeNumberBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPhone =  editPhoneNum.getText().toString();
                        if(newPhone.length() != 11 || !newPhone.matches("^[0-9]*$")){ //check if num only
                            Toast.makeText(settingActivity.this, "INVALID PHONE NUMBER. PLEASE ENTER A UK PHONE EG:07777777777", Toast.LENGTH_SHORT).show();
                        }else {
                            FirebaseFirestore fireStore;
                            fireStore = FirebaseFirestore.getInstance();
                            Map<String, Object> newUpdate = new HashMap<>();
                            newUpdate.put("phone", newPhone);
                            fireStore.collection("users").document(GlobalVar.current_user_id).update(newUpdate);
                            Toast.makeText(settingActivity.this, "Phone Number Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setCancelable(false);
                        AlertDialog dialog = changeNumberBuilder.create();
                        dialog.setOnShowListener(dialog1 -> {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                    .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                    .setTextColor(getResources().getColor(android.R.color.holo_blue_light));
                        });
                        dialog.show();
            }
        });

        drivingModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SWITCH1, drivingModeSwitch.isChecked());
                editor.apply();
                Toast.makeText(settingActivity.this, "Data saved!", Toast.LENGTH_SHORT).show();
            }
        });
        loadDataLocal();
        updateViews();
    }

    private void setupSettingUI(){
        drivingModeSwitch = findViewById(R.id.travelModeSwitch);
        changeContactNum = findViewById(R.id.updateContact);
        backBtn = findViewById(R.id.imageBackBtn);
        travelModeLabel = findViewById(R.id.travelModeLabel);
        updateMedicalRecord = findViewById(R.id.updateMedicalRecordBtn);

    }

    private void saveDataLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //this way we save it in TEXT preference.
        //editor.putString(TEXT, textView.getText().toString);
        editor.putBoolean(SWITCH1, drivingModeSwitch.isChecked());

        //in order to let editor to save our variable, we need to apply
        editor.apply();
        Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();
    }

    private void loadDataLocal(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        //(whatwewanttoget,default value)
//        text = sharedPreferences.getString(TEXT,"");
        switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
        System.out.println(switchOnOff + " this is the result");
        if(switchOnOff) {
            GlobalVar.travelMode = "driving";
        }else{
            GlobalVar.travelMode = "walking";
        }
    }

    private void updateViews(){
        //.setText(text);
        drivingModeSwitch.setChecked(switchOnOff);
    }
    private void checkPatientType(){
        if(GlobalVar.user_type == "patient"){
            findViewById(R.id.updateMedicalRecordBtn).setVisibility(View.VISIBLE);
            fireStore = FirebaseFirestore.getInstance();
            //setup the variable for dialogAlert.Builder later.
            DocumentReference medRef = fireStore.collection("medical_record").document(GlobalVar.current_user_id);
            medRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            condition = document.get("condition").toString();
                            allergic = document.get("allergic").toString();
                            daily_pref = document.get("daily_pref").toString();
                            disability = document.get("disability").toString();
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }else{
            findViewById(R.id.updateMedicalRecordBtn).setVisibility(View.GONE);
        }
    }

}


