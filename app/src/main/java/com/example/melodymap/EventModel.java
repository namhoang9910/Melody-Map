package com.example.melodymap;

public class EventModel {
    String eventName, eventDate, eventDescription, eventHost;
    int image;
    double eventPrice, eventLat, eventLong;


    public EventModel(String eventName, String eventDate, String eventDescription, String eventHost,
                      double eventPrice, int image, double eventLat, double eventLong) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.eventHost = eventHost;
        this.eventPrice = eventPrice;
        this.image = image;
        this.eventLat = eventLat;
        this.eventLong = eventLong;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getEventHost() {
        return eventHost;
    }

    public double getEventPrice() {
        return eventPrice;
    }

    public int getImage() {
        return image;
    }

    public double getEventLat() {
        return eventLat;
    }

    public double getEventLong() {
        return eventLong;
    }
}
