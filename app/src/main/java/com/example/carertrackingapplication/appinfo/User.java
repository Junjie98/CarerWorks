package com.example.carertrackingapplication.appinfo;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    public static final String TAG = "TAG";
    private String userName, userEmail, userGender, userDOB, userPhone, userRating;
    private int userType;

    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;

    //uses this method to initialise the user details right after login. Read it from database
    public void initializeUserInfo(){
        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        DocumentReference docRef = fireStore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        userName = document.get("name").toString();
                        userEmail = document.get("email").toString();
                        userGender = document.get("gender").toString();
                        userDOB = document.get("dob").toString();
                        userPhone = document.get("phone").toString();
                        userRating = document.get("rating").toString();
                        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW     " + userName);
                        //userType = (int) document.get("user_type");

                    } else {
                        Log.d(TAG, "No such document exist within the firebase database.");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    //getters and setters
    public String getName(){
        return userName;
    }
    public String getEmail(){
        return userEmail;
    }
    public String getGender(){
        return userGender;
    }
    public String getDOB(){
        return userDOB;
    }
    public String getPhone(){
        return userPhone;
    }
    public String getRating(){
        return userRating;
    }
    public int getUserType(){
        return userType;
    }

}
