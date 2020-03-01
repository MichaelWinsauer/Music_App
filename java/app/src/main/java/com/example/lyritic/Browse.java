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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Browse extends AppCompatActivity {
    int external_storage_permission_code;

    ConstraintLayout clMain;
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
    LinearLayout llSelection;
    ImageButton imgBtnPlay;
    ImageButton imgBtnAdd;



    float oldX;
    float oldY;
    Boolean isSelectionMode = false;
    Boolean isHoldingPlay = false;

    ConstraintLayout.LayoutParams defaultParams;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        initializeReferences();
        prepareMusicManager();
        initializeEventListener();


    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sorting, menu);
        this.menu = menu;

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(menuItem == null) {
            menuItem = menu.findItem(R.id.sortDate);
        }

        if(musicManager.getSongList().size() <= 0) {
            return true;
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

    @Override
    public void onResume() {
        super.onResume();

        refreshData();
    }

    @Override
    public void onBackPressed() {
        if(!isSelectionMode) {
            super.onBackPressed();
        } else {
            isSelectionMode = false;
            for (CheckBox cb : getAllCheckboxes()) {
                if(cb.isChecked()) {
                    cb.toggle();
                }
            }
            toggleSelection();
        }
    }

    private void toggleSelection() {
        if(isSelectionMode) {
            llSelection.setVisibility(View.VISIBLE);
            for(CheckBox c : getAllCheckboxes()) {
                c.setVisibility(View.VISIBLE);
            }
        } else {
            llSelection.setVisibility(View.GONE);
            for(CheckBox c : getAllCheckboxes()) {
                c.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initializeReferences() {
        if(musicManager == null) {
            if(DataManager.getMusicManager() != null) {
                musicManager = DataManager.getMusicManager();
            } else {
                musicManager = new MusicManager();
            }
        }

        clMain = findViewById(R.id.clMain);
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
        llSelection = findViewById(R.id.llSelection);
        imgBtnPlay = findViewById(R.id.imgBtnPlaySelection);
        imgBtnAdd = findViewById(R.id.imgBtnAddSelection);
    }

    private void prepareMusicManager() {
        if(musicManager.getSongList() != null && musicManager.getSongList().size() > 0) {
            if(llSongList.getChildCount() < 1) {
                createSongs(llSongList);
            }
        } else {
            Toast.makeText(this, "Keine Lieder im Musik-Ordner vorhanden!", Toast.LENGTH_SHORT).show();
        }
        toggleSelection();
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
                if(!musicManager.getPlayer().isPlaying()) {
                    callNextSong();
                }
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

        btnPlay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!isHoldingPlay) {
                    isHoldingPlay = true;

                    transition = new TransitionSet().addTransition(new ChangeBounds()).setDuration(100);
                    TransitionManager.beginDelayedTransition(clPlayer, transition);
                    ViewGroup.LayoutParams params = btnPlay.getLayoutParams();

                    params.height = Tools.dpToPx(100, Browse.this);
                    params.width = Tools.dpToPx(100, Browse.this);

                    btnPlay.setLayoutParams(params);
                }

                return false;
            }
        });

        btnPlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(!isHoldingPlay) {
                    return false;
                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    transition = new TransitionSet().addTransition(new ChangeBounds()).setDuration(100);
                    TransitionManager.beginDelayedTransition(clPlayer, transition);
                    ViewGroup.LayoutParams params = btnPlay.getLayoutParams();

                    params.height = Tools.dpToPx(50, Browse.this);
                    params.width = Tools.dpToPx(50, Browse.this);

                    btnPlay.setLayoutParams(params);

                    //Check for the swiping direction
                    if(Math.abs(oldX - event.getX()) > Math.abs(oldY - event.getY())) {
                        //LEFT - RIGHT
                        if(oldX > event.getX()) {
                            musicManager.backSong();
                            refreshData();
                            Toast.makeText(Browse.this, "Back", Toast.LENGTH_SHORT).show();
                        } else {
                            musicManager.skipSong();
                            refreshData();
                            Toast.makeText(Browse.this, "Skip", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //UP - DOWN
                        if (oldY > event.getY()) {
                            musicManager.toggleLoop();
                            Toast.makeText(Browse.this, "Loop", Toast.LENGTH_SHORT).show();
                        } else {
                            musicManager.toggleShuffle();
                            Toast.makeText(Browse.this, "Shuffle", Toast.LENGTH_SHORT).show();
                        }
                    }
                    isHoldingPlay = false;

                } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
//                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btnPlay.getLayoutParams();
//
//                    if(Math.abs(oldX - event.getX()) > Math.abs(oldY - event.getY())) {
//                        params.leftMargin = (int)(event.getX() - oldX);
//                        params.topMargin = (int)oldY;
//                    } else {
//                        params.topMargin = (int)(event.getY() - oldY);
//                        params.leftMargin = (int)oldX;
//                    }
//
//                    btnPlay.setLayoutParams(params);

                }

                return false;
            }
        });

        clPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Browse.this, Player.class);
                DataManager.setMusicManager(musicManager);
                startActivity(intent);
            }
        });

        musicManager.setSeekBarData(sbHandler, sbUpdater);

        imgBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> tmp = new ArrayList<>();
                for(CheckBox c : getAllCheckboxes() ) {

                    if(c.isChecked()) {
                        Song s = new Song();
                        s = musicManager.getSongById((Integer) ((ConstraintLayout)c.getParent()).getTag());
                        tmp.add(s);
                    }
                }
                if(tmp.size() > 0) {
                    isSelectionMode = false;
                    musicManager.setSongSelection(tmp);
                    toggleSelection();
                    refreshData();

                } else {
                    Toast.makeText(Browse.this, "Keine Lieder ausgewählt", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createSongs(final LinearLayout llSongList) {

        songList = musicManager.getSongList();
        sbSongProgress.setPadding(0, 0, 0, 0);

        if(musicManager.getCurrentSong() == null) {
            musicManager.setCurrentSong(songList.get(0));
            musicManager.setSongsByCurrentSong();
        }

        if(currentSong.getText().equals("")) {
            currentSong.setText(musicManager.getCurrentSong().getTitle());
            currentArtist.setText(musicManager.getCurrentSong().getInterpret());
        }

        for(Song s : songList) {
            final ConstraintLayout clSong = new ConstraintLayout(this);
            final TextView txtSongTitle = new TextView(this);
            final TextView txtSongArtist = new TextView(this);
            final TextView txtSongDuration = new TextView(this);
            final View viewSeperator = new View(this);
            final Button btnPlaySong = new Button(this);
            final ImageView imgView = new ImageView(this);
            final CheckBox cbSelect = new CheckBox(this);

            clSong.setTag(s.getId());
            txtSongTitle.setId(View.generateViewId());
            txtSongArtist.setId(View.generateViewId());
            txtSongDuration.setId(View.generateViewId());
            viewSeperator.setId(View.generateViewId());
            clSong.setId(View.generateViewId());
            btnPlaySong.setId(View.generateViewId());
            imgView.setId(View.generateViewId());
            cbSelect.setId(View.generateViewId());

            new ConstraintSet().constrainHeight(clSong.getId(), Tools.dpToPx(100, this));
            clSong.setBackgroundColor(this.getColor(R.color.colorSecondaryDark));
            clSong.addView(txtSongTitle, 0);
            clSong.addView(txtSongArtist, 1);
            clSong.addView(txtSongDuration, 2);
            clSong.addView(viewSeperator, 3);
            clSong.addView(imgView, 4);
            clSong.addView(cbSelect, 5);

            clSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isSelectionMode) {
                        togglePlayButton(btnPlaySong);
                        togglePlayButton((btnPlay));
                        TransitionManager.beginDelayedTransition(clSong);
                        musicManager.changeSong(musicManager.getSongById((Integer) view.getTag()));

                        refreshData();
                    } else {
                        cbSelect.toggle();

//                        musicManager.getSongById((Integer)clSong.getTag()).toggleSelection();
                    }
                }
            });

            clSong.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    isSelectionMode = true;

                    toggleSelection();


                    return false;
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

