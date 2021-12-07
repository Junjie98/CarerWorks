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

import com.example.carertrackingapplication.appinfo.CarerInfo;
import com.example.carertrackingapplication.variable.GlobalVar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private TextView signUpLabel;
    private EditText emailTxt, passwordTxt;
    private Button loginBtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthenticationListener;
    private FirebaseFirestore fireStore;
    private static boolean carer;
    private static boolean patient;

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogin); //above of logic
        loginUIView();
//        User user = new User();
//        user.initializeUserInfo();
//        GlobalVar.current_user = user.getName();

        mAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        firebaseAuthenticationListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    DocumentReference docRef = fireStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    //queryUserType();
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    Long typeNumber = (Long)document.get("user_type"); //use long instead of int. lol
                                    if(typeNumber == 2){
                                        GlobalVar.user_type = "patient";
                                        startActivity(new Intent(LoginActivity.this, MainUIPatientActivity.class));
                                        finish();
                                        return;
                                    }
                                    if (typeNumber == 1) {
                                        GlobalVar.user_rating = (String)document.get("rating");
                                        GlobalVar.user_type = "carer";
                                        GlobalVar.current_user = (String)document.get("name");
                                        GlobalVar.current_user_id = document.getId();
                                        startActivity(new Intent(LoginActivity.this, MainUICarerActivity.class));
                                        finish();
                                        return;
                                    }
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
//                    startActivity(new Intent(LoginActivity.this, MainUIPatientActivity.class));
//                    finish();
//                    return;
                }
//                if(user != null && usertype.getCarerBool() == true ){
//                    startActivity(new Intent(LoginActivity.this, MainUICarerActivity.class));
//                    finish();
//                    return;
//                }
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
                if(validateLoginField()){ //check if field is entered
                    final String email = emailTxt.getText().toString();
                    final String password = passwordTxt.getText().toString();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Unsuccessful.", Toast.LENGTH_SHORT).show();
                            }
//                            if(task.isSuccessful() && GlobalVar.user_type == "carer"){
//                                startActivity(new Intent(LoginActivity.this, MainUICarerActivity.class));
//                            }
//
//                            if(task.isSuccessful() && GlobalVar.user_type == "patient"){
//                                startActivity(new Intent(LoginActivity.this, MainUIPatientActivity.class));
//                            }

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


    private void getDetails(){
        DocumentReference docRef = fireStore.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //queryUserType();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Long typeNumber = (Long)document.get("user_type"); //use long instead of int. lol
                        if(typeNumber == 1){
                            GlobalVar.user_type = "carer";
                        }
                        if(typeNumber == 2){
                            GlobalVar.user_type = "patient";
                        }
                        if(typeNumber == 3){
                            GlobalVar.user_type = "patientFamily";
                        }
                        GlobalVar.current_user = document.get("name").toString();
                        String email = document.get("email").toString();
                        String gender = document.get("gender").toString();
                        String dob = document.get("dob").toString();
                        String phone = document.get("phone").toString();


                        //String name = document.get("name").toString();
                        //String rating =  document.get("rating").toString();


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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