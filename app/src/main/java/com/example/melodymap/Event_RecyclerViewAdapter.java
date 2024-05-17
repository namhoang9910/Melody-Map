package com.example.melodymap;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Event_RecyclerViewAdapter extends RecyclerView.Adapter<Event_RecyclerViewAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<EventModel> eventModels;


    public Event_RecyclerViewAdapter(Context context, ArrayList<EventModel> eventModels,
                                     RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.eventModels = eventModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setFilteredList(ArrayList<EventModel> filteredList) {
        this.eventModels = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Event_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout = giving a look to each row
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new Event_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull Event_RecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.eventName_tv.setText(eventModels.get(position).getEventName());

        // Format imageUrl (string)
        String imageUrl = eventModels.get(position).getImageUrl();
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .fit()
                .centerCrop()
                .into(holder.imageView);

        // Format date (Timestamp)
        Timestamp eventDate = eventModels.get(position).getEventDate();
        Date date = eventDate.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd - HH:mm");
        String dateString = dateFormat.format(date);
        holder.eventDate_tv.setText(dateString);

        // Format price (double)
        double eventPrice = eventModels.get(position).getEventPrice();
        if (eventPrice == 0) {
            holder.eventPrice_tv.setText("Free");
        } else {
            holder.eventPrice_tv.setText("$" + eventPrice);
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
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            imageView = itemView.findViewById(R.id.eventImage);
            eventName_tv = itemView.findViewById(R.id.eventName);
            eventDate_tv = itemView.findViewById(R.id.eventDate);
            eventPrice_tv = itemView.findViewById(R.id.eventPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }

    public void clearList() {
        eventModels.clear();
        notifyDataSetChanged();
    }
}