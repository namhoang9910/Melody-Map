package com.example.melodymap;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventInfoActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_info_fragment);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Format & load event Name
        String eventName = getIntent().getStringExtra("EVENT_NAME");
        TextView eventName_textView = findViewById(R.id.INFO_eventName);
        eventName_textView.setText(eventName);

        // Format & load host
        String eventHost = getIntent().getStringExtra("EVENT_HOST");
        TextView eventHost_textView = findViewById(R.id.INFO_eventHost);
        eventHost_textView.setText("Hosted by " + eventHost);


        // Format & load date
        long eventDateMillis = getIntent().getLongExtra("EVENT_DATE", 0);
        Date date = new Date(eventDateMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy @ hh:mm a", Locale.ENGLISH);

        String dateString = dateFormat.format(date);
        TextView eventDateTextView = findViewById(R.id.INFO_eventDate);
        eventDateTextView.setText(dateString);


        // Format & load genre
        String genre = getIntent().getStringExtra("EVENT_GENRE");
        TextView eventGenre_textView = findViewById(R.id.INFO_eventGenre);
        eventGenre_textView.setText(genre);

        // Format & load description
        String description = getIntent().getStringExtra("EVENT_DESCRIPTION");
        TextView eventDescription_textView = findViewById(R.id.INFO_eventDescription);
        eventDescription_textView.setText(description);


        // Format & load price
        double price = getIntent().getDoubleExtra("EVENT_PRICE", 0.0);
        String formattedPrice;
        if (price == 0) {
            formattedPrice = "Free";
        } else {
            formattedPrice = "$" + String.format("%.2f", price);
        }
        TextView eventPrice_textView = findViewById(R.id.INFO_eventPrice);
        eventPrice_textView.setText(formattedPrice);


        // Format & load image
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        ImageView imageView = findViewById(R.id.INFO_imageView);
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .fit()
                .centerCrop()
                .into(imageView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // When user selects "I'm In" button
        Button imInButton = findViewById(R.id.imInButton);
        imInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add to My Events
                String eventId = getIntent().getStringExtra("EVENT_ID"); // Assuming you have a way to retrieve the unique ID of the event
                addToMyEvents(eventId);
                // Close the activity after done
                finish();
            }
        });

        // Fetch venue address and display it
        fetchVenueAddressAndDisplay(eventHost);
    }

    private void addToMyEvents(String eventId) {
        // Get the current user ID
        //String userId = getCurrentUserId();
        String userId = "user1";

        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> eventList = (ArrayList<String>) document.get("eventList");
                            if (eventList != null && eventList.contains(eventId)) {
                                Log.d(TAG, "Already Registered");
                                Toast.makeText(EventInfoActivity.this, "Already Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                // Add the event ID to the user's eventList
                                db.collection("users")
                                        .document(userId)
                                        .update("eventList", FieldValue.arrayUnion(eventId))
                                        .addOnSuccessListener(aVoid -> {
                                            // Event ID added to user's eventList successfully
                                            Toast.makeText(EventInfoActivity.this, "Event added to My Events", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Error occurred while adding event ID to user's eventList
                                            Toast.makeText(EventInfoActivity.this, "Failed to add event to My Events", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error adding event to My Events", e);
                                        });
                            }
                        } else {
                            Log.d(TAG, "No such document");
                            Toast.makeText(EventInfoActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        Toast.makeText(EventInfoActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void fetchVenueAddressAndDisplay(String eventHost) {
        // Query to fetch venue address from Firestore based on venue name
            db.collection("venues")
                    .whereEqualTo("name", eventHost)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            String venueAddress = "Address Not Added";
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    // Venue document found, get venue address
                                    venueAddress = document.getString("address");
                                }
                            }
                            // Display the venue address in a TextView
                            TextView venueAddressTextView = findViewById(R.id.INFO_eventAddress);
                            // No need to continue looping as we expect only one match
                            venueAddressTextView.setText(venueAddress);

                        }
                    });
    }
}