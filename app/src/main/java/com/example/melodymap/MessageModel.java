package com.example.melodymap;

public class MessageModel {

    static String SENT_BY_ME = "me";
    static String SENT_BY_BOT = "bot";

    String message;
    String sentBy;

    public MessageModel(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }
}
