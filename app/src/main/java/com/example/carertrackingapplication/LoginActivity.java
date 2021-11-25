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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextView signUpLabel;
    private EditText emailTxt, passwordTxt;
    private Button loginBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthenticationListener;
    private static postgresql db = new postgresql();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin); //above of logic
        loginUIView();

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthenticationListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    startActivity(new Intent(LoginActivity.this, MainUIPatientActivity.class));
                    finish();
                    return;
                }
            }
        };

        //There is absolutely no problem in having OnClickListener() in onCreate(). In fact, all the ClickListeners
        // must be initialized as soon as possible before user gets to interact with your Activity. So, onCreate is a perfectly right place to do it.

        signUpLabel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, UserTypeActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.connectDB();
                if(validateLoginField()){ //check if field is entered
                    final String email = emailTxt.getText().toString();
                    final String password = passwordTxt.getText().toString();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Registration Unsuccessful.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    //startActivity(new Intent(LoginActivity.this, MainUIPatientActivity.class));//TEST. DELETE LATER
                }

            }
        });



    }

    private void loginUIView(){ //IMPORTANT TO SETUP. ELSE THE CODE WONT RUN
        signUpLabel = (TextView)findViewById(R.id.signup_label);
        emailTxt = (EditText)findViewById(R.id.editTextEmailAddress);
        passwordTxt = (EditText)findViewById(R.id.editTextPassword);
        loginBtn = (Button) findViewById(R.id.login_btn);
    }

    private boolean validateLoginField(){
        Boolean validateResult = false;
        String emailLogin = emailTxt.getText().toString();
        String passwordLogin = passwordTxt.getText().toString();
        if(emailLogin.isEmpty() || passwordLogin.isEmpty()){
            Toast.makeText(this, "Please fill up the username and password before retying.",
                    Toast.LENGTH_SHORT).show(); //error message. toast lenth short = time to be displayed
            validateResult = false;
        }
        else {
            validateResult = true;
        }
        return validateResult;


    }
    //whenever the activity is called, we are going to start the listener
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthenticationListener);
    }

    //stop when we leave this activity
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(firebaseAuthenticationListener);
    }
}