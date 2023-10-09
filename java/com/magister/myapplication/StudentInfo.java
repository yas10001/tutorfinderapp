package com.magister.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class StudentInfo implements Serializable {
    private String firstName;
    private String lastName;
    private String middleInitial;
    private String dateOfBirth;
    private String address;
    private LaborClass acc;
    private String ProfileImage;
    private String grade;
    private String ProfilePictureUrl;

    public StudentInfo() {
        // Default constructor required for Firebase Realtime Database
    }

    public StudentInfo(String firstName, String lastName, String middleInitial, String dateOfBirth, String address, LaborClass acc, String ProfileImage, String grade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.acc = acc;
        this.ProfileImage = ProfileImage;
        this.grade = grade;
        this.ProfilePictureUrl= ProfilePictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getProfileImage() {
        return ProfileImage;
    }
    public void setProfileImage(String ProfileImage) {
        this.ProfileImage = ProfileImage;
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
    public void setProfilePictureUrl(String ProfilePictureUrl) {
        this.ProfilePictureUrl = ProfilePictureUrl;
    }
    public String getProfilePictureUrl() {
        return ProfilePictureUrl;
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

    public LaborClass getAcc() {
        return acc;
    }

    public void setAcc(LaborClass acc) {
        this.acc = acc;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}

