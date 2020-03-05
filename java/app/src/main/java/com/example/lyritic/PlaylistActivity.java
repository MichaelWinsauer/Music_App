package com.example.lyritic;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlaylistActivity extends AppCompatActivity {

    private MusicManager musicManager;
    private Playlist playlist;

    private LinearLayout llSongList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        musicManager = DataManager.getMusicManager();
        playlist = DataManager.getPlaylist();

        llSongList = findViewById(R.id.llSongList);

        createSongs();
    }

    private void createSongs() {
        for(Song s : playlist.getSongList()) {
            getSupportFragmentManager().beginTransaction().add(R.id.llPlaylists, SongFragment.newInstance(s.getId()), Integer.toString(s.getId())).commit();
        }
    }

}
