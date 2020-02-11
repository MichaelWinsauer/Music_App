package com.example.lyritic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class Main extends AppCompatActivity {
    int external_storage_permission_code;
    LinearLayout llSongList;
    List<Song> songList;
    Context context;
    MusicManager musicManager;
    TextView currentSong;
    TextView currentArtist;
    Button btnPlay;
    SeekBar sbSongPosition;
    Button btnOptions;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        musicManager = new MusicManager();
        llSongList = findViewById(R.id.llSongList);
        context = getBaseContext();
        currentSong = findViewById(R.id.txtCurrentSong);
        currentArtist = findViewById(R.id.txtCurrentArtist);
        btnPlay = findViewById(R.id.btnPlay);
        sbSongPosition = findViewById(R.id.sbSongPosition);
        btnOptions = findViewById(R.id.btnOptions);

        sbSongPosition.setPadding(0, 0, 0, 0);

        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            songList = ContentLoader.load(getBaseContext());
            createSongs(llSongList);
            musicManager.setSongList(songList);
        } else {
            requestPermission();
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayButton(view);
                musicManager.toggleSong();
            }
        });

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Main.this, "Du suckst", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createSongs(final LinearLayout llSongList) {

        if(musicManager.getCurrentSong() == null) {
            musicManager.setCurrentSong(songList.get(0));
        }

        if(currentSong.getText().equals("")) {
            currentSong.setText(songList.get(0).getTitle());
            currentArtist.setText(songList.get(0).getInterpret());
        }

        for(Song s : songList) {
            final ConstraintLayout clSong = new ConstraintLayout(this);
            final TextView txtSongTitle = new TextView(this);
            final TextView txtSongArtist = new TextView(this);
            final TextView txtSongDuration = new TextView(this);
            final View viewSeperator = new View(this);
//            final ImageView ivPlay = new ImageView(this);
            final Button btnPlaySong = new Button(this);



            ConstraintSet cs = new ConstraintSet();

            clSong.setTag(s.getId());
            txtSongTitle.setId(View.generateViewId());
            txtSongArtist.setId(View.generateViewId());
            txtSongDuration.setId(View.generateViewId());
            viewSeperator.setId(View.generateViewId());
//            ivPlay.setId(View.generateViewId());
            clSong.setId(View.generateViewId());
            btnPlaySong.setId(View.generateViewId());

            new ConstraintSet().constrainHeight(clSong.getId(), Tools.dpToPx(100, this));
            clSong.setBackgroundColor(this.getColor(R.color.colorSecondaryDark));
            clSong.addView(txtSongTitle, 0);
            clSong.addView(txtSongArtist, 1);
            clSong.addView(txtSongDuration, 2);
            clSong.addView(btnPlaySong, 3);
//            clSong.addView(ivPlay, 3);
            clSong.addView(viewSeperator, 4);
            clSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    musicManager.changeSong(musicManager.getSongById((Integer) view.getTag()));
                    currentSong.setText(musicManager.getCurrentSong().getTitle());
                    currentArtist.setText(musicManager.getCurrentSong().getInterpret());
                    togglePlayButton(btnPlaySong);
                    togglePlayButton((btnPlay));
                    //doesn't work yet... :D
//                    view.setBackgroundColor(Main.this.getColor(R.color.colorPrimary));

//                    for(Song song : songList) {
//                        if((Integer) view.getTag() != song.getId()) {
//                            for(View v : llSongList.getTouchables()){
//                                v.setBackgroundColor(Main.this.getColor(R.color.colorPrimaryDark));
//                            }
//                        }
//                    }
                }
            });

            cs.clone(clSong);

            txtSongTitle.setText(s.getTitle());
            txtSongTitle.setTextColor(this.getColor(R.color.colorPrimary));
            txtSongTitle.setTextSize(14);
            txtSongTitle.setWidth(Tools.dpToPx(215, this));
            txtSongTitle.setMaxLines(1);
            txtSongTitle.setEllipsize(TextUtils.TruncateAt.END);
            txtSongTitle.setTypeface(txtSongTitle.getTypeface(), Typeface.BOLD);

            cs.connect(txtSongTitle.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(15, this));
            cs.connect(txtSongTitle.getId(), ConstraintSet.LEFT, clSong.getId(), ConstraintSet.LEFT, Tools.dpToPx(75, this));
            cs.applyTo(clSong);

            txtSongArtist.setText(s.getInterpret());
            txtSongArtist.setTextColor(this.getColor(R.color.colorPrimary));
            txtSongArtist.setTextSize(10);
            txtSongArtist.setTypeface(txtSongArtist.getTypeface(), Typeface.ITALIC);

            cs.connect(txtSongArtist.getId(), ConstraintSet.TOP, txtSongTitle.getId(), ConstraintSet.BOTTOM, Tools.dpToPx(0, this));
            cs.connect(txtSongArtist.getId(), ConstraintSet.LEFT, txtSongTitle.getId(), ConstraintSet.LEFT, Tools.dpToPx(0, this));
            cs.connect(txtSongArtist.getId(), ConstraintSet.BOTTOM, clSong.getId(), ConstraintSet.BOTTOM, Tools.dpToPx(20, this));
            cs.applyTo(clSong);

            txtSongDuration.setText(s.durationToString(Math.round(s.getDuration())));
            txtSongDuration.setTextColor(this.getColor(R.color.colorAccent));
            txtSongDuration.setTextSize(10);

            cs.connect(txtSongDuration.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(25, this));
            cs.connect(txtSongDuration.getId(), ConstraintSet.RIGHT, clSong.getId(), ConstraintSet.RIGHT, Tools.dpToPx(20, this));
            cs.applyTo(clSong);

            viewSeperator.setBackground(this.getDrawable(R.drawable.song_seperator));

            cs.connect(viewSeperator.getId(), ConstraintSet.BOTTOM, clSong.getId(), ConstraintSet.BOTTOM, 0);
            cs.connect(viewSeperator.getId(), ConstraintSet.RIGHT, clSong.getId(), ConstraintSet.RIGHT, 0);
            cs.connect(viewSeperator.getId(), ConstraintSet.LEFT, txtSongTitle.getId(), ConstraintSet.LEFT, Tools.dpToPx(70, this));
