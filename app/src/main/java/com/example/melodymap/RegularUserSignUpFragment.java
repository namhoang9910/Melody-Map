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

public class RegularUserSignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private EditText pNumberField;
    private EditText usernameField;
    private Button signupButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regular_user_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailField = view.findViewById(R.id.email);
        passwordField = view.findViewById(R.id.password);
        pNumberField = view.findViewById(R.id.pNumber);
        usernameField = view.findViewById(R.id.username);
        signupButton = view.findViewById(R.id.signup);

        signupButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String pNumber = pNumberField.getText().toString();
            String username = usernameField.getText().toString();
            createRegularUser(email, password, pNumber, username);
        });

        return view;
    }

    private void createRegularUser(String email, String password, String pNumber, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        RegularUser regularUser = new RegularUser(email, pNumber, username);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(user.getUid()).set(regularUser);
                        Toast.makeText(getActivity(), "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}