//            imgView.setImageBitmap(s.getCover());

            cs.connect(imgView.getId(), ConstraintSet.LEFT, clSong.getId(), ConstraintSet.LEFT, 0);
            cs.connect(imgView.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, 0);
            cs.applyTo(clSong);

            cbSelect.setHighlightColor(getColor(R.color.colorAccent));

            cs.connect(cbSelect.getId(), ConstraintSet.LEFT , clSong.getId(), ConstraintSet.LEFT, Tools.dpToPx(20, this));
            cs.connect(cbSelect.getId(), ConstraintSet.TOP , clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(20, this));
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

    private void refreshData() {

        if(musicManager.getSongList().size() <= 0) {
            return;
        }
        currentSong.setText(musicManager.getCurrentSong().getTitle());
        currentArtist.setText(musicManager.getCurrentSong().getInterpret());
        btnPlay.setBackgroundResource(R.drawable.playbutton_animation);

        for(int i = 0; i < llSongList.getChildCount(); i++) {
            if((Integer)llSongList.getChildAt(i).getTag() == musicManager.getCurrentSong().getId()) {
                llSongList.getChildAt(i).setBackgroundColor(Browse.this.getColor(R.color.colorAccent));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(2)).setTextColor(getColor(R.color.colorPrimaryDark));
            } else {
                llSongList.getChildAt(i).setBackgroundColor(getColor(R.color.colorSecondaryDark));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(2)).setTextColor(getColor(R.color.colorAccent));
            }
        }
    }

    private void callNextSong() {
        musicManager.changeSong(musicManager.getNextSong());
        musicManager.setSongsByCurrentSong();
    }

    private List<View> getChildren(ViewGroup vg) {
        List<View> views = new ArrayList<>();

        for(int i = 0; i < vg.getChildCount(); i++) {
            views.add(vg.getChildAt(i));
        }

        return views;
    }

    private List<CheckBox> getAllCheckboxes() {

        List<CheckBox> checkBoxes = new ArrayList<>();

        for(View view : getChildren(llSongList))  {
            if(view instanceof ViewGroup) {
                for(View cb : getChildren((ViewGroup) view)) {
                    if(cb instanceof CheckBox) {
                        checkBoxes.add((CheckBox) cb);
                    }
                }
            }
        }

        return checkBoxes;
    }
}