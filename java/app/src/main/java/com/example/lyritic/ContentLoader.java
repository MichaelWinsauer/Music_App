package com.example.lyritic;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ContentLoader {

    private String path;
    private File file;
    private static List<Song> songs = new ArrayList<>();

    public static List<Song> load(Context context) {

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.IS_MUSIC,
                MediaStore.Audio.AudioColumns.DATE_ADDED
        };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if(c == null)
            return null;

        while(c.moveToNext()) {
            Song s = new Song();

            s.setTitle(c.getString(0));
            s.setInterpret(c.getString(1));
            s.setAlbum(c.getString(2));
            s.setDuration(Double.parseDouble(c.getString(3))/1000);
            s.setAbsolutePath(c.getString(4));
            s.setSize(Double.parseDouble(c.getString(5)));
            s.setIsMusic(Boolean.parseBoolean(c.getString(6)));
            s.setDateAdded(new java.util.Date((long)Double.parseDouble(c.getString(7))*1000));

            songs.add(s);
        }

        return songs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static List<Song> getSongs() {
        return songs;
    }
}
