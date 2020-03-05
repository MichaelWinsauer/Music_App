package com.example.lyritic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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


    private float oldX;
    private float oldY;
    private Boolean isHoldingPlay = false;
    public BrowseFragment() {
        // Required empty public constructor
    }

    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
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

        refreshData();
    }

    public void toggleSelection() {
        if(musicManager.getSelectionMode()) {
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
                    refreshData();
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
                Intent intent = new Intent(getContext(), Player.class);
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
                    musicManager.setSelectionMode(false);
                    musicManager.setSongSelection(tmp);
                    toggleSelection();
                    refreshData();

                } else {
                    Toast.makeText(root.getContext(), "Keine Lieder ausgewählt", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            final ConstraintLayout clSong = new ConstraintLayout(root.getContext());
            final TextView txtSongTitle = new TextView(root.getContext());
            final TextView txtSongArtist = new TextView(root.getContext());
            final TextView txtSongDuration = new TextView(root.getContext());
            final View viewSeperator = new View(root.getContext());
            final Button btnPlaySong = new Button(root.getContext());
            final CheckBox cbSelect = new CheckBox(root.getContext());
            final ImageButton imgBtnFav = new ImageButton(root.getContext());

            clSong.setTag(s.getId());
            txtSongTitle.setId(View.generateViewId());
            txtSongArtist.setId(View.generateViewId());
            txtSongDuration.setId(View.generateViewId());
            viewSeperator.setId(View.generateViewId());
            clSong.setId(View.generateViewId());
            btnPlaySong.setId(View.generateViewId());
            cbSelect.setId(View.generateViewId());
            imgBtnFav.setId(View.generateViewId());

            new ConstraintSet().constrainHeight(clSong.getId(), Tools.dpToPx(100, getActivity()));
            clSong.setBackgroundColor(root.getContext().getColor(R.color.colorSecondaryDark));
            clSong.addView(txtSongTitle, 0);
            clSong.addView(txtSongArtist, 1);
            clSong.addView(txtSongDuration, 2);
            clSong.addView(viewSeperator, 3);
            clSong.addView(cbSelect, 4);
            clSong.addView(imgBtnFav, 5);

            clSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!musicManager.getSelectionMode()) {
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
                    musicManager.setSelectionMode(true);

                    toggleSelection();


                    return false;
                }
            });

            imgBtnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(musicManager.toggleFavorite(musicManager.getSongById((Integer)clSong.getTag()))) {
                        imgBtnFav.setImageResource(R.drawable.heart);
                    } else {
                        imgBtnFav.setImageResource(R.drawable.heart_outline);
                    }
                }
            });

            cs.clone(clSong);

            txtSongTitle.setText(s.getTitle());
            txtSongTitle.setTextColor(root.getContext().getColor(R.color.colorPrimary));
            txtSongTitle.setTextSize(14);
            txtSongTitle.setWidth(Tools.dpToPx(215, getActivity()));
            txtSongTitle.setMaxLines(1);
            txtSongTitle.setEllipsize(TextUtils.TruncateAt.END);
            txtSongTitle.setTypeface(txtSongTitle.getTypeface(), Typeface.BOLD);

            cs.connect(txtSongTitle.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(15, getActivity()));
            cs.connect(txtSongTitle.getId(), ConstraintSet.LEFT, clSong.getId(), ConstraintSet.LEFT, Tools.dpToPx(75, getActivity()));
            cs.applyTo(clSong);

            txtSongArtist.setText(s.getInterpret());
            txtSongArtist.setTextColor(root.getContext().getColor(R.color.colorPrimary));
            txtSongArtist.setTextSize(10);
            txtSongArtist.setTypeface(txtSongArtist.getTypeface(), Typeface.ITALIC);

            cs.connect(txtSongArtist.getId(), ConstraintSet.TOP, txtSongTitle.getId(), ConstraintSet.BOTTOM, Tools.dpToPx(0, getActivity()));
            cs.connect(txtSongArtist.getId(), ConstraintSet.LEFT, txtSongTitle.getId(), ConstraintSet.LEFT, Tools.dpToPx(0, getActivity()));
            cs.connect(txtSongArtist.getId(), ConstraintSet.BOTTOM, clSong.getId(), ConstraintSet.BOTTOM, Tools.dpToPx(20, getActivity()));
            cs.applyTo(clSong);

            txtSongDuration.setText(s.durationToString(Math.round(s.getDuration())));
            txtSongDuration.setTextColor(root.getContext().getColor(R.color.colorAccent));
            txtSongDuration.setTextSize(10);

            cs.connect(txtSongDuration.getId(), ConstraintSet.TOP, clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(25, getActivity()));
            cs.connect(txtSongDuration.getId(), ConstraintSet.RIGHT, clSong.getId(), ConstraintSet.RIGHT, Tools.dpToPx(20, getActivity()));
            cs.applyTo(clSong);

            viewSeperator.setBackground(root.getContext().getDrawable(R.drawable.song_seperator));

            cs.connect(viewSeperator.getId(), ConstraintSet.BOTTOM, clSong.getId(), ConstraintSet.BOTTOM, 0);
            cs.connect(viewSeperator.getId(), ConstraintSet.RIGHT, clSong.getId(), ConstraintSet.RIGHT, 0);
            cs.connect(viewSeperator.getId(), ConstraintSet.LEFT, txtSongTitle.getId(), ConstraintSet.LEFT, Tools.dpToPx(70, getActivity()));
            cs.applyTo(clSong);

            cbSelect.setHighlightColor(root.getContext().getColor(R.color.colorAccent));

            cs.connect(cbSelect.getId(), ConstraintSet.LEFT , clSong.getId(), ConstraintSet.LEFT, Tools.dpToPx(20, getActivity()));
            cs.connect(cbSelect.getId(), ConstraintSet.TOP , clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(20, getActivity()));
            cs.applyTo(clSong);

            imgBtnFav.setBackgroundColor(root.getContext().getColor(R.color.colorTransparent));
            if(!musicManager.getFav(musicManager.getSongById((Integer)clSong.getTag()))) {
                imgBtnFav.setImageResource(R.drawable.heart_outline);
            } else {
                imgBtnFav.setImageResource(R.drawable.heart);
            }

            cs.connect(imgBtnFav.getId(), ConstraintSet.RIGHT,  txtSongDuration.getId(), ConstraintSet.LEFT, Tools.dpToPx(20, getActivity()));
            cs.connect(imgBtnFav.getId(), ConstraintSet.TOP,  clSong.getId(), ConstraintSet.TOP, Tools.dpToPx(15, getActivity()));
            cs.applyTo(clSong);

            llSongList.addView(clSong);
        }

        for(CheckBox cb : getAllCheckboxes()) {
            cb.setVisibility(View.INVISIBLE);
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

    private void refreshData() {

        if(musicManager.getSongList().size() <= 0) {
            return;
        }

        if(musicManager.getCurrentSong() == null) {
            musicManager.setCurrentSong(musicManager.getSongList().get(0));
        }

        currentSong.setText(musicManager.getCurrentSong().getTitle());
        currentArtist.setText(musicManager.getCurrentSong().getInterpret());

        btnPlay.setBackgroundResource(R.drawable.playbutton_animation);

        for(int i = 0; i < llSongList.getChildCount(); i++) {
            if((Integer)llSongList.getChildAt(i).getTag() == musicManager.getCurrentSong().getId()) {
                llSongList.getChildAt(i).setBackgroundColor(root.getContext().getColor(R.color.colorAccent));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(0)).setTextColor(root.getContext().getColor(R.color.colorPrimaryDark));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(1)).setTextColor(root.getContext().getColor(R.color.colorPrimaryDark));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(2)).setTextColor(root.getContext().getColor(R.color.colorPrimaryDark));
                ((ImageButton)getChildren((ViewGroup)llSongList.getChildAt(i)).get(5)).setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.SRC_IN);


            } else {
                llSongList.getChildAt(i).setBackgroundColor(root.getContext().getColor(R.color.colorSecondaryDark));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(0)).setTextColor(root.getContext().getColor(R.color.colorPrimary));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(1)).setTextColor(root.getContext().getColor(R.color.colorPrimary));
                ((TextView)getChildren((ViewGroup)llSongList.getChildAt(i)).get(2)).setTextColor(root.getContext().getColor(R.color.colorAccent));
                ((ImageButton)getChildren((ViewGroup)llSongList.getChildAt(i)).get(5)).setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
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

    public void refreshSongList() {
        createSongs(llSongList, null);
    }

}
