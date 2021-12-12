package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class settingActivity extends AppCompatActivity {

    Switch drivingModeSwitch;
    Button changeContactNum;
    ImageButton backBtn;
    TextView travelModeLabel;
    //constants
    public static final String SHARED_PREFERENCES = "sharedPrefs";
    //shared preferences for the text
    //public static final String TEXT = "text";
    public static final String SWITCH1 = "switch1";
    public static boolean switchOnOff;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setupSettingUI();

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

//    private void changePassword(){
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        final String email = currentUser.getEmail();
//        AlertDialog.Builder changePassBuilder = new AlertDialog.Builder(settingActivity.this);
//        changePassBuilder.setTitle("Change Password");
//        changePassBuilder.setMessage("Enter your old password");
//        final EditText oldPassField = new EditText(this);
//        changePassBuilder.setView(oldPassField);
//        final String oldPassw = oldPassField.getText().toString();
//        changePassBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        changePassBuilder.setPositiveButton("Validate User", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassw);
//
//                        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    AlertDialog.Builder changePassBuilder = new AlertDialog.Builder(settingActivity.this);
//                                    changePassBuilder.setTitle("Change Password");
//                                    changePassBuilder.setMessage("Enter your new password");
//                                    final EditText oldPassField = new EditText(this);
//                                    currentUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if(!task.isSuccessful()){
//                                                Toast.makeText(settingActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
//                                            }else {
//                                                Toast.makeText(settingActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                                }else {
//                                    Toast.makeText(settingActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
//                                .setCancelable(false);
//                        AlertDialog dialog = changePassBuilder.create();
//                        dialog.setOnShowListener(dialog1 -> {
//                            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//                                    .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
//                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
//                                    .setTextColor(getResources().getColor(android.R.color.holo_blue_light));
//                        });
//
//                        dialog.show();
//                    }
//                });
//
//    }
}