//            cs.connect(viewSeperator.getId(), ConstraintSet.RIGHT, clSong.getId(), ConstraintSet.RIGHT, Tools.dpToPx(35, this));
//            cs.connect(viewSeperator.getId(), ConstraintSet.LEFT, txtSongTitle.getId(), ConstraintSet.LEFT, Tools.dpToPx(35, this));
            cs.applyTo(clSong);

//            ivPlay.setImageDrawable(this.getDrawable(R.drawable.playbutton_dark));
//            ivPlay.setLayoutParams(new ConstraintLayout.LayoutParams(Tools.dpToPx(35, this), Tools.dpToPx(35, this)));

//            cs.connect(ivPlay.getId(), ConstraintSet.LEFT, clSong.getId(), ConstraintSet.LEFT, Tools.dpToPx(20, this));
//            cs.connect(ivPlay.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(20, this));
//            cs.applyTo(clSong);

            btnPlaySong.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            btnPlaySong.setWidth(Tools.dpToPx(25, this));
            btnPlaySong.setHeight(Tools.dpToPx(35, this));

            cs.centerVertically(btnPlaySong.getId(), clSong.getId());
            cs.connect(btnPlaySong.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(0, this));
            cs.connect(btnPlaySong.getId(), ConstraintSet.BOTTOM, clSong.getId(), ConstraintSet.BOTTOM, Tools.dpToPx(0, this));
            cs.applyTo(clSong);

            llSongList.addView(clSong);
        }
    }

    private void togglePlayButton(View view) {
        AnimationDrawable adPlay;

        if(musicManager.getPlayer().isPlaying()) {
            view.setBackgroundResource(R.drawable.playbutton_animation_reverse);
            adPlay = (AnimationDrawable) view.getBackground();
            adPlay.start();
        } else {
            view.setBackgroundResource(R.drawable.playbutton_animation);
            adPlay = (AnimationDrawable) view.getBackground();
            adPlay.start();
        }
    }

    private void requestPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        }
        ActivityCompat.requestPermissions(Main.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, external_storage_permission_code);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == external_storage_permission_code) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                songList = ContentLoader.load(getBaseContext());
                createSongs(llSongList);
                musicManager.setSongList(songList);
            } else {
                Toast.makeText(this, "Keine Berechtigungen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}