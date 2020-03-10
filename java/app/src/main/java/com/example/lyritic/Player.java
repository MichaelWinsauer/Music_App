package com.example.lyritic;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.marcinmoskala.arcseekbar.ArcSeekBar;

import java.io.File;


public class Player extends AppCompatActivity {

    private ConstraintLayout clBase;
    private ImageView ivCover;
    private TextView txtTitle;
    private TextView txtArtist;
    private TextView txtDuration;
    private ImageButton btnPlay;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private MusicManager musicManager;
    private ImageButton btnDetails;
    private ArcSeekBar asbSongProgress;
    private Runnable sbUpdater;
    private Handler sbHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        musicManager = DataManager.getMusicManager();

        initializeReferences();
        initializeEventListener();
        if(musicManager.getCurrentSong() != null) {
            displayData();
        }

    }

    private void initializeReferences() {
        sbHandler = new Handler();

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
        btnDetails = findViewById(R.id.imgBtnDetails);
        asbSongProgress = findViewById(R.id.asbSongProgression);

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
                if(musicManager.getPlayer().isLooping()) {
                    v.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.colorAccent));
                } else {
                    v.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.colorPrimaryDark));
                }
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
                if(musicManager.getShuffled()) {
                    v.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.colorAccent));
                } else {
                    v.setBackgroundTintList(getBaseContext().getResources().getColorStateList(R.color.colorPrimaryDark));
                }
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Player.this, Details.class);
                startActivity(intent);
            }
        });

        sbUpdater = new Runnable() {
            @Override
            public void run() {
                asbSongProgress.setProgress(musicManager.getPercentageProgress());
                sbHandler.postDelayed(this, 50);
            }
        };

        musicManager.setSeekBarData(sbHandler, sbUpdater);
        //Most retarded work-around I've ever seen lol...
        musicManager.toggleSong();
        musicManager.toggleSong();
    }

    private void displayData() {
        refreshData();
    }

    private void displayImage() {


        if(musicManager.getCurrentSong().getCover() != null ) {

            ivCover.setImageBitmap(Tools.createClippingMask(
                    musicManager.getCurrentSong().getCover(),
                    BitmapFactory.decodeResource(getResources(), R.drawable.cover_mask)
                    )
            );
        } else {
            ivCover.setImageBitmap(Tools.createClippingMask(
                    BitmapFactory.decodeResource(getResources(), R.drawable.missing_img),
                    BitmapFactory.decodeResource(getResources(), R.drawable.cover_mask)
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
