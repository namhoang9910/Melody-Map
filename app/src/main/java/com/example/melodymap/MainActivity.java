package com.example.melodymap;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.melodymap.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        replaceFragment(new ExploreFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            /*switch (item.getItemId()) {
                case R.id.explore:
                    replaceFragment(new ExploreFragment());
                    break;
                case R.id.myEvents:
                    replaceFragment(new MyEventsFragment());
                    break;
                case R.id.inbox:
                    replaceFragment(new InboxFragment());
                    break;
                case R.id.account:
                    replaceFragment(new AccountFragment());
                    break;
            }*/
            int itemId = item.getItemId();
            if (itemId == R.id.explore) {
                replaceFragment(new ExploreFragment());
            } else if (itemId == R.id.myEvents) {
                replaceFragment(new MyEventsFragment());
            } else if (itemId == R.id.inbox) {
                replaceFragment(new InboxFragment());
            } else if (itemId == R.id.account) {
                replaceFragment(new AccountFragment());
            }

            return true;
        });


        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}