package com.example.lyritic;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ContentLoader {

    private String path;
    private File file;
    private static List<Song> songs = new ArrayList<>();

//    public static List<Song> load(List<String> paths) {
//
//
//        for(String path : paths) {
//
//            File folder = new File(path);
//
//            File[] files = folder.listFiles();
//
//            for(File f : files) {
//                f = new File(path);
//
//                if(f.isDirectory())
//                    continue;
//
//                if(!f.getName().contains(".mp3")) {
//                    continue;
//                }
//
//                Song s = new Song();
//
//                MediaMetadataRetriever mdr = new MediaMetadataRetriever();
//                mdr.setDataSource(path);
//
//                s.setTitle(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
//                s.setInterpret(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
//                s.setAlbum(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
//                s.setGenre(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
//                s.setLength(Double.parseDouble(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
//                songs.add(s);
//            }
//        }
//
//        return songs;
//    }

    public static List<Song> load(ContentResolver cr) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor c = cr.query(uri, null, null, null, null);


        if(c == null || !c.moveToNext()) {
            return null;
        }

        c.moveToFirst();

        do {
            Song s = new Song();
            s.setInterpret(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            s.setTitle(c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            s.setAlbum(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            s.setLength(Double.parseDouble(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION))));
            s.setSize(Double.parseDouble(c.getString(c.getColumnIndex(MediaStore.Audio.Media.SIZE))));
            s.setLocation(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA)));

            songs.add(s);
        }   while (c.moveToNext());

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
