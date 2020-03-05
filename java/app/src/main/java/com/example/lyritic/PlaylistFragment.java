package com.example.lyritic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class PlaylistFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private int playlistId;
    private MusicManager musicManager;
    private Playlist playlist;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    public static PlaylistFragment newInstance(int playlistId) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Integer.toString(playlistId));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicManager = DataManager.getMusicManager();

        if (getArguments() != null) {
            this.playlistId = getArguments().getInt(ARG_PARAM1);
            playlist = musicManager.getPlaylistById(playlistId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.playlist, container, true);

        LinearLayout llSongList = view.findViewById(R.id.llSongList);

        createSongs();

        return view;
    }

    private void createSongs() {
        for(Song s : playlist.getSongList()) {
            getFragmentManager().beginTransaction().add(R.id.llPlaylists, SongFragment.newInstance(s.getId()), Integer.toString(s.getId())).commit();
        }
    }
}
