package com.example.carertrackingapplication.appinfo;



public class Appointment {

    String address, date, time, duration, name, notes, postcode,user_id,status,carer_id,carer_name, family_id_appointment;

    public String getCarer_name() {
        return carer_name;
    }

    public void setCarer_name(String carer_name) {
        this.carer_name = carer_name;
    }

    private Appointment(){ //for firebase

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCarer_id() {
        return carer_id;
    }

    public void setCarer_id(String carer_id) {
        this.carer_id = carer_id;
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

    public String getFamily_id_appointment() {
        return family_id_appointment;
    }

    public void setFamily_id_appointment(String family_id_appointment) {
        this.family_id_appointment = family_id_appointment;
    }

    private Appointment(String address, String date, String time, String duration, String name, String notes, String postcode, String user_id, String carer_name, String carer_id, String family_id_appointment){ //for us to retrieve data.
        this.address = address;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.name = name;
        this.notes = notes;
        this.postcode = postcode;
        this.user_id = user_id;
        this.carer_name = carer_name;
        this.carer_id = carer_id;
        this.family_id_appointment = family_id_appointment;
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
