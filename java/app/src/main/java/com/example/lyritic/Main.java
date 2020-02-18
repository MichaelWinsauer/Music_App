package com.example.lyritic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends AppCompatActivity {
    int external_storage_permission_code;
    LinearLayout llSongList;
    List<Song> songList;
    Context context;
    MusicManager musicManager;
    TextView currentSong;
    TextView currentArtist;
    Button btnPlay;
    SeekBar sbSongProgress;
    Handler sbHandler;
    Runnable sbUpdater;
    Menu menu;
    MenuItem menuItem;
    ConstraintSet cs;
    ConstraintSet csPlay;
    ConstraintLayout clPlayer;
    TransitionSet transition;

    float oldX;
    float oldY;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        initializeReferences();
        prepareMusicManager();
        initializeEventListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sorting, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(menuItem == null) {
            menuItem = menu.findItem(R.id.sortDate);
        }

        if(menuItem.getItemId() != item.getItemId() ) {
            songList = musicManager.sortSongList(item.getItemId());
        } else {
            Collections.reverse(songList);
        }

        llSongList.removeAllViews();
        createSongs(llSongList);

        menuItem = item;

        return true;
    }

    private void prepareMusicManager() {
        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            musicManager.setSongList(ContentLoader.load(getBaseContext()));
            songList = musicManager.getSongList();
            createSongs(llSongList);
        } else {
            requestPermission();
        }
    }

    private void initializeReferences() {
        musicManager = new MusicManager();
        llSongList = findViewById(R.id.llSongList);
        context = getBaseContext();
        currentSong = findViewById(R.id.txtCurrentSong);
        currentArtist = findViewById(R.id.txtCurrentArtist);
        btnPlay = findViewById(R.id.btnPlay);
        sbSongProgress = findViewById(R.id.sbSongPosition);
        sbHandler = new Handler();
        clPlayer = findViewById(R.id.clPlayer);
        cs = new ConstraintSet();
        csPlay = new ConstraintSet();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeEventListener() {

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayButton(view);
                musicManager.toggleSong();
            }
        });

        sbUpdater = new Runnable() {
            @Override
            public void run() {
                sbSongProgress.setProgress(musicManager.getPercentageProgress());
                sbHandler.postDelayed(this, 50);
            }
        };

        sbSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                musicManager.toggleSong();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicManager.skipTo(seekBar.getProgress());
                musicManager.toggleSong();
            }
        });

        btnPlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    transition = new TransitionSet().addTransition(new ChangeBounds()).setDuration(100).setStartDelay(150);
                    TransitionManager.beginDelayedTransition(clPlayer, transition);
                    ViewGroup.LayoutParams params = btnPlay.getLayoutParams();

                    params.height = Tools.dpToPx(100, Main.this);
                    params.width = Tools.dpToPx(100, Main.this);

                    btnPlay.setLayoutParams(params);
                } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    //Brauch ich hier wirklich was?
                    //eigentlich nur, wenn ich den Button tatsächlich als "Joystick" bewegen will

                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    transition = new TransitionSet().addTransition(new ChangeBounds()).setDuration(100);
                    TransitionManager.beginDelayedTransition(clPlayer, transition);
                    ViewGroup.LayoutParams params = btnPlay.getLayoutParams();

                    params.height = Tools.dpToPx(50, Main.this);
                    params.width = Tools.dpToPx(50, Main.this);

                    btnPlay.setLayoutParams(params);

                    //Check for the swiping direction

                    //LEFT - RIGHT
                    if(oldX > event.getX()) {

                    } else {

                    }

                    //UP - DOWN
                    if (oldY > event.getY()) {
                        musicManager.toggleLoop();
                        //nur temporär
                        if(musicManager.getPlayer().isLooping())
                            Toast.makeText(Main.this, "LOOOOOPING!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Main.this, "NOOOOO", Toast.LENGTH_SHORT).show();
                    } else {

                    }

                }
                return false;
            }
        });

        clPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, Player.class);
                DataManager.setMusicManager(musicManager);
                startActivity(intent);
            }
        });

        musicManager.setSeekBarData(sbHandler, sbUpdater);
    }


    private void createSongs(final LinearLayout llSongList) {

        sbSongProgress.setPadding(0, 0, 0, 0);

        if(musicManager.getCurrentSong() == null) {
            musicManager.setCurrentSong(songList.get(0));
            musicManager.setSongsByCurrentSong();
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
            final Button btnPlaySong = new Button(this);

            clSong.setTag(s.getId());
            txtSongTitle.setId(View.generateViewId());
            txtSongArtist.setId(View.generateViewId());
            txtSongDuration.setId(View.generateViewId());
            viewSeperator.setId(View.generateViewId());
            clSong.setId(View.generateViewId());
            btnPlaySong.setId(View.generateViewId());

            new ConstraintSet().constrainHeight(clSong.getId(), Tools.dpToPx(100, this));
            clSong.setBackgroundColor(this.getColor(R.color.colorSecondaryDark));
            clSong.addView(txtSongTitle, 0);
            clSong.addView(txtSongArtist, 1);
            clSong.addView(txtSongDuration, 2);
            clSong.addView(viewSeperator, 3);
            clSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    togglePlayButton(btnPlaySong);
                    togglePlayButton((btnPlay));
                    TransitionManager.beginDelayedTransition(clSong);
                    musicManager.changeSong(musicManager.getSongById((Integer) view.getTag()));
                    currentSong.setText(musicManager.getCurrentSong().getTitle());
                    currentArtist.setText(musicManager.getCurrentSong().getInterpret());

                    for(int i = 0; i < llSongList.getChildCount(); i++) {
                        if(llSongList.getChildAt(i).getId() == view.getId()) {
                            if(musicManager.getPlayer().isPlaying()) {
                                view.setBackgroundColor(Main.this.getColor(R.color.colorAccent));
                            } else {
                                view.setBackgroundColor(Main.this.getColor(R.color.colorSecondaryDark));
                            }
                        } else {
                            llSongList.getChildAt(i).setBackgroundColor(Main.this.getColor(R.color.colorSecondaryDark));
                        }
                    }
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
            cs.applyTo(clSong);

//            btnPlaySong.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
//            btnPlaySong.setWidth(Tools.dpToPx(25, this));
//            btnPlaySong.setHeight(Tools.dpToPx(35, this));
//
//            cs.centerVertically(btnPlaySong.getId(), clSong.getId());
//            cs.connect(btnPlaySong.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(0, this));
//            cs.connect(btnPlaySong.getId(), ConstraintSet.BOTTOM, clSong.getId(), ConstraintSet.BOTTOM, Tools.dpToPx(0, this));
//            cs.applyTo(clSong);

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