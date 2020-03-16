package com.example.lyritic;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.marcinmoskala.arcseekbar.ArcSeekBar;


public class PlayerFragment extends Fragment {

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

    private PlayerListener playerListener;
    
    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.player, container, false);

        musicManager = DataManager.getMusicManager();

        initializeReferences(view);
        initializeEventListener();

        if(musicManager.getCurrentSong() != null) {
            displayData();
        }

        if(!musicManager.getPlaying()) {
            btnPlay.setImageDrawable(getActivity().getDrawable(R.drawable.play_arrow_50dp));
        } else {
            btnPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_black_24dp));
        }

        return view;
    }
    

    private void initializeReferences(View view) {
        sbHandler = new Handler();

        clBase = view.findViewById(R.id.clBase);
        ivCover = view.findViewById(R.id.ivCover);
        txtTitle = view.findViewById(R.id.txtSongTitle);
        txtArtist = view.findViewById(R.id.txtSongArtist);
        txtDuration = view.findViewById(R.id.txtPosition);
        btnPlay = view.findViewById(R.id.imgbtnPlay);
        btnPrev = view.findViewById(R.id.imgbtnPrev);
        btnNext = view.findViewById(R.id.imgbtnNext);
        btnRepeat = view.findViewById(R.id.imgbtnRepeat);
        btnShuffle = view.findViewById(R.id.imgbtnShuffle);
        btnDetails = view.findViewById(R.id.imgBtnDetails);
        asbSongProgress = view.findViewById(R.id.asbSongProgression);
    }

    private void initializeEventListener() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerListener.onPlayClicked();
                if(!musicManager.getPlaying()) {
                    btnPlay.setImageDrawable(getActivity().getDrawable(R.drawable.play_arrow_50dp));
                } else {
                    btnPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_black_24dp));
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleLoop();
                if(musicManager.getPlayer().isLooping()) {
                    v.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorAccent));
                } else {
                    v.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorPrimaryDark));
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
                    v.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorAccent));
                } else {
                    v.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorPrimaryDark));
                }
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Details.class);
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

        sbHandler.postDelayed(sbUpdater, 50);

        displayImage();

        if(!musicManager.getPlaying()) {
            btnPlay.setImageDrawable(getActivity().getDrawable(R.drawable.play_arrow_50dp));
        } else {
            btnPlay.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pause_black_24dp));
        }
    }

    public interface PlayerListener {
        public void onPlayClicked();
    }

    @Override
    public void onAttach(Context context) {
        playerListener = (PlayerListener) context;

        super.onAttach(context);
    }
}
