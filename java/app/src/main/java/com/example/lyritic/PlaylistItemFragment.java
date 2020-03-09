package com.example.lyritic;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class PlaylistItemFragment extends Fragment {

    private static final String ARG_PARAM1 = "0";

    private MusicManager musicManager;
    private Playlist playlist;
    private int playlistId;

    private ConstraintLayout clPlaylistBase;
    private TextView txtPlaylistName;
    private TextView txtPlaylistSongCount;
    private PlaylistItemListener playlistItemListener;

    public PlaylistItemFragment() {
        // Required empty public constructor
    }

    public static PlaylistItemFragment newInstance(int playlistId) {
        PlaylistItemFragment fragment = new PlaylistItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, playlistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicManager = DataManager.getMusicManager();
        if (getArguments() != null) {
            playlistId = getArguments().getInt(ARG_PARAM1);

            for(Playlist p : musicManager.getPlaylists()) {
                if(p.getId() == playlistId) {
                    playlist = p;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);

        final Context c = getActivity();

        clPlaylistBase = view.findViewById(R.id.clPlaylistBase);
        txtPlaylistName = view.findViewById(R.id.txtPlaylistName);
        txtPlaylistSongCount = view.findViewById(R.id.txtPlaylistSongCount);

        txtPlaylistName.setText(playlist.getName());
        txtPlaylistSongCount.setText(Integer.toString(playlist.getSongList().size()));
        clPlaylistBase.setTag(playlist.getId());

        clPlaylistBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleSong();
                DataManager.setPlaylist(playlist);
                Intent intent = new Intent(getActivity(), PlaylistActivity.class);
                getActivity().startActivity(intent);
            }
        });

        clPlaylistBase.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Delete Playlist?");
                alertDialog.setMessage("Are you sure, you want to delete '" + playlist.getName() + "'?");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        musicManager.removePlaylist(playlist);
                        playlistItemListener.onDelete();
                    }
                });
                alertDialog.show();

                return false;
            }
        });
        return view;
    }

    public interface PlaylistItemListener {
        public void onDelete();
    }

    @Override
    public void onAttach(Context context) {
        playlistItemListener = (PlaylistItemListener) context;

        super.onAttach(context);
    }
}
