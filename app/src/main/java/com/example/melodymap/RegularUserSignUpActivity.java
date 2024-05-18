package com.example.melodymap;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegularUserSignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private EditText pNumberField;
    private EditText usernameField;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_user_sign_up);

        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        pNumberField = findViewById(R.id.pNumber);
        usernameField = findViewById(R.id.username);
        signupButton = findViewById(R.id.signupUser_button);

        signupButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String pNumber = pNumberField.getText().toString();
            String username = usernameField.getText().toString();
            createRegularUser(email, password, pNumber, username);
        });
    }

    private void createRegularUser(String email, String password, String pNumber, String username) {
        // Check if any of the fields are empty
        if (email.isEmpty() || password.isEmpty() || pNumber.isEmpty() || username.isEmpty()) {
            // Display a toast indicating empty credentials
            Toast.makeText(RegularUserSignUpActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return; // Stop further execution
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        RegularUser regularUser = new RegularUser(email, pNumber, username);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(user.getUid()).set(regularUser);
                        Toast.makeText(RegularUserSignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegularUserSignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
