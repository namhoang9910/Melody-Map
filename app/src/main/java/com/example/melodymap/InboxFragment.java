package com.example.melodymap;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.auth.FirebaseUser;

public class InboxFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView messageList;
    private MessageAdapter messageAdapter;
    private List<Message> messages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        EditText inputField = view.findViewById(R.id.inputField);
        Button sendButton = view.findViewById(R.id.sendButton);

        messageList = view.findViewById(R.id.messageList);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        messageList.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageList.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> {
            String messageText = inputField.getText().toString();
            if (!messageText.isEmpty()) {
                Map<String, Object> message = new HashMap<>();
                message.put("text", messageText);
                message.put("user", mAuth.getCurrentUser().getEmail());
                db.collection("messages").add(message);
                inputField.setText("");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadMessages();
        }
    }

    private void loadMessages() {
        String userEmail = mAuth.getCurrentUser().getEmail();
        db.collection("messages")
                .whereEqualTo("user", userEmail)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String user = (String) dc.getDocument().getData().get("user");
                            String text = (String) dc.getDocument().getData().get("text");
                            messages.add(new Message(user, text));
                            messageAdapter.notifyDataSetChanged();
                            messageList.scrollToPosition(messages.size() - 1);
                        }
                    }
                });
    }

    private void searchVenues(String query) {
        db.collection("venues")
                .whereEqualTo("name", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String venueName = document.getString("name");
                            String messageText = "Hello " + venueName + ", I would like to inquire...";
                            sendMessageToVenue(venueName, messageText);
                        }
                    } else {
                        Log.d("Error getting documents: ", task.getException().getMessage());
                        Toast.makeText(getActivity(), "Error getting venues: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendMessageToVenue(String venueName, String messageText) {
        Map<String, Object> message = new HashMap<>();
        message.put("text", messageText);
        message.put("user", mAuth.getCurrentUser().getEmail());
        message.put("venue", venueName);
        db.collection("messages").add(message);
    }
}