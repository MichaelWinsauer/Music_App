package com.example.lyritic;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Song {

    private static int count;

    private String title;
    private String interpret;
    private String album;
    private String genre;
    private String absolutePath;
    private Date dateAdded;

    private double duration;
    private double size;

    private int id;

    private boolean isMusic;
    private boolean isPlaying;

    public Song()
    {
        count++;
        id = count;
    }

    public boolean play(Context context){
        MediaPlayer player = new MediaPlayer();

        try {
            player.setDataSource(context, Uri.parse(getAbsolutePath()));
            player.prepare();
        } catch (IOException e) {
            isPlaying = false;
            e.printStackTrace();
        } catch (Exception e) {
            isPlaying = false;
        }

        isPlaying = true;
        player.start();

        return isPlaying;
    }

    public boolean pause() {
        MediaPlayer player = new MediaPlayer();

        try {
            player.setDataSource(this.absolutePath);
        } catch (IOException e) {
            isPlaying = true;
        } catch (Exception e) {
            isPlaying = true;
        }

        if(isPlaying) {
            isPlaying = false;
            player.pause();
        }

        return isPlaying;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        Song.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(boolean isMusic) {
        this.isMusic = isMusic;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
