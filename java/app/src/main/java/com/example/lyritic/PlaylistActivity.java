package com.example.lyritic;

import android.os.Bundle;
import android.os.Handler;
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

        if(playlist.getSongList().size() > 0) {
            musicManager.setSongListFromPlaylist(playlist.getSongList());
        }

        llSongList = findViewById(R.id.llSongList);
        imgBtnPlaylistPlay = findViewById(R.id.imgBtnPlaylistPlay);
        imgBtnPlaylistShuffle = findViewById(R.id.imgBtnPlaylistShuffle);
        imgBtnPlaylistRepeat = findViewById(R.id.imgBtnPlaylistRepeat);
        txtPlaylistName = findViewById(R.id.txtPlaylistName);

        txtPlaylistName.setText(playlist.getName());

        initializeListener();
        createSongs();

        if(!musicManager.getPlaying()) {
            imgBtnPlaylistPlay.setBackground(getDrawable(R.drawable.ic_play_arrow_black_24dp));
        } else {
            imgBtnPlaylistPlay.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));
        }

        if(musicManager.getShuffled()) {
            imgBtnPlaylistShuffle.setImageDrawable(getDrawable(R.drawable.shuffle_active));
        } else {
            imgBtnPlaylistShuffle.setImageDrawable(getDrawable(R.drawable.shuffle));
        }

        if(musicManager.getPlayer().isLooping()) {
            imgBtnPlaylistRepeat.setImageDrawable(getDrawable(R.drawable.repeat_active));
        } else {
            imgBtnPlaylistRepeat.setImageDrawable(getDrawable(R.drawable.repeat));
        }
    }

    private void initializeListener() {
        imgBtnPlaylistPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(playlist.getSongList().size() <= 0 || playlist.getSongList() == null) {
                    return;
                }

                if(!playlist.getSongList().contains(musicManager.getCurrentSong())) {
                    musicManager.changeSong(playlist.getSongList().get(0));
                } else {
                    musicManager.toggleSong();
                }

                if(!musicManager.getPlaying()) {
                    imgBtnPlaylistPlay.setBackground(getDrawable(R.drawable.ic_play_arrow_black_24dp));
                } else {
                    imgBtnPlaylistPlay.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));
                }
            }
        });

        imgBtnPlaylistRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleLoop();
                if(musicManager.getPlayer().isLooping()) {
                    imgBtnPlaylistRepeat.setImageDrawable(getDrawable(R.drawable.repeat_active));
                } else {
                    imgBtnPlaylistRepeat.setImageDrawable(getDrawable(R.drawable.repeat));
                }
            }
        });

        imgBtnPlaylistShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicManager.toggleShuffle();
                if(musicManager.getShuffled()) {
                    imgBtnPlaylistShuffle.setImageDrawable(getDrawable(R.drawable.shuffle_active));
                } else {
                    imgBtnPlaylistShuffle.setImageDrawable(getDrawable(R.drawable.shuffle));
                }
            }
        });

        musicManager.toggleSong();
        musicManager.toggleSong();
    }

    private void createSongs() {
        llSongList.removeAllViews();
        for(Song s : playlist.getSongList()) {
            getSupportFragmentManager().beginTransaction().add(R.id.llSongList, SongFragment.newInstance(s.getId()), Integer.toString(s.getId())).commit();
        }
    }

    @Override
    public void onSongClicked(View v, Song s) {
        musicManager.changeSong(s);
        if(!musicManager.getPlaying()) {
            imgBtnPlaylistPlay.setBackground(getDrawable(R.drawable.ic_play_arrow_black_24dp));
        } else {
            imgBtnPlaylistPlay.setBackground(getDrawable(R.drawable.ic_pause_black_24dp));
        }
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

    @Override
    public void onBackPressed() {
        musicManager.restoreSongList();

        super.onBackPressed();
    }
}
