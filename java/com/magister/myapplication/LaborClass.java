package com.magister.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
@IgnoreExtraProperties
public class LaborClass implements Serializable {
    private String email;
    private String phone;
    private String password;
    private String role;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LaborClass() {
        // Default constructor required for Firebase
    }

    public LaborClass(String email, String phone, String role, String password) {

        this.email = email;
        this.phone = phone;
        this.role = role;
        this.password = password;
    }
}




