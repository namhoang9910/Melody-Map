package com.example.melodymap;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Event_RecyclerViewAdapter extends RecyclerView.Adapter<Event_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<EventModel> eventModels;

    public Event_RecyclerViewAdapter(Context context, ArrayList<EventModel> eventModels) {
        this.context = context;
        this.eventModels = eventModels;
    }
    @NonNull
    @Override
    public Event_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout = giving a look to each row
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new Event_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Event_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // binding process as it comes back to us when scrolling
        holder.imageView.setImageResource(eventModels.get(position).getImage());
        holder.eventName_tv.setText(eventModels.get(position).getEventName());
        holder.eventDate_tv.setText(eventModels.get(position).getEventDate());

        // For int
        double eventPrice = eventModels.get(position).getEventPrice();
        if (eventPrice == 0) {
            holder.eventPrice_tv.setText("Free"); // Set text to "Free" if price is 0
        } else {
            holder.eventPrice_tv.setText("$" + eventPrice); // Otherwise, concatenate $ with the price string
        }
    }

    @Override
    public int getItemCount() {

        return eventModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // similar to onCreate
        ImageView imageView;
        TextView eventName_tv, eventDate_tv, eventPrice_tv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.eventImage);
            eventName_tv = itemView.findViewById(R.id.eventName);
            eventDate_tv = itemView.findViewById(R.id.eventDate);
            eventPrice_tv = itemView.findViewById(R.id.eventPrice);


        }
    }
}
