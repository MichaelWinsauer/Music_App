package com.example.lyritic;

import android.widget.LinearLayout;

public class DataManager {
    private static MusicManager musicManager;
    private static Playlist playlist;

    public static MusicManager getMusicManager() {
        return musicManager;
    }

    public static void setMusicManager(MusicManager musicManager) {
        DataManager.musicManager = musicManager;
    }

    public static Playlist getPlaylist() {
        return playlist;
    }

    public static void setPlaylist(Playlist playlist) {
        DataManager.playlist = playlist;
    }
}
