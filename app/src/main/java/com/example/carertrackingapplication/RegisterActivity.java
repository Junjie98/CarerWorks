package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private TextView loginLabel;
    private Button registerBtn;
    private EditText emailTxtField, fullNameTxtField, passwordTxtField, dobTxtField, genderTxtField, phoneTxtField;
    private FirebaseAuth firebaseAuth;

    //for database firestore
    private FirebaseFirestore fireStore;
//    private boolean carer = false;
//    private boolean patient = false;
//    private boolean patientFamily = false;

    private String ratingAtCreated = "0.0";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregistration);
        registerUIView();
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
                    int admintype = 0;
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
                                UserTypeActivity a = new UserTypeActivity();

                                //update the server as it differs from my json file during creation.
                                DocumentReference docRef = fireStore.collection("users").document(user_id);

                                if(a.getCarerBool()) {
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users").child("Carer").child((user_id));
                                    current_user_db.setValue(true); //save it to the database ? //CHECK RULES
                                    //DocumentReference docRef = fireStore.collection("users").document(user_id);
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
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users").child("Patient").child((user_id));
                                    current_user_db.setValue(true); //save it to the database ?
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
                                    System.out.println("I am in patient database");

                                }
                                if(a.getPatientFamilyBool()) {
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users").child("PatientFamily").child((user_id));
                                    current_user_db.setValue(true); //save it to the database ?
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email",userEmail);
                                    user.put("name",userFullName);
                                    //user.put("password",userPassword);
                                    user.put("dob",userDob);
                                    user.put("gender",userGender);
                                    user.put("phone",userPhone);
                                    user.put("user_type", patientFamilyType);
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
    }

    private boolean validateRegisterField() {
        Boolean validateResult = false;
        String emailRegister = emailTxtField.getText().toString();
        String fullNameRegister = fullNameTxtField.getText().toString();
        String passwordRegister = passwordTxtField.getText().toString();
        String dobRegister = dobTxtField.getText().toString();
        String genderRegister = genderTxtField.getText().toString();
        String phoneNumber = phoneTxtField.getText().toString();

        if (emailRegister.isEmpty() || fullNameRegister.isEmpty() || passwordRegister.isEmpty() || dobRegister.isEmpty() || genderRegister.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill up all the fields before retying.",
                    Toast.LENGTH_SHORT).show(); //error message. toast lenth short = time to be displayed
            validateResult = false;

        }


//        if(passwordRegister.length()<=9){
//            validateResult = false;
//
//        }
        else {
            validateResult = true;
        }
        return validateResult;
    }

    }