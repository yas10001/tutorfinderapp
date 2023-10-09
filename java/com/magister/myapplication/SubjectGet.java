package com.magister.myapplication;

public class SubjectGet {
    private String name;
    private String imageUrl;

    public SubjectGet() {
        // Default constructor required for DataSnapshot.getValue(SubjectGet.class)
    }
    public SubjectGet(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter for imageUrl
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
