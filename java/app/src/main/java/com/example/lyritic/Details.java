package com.example.lyritic;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Details extends AppCompatActivity {

    MusicManager musicManager;
    EditText txtTitle;
    EditText txtArtist;
    EditText txtAlbum;
    EditText txtGenre;
    Button btnSave;
    ImageView imgCover;
    TextView txtSongTitleCover;
    TextView txtSongArtistCover;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        initializeReferences();
        loadCurrentSongData();
        initializeListener();
    }

    private void initializeReferences() {

        if(DataManager.getMusicManager() != null) {
            musicManager = DataManager.getMusicManager();
        }

        txtTitle = findViewById(R.id.txtTitle);
        txtArtist = findViewById(R.id.txtArtist);
        txtAlbum = findViewById(R.id.txtAlbum);
        txtGenre = findViewById(R.id.txtGenre);
        imgCover = findViewById(R.id.imgCover);
        txtSongTitleCover = findViewById(R.id.txtTitleCover);
        txtSongArtistCover = findViewById(R.id.txtArtistCover);
    }

    private void loadCurrentSongData() {
        txtTitle.setText(musicManager.getCurrentSong().getTitle());
        txtArtist.setText(musicManager.getCurrentSong().getInterpret());
        txtAlbum.setText(musicManager.getCurrentSong().getAlbum());
        txtGenre.setText(musicManager.getCurrentSong().getGenre());
        imgCover.setImageBitmap(musicManager.getCurrentSong().getCover());
        txtSongTitleCover.setText(musicManager.getCurrentSong().getTitle());
        txtSongArtistCover.setText(musicManager.getCurrentSong().getInterpret());
    }

    private void initializeListener() {
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //SAVE LOGIC
//            }
//        });
    }
}
