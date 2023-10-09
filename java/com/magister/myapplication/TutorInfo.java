package com.magister.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class TutorInfo implements Serializable {

    private String firstName;
    private String lastName;
    private String middleInitial;
    private String dateOfBirth;
    private String address;
    private String phone;
    private String imageUrl;
    private String rate;


    private LaborClass acc;

    private String expertise;
    private String profilePictureUrl;
    private String status;
    private String fileUrl;
    private String Bio;
    private String Schedule;



    public TutorInfo() {
    }

    public TutorInfo(String Bio, String Schedule, LaborClass acc, String rate, String firstName, String lastName, String middleInitial, String dateOfBirth, String address, String phone, String imageUrl, String expertise, String profilePictureUrl, String status, String fileUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.dateOfBirth = dateOfBirth;
        this.Bio = Bio;
        this.Schedule = Schedule;
        this.address = address;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.expertise = expertise;
        this.profilePictureUrl = profilePictureUrl;
        this.status = status;
        this.acc = acc;
        this.fileUrl = fileUrl;
        this.rate = rate;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getBio() {
        return Bio;
    }
    public String getSchedule() {
        return Schedule;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setBio(String Bio) {
        this.Bio = Bio;
    }
    public void setSchedule(String Schedule) {
        this.Schedule = Schedule;
    }

    public LaborClass getAcc() {
        return acc;
    }

    public void setAcc(LaborClass acc) {
        this.acc = acc;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
