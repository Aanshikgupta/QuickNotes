package com.example.mynotes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mynotes.database.NotesDatabase;
import com.example.mynotes.databinding.ActivityCreateNoteBinding;
import com.example.mynotes.models.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private ActivityCreateNoteBinding activityCreateNoteBinding;
    private NotesDatabase database;
    private int noteId;
    private Note selectedNote;
    private String selectedColor, selectedImage = "";
    private BottomSheetBehavior bottomSheetBehavior = null;
    private static final int REQUEST_CODE_STORAGE = 1;
    private String webLink = null;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        activityCreateNoteBinding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(activityCreateNoteBinding.getRoot());
        database = NotesDatabase.getInstance(this);


        basicSetUp();
        intiBottomSheet();
        colorSetup();
        setSubtitleIndicatorColor();


        activityCreateNoteBinding.imageBackIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        saveNote();

    }

    private void basicSetUp() {

        selectedColor = "#333333";
        selectedNote = null;
        noteId = getIntent().getIntExtra("NOTE_ID", -1);


        if (noteId != -1) {
            selectedNote = database.notesDao().getNote(noteId);
            selectedImage = selectedNote.getImagePath() == null ? selectedImage : selectedNote.getImagePath();
            selectedColor = selectedNote.getColor() == null ? selectedColor : selectedNote.getColor();
        }
        if (selectedNote != null) {

            //show image
            if (selectedNote.getImagePath() != null && !selectedNote.getImagePath().equals("")) {
                Bitmap bmImg = BitmapFactory.decodeFile(selectedNote.getImagePath());
                activityCreateNoteBinding.imageNote.setImageBitmap(bmImg);
                activityCreateNoteBinding.imageNote.setVisibility(View.VISIBLE);
            }

            activityCreateNoteBinding.inputNoteTitle.setText(selectedNote.getTitle());
            activityCreateNoteBinding.inputNoteSubTitle.setText(selectedNote.getSubTitle());
            activityCreateNoteBinding.textDateTime.setText(selectedNote.getDateTime());
            activityCreateNoteBinding.inputNote.setText(selectedNote.getNoteText());
            webLink = selectedNote.getWebLink();
            if (webLink != null && !webLink.isEmpty()) {
                activityCreateNoteBinding.webUrlTextView.setText(webLink);
                activityCreateNoteBinding.webLinkLinearLayout.setVisibility(View.VISIBLE);
            }
            selectedColor = selectedNote.getColor();
            setSubtitleIndicatorColor();
        }

        activityCreateNoteBinding.deleteUrlImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webLink = null;
                activityCreateNoteBinding.webUrlTextView.setText("");
                activityCreateNoteBinding.webLinkLinearLayout.setVisibility(View.GONE);

            }
        });
    }

    private void saveNote() {

        activityCreateNoteBinding.saveNoteImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM YYYY HH:mm a", Locale.getDefault());
                selectedNote = selectedNote == null ? new Note() : selectedNote;

                selectedNote.setTitle(activityCreateNoteBinding.inputNoteTitle.getText().toString());
                selectedNote.setSubTitle(activityCreateNoteBinding.inputNoteSubTitle.getText().toString());
                selectedNote.setNoteText(activityCreateNoteBinding.inputNote.getText().toString());
                selectedNote.setDateTime(simpleDateFormat.format(new Date()));
                selectedNote.setImagePath(selectedImage);
                selectedNote.setWebLink(webLink);
                selectedNote.setColor(selectedColor);

                NotesDatabase.databaseWriterExecutor.execute(() -> {
                    database.notesDao().insertNote(selectedNote);
                });

                Toast.makeText(CreateNoteActivity.this, "Note Inserted Successfully", Toast.LENGTH_SHORT).show();

                finish();
            }
        });


    }

    private void colorSetup() {

        final ImageView imageColor1 = findViewById(R.id.image_color_1);
        final ImageView imageColor2 = findViewById(R.id.image_color_2);
        final ImageView imageColor3 = findViewById(R.id.image_color_3);
        final ImageView imageColor4 = findViewById(R.id.image_color_4);
        final ImageView imageColor5 = findViewById(R.id.image_color_5);

        final View viewColor1 = findViewById(R.id.view_color_1);
        final View viewColor2 = findViewById(R.id.view_color_2);
        final View viewColor3 = findViewById(R.id.view_color_3);
        final View viewColor4 = findViewById(R.id.view_color_4);
        final View viewColor5 = findViewById(R.id.view_color_5);

        viewColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#333333";
                imageColor1.setVisibility(View.VISIBLE);
                imageColor2.setVisibility(View.INVISIBLE);
                imageColor3.setVisibility(View.INVISIBLE);
                imageColor4.setVisibility(View.INVISIBLE);
                imageColor5.setVisibility(View.INVISIBLE);
                setSubtitleIndicatorColor();
            }
        });

        viewColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#FDBE3B";
                imageColor1.setVisibility(View.INVISIBLE);
                imageColor2.setVisibility(View.VISIBLE);
                imageColor3.setVisibility(View.INVISIBLE);
                imageColor4.setVisibility(View.INVISIBLE);
                imageColor5.setVisibility(View.INVISIBLE);
                setSubtitleIndicatorColor();
            }
        });

        viewColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#FF4842";
                imageColor1.setVisibility(View.INVISIBLE);
                imageColor2.setVisibility(View.INVISIBLE);
                imageColor3.setVisibility(View.VISIBLE);
                imageColor4.setVisibility(View.INVISIBLE);
                imageColor5.setVisibility(View.INVISIBLE);
                setSubtitleIndicatorColor();
            }
        });

        viewColor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#3A52FC";
                imageColor1.setVisibility(View.INVISIBLE);
                imageColor2.setVisibility(View.INVISIBLE);
                imageColor3.setVisibility(View.INVISIBLE);
                imageColor4.setVisibility(View.VISIBLE);
                imageColor5.setVisibility(View.INVISIBLE);
                setSubtitleIndicatorColor();
            }
        });

        viewColor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedColor = "#000000";
                imageColor1.setVisibility(View.INVISIBLE);
                imageColor2.setVisibility(View.INVISIBLE);
                imageColor3.setVisibility(View.INVISIBLE);
                imageColor4.setVisibility(View.INVISIBLE);
                imageColor5.setVisibility(View.VISIBLE);
                setSubtitleIndicatorColor();
            }
        });


        ((ImageView) findViewById(R.id.addImageImageView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior != null)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                requestPermission();
            }
        });


        ((ImageView) findViewById(R.id.addWebUrlImageImageView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showUrlDialog();
            }
        });

        ((ImageView) findViewById(R.id.deleteNoteImageView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (selectedNote != null) {
                    database.notesDao().deleteNote(selectedNote);
                    finish();
                } else {
                    Toast.makeText(CreateNoteActivity.this, "Create a note first", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showUrlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_link, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        dialogButtonClickHandle(alertDialog);

    }

    private void dialogButtonClickHandle(AlertDialog alertDialog) {
        //YES BUTTON
        alertDialog.findViewById(R.id.alert_dialog_cancel_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        //NO BUTTON
        alertDialog.findViewById(R.id.alert_dialog_add_text_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webLink = ((EditText) alertDialog.findViewById(R.id.alert_dialog_url_edit_text_view)).getText().toString();
                if (webLink == null || webLink.isEmpty()) {
                    webLink = null;
                    Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_LONG).show();
                } else if (!Patterns.WEB_URL.matcher(webLink).matches()) {
                    webLink = null;
                    Toast.makeText(CreateNoteActivity.this, "Enter valid URL", Toast.LENGTH_LONG).show();
                } else {
                    activityCreateNoteBinding.webUrlTextView.setText(webLink);
                    activityCreateNoteBinding.webLinkLinearLayout.setVisibility(View.VISIBLE);
                    alertDialog.dismiss();
                }

            }
        });


    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
        } else {
            selectImage();
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ImageView noteImage = activityCreateNoteBinding.imageNote;
                        noteImage.setImageBitmap(bitmap);
                        noteImage.setVisibility(View.VISIBLE);
                        getPathFromUri(selectedImageUri);
                    } catch (Exception e) {
                        Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void getPathFromUri(Uri selectedImageUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedImage = cursor.getString(columnIndex);
        } else {

        }
        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }
    }

    private void setSubtitleIndicatorColor() {

        GradientDrawable gradientDrawable = (GradientDrawable) activityCreateNoteBinding.subtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedColor));

    }

    private void intiBottomSheet() {

        final LinearLayout linearLayout = findViewById(R.id.layoutMiscellaneous);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);

        findViewById(R.id.textMiscellaneuos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


    }
}