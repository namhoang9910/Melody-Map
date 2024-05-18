package com.example.melodymap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView userInfo;
    private Button signOut;
    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private Button signupRegularUserButton;
    private Button signupVenueButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        emailField = view.findViewById(R.id.email);
        passwordField = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login);
        signupRegularUserButton = view.findViewById(R.id.signupRegularUser);
        signupVenueButton = view.findViewById(R.id.signupVenue);

        userInfo = view.findViewById(R.id.userInfo);
        signOut = view.findViewById(R.id.signOut);

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            loginUser(email, password);
        });

        signupRegularUserButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new RegularUserSignUpFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        signupVenueButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new VenueSignUpFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        signOut.setOnClickListener(v -> {
            mAuth.signOut();
            updateUI(null);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        updateUI(user);
                    } else {
                        Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            userInfo.setText("Logged in as: " + user.getEmail());
            userInfo.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.VISIBLE);
            emailField.setVisibility(View.GONE);
            passwordField.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            signupRegularUserButton.setVisibility(View.GONE);
            signupVenueButton.setVisibility(View.GONE);
        } else {
            userInfo.setVisibility(View.GONE);
            signOut.setVisibility(View.GONE);
            emailField.setVisibility(View.VISIBLE);
            passwordField.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            signupRegularUserButton.setVisibility(View.VISIBLE);
            signupVenueButton.setVisibility(View.VISIBLE);
        }
    }
}