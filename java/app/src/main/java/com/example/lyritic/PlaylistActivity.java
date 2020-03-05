package com.example.lyritic;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity {

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

        Toast.makeText(this, "In der Activity!", Toast.LENGTH_SHORT).show();
    }

    private void createSongs() {
        for(Song s : playlist.getSongList()) {
            getSupportFragmentManager().beginTransaction().add(R.id.llSongList, SongFragment.newInstance(s.getId()), Integer.toString(s.getId())).commit();
        }
    }

}
