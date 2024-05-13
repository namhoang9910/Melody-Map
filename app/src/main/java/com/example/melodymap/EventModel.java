package com.example.melodymap;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class EventModel {
    String eventName, eventDescription, eventHost, eventGenre, imageUrl;
    Timestamp eventDate;
    double eventPrice, eventLat, eventLong;
    GeoPoint eventGeoPoint;


    public EventModel(String eventName, Timestamp eventDate, String eventDescription, String eventHost, String eventGenre,
                      double eventPrice, String imageUrl, GeoPoint eventGeoPoint) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventHost = eventHost;
        this.eventGenre = eventGenre;
        this.eventPrice = eventPrice;
        this.imageUrl = imageUrl;
        this.eventGeoPoint = eventGeoPoint;
    }

    public String getEventName() {
        return eventName;
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventHost() {
        return eventHost;
    }

    public String getEventGenre() {
        return eventGenre;
    }
    public double getEventPrice() {
        return eventPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public GeoPoint getEventGeoPoint() {
        return eventGeoPoint;
    }

    public double getEventLat() {
        if (eventGeoPoint != null) {
            return eventGeoPoint.getLatitude();
        } else {
            return 0.0; // Or handle null case as per your requirement
        }
    }

    // Method to get longitude
    public double getEventLng() {
        if (eventGeoPoint != null) {
            return eventGeoPoint.getLongitude();
        } else {
            return 0.0; // Or handle null case as per your requirement
        }
    }

}
