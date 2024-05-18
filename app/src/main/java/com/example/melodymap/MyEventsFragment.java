package com.example.melodymap;

import static android.content.ContentValues.TAG;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyEventsFragment extends Fragment implements RecyclerViewInterface {
    private FirebaseFirestore db;
    private RecyclerView recyclerView, upcomingEventRecycler;
    private Event_RecyclerViewAdapter adapter, upcomingEventAdapter;
    private ArrayList<EventModel> eventModels = new ArrayList<>();
    private ArrayList<EventModel> upcomingEventList = new ArrayList<>();
    ImageView upcomingEventImageView;
    TextView upcomingEventNameTextView, upcomingEventDescriptionTextView;
    TextView upcomingEventDateTextView, upcomingEventHourTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_events, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.myEventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Event_RecyclerViewAdapter(getContext(), eventModels, this::onItemClick);
        recyclerView.setAdapter(adapter);

        // Initialize RecyclerView for upcoming event
        upcomingEventRecycler = rootView.findViewById(R.id.upcomingEventRecycler);
        upcomingEventAdapter = new Event_RecyclerViewAdapter(getContext(), upcomingEventList, this::onItemClick);
        upcomingEventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingEventRecycler.setAdapter(upcomingEventAdapter);

        // Find the ImageView for the upcoming event
        upcomingEventImageView = rootView.findViewById(R.id.upcomingImage);
        // Get the display metrics & set the image width and height to be the same
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int imageViewWidth = (int) (displayMetrics.widthPixels * 0.9);
        upcomingEventImageView.getLayoutParams().width = imageViewWidth;
        upcomingEventImageView.getLayoutParams().height = imageViewWidth;
        upcomingEventImageView.requestLayout();

        // Find event name for upcoming event
        upcomingEventNameTextView = rootView.findViewById(R.id.upcoming_eName_Text);

        // Find event date for upcoming event
        upcomingEventDateTextView = rootView.findViewById(R.id.upcoming_eDate_Text);
        upcomingEventHourTextView = rootView.findViewById(R.id.upcoming_eHour_Text);

        // Find event description for upcoming event
        upcomingEventDescriptionTextView = rootView.findViewById(R.id.upcoming_eDescription_Text);

        // Firebase: get the registered events for the user
        getEventListFromUser("user1", eventList -> {
            if (eventList != null && !eventList.isEmpty()) {
                fetchEventsDetails(eventList);
            } else {
                Log.d(TAG, "No events found for the user");
                Toast.makeText(getContext(), "No events found for the user", Toast.LENGTH_SHORT).show();
            }
        });

        // Find the Card_Upcoming CardView
        CardView cardUpcoming = rootView.findViewById(R.id.Card_Upcoming);

        // Set OnClickListener on Card_Upcoming
        cardUpcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there is an upcoming event
                if (!upcomingEventList.isEmpty()) {
                    // Start the EventInfoActivity with the first upcoming event
                    startEventInfoActivity(upcomingEventList.get(0));
                } else {
                    // Handle the case when there are no upcoming events
                    Toast.makeText(getContext(), "No upcoming events", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }

    // Method to start EventInfoActivity with event data
    private void startEventInfoActivity(EventModel event) {
        Intent intent = new Intent(getActivity(), EventInfoActivity.class);
        intent.putExtra("EVENT_ID", event.getEventId());
        intent.putExtra("EVENT_NAME", event.getEventName());
        intent.putExtra("EVENT_PRICE", event.getEventPrice());
        intent.putExtra("IMAGE_URL", event.getImageUrl());
        intent.putExtra("EVENT_HOST", event.getEventHost());
        intent.putExtra("EVENT_DATE", event.getEventDate());
        intent.putExtra("EVENT_GENRE", event.getEventGenre());
        intent.putExtra("EVENT_DESCRIPTION", event.getEventDescription());
        startActivity(intent);
    }

    private void getEventListFromUser(String userId, OnEventListRetrievedListener listener) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> eventList = (ArrayList<String>) document.get("eventList");
                            if (eventList == null) {
                                eventList = new ArrayList<>();
                            }
                            listener.onEventListRetrieved(eventList);
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(getContext(), "User document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(getContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchEventsDetails(List<String> eventIds) {
        db.collection("events")
                .whereIn("eventId", eventIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventModels.clear();
                        EventModel upcomingEvent = null;
                        Timestamp currentTime = new Timestamp(new java.util.Date());
                        int eventCount = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventId = document.getString("eventId");
                            String eventName = document.getString("eventName");
                            com.google.firebase.Timestamp eventDate = document.getTimestamp("eventDate");
                            String eventDescription = document.getString("eventDescription");
                            String eventHost = document.getString("eventHost");
                            String eventGenre = document.getString("eventGenre");
                            String imageUrl = document.getString("imageUrl");

                            com.google.firebase.firestore.GeoPoint eventLocation = document.getGeoPoint("location");

                            Double eventPriceDouble = document.getDouble("eventPrice");
                            double eventPrice = eventPriceDouble != null ? eventPriceDouble : 0.0;

                            EventModel event = new EventModel(eventId, eventName, eventDate, eventDescription,
                                    eventHost, eventGenre, eventPrice, imageUrl, eventLocation, eventCount);
                            eventModels.add(event);
                            eventCount++;

                            // Find the next upcoming event after the current time
                            if (eventDate != null && eventDate.compareTo(currentTime) > 0) {
                                if (upcomingEvent == null || eventDate.compareTo(upcomingEvent.getEventDate()) < 0) {
                                    upcomingEvent = event;
                                }
                            }
                        }

                        // Set the upcoming event to the TextView
                        if (upcomingEvent != null) {
                            upcomingEventList.add(upcomingEvent);
                            upcomingEventAdapter.notifyDataSetChanged();

                            // Fetch imageUrl from the upcoming event and load it into the ImageView
                            String upcomingEventImageUrl = upcomingEvent.getImageUrl();
                            if (upcomingEventImageUrl != null && !upcomingEventImageUrl.isEmpty()) {
                                // Load imageUrl into the ImageView using your preferred image loading library (e.g., Picasso, Glide)
                                Picasso.get().load(upcomingEventImageUrl).into(upcomingEventImageView);
                            }

                            // Set the upcoming event name to the TextView
                            upcomingEventNameTextView.setText(upcomingEvent.getEventName());

                            // Format the timestamp to a readable date string
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
                            String eventDateString = dateFormat.format(upcomingEvent.getEventDate().toDate());

                            // Extract hours from the timestamp
                            SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                            String eventHourString = hourFormat.format(upcomingEvent.getEventDate().toDate());

                            // Set the formatted date string to the TextView
                            upcomingEventDateTextView.setText(eventDateString);

                            // Set the extracted hour string to the hour TextView
                            upcomingEventHourTextView.setText(eventHourString);

                            // Set the upcoming event name to the TextView
                            upcomingEventDescriptionTextView.setText(upcomingEvent.getEventDescription());
                        }

                        // Sort events by date
                        Collections.sort(eventModels, new Comparator<EventModel>() {
                            @Override
                            public int compare(EventModel e1, EventModel e2) {
                                return e1.getEventDate().compareTo(e2.getEventDate());
                            }
                        });
                        adapter.notifyDataSetChanged();


                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
    @Override
    public void onItemClick(int position) {
        // When item from RecyclerViewInterface is clicked, show event info
        Intent intent = new Intent(getActivity(), EventInfoActivity.class);

        // Pass necessary data to the activity using Intent extras
        EventModel selectedEvent = eventModels.get(position);
        intent.putExtra("EVENT_ID", selectedEvent.getEventId());
        intent.putExtra("EVENT_NAME", selectedEvent.getEventName());
        intent.putExtra("EVENT_PRICE", selectedEvent.getEventPrice());
        intent.putExtra("IMAGE_URL", selectedEvent.getImageUrl());
        intent.putExtra("EVENT_HOST", selectedEvent.getEventHost());
        intent.putExtra("EVENT_DATE", selectedEvent.getEventDate());
        intent.putExtra("EVENT_GENRE", selectedEvent.getEventGenre());
        intent.putExtra("EVENT_DESCRIPTION", selectedEvent.getEventDescription());

        startActivity(intent);
    }

    public interface OnEventListRetrievedListener {
        void onEventListRetrieved(ArrayList<String> eventList);
    }
}
