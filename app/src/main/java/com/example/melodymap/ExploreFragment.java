package com.example.melodymap;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap googleMap;
    FirebaseFirestore db;
    // Temporary hard-coding data to test out RecyclerView
    // To-do: connect to Firebase
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

        // To be removed: hardcoded database
        RecyclerView recyclerView = rootView.findViewById(R.id.eventRecyclerView);
        setUpEventModel();
        Event_RecyclerViewAdapter adapter = new Event_RecyclerViewAdapter(getActivity(),
                eventModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    // Hard-coded, to be removed
    public void setUpEventModel() {
        EventModel melbourneEvent1a = new EventModel("Event 1a: 80's Synth Pop", "May 11, 2024", "This is a jazz event",
                "Club Retro", 10, R.drawable.club_retro, -37.81241686315203, 144.96188789533943);
        EventModel melbourneEvent2a = new EventModel("Event 2a: Pop Rock Party", "May 12, 2024", "An exciting music night",
                "Vibras Club", 0, R.drawable.vibras_club, -37.811083452388935, 144.97043719678723);
        EventModel melbourneEvent3a = new EventModel("Event 3a: Beyonce Tribute", "May 13, 2024", "Party! YOLO!!",
                "Sub Club", 25.99, R.drawable.sub_club, -37.81716909251318, 144.96580233995357);
        EventModel melbourneEvent1b = new EventModel("Event 1b: 70's Synth Pop", "May 21, 2024", "This is a jazz event",
                "Club Retro", 10, R.drawable.club_retro, -37.81241686315203, 144.96188789533943);
        EventModel melbourneEvent2b = new EventModel("Event 2b: Rock Party", "May 22, 2024", "An exciting music night",
                "Vibras Club", 39.99, R.drawable.vibras_club, -37.811083452388935, 144.97043719678723);
        EventModel melbourneEvent3b = new EventModel("Event 3b: Michael Jackson Tribute", "May 23, 2024", "Party! YOLO!!",
                "Sub Club", 0, R.drawable.sub_club, -37.81716909251318, 144.96580233995357);
        EventModel melbourneEvent2c = new EventModel("Event 2c: Party", "May 29, 2024", "An exciting music night",
                "Vibras Club", 39.99, R.drawable.vibras_club, -37.811083452388935, 144.97043719678723);
        EventModel melbourneEvent2d = new EventModel("Event 2d: Party", "May 29, 2024", "An exciting music night",
                "Vibras Club", 0, R.drawable.vibras_club, -37.811083452388935, 144.97043719678723);
        EventModel melbourneEvent2e = new EventModel("Event 2e: Party", "May 29, 2024", "An exciting music night",
                "Vibras Club", 39.99, R.drawable.vibras_club, -37.811083452388935, 144.97043719678723);

        eventModels.add(melbourneEvent1a);
        eventModels.add(melbourneEvent2a);
        eventModels.add(melbourneEvent3a);
        eventModels.add(melbourneEvent1b);
        eventModels.add(melbourneEvent2b);
        eventModels.add(melbourneEvent3b);
        eventModels.add(melbourneEvent2c);
        eventModels.add(melbourneEvent2d);
        eventModels.add(melbourneEvent2e);

    }
}