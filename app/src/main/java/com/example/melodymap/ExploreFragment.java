package com.example.melodymap;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExploreFragment extends Fragment implements
        OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, RecyclerViewInterface,
        GoogleMap.OnMarkerClickListener {

    private View rootView;
    RecyclerView recyclerView;
    GoogleMap googleMap;
    SupportMapFragment mapFragment;
    FirebaseFirestore db;
    private Event_RecyclerViewAdapter adapter;
    SearchView searchView;
    ArrayList<EventModel> eventModels = new ArrayList<>();

    private static final LatLng MELBOURNE_CBD = new LatLng(-37.8136, 144.9631);
    private static final double NEARBY_DISTANCE_DIAMETER_KM = 2.0; // "NearBy" distance diameter

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private Button allEventsButton, nearByButton, freeEventsButton;
    boolean isFreeEvent = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_explore, container, false);

        searchView = rootView.findViewById(R.id.searchView);
        searchView.clearFocus();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        allEventsButton = rootView.findViewById(R.id.allEventsButton);
        nearByButton = rootView.findViewById(R.id.nearByButton);
        freeEventsButton = rootView.findViewById(R.id.freeEventsButton);

        // Set onClickListeners for allEventsButton
        allEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allEventsButton.setSelected(true);
                nearByButton.setSelected(false);
                freeEventsButton.setSelected(false);

                // Perform any other actions for allEventsButton
                filterAllEvents();
                filterList(searchView.getQuery().toString());
            }
        });

        // Set onClickListeners for nearByButton
        nearByButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allEventsButton.setSelected(false);
                nearByButton.setSelected(true);
                freeEventsButton.setSelected(false);
                // Perform any other actions for nearByButton
                filterNearByEvents();
                filterList(searchView.getQuery().toString());

            }
        });

        // Set onClickListeners for freeEventsButton
        freeEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allEventsButton.setSelected(false);
                nearByButton.setSelected(false);
                freeEventsButton.setSelected(true);

                // Perform any other actions for freeEventsButton
                filterFreeEvents();
                filterList(searchView.getQuery().toString());
            }
        });

        allEventsButton.performClick();

        ExploreFragment fragment = this;

        // Firebase
        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String eventId = document.getString("eventId");
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
                                EventModel event1 = new EventModel(eventId, eventName, eventDate, eventDescription,
                                        eventHost, eventGenre, eventPrice, imageUrl, eventLocation);
                                eventModels.add(event1);
                            }

                            // Data from firebase doesn't exist outside onComplete
                            recyclerView = rootView.findViewById(R.id.eventRecyclerView);
                            adapter = new Event_RecyclerViewAdapter(getActivity(), eventModels, fragment);

                            // Sort events by date
                            Collections.sort(eventModels, new Comparator<EventModel>() {
                                @Override
                                public int compare(EventModel e1, EventModel e2) {
                                    return e1.getEventDate().compareTo(e2.getEventDate());
                                }
                            });
                            adapter.notifyDataSetChanged();

                            recyclerView.setAdapter(adapter);
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }
                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    filterList(newText);
                                    return true;
                                }
                            });

                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        // Inflate the layout for this fragment
        return rootView;
    }

    public void filterAllEvents() {
        ArrayList<EventModel> filteredList = new ArrayList<>();
        for (EventModel event : eventModels) {
            filteredList.add(event);
        }

        if (!filteredList.isEmpty()) {
            adapter.setFilteredList(filteredList);
            updateMapMarkers(filteredList);
        }
    }

    public void filterList(String text) {
        ArrayList<EventModel> filteredList = new ArrayList<>();

        // Apply filters and search keyword filter to the original eventModels list
        for (EventModel event : eventModels) {
            boolean isNearby = nearByButton.isSelected() && calculateDistance(MELBOURNE_CBD, new LatLng(event.getEventLat(), event.getEventLng())) <= NEARBY_DISTANCE_DIAMETER_KM;
            boolean isFree = freeEventsButton.isSelected() && event.getEventPrice() == 0.0;
            boolean headingMatchesSearch = event.getEventName().toLowerCase().contains(text.toLowerCase());
            boolean genreMatchesSearch = event.getEventGenre().toLowerCase().contains(text.toLowerCase());
            boolean descriptionMatchesSearch = event.getEventDescription().toLowerCase().contains(text.toLowerCase());


            if ((isNearby || !nearByButton.isSelected()) && (isFree || !freeEventsButton.isSelected()) &&
                    (headingMatchesSearch || genreMatchesSearch || descriptionMatchesSearch)) {
                filteredList.add(event);
            }
        }

        if (!filteredList.isEmpty()) {
            adapter.setFilteredList(filteredList);
            updateMapMarkers(filteredList);
        }
    }


    public void filterFreeEvents() {
        ArrayList<EventModel> filteredList = new ArrayList<>();
        for (EventModel event : eventModels) {
            if (event.getEventPrice() == 0.0) {
                filteredList.add(event);
            }
        }

        if (!filteredList.isEmpty()) {
            adapter.setFilteredList(filteredList);
            updateMapMarkers(filteredList);
        }
    }

    public void filterNearByEvents() {
        ArrayList<EventModel> filteredList = new ArrayList<>();
        for (EventModel event : eventModels) {
            // Calculate the distance between the event location and the user's location
            LatLng eventLocation = new LatLng(event.getEventLat(), event.getEventLng());
            double distance = calculateDistance(MELBOURNE_CBD, eventLocation);

            // Adjust the threshold distance as needed
            if (distance <= NEARBY_DISTANCE_DIAMETER_KM) {
                filteredList.add(event);
            }
        }

        if (!filteredList.isEmpty()) {
            adapter.setFilteredList(filteredList);
            updateMapMarkers(filteredList);
        }
    }

    // Example method to calculate distance between two LatLng objects
    private double calculateDistance(LatLng location1, LatLng location2) {
        // Earth radius in kilometers
        final double RADIUS = 6371.0;

        // Convert latitude and longitude values from degrees to radians
        double lat1Radians = Math.toRadians(location1.latitude);
        double lon1Radians = Math.toRadians(location1.longitude);
        double lat2Radians = Math.toRadians(location2.latitude);
        double lon2Radians = Math.toRadians(location2.longitude);

        // Calculate the differences in latitude and longitude
        double latDiff = lat2Radians - lat1Radians;
        double lonDiff = lon2Radians - lon1Radians;

        // Calculate the distance using the Haversine formula
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(lat1Radians) * Math.cos(lat2Radians) *
                        Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = RADIUS * c;

        // Print out the calculated distance
        System.out.println("Distance between Location 1 and Location 2: " + distance + " km");

        return distance;
    }


    @Override
   public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;

        // Clear any existing markers on the map
        googleMap.clear();

        // Set up marker info window adapter
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set up on info window & marker click listener
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnMarkerClickListener(this);

        // Retrieve event data from Firestore
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Add markers for event locations
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (EventModel event : eventModels) {
                                LatLng eventLatLng = new LatLng(event.getEventGeoPoint().getLatitude(), event.getEventGeoPoint().getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(eventLatLng).title(event.getEventGenre()));
                                builder.include(eventLatLng); // Include event location in the bounds
                            }

                            // Build the bounds
                            LatLngBounds bounds = builder.build();

                            // Set padding for the bounds (optional)
                            int padding = 100; // Padding in pixels
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                            updateMapMarkers(eventModels);
                            // Move the camera to show all markers within the bounds
                            googleMap.moveCamera(cameraUpdate);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void onInfoWindowClick(Marker marker) {
        // Retrieve the title of the clicked marker
        String markerTitle = marker.getTitle();

        // Find the corresponding event in the eventModels list
        EventModel selectedEvent = null;
        for (EventModel event : eventModels) {
            if (event.getEventGenre().equals(markerTitle)) {
                selectedEvent = event;
                break;
            }
        }

        // If a corresponding event is found, launch the EventInfoActivity
        if (selectedEvent != null) {
            // Find the position of the selected event in the eventModels list
            int position = eventModels.indexOf(selectedEvent);

            // Call the onItemClick method to launch the EventInfoActivity
            onItemClick(position);
        }
    }
    private void updateMapMarkers(ArrayList<EventModel> events) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(MELBOURNE_CBD); // Include Melbourne CBD in bounds

        // Edit the default location (You Are Here) - Custom design
        googleMap.clear();
        int width = 48; // Width of the marker icon (in pixels)
        int height = 48; // Height of the marker icon (in pixels)
        float dotRadius = 16; // Radius of the black dot (in pixels)

        // Create the dark gray marker icon using the method
        BitmapDescriptor markerIcon = createYouAreHereIcon(width, height, dotRadius);

        // Create the marker options and add the marker to the map
        MarkerOptions youAreHereLocation = new MarkerOptions()
                .position(MELBOURNE_CBD)
                .title("You Are Here")
                .icon(markerIcon);
        googleMap.addMarker(youAreHereLocation);

        for (EventModel event : events) {
            LatLng eventLatLng = new LatLng(event.getEventLat(), event.getEventLng());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(eventLatLng)
                    .title(event.getEventGenre());

            googleMap.addMarker(markerOptions);

            builder.include(eventLatLng); // Include event location in the bounds

        }

        // Build the bounds
        LatLngBounds bounds = builder.build();

        // Set padding for the bounds (optional)
        int padding = 100; // Padding in pixels
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        // Move the camera to show all markers within the bounds
        googleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onItemClick(int position) {
        // When item from RecyclerViewInterface is clicked, show event info
        Intent intent = new Intent(getActivity(), EventInfoActivity.class);

        // Pass necessary data to the activity using Intent extras
        intent.putExtra("EVENT_ID", eventModels.get(position).getEventId());
        intent.putExtra("EVENT_NAME", eventModels.get(position).getEventName());
        intent.putExtra("EVENT_PRICE", eventModels.get(position).getEventPrice());
        intent.putExtra("IMAGE_URL", eventModels.get(position).getImageUrl());
        intent.putExtra("EVENT_HOST", eventModels.get(position).getEventHost());
        intent.putExtra("EVENT_DATE", eventModels.get(position).getEventDate());
        intent.putExtra("EVENT_GENRE", eventModels.get(position).getEventGenre());
        intent.putExtra("EVENT_DESCRIPTION", eventModels.get(position).getEventDescription());


        startActivity(intent);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        // Show the info window for the clicked marker
        marker.showInfoWindow();

        // Retrieve the title of the clicked marker
        String markerTitle = marker.getTitle();

        // Find the corresponding event in the eventModels list
        EventModel selectedEvent = null;
        for (EventModel event : eventModels) {
            if (event.getEventGenre().equals(markerTitle)) {
                selectedEvent = event;
                break;
            }
        }

        // Update the RecyclerView to display only the selected event
        if (selectedEvent != null) {
            ArrayList<EventModel> selectedEventList = new ArrayList<>();
            selectedEventList.add(selectedEvent);
            adapter.setFilteredList(selectedEventList);
        }

        // Return true to consume the event and prevent the default behavior
        return true;
    }


    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // Set up info window content
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            // If you return null here, getInfoWindow will be called.
            return null;
        }

        private void render(Marker marker, View view) {
            // Customize the info window contents based on the marker
            String title = marker.getTitle();
            TextView titleTextView = view.findViewById(R.id.title);
            titleTextView.setText(title);
        }
    }

    private BitmapDescriptor createYouAreHereIcon(int width, int height, float dotRadius) {
        // Increase the dimensions of the bitmap to accommodate the outer layer
        Bitmap bitmap = Bitmap.createBitmap(width + 50, height + 50, Bitmap.Config.ARGB_8888);

        // Create a canvas for drawing on the bitmap
        Canvas canvas = new Canvas(bitmap);

        // Define the colors for the circles
        int colorBlack = 0xFF000000; // Black color in ARGB format (0xFF000000)
        int colorWhite = 0xFFFFFFFF; // White color in ARGB format (0xFFFFFFFF)
        int colorGray = 0x80C0C0C0; // Transparent gray color in ARGB format (0x80C0C0C0)

        // Draw the outer gray circle
        Paint outerCirclePaint = new Paint();
        outerCirclePaint.setColor(colorGray);
        outerCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2f + 25, height / 2f + 25, dotRadius + 25, outerCirclePaint);

        // Draw the white border
        Paint borderPaint = new Paint();
        borderPaint.setColor(colorWhite);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5);
        canvas.drawCircle(width / 2f + 25, height / 2f + 25, dotRadius + 10, borderPaint);

        // Draw the black circle at the center
        Paint blackCirclePaint = new Paint();
        blackCirclePaint.setColor(colorBlack);
        blackCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2f + 25, height / 2f + 25, dotRadius, blackCirclePaint);



        // Create a BitmapDescriptor from the custom bitmap and return it
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }




}