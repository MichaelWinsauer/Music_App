package com.example.lyritic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SongFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private int songId;

    private MusicManager musicManager;
    private Song song;

    private TextView txtTitle;
    private TextView txtArtist;
    private TextView txtDuration;
    private ImageButton imgBtnFav;
    private ImageView imgCover;

    public SongFragment() {
        // Required empty public constructor
    }

    public static SongFragment newInstance(int songId) {
        SongFragment fragment = new SongFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Integer.toString(songId));
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

        musicManager = DataManager.getMusicManager();

        song = musicManager.getSongById(songId);

        View view = inflater.inflate(R.layout.song_fragment, container, false);

        txtTitle = view.findViewById(R.id.txtSongTitle);
        txtArtist = view.findViewById(R.id.txtSongArtist);
        txtDuration = view.findViewById(R.id.txtSongDuration);
        imgBtnFav = view.findViewById(R.id.imgBtnFav);
        imgCover = view.findViewById(R.id.imgSongCover);

        loadSongData();

        return view;
    }

    private void loadSongData() {
        txtTitle.setText(song.getTitle());
        txtArtist.setText(song.getInterpret());
        txtDuration.setText(song.getFormatDuration());
        imgCover.setImageBitmap(song.getCover());
        imgBtnFav.setImageResource(R.drawable.heart_outline);

        imgBtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicManager.toggleFavorite(musicManager.getSongById(song.getId()))) {
                    imgBtnFav.setImageResource(R.drawable.heart);
                } else {
                    imgBtnFav.setImageResource(R.drawable.heart_outline);
                }
            }
        });

    }
}
