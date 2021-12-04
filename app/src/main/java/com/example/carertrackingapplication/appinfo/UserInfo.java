package com.example.carertrackingapplication.appinfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserInfo {

    private String userName, userEmail, userGender, userDOB, userPhone;
    private int userType;

    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;

    public UserInfo(String name, String email, String gender, String dob, String phone){
        this.userName = name;
        this.userEmail = email;
        this.userGender = gender;
        this.userDOB = dob;
        this.userPhone = phone;

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
    public int getUserType(){
        return userType;
    }

}
