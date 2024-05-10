package com.example.melodymap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ExploreFragment extends Fragment implements OnMapReadyCallback {
    GoogleMap googleMap;
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