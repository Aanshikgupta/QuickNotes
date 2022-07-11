package com.example.mynotes.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.CreateNoteActivity;
import com.example.mynotes.R;
import com.example.mynotes.models.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    Context context;
    List<Note> notes;

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public NotesAdapter(Context context, List<Note> notesList) {
        this.context = context;
        this.notes = notesList;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        Note note = notes.get(position);
        holder.noteText.setText(note.getNoteText());
        holder.noteTitle.setText(note.getTitle());
        holder.noteSubtitle.setText(note.getSubTitle());
        holder.noteDateTime.setText(note.getDateTime());
        if (note.getImagePath() != null && !note.getImagePath().equals("")) {
            Bitmap bmImg = BitmapFactory.decodeFile(note.getImagePath());
            holder.noteImage.setImageBitmap(bmImg);
            holder.noteImage.setVisibility(View.VISIBLE);
        } else {
            holder.noteImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateNoteActivity.class);
                intent.putExtra("NOTE_ID", note.getId());
                context.startActivity(intent);
            }
        });

        if (note.getColor() != null && !note.getColor().equals("")) {
            GradientDrawable gradientDrawable = (GradientDrawable) holder.noteItemLayout.getBackground();
            gradientDrawable.setColor(Color.parseColor(note.getColor()));
        }


    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteSubtitle, noteDateTime, noteText;
        ImageView noteImage;
        LinearLayout noteItemLayout;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteItemTitle);
            noteSubtitle = itemView.findViewById(R.id.noteItemSubTitle);
            noteDateTime = itemView.findViewById(R.id.noteItemDateTime);
            noteText = itemView.findViewById(R.id.noteItemNoteText);
            noteImage = itemView.findViewById(R.id.noteItemImageView);
            noteItemLayout = itemView.findViewById(R.id.noteItemLayout);
        }
    }
}