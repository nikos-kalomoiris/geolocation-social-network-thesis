package com.example.thesis.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thesis.DatabaseModels.Event;
import com.example.thesis.DatabaseModels.Note;
import com.example.thesis.R;
import com.example.thesis.Utility.Adapters.EventsRecyclerViewAdapter;
import com.example.thesis.Utility.Adapters.Markers.ClusterMarker;
import com.example.thesis.Utility.Adapters.NotesRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private static ViewPager2 mainPager;
    private RecyclerView recyclerView;
    private NotesRecyclerViewAdapter adapter;
    private ArrayList<ClusterMarker> notesList = new ArrayList<>();
    private DatabaseReference notesRef;
    private ChildEventListener notesListener;
    private FirebaseUser user;
    boolean firstTime = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPager = getActivity().findViewById(R.id.mainPager); //Get mainViewPager to disable swipe in mapFragment
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Debug", "Notes List Created");
        createNotesListener();
        firstTime = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        recyclerView = view.findViewById(R.id.notesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new NotesRecyclerViewAdapter(getContext(), notesList);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Debug", "NotesFragment - onResume");
        mainPager = getActivity().findViewById(R.id.mainPager);
        mainPager.setUserInputEnabled(true);
        if(!firstTime) {
            setNotesListener();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        notesList.clear();
        if(notesListener != null) {
            notesRef.removeEventListener(notesListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notesList.clear();
//        if(notesListener != null) {
//            notesRef.removeEventListener(notesListener);
//        }

    }

    private void setNotesListener() {
        notesList.clear();
        notesRef = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.notes_collection));

        notesRef.addChildEventListener(notesListener);
    }

    private void createNotesListener() {
        notesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Note currNote = dataSnapshot.getValue(Note.class);
                String key = dataSnapshot.getKey();

                DatabaseReference author = FirebaseDatabase.getInstance().getReference()
                        .child(getString(R.string.users_collection))
                        .child(user.getUid())
                        .child(getString(R.string.friends_list_collection))
                        .child(currNote.getAuthor().getuId());

                author.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() || currNote.getAuthor().getuId().equals(user.getUid())) {
                            ClusterMarker note = new ClusterMarker("Note", currNote, key);
                            notesList.add(note);
                            adapter.updateNotesList(notesList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Error", databaseError.getMessage());
                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Note currNote = dataSnapshot.getValue(Note.class);
                String key = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        };
    }
}


