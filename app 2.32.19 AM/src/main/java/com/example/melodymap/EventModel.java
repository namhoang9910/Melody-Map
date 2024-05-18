package com.example.melodymap;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import java.util.Date;


public class EventModel {
    String eventId, eventName, eventDescription, eventHost, eventGenre, imageUrl;
    Timestamp eventDate;
    double eventPrice, eventLat, eventLong;
    GeoPoint eventGeoPoint;
    private int currentPosition;



    public EventModel(String eventId, String eventName, Timestamp eventDate, String eventDescription, String eventHost, String eventGenre,
                      double eventPrice, String imageUrl, GeoPoint eventGeoPoint, int currentPosition) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventHost = eventHost;
        this.eventGenre = eventGenre;
        this.eventPrice = eventPrice;
        this.imageUrl = imageUrl;
        this.eventGeoPoint = eventGeoPoint;
        // Initialize other fields...
        this.currentPosition = currentPosition;
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

    public String getEventId() {
        return eventId;
    }

    public boolean isPastEvent() {
        Timestamp currentTime = new Timestamp(new java.util.Date());
        return eventDate.compareTo(currentTime) < 0;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
