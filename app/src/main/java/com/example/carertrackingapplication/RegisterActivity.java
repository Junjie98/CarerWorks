package com.example.carertrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextView loginLabel;
    private Button registerBtn;
    private EditText emailTxtField, confirmEmailTxtField, passwordTxtField, confirmPasswordTxtField;
    private FirebaseAuth firebaseAuth;
    private boolean carer = false;
    private boolean patient = false;
    private boolean patientFamily = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userregistration);
        registerUIView();
        firebaseAuth = FirebaseAuth.getInstance();
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
                validateRegisterField();

                if(validateRegisterField()){
                    String userEmail = emailTxtField.getText().toString().trim();
//                    String emailRegisterConfirmation = confirmEmailTxtField.getText().toString().trim();
                    String userPassword = passwordTxtField.getText().toString().trim();
//                    String passwordRegisterConfirmation = confirmPasswordTxtField.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration Unsuccessful.", Toast.LENGTH_SHORT).show();
                            }else{
                                //Toast.makeText(RegisterActivity.this, "Registration Unsuccessfully.", Toast.LENGTH_SHORT).show();
                                String user_id = firebaseAuth.getCurrentUser().getUid();
                                System.out.println(user_id);
                                UserTypeActivity a = new UserTypeActivity();

                                System.out.println(a.getCarerBool());
                                System.out.println(a.getPatientBool());
                                //update the server as it differs from my json file during creation.
                                if(a.getCarerBool()) {
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users").child("Carer").child((user_id));
                                    current_user_db.setValue(true); //save it to the database ?
                                    System.out.println("I am in carer database");
                                }
                                if(a.getPatientBool()) {
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users").child("Patient").child((user_id));
                                    current_user_db.setValue(true); //save it to the database ?
                                    System.out.println("I am in patient database");
                                }
                                if(a.getPatientFamilyBool()) {
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance("https://carertrackingapplication-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("Users").child("PatientFamily").child((user_id));
                                    current_user_db.setValue(true); //save it to the database ?
                                    System.out.println("I am in patientFamily database");
                                }
                            }
                        }
                    });

                    //startActivity(new Intent(RegisterActivity.this, LoginActivity.class));//TEST. DELETE LATER
                }
            }
        });
    }


    protected void registerUIView(){
        loginLabel = (TextView)findViewById(R.id.loginHereLabel); //casting it into TextView as it is in a new function.
        emailTxtField = (EditText)findViewById((R.id.registerEmailText));
        confirmEmailTxtField = (EditText)findViewById(R.id.registerEmailTextConfirmation);
        passwordTxtField = (EditText)findViewById(R.id.registerPasswordText);
        confirmPasswordTxtField= (EditText)findViewById(R.id.registerPasswordConfirmationText);
        registerBtn = (Button)findViewById(R.id.registerButton);
    }

    private boolean validateRegisterField() {
        Boolean validateResult = false;
        String emailRegister = emailTxtField.getText().toString();
        String emailRegisterConfirmation = confirmEmailTxtField.getText().toString();
        String passwordRegister = passwordTxtField.getText().toString();
        String passwordRegisterConfirmation = confirmPasswordTxtField.getText().toString();
        if (emailRegister.isEmpty() || emailRegisterConfirmation.isEmpty() || passwordRegister.isEmpty() || passwordRegisterConfirmation.isEmpty()) {
            Toast.makeText(this, "Please fill up all the fields before retying.",
                    Toast.LENGTH_SHORT).show(); //error message. toast lenth short = time to be displayed
            validateResult = false;
        } else {
            validateResult = true;
        }
        return validateResult;
    }

    /////////// GETTERS AND SETTERS//////////////

    public void setPatientBool(boolean t){
        patient = t;
    }
    public void setPatientFamilyBool(boolean t){
        patientFamily = t;
    }
    public void setCarerBool(boolean t){
        carer = t;
    }

//    //whenever the activity is called, we are going to start the listener
//    protected void onStart(){
//        super.onStart();
//        firebaseAuth.addAuthStateListener(firebaseAuthListener);
//    }
//
//    //stop when we leave this activity
//    protected void onStop() {
//        super.onStop();
//        firebaseAuth.addAuthStateListener(firebaseAuthListener);
//    }
//}


    }