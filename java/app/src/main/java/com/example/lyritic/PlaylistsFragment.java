package com.example.lyritic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class PlaylistsFragment extends Fragment {

    private MusicManager musicManager;
    private LinearLayout llPlaylists;

    public PlaylistsFragment() {
        // Required empty public constructor
    }

    public static PlaylistsFragment newInstance(String param1, String param2) {
        return new PlaylistsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicManager = DataManager.getMusicManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlists, container, false);
        llPlaylists = view.findViewById(R.id.llPlaylists);
        loadPlaylists();

        return view;
    }

    private void loadPlaylists() {
        for (Playlist p : musicManager.getPlaylists()) {
            getFragmentManager().beginTransaction().add(llPlaylists.getId(), .newInstance(p), p.getId());
        }
    }
}
