package com.magister.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class ParentInfo implements Serializable {
    private String firstName;
    private String lastName;
    private String middleName;
    private String dateOfBirth;
    private String address;
    private String grade;
    private String profileImage;
    private LaborClass acc;
    private String ProfilePictureUrl;


    public String getProfilePictureUrl() {
        return ProfilePictureUrl;
    }
    public void setProfilePictureUrl(String ProfilePictureUrl) {
        this.ProfilePictureUrl = ProfilePictureUrl;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setAcc(LaborClass acc) {
        this.acc = acc;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public LaborClass getAcc() {
        return acc;
    }

    // No-argument constructor (required for Firebase)
    public ParentInfo() {
        // Empty constructor required by Firebase for deserialization
    }

    // Constructor with parameters
    public ParentInfo(String firstName, String lastName, String middleName, String dateOfBirth, String address, LaborClass acc, String profileImage, String grade) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.acc = acc;
        this.profileImage = profileImage;
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
