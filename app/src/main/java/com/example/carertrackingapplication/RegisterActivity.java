package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private TextView loginLabel;
    private Button registerBtn;
    private EditText emailTxtField,patientFamilyField ,fullNameTxtField, passwordTxtField, dobTxtField, genderTxtField, phoneTxtField;
    private FirebaseAuth firebaseAuth;
    EditText illnessConditionField, allergicIntolerancesField, dailyPreferenceField, personalDifficultiesField;
    private DatePickerDialog.OnDateSetListener setDateListener;
    private String date;
    boolean existInDatabase;
    //for database firestore
    private FirebaseFirestore fireStore;
    //default value for patient medical record.
    String condition = "none";
    String allergic = "none";
    String dailyPref = "none";
    String disability = "none";
    private Double ratingAtCreated = 0.0;
    private String passwordPolicyRexpression = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#£!$%]).{9,25}$";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregistration);
        registerUIView();
        dateFieldSetup();
        UserTypeActivity a = new UserTypeActivity();
        checkUserType();

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        //The onCreate() method of Application class implementation is the entry point of your Android application where you get control over the logic part.

        loginLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validateRegisterField();

                if(validateRegisterField()){
                    String userEmail = emailTxtField.getText().toString().trim();
                    String userFullName = fullNameTxtField.getText().toString().trim();
                    String userPassword = passwordTxtField.getText().toString().trim();
                    String userDob = dobTxtField.getText().toString().trim();
                    String userGender = genderTxtField.getText().toString().trim();
                    String userPhone = phoneTxtField.getText().toString().trim();
                    String famPatientID = patientFamilyField.getText().toString().trim();


                    //set user details into it
                    condition = illnessConditionField.getText().toString().trim();
                    allergic = allergicIntolerancesField.getText().toString().trim();
                    dailyPref = dailyPreferenceField.getText().toString().trim();
                    disability = personalDifficultiesField.getText().toString().trim();


                   // int admintype = 0;
                    int carertype = 1;
                    int patienttype = 2;
                    int patientFamilyType =3;

                    firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration Unsuccessful.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(RegisterActivity.this, "Registration Successfully.", Toast.LENGTH_SHORT).show();
                                String user_id = firebaseAuth.getCurrentUser().getUid();


                                //update the server as it differs from my json file during creation.
                                DocumentReference docRef = fireStore.collection("users").document(user_id);


                                if(a.getCarerBool()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email",userEmail);
                                    user.put("name",userFullName);
                                   // user.put("password",userPassword);
                                    user.put("dob",userDob);
                                    user.put("gender",userGender);
                                    user.put("phone",userPhone);
                                    user.put("rating",ratingAtCreated);
                                    user.put("user_type", carertype);
                                    docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG,"onSuccess: profile is created for " + user_id);
                                            try{
                                                Thread.sleep(1000);
                                                //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    System.out.println("I am in carer database"); //role 0 admin, role 1 carer, role 2 patient, role 3 patient fam
                                }
                                if(a.getPatientBool()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email",userEmail);
                                    user.put("name",userFullName);
                                    //user.put("password",userPassword);
                                    user.put("dob",userDob);
                                    user.put("gender",userGender);
                                    user.put("phone",userPhone);
                                    user.put("user_type", patienttype);
                                    docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG,"onSuccess: profile is created for " + user_id);
                                            try{
                                                Thread.sleep(1000);
                                                //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                    //setup doc ref for medical record database.
                                    DocumentReference dofRefPatientInfo = fireStore.collection("medical_record").document(user_id);
                                    Map<String, Object> userMedicalRecord = new HashMap<>();
                                    userMedicalRecord.put("condition",condition);
                                    userMedicalRecord.put("allergic",allergic);
                                    userMedicalRecord.put("daily_pref",dailyPref);
                                    userMedicalRecord.put("disability",disability);
                                    dofRefPatientInfo.set(userMedicalRecord).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG,"onSuccess: medical record saved for  " + user_id);
                                            try{
                                                Thread.sleep(1000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    System.out.println("I am in patient database");

                                }
                                if(a.getPatientFamilyBool()) {
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email",userEmail);
                                    user.put("name",userFullName);
                                    //user.put("password",userPassword);
                                    user.put("dob",userDob);
                                    user.put("gender",userGender);
                                    user.put("phone",userPhone);
                                    user.put("user_type", patientFamilyType);
                                    user.put("patient_family_id", famPatientID);
                                    docRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG,"onSuccess: profile is created for " + user_id);
                                            try{
                                                Thread.sleep(1000);
                                                //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    System.out.println("I am in patientFamily database");
                                }
                            }
                        }
                    });

                }
            }
        });
    }


    private void checkUserType(){

        if(GlobalVar.user_type == "patientFamily"){
            patientFamilyField.setVisibility(View.VISIBLE);
        }else{
            patientFamilyField.setVisibility(View.GONE);
        }

        if(GlobalVar.user_type == "patient"){
            findViewById(R.id.helloExplain).setVisibility(View.VISIBLE);
            findViewById(R.id.conditionLabel).setVisibility(View.VISIBLE);
            findViewById(R.id.illnessGetField).setVisibility(View.VISIBLE);
            findViewById(R.id.allergicLabel).setVisibility(View.VISIBLE);
            findViewById(R.id.allergicGetField).setVisibility(View.VISIBLE);
            findViewById(R.id.dailyPrefLabel).setVisibility(View.VISIBLE);
            findViewById(R.id.getPreferencesField).setVisibility(View.VISIBLE);
            findViewById(R.id.personalDiffLabel).setVisibility(View.VISIBLE);
            findViewById(R.id.personalDifficultiesField).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.helloExplain).setVisibility(View.GONE);
            findViewById(R.id.conditionLabel).setVisibility(View.GONE);
            findViewById(R.id.illnessGetField).setVisibility(View.GONE);
            findViewById(R.id.allergicLabel).setVisibility(View.GONE);
            findViewById(R.id.allergicGetField).setVisibility(View.GONE);
            findViewById(R.id.dailyPrefLabel).setVisibility(View.GONE);
            findViewById(R.id.getPreferencesField).setVisibility(View.GONE);
            findViewById(R.id.personalDiffLabel).setVisibility(View.GONE);
            findViewById(R.id.personalDifficultiesField).setVisibility(View.GONE);
        }
    }
    protected void registerUIView(){
        loginLabel = (TextView)findViewById(R.id.loginHereLabel); //casting it into TextView as it is in a new function.
        emailTxtField = (EditText)findViewById((R.id.registerEmailText));
        fullNameTxtField = (EditText)findViewById((R.id.registerFullNameTxt));
        dobTxtField = (EditText)findViewById((R.id.registerDob));
        genderTxtField = (EditText)findViewById((R.id.registerGender));
        phoneTxtField = (EditText)findViewById((R.id.registerPhone));
        //confirmEmailTxtField = (EditText)findViewById(R.id.fullNameTxt);
        passwordTxtField = (EditText)findViewById(R.id.registerPasswordText);
        //confirmPasswordTxtField= (EditText)findViewById(R.id.registerPasswordConfirmationText);
        registerBtn = (Button)findViewById(R.id.registerButton);

        patientFamilyField = findViewById(R.id.familyPatientID);
        illnessConditionField = findViewById(R.id.illnessGetField);
        allergicIntolerancesField = findViewById(R.id.allergicGetField);
        dailyPreferenceField = findViewById(R.id.getPreferencesField);
        personalDifficultiesField = findViewById(R.id.personalDifficultiesField);
    }

    private void dateFieldSetup(){
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        dobTxtField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,setDateListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setDateListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                date = dayOfMonth+"/"+month+"/"+year;
                dobTxtField.setText(date);
            }
        };
    }

    private boolean validatePasswordPolicy(String pass, String policy){
        Pattern pattern = Pattern.compile(policy);
        Matcher matcher = pattern.matcher(pass);
        return matcher.matches();
    }

    private boolean validateRegisterField() {
        Boolean validateResult = false;
        String emailRegister = emailTxtField.getText().toString();
        String fullNameRegister = fullNameTxtField.getText().toString();
        String passwordRegister = passwordTxtField.getText().toString();
        String dobRegister = dobTxtField.getText().toString();
        String genderRegister = genderTxtField.getText().toString();
        String phoneNumber = phoneTxtField.getText().toString();
        String checkPatientFamilyField = patientFamilyField.getText().toString();

        if (emailRegister.isEmpty() || fullNameRegister.isEmpty() || passwordRegister.isEmpty() || dobRegister.isEmpty() || genderRegister.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill up all the fields before retying.",
                    Toast.LENGTH_SHORT).show(); //error message. toast lenth short = time to be displayed
            validateResult = false;
        }else{
            //check this first then check password policy below
            if(GlobalVar.user_type == "patientFamily"){

                if(checkPatientFamilyField.isEmpty()){
                    Toast.makeText(this, "Please fill up patientIDField.",
                            Toast.LENGTH_SHORT).show();
                    validateResult = false;
                }else{
                    validateResult = true;
                }

            }

            if(validatePasswordPolicy(passwordRegister,passwordPolicyRexpression)){
                validateResult = true;
            }else{
                Toast.makeText(this, "Please follow the password policy.",
                        Toast.LENGTH_SHORT).show();
            }
        }

        if(GlobalVar.user_type == "patientFamily"){

            if(checkPatientFamilyField.isEmpty()){
                Toast.makeText(this, "Please fill up patientIDField.",
                        Toast.LENGTH_SHORT).show();
                validateResult = false;
            }else{
                validateResult = true;
            }

        }
//        else {
//            validateResult = true;
//        }
        return validateResult;
    }

    }