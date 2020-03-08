package com.example.lyritic;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity implements SongFragment.SongFragmentListener {

    private MusicManager musicManager;
    private Playlist playlist;

    private LinearLayout llSongList;
    private TextView txtPlaylistName;
    private ImageButton imgBtnPlaylistPlay;
    private ImageButton imgBtnPlaylistShuffle;
    private ImageButton imgBtnPlaylistRepeat;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        musicManager = DataManager.getMusicManager();
        playlist = DataManager.getPlaylist();

        musicManager.setPlaylistBackup(musicManager.getSongList());
        musicManager.setSongList(playlist.getSongList());

        llSongList = findViewById(R.id.llSongList);
        imgBtnPlaylistPlay = findViewById(R.id.imgBtnPlaylistPlay);
        imgBtnPlaylistShuffle = findViewById(R.id.imgBtnPlaylistShuffle);
        imgBtnPlaylistRepeat = findViewById(R.id.imgBtnPlaylistRepeat);
        txtPlaylistName = findViewById(R.id.txtPlaylistName);

        txtPlaylistName.setText(playlist.getName());

        initializeListener();
        createSongs();
    }

    private void initializeListener() {
        imgBtnPlaylistPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleSong();
            }
        });

        imgBtnPlaylistRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleLoop();
            }
        });

        imgBtnPlaylistShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleShuffle();
            }
        });
    }

    private void createSongs() {
        llSongList.removeAllViews();
        for(Song s : playlist.getSongList()) {
            getSupportFragmentManager().beginTransaction().add(R.id.llSongList, SongFragment.newInstance(s.getId()), Integer.toString(s.getId())).commit();
        }
    }

    @Override
    public void onSongClicked(View v, Song s) {

    }

    @Override
    public void onFavClicked(View v, Song s) {
        createSongs();
    }

    @Override
    public void onSongHold(View v, Song s) {

    }

    @Override
    public void onSongSelected(View v, Song s) {

    }

}
