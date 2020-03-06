package com.example.lyritic;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity implements SongFragment.SongFragmentListener {

    private MusicManager musicManager;
    private Playlist playlist;

    private LinearLayout llSongList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        musicManager = DataManager.getMusicManager();
        playlist = DataManager.getPlaylist();

        llSongList = findViewById(R.id.llSongList);

        createSongs();
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
