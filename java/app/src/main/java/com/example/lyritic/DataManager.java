package com.example.lyritic;

public class DataManager {
    private static MusicManager musicManager;

    public static MusicManager getMusicManager() {
        return musicManager;
    }

    public static void setMusicManager(MusicManager musicManager) {
        DataManager.musicManager = musicManager;
    }
}
