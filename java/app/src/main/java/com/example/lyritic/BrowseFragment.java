package com.example.lyritic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BrowseFragment extends Fragment {

    private View root;
    private ConstraintLayout clMain;
    private LinearLayout llSongList;
    private List<Song> songList;
    private Context context;
    private MusicManager musicManager;
    private TextView currentSong;
    private TextView currentArtist;
    private Button btnPlay;
    private SeekBar sbSongProgress;
    private Handler sbHandler;
    private Runnable sbUpdater;
    private Menu menu;
    private MenuItem menuItem;
    private ConstraintSet cs;
    private ConstraintSet csPlay;
    private ConstraintLayout clPlayer;
    private TransitionSet transition;
    private LinearLayout llSelection;
    private ImageButton imgBtnPlay;
    private ImageButton imgBtnAdd;
    private FloatingActionButton fabBackToTop;
    private ScrollView svSongList;

    private float oldX;
    private float oldY;
    private Boolean isHoldingPlay = false;
    public BrowseFragment() {

    }

    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.browse, container, false);

        initializeReferences();
        prepareMusicManager();
        initializeEventListener();
        
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void toggleSelection() {
        if(musicManager.getSelectionMode()) {
            llSelection.setVisibility(View.VISIBLE);

        } else {
            llSelection.setVisibility(View.GONE);

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

        clMain = root.findViewById(R.id.clMain);
        llSongList = root.findViewById(R.id.llSongList);
        context = getActivity().getBaseContext();
        currentSong = root.findViewById(R.id.txtCurrentSong);
        currentArtist = root.findViewById(R.id.txtCurrentArtist);
        btnPlay = root.findViewById(R.id.btnPlay);
        sbSongProgress = root.findViewById(R.id.sbSongPosition);
        sbHandler = new Handler();
        clPlayer = root.findViewById(R.id.clPlayer);
        cs = new ConstraintSet();
        csPlay = new ConstraintSet();
        llSelection = root.findViewById(R.id.llSelection);
        imgBtnPlay = root.findViewById(R.id.imgBtnPlaySelection);
        imgBtnAdd = root.findViewById(R.id.imgBtnAddSelection);
        fabBackToTop = root.findViewById(R.id.fabBackToTop);
        svSongList = root.findViewById(R.id.svSongList);
    }

    private void prepareMusicManager() {
        if(musicManager.getSongList() != null && musicManager.getSongList().size() > 0) {
            if(llSongList.getChildCount() < 1) {
                createSongs(llSongList, musicManager.getSongList());
            }
        } else {
            Toast.makeText(root.getContext(), "Keine Lieder im Musik-Ordner vorhanden!", Toast.LENGTH_SHORT).show();
        }
        toggleSelection();
        fabBackToTop.hide();
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
//                    refreshData();
                }
                sbHandler.postDelayed(this, 50);
            }
        };

        setSeekBarData();

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

                    params.height = Tools.dpToPx(100, getActivity());
                    params.width = Tools.dpToPx(100, getActivity());

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

                    params.height = Tools.dpToPx(50, getActivity());
                    params.width = Tools.dpToPx(50, getActivity());

                    btnPlay.setLayoutParams(params);

                    //Check for the swiping direction
                    if(Math.abs(oldX - event.getX()) > Math.abs(oldY - event.getY())) {
                        //LEFT - RIGHT
                        if(oldX > event.getX()) {
                            musicManager.backSong();
                            refreshData();
                            Toast.makeText(root.getContext(), "Back", Toast.LENGTH_SHORT).show();
                        } else {
                            musicManager.skipSong();
                            refreshData();
                            Toast.makeText(root.getContext(), "Skip", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //UP - DOWN
                        if (oldY > event.getY()) {
                            musicManager.toggleLoop();
                            Toast.makeText(root.getContext(), "Loop", Toast.LENGTH_SHORT).show();
                        } else {
                            musicManager.toggleShuffle();
                            Toast.makeText(root.getContext(), "Shuffle", Toast.LENGTH_SHORT).show();
                        }
                    }
                    isHoldingPlay = false;
                }

                return false;
            }
        });

        clPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.halt();
                Intent intent = new Intent(getContext(), Player.class);
                DataManager.setMusicManager(musicManager);
                startActivity(intent);
            }
        });

        imgBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> tmp = new ArrayList<>();

                for(Song s : musicManager.getSongList()) {
                    if(s.getSelected()) {
                        tmp.add(s);
                    }
                }

                if(tmp.size() > 0) {
                    musicManager.setSelectionMode(false);
                    musicManager.setSongSelection(tmp);
                    musicManager.deselectAllSongs();
                    toggleSelection();
                    refreshData();
                } else {
                    Toast.makeText(root.getContext(), "No songs selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Song> tmp = new ArrayList<>();

                for(Song s : musicManager.getSongList()) {
                    if(s.getSelected()) {
                        tmp.add(s);
                    }
                }

                if(tmp.size() > 0) {
                    musicManager.setSelectionMode(false);
                    musicManager.deselectAllSongs();
                    toggleSelection();
                    refreshData();

                    musicManager.setTempSongs(tmp);
                    new AddSongsToPlaylistDialogFragment().show(getActivity().getSupportFragmentManager(), "AddSongsToPlaylist");

                } else {
                    Toast.makeText(root.getContext(), "No songs selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabBackToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSongList.smoothScrollTo(0, 0);
            }
        });

        svSongList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY < 1500) {
                    fabBackToTop.hide();
                } else {
                    fabBackToTop.show();
                }
            }
        });

        musicManager.toggleSong();
        musicManager.toggleSong();
    }

    public void setSeekBarData() {
        musicManager.setSeekBarData(sbHandler, sbUpdater);
    }

    private void createSongs(final LinearLayout llSongList, List<Song> songs) {

        if(songs != null) {
            songList = songs;
        }

        if(songList == null) {
            songList = musicManager.getSongList();
        }

        llSongList.removeAllViews();
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
            getFragmentManager().beginTransaction().add(llSongList.getId(), SongFragment.newInstance(s.getId()), Integer.toString(s.getId())).commit();
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

    public void searchSong(String query) {
        List<Song> tmp = new ArrayList<>();

        for(Song s : musicManager.getSongList()) {
            String titleLowercase = s.getTitle().toLowerCase();
            String artistLowercase = s.getInterpret().toLowerCase();
            String queryLowercase = query.toLowerCase();

            if(titleLowercase.contains(queryLowercase) || artistLowercase.contains(queryLowercase)) {
                tmp.add(s);
            }
        }

        createSongs(llSongList, tmp);
    }

    public void refreshData() {

        currentSong.setText(musicManager.getCurrentSong().getTitle());
        currentArtist.setText(musicManager.getCurrentSong().getInterpret());

        for(int i = 0; i < llSongList.getChildCount(); i++) {
            if((Integer)llSongList.getChildAt(i).getTag() == musicManager.getCurrentSong().getId()) {
                llSongList.getChildAt(i).setBackgroundColor(root.getContext().getColor(R.color.colorAccent));
            } else {
                llSongList.getChildAt(i).setBackgroundColor(root.getContext().getColor(R.color.colorSecondaryDark));
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

    public void refreshSongList() {
        createSongs(llSongList, null);
    }

    public void setSongData(Song s) {
        currentSong.setText(s.getTitle());
        currentArtist.setText(s.getInterpret());
    }
}