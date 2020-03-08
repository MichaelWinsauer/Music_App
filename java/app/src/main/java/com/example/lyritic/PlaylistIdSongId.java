package com.example.lyritic;

import java.util.ArrayList;
import java.util.List;

public class PlaylistIdSongId {
    private int playlistId;
    private List<Integer> songIds = new ArrayList<>();

    public PlaylistIdSongId() {

    }

    public List<Integer> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Integer> songIds) {
        this.songIds = songIds;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
}
