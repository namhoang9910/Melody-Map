package com.example.melodymap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class VenueSignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private EditText genreField;
    private EditText locationField;
    private EditText nameField;
    private EditText openingHoursField;
    private EditText phoneNumberField;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_sign_up);

        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        genreField = findViewById(R.id.genre);
        locationField = findViewById(R.id.location);
        nameField = findViewById(R.id.name);
        openingHoursField = findViewById(R.id.openingHours);
        phoneNumberField = findViewById(R.id.phoneNumber);
        signupButton = findViewById(R.id.signupVenue_button);

        signupButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String genre = genreField.getText().toString();
            GeoPoint location = new GeoPoint(0, 0);  // Replace with actual location
            String name = nameField.getText().toString();
            String openingHours = openingHoursField.getText().toString();
            String phoneNumber = phoneNumberField.getText().toString();
            createVenue(email, password, genre, location, name, openingHours, phoneNumber);
        });
    }

    private void createVenue(String email, String password, String genre, GeoPoint location, String name, String openingHours, String phoneNumber) {
        // Check if any of the fields are empty
        if (email.isEmpty() || password.isEmpty() || genre.isEmpty() || name.isEmpty() || openingHours.isEmpty() || phoneNumber.isEmpty()) {
            // Display a toast indicating empty credentials
            Toast.makeText(VenueSignUpActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Venue venue = new Venue(email, genre, location, name, openingHours, phoneNumber);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("venues").document(user.getUid()).set(venue);
                        Toast.makeText(VenueSignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(VenueSignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
