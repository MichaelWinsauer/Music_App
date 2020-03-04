package com.example.lyritic;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaylistsFragment extends Fragment {

    private MusicManager musicManager;

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
        loadPlaylists();

        return view;
    }

    private void loadPlaylists() {
        for (Playlist p : musicManager.getPlaylists()) {
            getFragmentManager().beginTransaction().add(R.id.llPlaylists, PlaylistItemFragment.newInstance(p.getId()), Integer.toString(p.getId())).commit();
        }
    }
}
