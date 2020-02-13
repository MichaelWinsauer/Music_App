package com.example.lyritic;

import java.util.Date;

public class Song {

    private static int count;

    private String title;
    private String interpret;
    private String album;
    private String genre;
    private String absolutePath;
    private String formatDuration;

    private Date dateAdded;

    private double duration;
    private double size;

    private int id;

    public Song()
    {
        count++;
        id = count;
    }

    public String durationToString(long seconds) {

        long min = (seconds % 3600) / 60;
        long sec = seconds % 60;

        formatDuration = String.format("%02d:%02d",  min, sec);

        return formatDuration;
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getFormatDuration() {
        return formatDuration;
    }

    public void setFormatDuration(String formatDuration) {
        this.formatDuration = formatDuration;
    }
}
