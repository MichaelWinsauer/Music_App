package com.example.lyritic;

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class Player extends AppCompatActivity {

    ConstraintLayout clBase;
    ImageView ivCover;
    TextView txtTitle;
    TextView txtArtist;
    TextView txtDuration;
    ImageButton btnPlay;
    ImageButton btnPrev;
    ImageButton btnNext;
    ImageButton btnRepeat;
    ImageButton btnShuffle;
    MusicManager musicManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        initializeReferences();
        initializeEventListener();
        displayData();
    }

    private void initializeReferences() {
        musicManager = DataManager.getMusicManager();

        clBase = findViewById(R.id.clBase);
        ivCover = findViewById(R.id.ivCover);
        txtTitle = findViewById(R.id.txtSongTitle);
        txtArtist = findViewById(R.id.txtSongArtist);
        txtDuration = findViewById(R.id.txtPosition);
        btnPlay = findViewById(R.id.imgbtnPlay);
        btnPrev = findViewById(R.id.imgbtnPrev);
        btnNext = findViewById(R.id.imgbtnNext);
        btnRepeat = findViewById(R.id.imgbtnRepeat);
        btnShuffle = findViewById(R.id.imgbtnShuffle);
    }

    private void initializeEventListener() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleSong();

            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleLoop();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.skipSong();
                refreshData();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.backSong();
                refreshData();
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleShuffle();
            }
        });
    }

    private void displayData() {
        refreshData();
    }

    private void displayImage() {
        if(musicManager.getCurrentSong().getCover() != null ) {
            ivCover.setImageBitmap(Tools.createClippingMask(
                    musicManager.getCurrentSong().getCover(),
                    BitmapFactory.decodeResource(getResources(), R.drawable.cover_mask),
                    true
                    )
            );

        } else {
            ivCover.setImageBitmap(Tools.createClippingMask(
                    BitmapFactory.decodeResource(getResources(), R.drawable.gthf61nec1h41),
                    BitmapFactory.decodeResource(getResources(), R.drawable.cover_mask),
                    true
                    )
            );
        }
    }

    private void refreshData() {
        txtTitle.setText(musicManager.getCurrentSong().getTitle());
        txtArtist.setText(musicManager.getCurrentSong().getInterpret());
        txtDuration.setText(musicManager.getCurrentSong().durationToString((long) musicManager.getCurrentSong().getDuration()));

        displayImage();
    }
}
