package com.example.melodymap;

public class RegularUser {
    private String email;
    private String pNumber;
    private String username;

    // Default constructor
    public RegularUser() {}

    // Parameterized constructor
    public RegularUser(String email, String pNumber, String username) {
        this.email = email;
        this.pNumber = pNumber;
        this.username = username;
    }

    // Getter and setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and setter for pNumber
    public String getPNumber() {
        return pNumber;
    }

    public void setPNumber(String pNumber) {
        this.pNumber = pNumber;
    }

    // Getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
