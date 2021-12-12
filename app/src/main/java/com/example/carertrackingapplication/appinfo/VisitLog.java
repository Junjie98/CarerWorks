package com.example.carertrackingapplication.appinfo;

public class VisitLog {

    String address, date, time, duration, name, notes, postcode,user_id,status,carer_id,carer_name,carerNotes,end_time,rated;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getCarer_name() {
        return carer_name;
    }

    public void setCarer_name(String carer_name) {
        this.carer_name = carer_name;
    }

    public String getCarerNotes() {
        return carerNotes;
    }

    public void setCarerNotes(String carerNotes) {
        this.carerNotes = carerNotes;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    private VisitLog(String address, String carerNotes, String carer_name, String carer_id, String date, String duration,
                     String end_time, String name, String notes, String postcode, String status, String time, String user_id, String rated){

        this.address = address;
        this.carerNotes = carerNotes;
        this.carer_name = carer_name;
        this.carer_id = carer_id;
        this.date = date;
        this.duration = duration;
        this.end_time = end_time;
        this.name = name;
        this.notes = notes;
        this.postcode = postcode;
        this.status = status;
        this.time = time;
        this.user_id = user_id;
        this.rated = rated;
    }

    private VisitLog(){

    }

}
