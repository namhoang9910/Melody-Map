package com.example.melodymap;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.util.Date;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap googleMap;
    FirebaseFirestore db;
    // Temporary hard-coding data to test out RecyclerView
    // To-do: connect to Firebase
    RecyclerView recyclerView;
    ArrayList<EventModel> eventModels = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Button nearByButton;
    private Button genreButton;
    private Button freeEventsButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        nearByButton = rootView.findViewById(R.id.nearByButton);
        genreButton = rootView.findViewById(R.id.genreButton);
        freeEventsButton = rootView.findViewById(R.id.freeEventsButton);

        // Set onClickListeners for nearByButton
        nearByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select nearByButton
                nearByButton.setSelected(true);
                // Deselect genreButton and freeEventsButton
                genreButton.setSelected(false);
                freeEventsButton.setSelected(false);
                // Perform any other actions for nearByButton
            }
        });

        // Set onClickListeners for genreButton
        genreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select genreButton
                genreButton.setSelected(true);
                // Deselect nearByButton and freeEventsButton
                nearByButton.setSelected(false);
                freeEventsButton.setSelected(false);
                // Perform any other actions for genreButton
            }
        });

        // Set onClickListeners for freeEventsButton
        freeEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Select freeEventsButton
                freeEventsButton.setSelected(true);
                // Deselect nearByButton and genreButton
                nearByButton.setSelected(false);
                genreButton.setSelected(false);
                // Perform any other actions for freeEventsButton
            }
        });

        nearByButton.performClick();

        // Firebase
        db = FirebaseFirestore.getInstance();

        // The easy way
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String eventName = document.getString("eventName");
                                Timestamp eventDate = document.getTimestamp("eventDate");
                                String eventDescription = document.getString("eventDescription");
                                String eventHost = document.getString("eventHost");
                                String eventGenre = document.getString("eventGenre");
                                String imageUrl = document.getString("imageUrl");

                                GeoPoint eventLocation = document.getGeoPoint("location");

                                Double eventPriceDouble = document.getDouble("eventPrice");
                                double eventPrice = eventPriceDouble != null ? eventPriceDouble : 0.0;

                                // Handle potential null values for eventLat and eventLong
                                Double eventLatDouble = document.getDouble("eventLat");
                                double eventLat = eventLatDouble != null ? eventLatDouble : 0.0;

                                Double eventLongDouble = document.getDouble("eventLong");
                                double eventLong = eventLongDouble != null ? eventLongDouble : 0.0;

                                // Create an EventModel object and add it to the list
                                EventModel event1 = new EventModel(eventName, eventDate, eventDescription,
                                        eventHost, eventGenre, eventPrice, imageUrl, eventLocation);
                                eventModels.add(event1);
                            }
                            // Data from firebase doesn't exist outside onComplete
                            RecyclerView recyclerView = rootView.findViewById(R.id.eventRecyclerView);
                            Event_RecyclerViewAdapter adapter = new Event_RecyclerViewAdapter(getActivity(), eventModels);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;

        LatLng melbourneCBD = new LatLng(-37.8136, 144.9631); // Melbourne CBD coordinates
        googleMap.addMarker(new MarkerOptions().position(melbourneCBD).title("MelbourneCBD"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourneCBD, 13)); // Zoom level 12
    }
}