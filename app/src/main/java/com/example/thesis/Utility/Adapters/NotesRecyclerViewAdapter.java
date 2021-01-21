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
import com.example.thesis.Interfaces.OnListOptionsClick;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ClusterMarker> notesList = new ArrayList<>();
    private static OnListOptionsClick listOptionsClick;

    public NotesRecyclerViewAdapter(Context context, ArrayList<ClusterMarker> notesList) {
        this.context = context;
        this.notesList.addAll(notesList);
    }

    @NonNull
    @Override
    public NotesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notes_list_component, parent, false);
        return new NotesRecyclerViewAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NotesRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.noteImage.setImageResource(R.drawable.ic_fab_note);
        Glide.with(context)
                .load(notesList.get(position).getNote().getAuthor().getuIconUrl())
                .into(holder.authorImage);
        holder.title.setText(notesList.get(position).getTitle());
        holder.snippet.setText(notesList.get(position).getSnippet());
        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOptionsClick.clickDetails(notesList.get(position));
            }
        });

        holder.locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng location = new LatLng(
                        notesList.get(position).getNote().getGeoPoint().getLatitude(),
                        notesList.get(position).getNote().getGeoPoint().getLongitude()
                );
                listOptionsClick.clickLocation(location);
            }
        });
    }

    public void updateNotesList(ArrayList<ClusterMarker> notes) {
        notesList.clear();
        notesList.addAll(notes);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.notesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView noteImage, authorImage;
        TextView title, snippet;
        ImageButton detailsBtn, locationBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteImage = itemView.findViewById(R.id.noteImage);
            authorImage = itemView.findViewById(R.id.authorImage);
            title = itemView.findViewById(R.id.noteTitle);
            snippet = itemView.findViewById(R.id.noteSnippet);
            detailsBtn = itemView.findViewById(R.id.noteDetailsBtn);
            locationBtn = itemView.findViewById(R.id.noteLocationBtn);
        }
    }

    public static void setOnListOptionsClickInterface(OnListOptionsClick intf) {
        listOptionsClick = intf;
    }
}
