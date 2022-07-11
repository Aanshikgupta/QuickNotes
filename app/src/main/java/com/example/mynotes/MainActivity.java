package com.example.mynotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mynotes.adapters.NotesAdapter;
import com.example.mynotes.database.NotesDatabase;
import com.example.mynotes.databinding.ActivityMainBinding;
import com.example.mynotes.models.Note;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private NotesAdapter adapter;
    private List<Note> notesList;
    private NotesDatabase database;
    private RecyclerView notesRecyclerView;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onResume() {
        super.onResume();
        getNotes();
        if (adapter != null) {
            adapter.setNotes(notesList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        initialiseDB();

        getNotes();

        activityMainBinding.addNoteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateNoteActivity.class));
            }
        });

        setAdapter();

    }


    private void getNotes() {
        notesList = database.notesDao().getAllNotes();
    }

    private void initialiseDB() {
        database = NotesDatabase.getInstance(this);
    }

    private void setAdapter() {
        adapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        notesRecyclerView = activityMainBinding.notesRecyclerView;
        notesRecyclerView.setLayoutManager(layoutManager);
        notesRecyclerView.setAdapter(adapter);
    }


}