package com.example.lyritic;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlaylistsFragment extends Fragment {

    private MusicManager musicManager;

    private FloatingActionButton fabAddNewPlaylist;
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlists, container, false);
        fabAddNewPlaylist = view.findViewById(R.id.fabAddNewPlaylist);
        llPlaylists = view.findViewById(R.id.llPlaylists);

        fabAddNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddPlaylistDialog().show(getActivity().getSupportFragmentManager(), "AddPlaylist");
            }
        });

        loadPlaylists();

        return view;
    }

    public void loadPlaylists()
    {
        llPlaylists.removeAllViews();

        for (Playlist p : musicManager.getPlaylists()) {
            getFragmentManager().beginTransaction().add(R.id.llPlaylists, PlaylistItemFragment.newInstance(p.getId()), Integer.toString(p.getId())).commit();
        }
    }
}
