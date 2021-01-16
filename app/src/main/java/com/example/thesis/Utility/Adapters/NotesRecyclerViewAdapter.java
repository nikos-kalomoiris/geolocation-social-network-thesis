package com.example.thesis.Utility.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ClusterMarker> eventsList = new ArrayList<>();

    public NotesRecyclerViewAdapter(Context context, ArrayList<ClusterMarker> eventsList) {
        this.context = context;
        this.eventsList.addAll(eventsList);
    }

    @NonNull
    @Override
    public NotesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.events_list_component, parent, false);
        return new NotesRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NotesRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.eventImage.setImageResource(R.drawable.ic_fab_event);
        Glide.with(context)
                .load(eventsList.get(position).getEvent().getAuthor().getuIconUrl())
                .into(holder.authorImage);
        holder.title.setText(eventsList.get(position).getTitle());
        holder.snippet.setText(eventsList.get(position).getSnippet());
        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void updateEventsList(ArrayList<ClusterMarker> events) {
        eventsList.clear();
        eventsList.addAll(events);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView eventImage, authorImage;
        TextView title, snippet;
        ImageButton detailsBtn, locationBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            authorImage = itemView.findViewById(R.id.authorImage);
            title = itemView.findViewById(R.id.eventTitle);
            snippet = itemView.findViewById(R.id.eventSnippet);
            detailsBtn = itemView.findViewById(R.id.eventDetailsBtn);
            locationBtn = itemView.findViewById(R.id.eventLocationBtn);
        }
    }
}
