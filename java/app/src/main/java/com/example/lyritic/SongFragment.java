package com.example.lyritic;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class SongFragment extends Fragment implements MusicManager.SongListener {
    private static final String ARG_PARAM1 = "0";

    private int songId;

    private MusicManager musicManager;
    private Song song;

    private TextView txtTitle;
    private TextView txtArtist;
    private TextView txtDuration;
    private ImageButton imgBtnFav;
    private ImageView imgCover;
    private ConstraintLayout clBase;
    private SongFragmentListener songFragmentListener;
    private Activity activity;

    public SongFragment() {
        // Required empty public constructor
    }

    public static SongFragment newInstance(int songId) {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, songId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songId = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        musicManager = DataManager.getSongFragmentMusicManager();
        musicManager.addSongListener(this);

        song = musicManager.getSongById(getArguments().getInt(ARG_PARAM1));

        View view = inflater.inflate(R.layout.song_fragment, container, false);

        txtTitle = view.findViewById(R.id.txtSongTitle);
        txtArtist = view.findViewById(R.id.txtSongArtist);
        txtDuration = view.findViewById(R.id.txtSongDuration);
        imgBtnFav = view.findViewById(R.id.imgBtnFav);
        imgCover = view.findViewById(R.id.imgSongCover);
        clBase = view.findViewById(R.id.clSongBase);

        loadSongData();

        return view;
    }

    private void loadSongData() {

        if(song == null) {
            return;
        }

        txtTitle.setText(song.getTitle());
        txtArtist.setText(song.getInterpret());
        txtDuration.setText(song.durationToString((long)song.getDuration()));

        //Wegen performance Problemen auskommentiert.
        if(song.getCover() != null) {
//            imgCover.setImageBitmap(Tools.cropBitmapToSquare(song.getCover()));
        } else {
//            imgCover.setImageBitmap(Tools.cropBitmapToSquare(BitmapFactory.decodeResource(getResources(), R.drawable.missing_img)));
        }
        clBase.setTag(song.getId());

        if(musicManager.getCurrentSong().getId() == song.getId()) {
            clBase.setBackgroundColor(activity.getColor(R.color.colorAccent));
        } else {
            clBase.setBackgroundColor(activity.getColor(R.color.colorSecondaryDark));
        }

        if(song.getSelected()) {
            clBase.setBackgroundColor(getActivity().getColor(R.color.colorPrimaryDark));
        }

        if(musicManager.getFav(musicManager.getSongById(song.getId()))) {
            imgBtnFav.setImageResource(R.drawable.heart);
        } else {
            imgBtnFav.setImageResource(R.drawable.heart_outline);
        }

        imgBtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicManager.toggleFavorite(musicManager.getSongById(song.getId()))) {
                    imgBtnFav.setImageResource(R.drawable.heart);
                } else {
                    imgBtnFav.setImageResource(R.drawable.heart_outline);
                }
                songFragmentListener.onFavClicked(v, song);
            }
        });

        clBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!musicManager.getSelectionMode()) {
                    songFragmentListener.onSongClicked(v, song);
                    return;
                }

                if(!song.getSelected()) {
                    song.setSelected(true);
                    clBase.setBackgroundColor(activity.getColor(R.color.colorPrimaryDark));
                } else {
                    song.setSelected(false);
                    clBase.setBackgroundColor(activity.getColor(R.color.colorSecondaryDark));
                }

                songFragmentListener.onSongSelected(v, song);
            }
        });

        clBase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                songFragmentListener.onSongHold(v, song);

                return false;
            }
        });
    }

    @Override
    public void songChanged(Song s) {
        if(s.getId() == song.getId()) {
            clBase.setBackgroundColor(activity.getColor(R.color.colorAccent));
        } else {
            clBase.setBackgroundColor(activity.getColor(R.color.colorSecondaryDark));
        }
    }

    public interface SongFragmentListener {
        public void onSongClicked(View v, Song s);
        public void onFavClicked(View v, Song s);
        public void onSongHold(View v, Song s);
        public void onSongSelected(View v, Song s);
    }

    @Override
    public void onAttach(Context context) {

        songFragmentListener = (SongFragmentListener) context;
        activity = (Activity) context;
        super.onAttach(context);
    }
}
