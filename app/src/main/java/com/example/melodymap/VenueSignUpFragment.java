package com.example.melodymap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class VenueSignUpFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailField = view.findViewById(R.id.email);
        passwordField = view.findViewById(R.id.password);
        genreField = view.findViewById(R.id.genre);
        locationField = view.findViewById(R.id.location);
        nameField = view.findViewById(R.id.name);
        openingHoursField = view.findViewById(R.id.openingHours);
        phoneNumberField = view.findViewById(R.id.phoneNumber);
        signupButton = view.findViewById(R.id.signup);

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

        return view;
    }

    private void createVenue(String email, String password, String genre, GeoPoint location, String name, String openingHours, String phoneNumber) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Venue venue = new Venue(email, genre, location, name, openingHours, phoneNumber);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("venues").document(user.getUid()).set(venue);
                        Toast.makeText(getActivity(), "Sign Up Successful", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}