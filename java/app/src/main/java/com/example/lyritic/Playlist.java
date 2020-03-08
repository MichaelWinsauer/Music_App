package com.example.lyritic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Playlist implements Serializable {

    private static int count;
    private int id;
    private String name;
    private List<Song> songList = new ArrayList<>();

    public Playlist() {
        id = count;
        count++;
    }

    public Playlist(String name) {
        id = count;
        count++;

        this.name = name;
    }


    public void addSong(Song s) {
        songList.add(s);
    }

    public void removeSong(Song s) {
        songList.remove(s);
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
