package com.example.melodymap;

import android.os.Bundle;
import android.util.Log;
import com.example.melodymap.BuildConfig;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// Implement Chat GPT's into the app
public class InboxFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    private EditText message;
    private ImageView send;
    private List<MessageModel> list;

    private MessageAdapter adapter;

    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();

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

        recyclerView = view.findViewById(R.id.messageRecyclerView);
        message = view.findViewById(R.id.message);
        send = view.findViewById(R.id.send_message_btn);

        list = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(list);
        recyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = message.getText().toString();

                if (question.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter a question", Toast.LENGTH_SHORT).show();
                } else {
                    addToChat(question, MessageModel.SENT_BY_ME);
                    message.setText("");

                    callAPI(question);
                }
            }
        });

        return view;
    }

    private void callAPI(String question) {
        list.add(new MessageModel("Typing...", MessageModel.SENT_BY_BOT));

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray messagesArray = new JSONArray();
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant specialized in promoting music events from a company called MelodyMap. " +
                    "Provide engaging and informative responses about upcoming music events, concerts, and festivals.");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);

            messagesArray.put(systemMessage);
            messagesArray.put(userMessage);

            jsonObject.put("messages", messagesArray);
            jsonObject.put("max_tokens", 200);
            jsonObject.put("temperature", 0.7);


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + BuildConfig.AI_API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addResponse("Failed to load: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(response.body().string());
                        JSONArray choicesArray = responseJson.getJSONArray("choices");
                        JSONObject firstChoice = choicesArray.getJSONObject(0);
                        String result = firstChoice.getJSONObject("message").getString("content");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addResponse(result.trim());
                            }
                        });
                    } catch (JSONException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addResponse("Failed to parse response: " + e.getMessage());
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                addResponse("Failed to load: " + response.body().string());
                            } catch (IOException e) {
                                addResponse("Failed to load and parse error body: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    private void addResponse(String s) {
        list.remove(list.size() - 1);
        addToChat(s, MessageModel.SENT_BY_BOT);
    }

    private void addToChat(String message, String sentBy) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.add(new MessageModel(message, sentBy));
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }
}
