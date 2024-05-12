package com.example.melodymap;

import com.google.firebase.firestore.GeoPoint;

// Venue.java
public class Venue {
    private String email;
    private String genre;
    private GeoPoint location;
    private String name;
    private String openingHours;
    private String phoneNumber;

    public Venue() {}

    public Venue(String email, String genre, GeoPoint location, String name, String openingHours, String ownerName, String phoneNumber) {
        this.email = email;
        this.genre = genre;
        this.location = location;
        this.name = name;
        this.openingHours = openingHours;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningHours() {
        return openingHours;
    }
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

