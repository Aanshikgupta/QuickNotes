package com.example.mynotes.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mynotes.dao.NotesDao;
import com.example.mynotes.models.Note;
import com.example.mynotes.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = Constants.DB_VERSION, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
    private static NotesDatabase notesDatabase;

    public abstract NotesDao notesDao();

    public static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(Constants.NO_OF_THREADS);
    public static final RoomDatabase.Callback sRoomDbCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            databaseWriterExecutor.execute(() -> {
                NotesDao dao = notesDatabase.notesDao();


                //clean slate
                dao.deleteAllNotes();


            });
        }
    };


    public static synchronized NotesDatabase getInstance(Context context) {
        if (notesDatabase == null) {
            notesDatabase = Room.databaseBuilder(context.getApplicationContext(), NotesDatabase.class, Constants.DB_NAME)
                    .addCallback(sRoomDbCallBack)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return notesDatabase;

    }
}