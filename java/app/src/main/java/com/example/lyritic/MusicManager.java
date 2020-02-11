package com.example.lyritic;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class MusicManager {
    private Song currentSong;
    private Song prevSong;
    private Song nextSong;
    private MediaPlayer player;
    private List<Song> songList;

    public MusicManager() {
        songList = new ArrayList<>();
        player = new MediaPlayer();
    }

    public void play() {

        if(currentSong == null) {
            return;
        }

        try {
            player.setDataSource(currentSong.getAbsolutePath());
            player.prepare();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.start();
    }

    public Song getSongById(int id) {
        if(songList != null && songList.size() > 0) {
            for (Song s : songList) {
                if (s.getId() == id) {
                    return s;
                }
            }
        }
        return null;
    }

    public void changeSong(Song newSong) {
        if(currentSong == null) {
            setCurrentSong(newSong);
            play();

            return;
        }

        if(currentSong.getId() == newSong.getId()) {
            if(player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
        } else {
            player.reset();
            setCurrentSong(newSong);
            play();
        }
    }

    public void toggleSong() {
        if(!player.isPlaying()) {
            player.start();
        } else {
            player.pause();
        }
    }

    public List<Song> sortSongList(Integer sort, boolean asc) {

        switch (sort) {
            //Alphabetic
            case 1:

                if(asc) {

                }

                break;

            //Artist
            case 2:

                break;

            //Date_Added
            case 3:

                break;

            //Album
            case 4:

                break;

            //length
            case 5:

                break;
        }

        return songList;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    public Song getPrevSong() {
        return prevSong;
    }

    public void setPrevSong(Song prevSong) {
        this.prevSong = prevSong;
    }

    public Song getNextSong() {
        return nextSong;
    }

    public void setNextSong(Song nextSong) {
        this.nextSong = nextSong;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public List<Song> getSongList() {
        return this.songList;
    }
}
