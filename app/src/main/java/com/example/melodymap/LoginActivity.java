package com.example.melodymap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView userInfo;
    private Button signOut;
    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private Button signupRegularUserButton;
    private Button signupVenueButton;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        signupRegularUserButton = findViewById(R.id.signupRegularUser);
        signupVenueButton = findViewById(R.id.signupVenue);

        userInfo = findViewById(R.id.userInfo);
        signOut = findViewById(R.id.signOut);

        // Check if there are saved credentials
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(PREF_EMAIL, "");
        String savedPassword = prefs.getString(PREF_PASSWORD, "");

        // If saved credentials exist, auto-fill the email and password fields
        if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
            emailField.setText(savedEmail);
            passwordField.setText(savedPassword);
        }

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            loginUser(email, password);
        });

        signupRegularUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegularUserSignUpActivity.class);
            startActivity(intent);
        });

        signupVenueButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, VenueSignUpActivity.class);
            startActivity(intent);
        });

        signOut.setOnClickListener(v -> {
            mAuth.signOut();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void loginUser(String email, String password) {
        // Check if any of the fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            // Display a toast indicating empty credentials
            Toast.makeText(LoginActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Save the email and password
                        saveCredentials(email, password);

                        // Set the isUserSignedIn flag to true
                        SharedPreferences preferences = getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isUserSignedIn", true);
                        editor.apply();

                        // Start MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveCredentials(String email, String password) {
        // Save the email and password to SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_PASSWORD, password);
        editor.apply();
    }
}
