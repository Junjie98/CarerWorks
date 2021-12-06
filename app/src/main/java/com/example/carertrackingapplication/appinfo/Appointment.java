package com.example.carertrackingapplication.appinfo;

import java.util.ArrayList;

public class Appointment {

    String address, date, time, duration, name, notes, postcode,user_id;
    private Appointment(){ //for firebase

    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getUser_ID() {
        return user_id;
    }

    public void setUser_ID(String user_id) {
        this.user_id = user_id;
    }

    private Appointment(String address, String date, String time, String duration, String name, String notes, String postcode, String user_id){ //for us to retrieve data.
        this.address = address;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.name = name;
        this.notes = notes;
        this.postcode = postcode;
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }
}
