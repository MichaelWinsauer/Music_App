package com.example.lyritic;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicData implements Serializable {

    private List<Integer> playlistIds = new ArrayList<>();
    private HashMap<Integer, String> playlistNames = new HashMap<>();
    private List<String> saveStrings = new ArrayList<>();
    private List<PlaylistIdSongId> songs = new ArrayList<>();

    public MusicData() {

    }


    public void save(Context context) {
        try(FileOutputStream fos = context.openFileOutput("musicData.txt", Context.MODE_PRIVATE)) {
            for(int i = 0; i < saveStrings.size(); i++) {
                fos.write(saveStrings.get(i).getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MusicData read(Context context) {
        playlistIds.clear();
        playlistNames.clear();
        try(FileInputStream fis = context.openFileInput("musicData.txt")) {
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            String text;

            while((text = reader.readLine()) != null) {
                String[] data = text.split(";");
                playlistIds.add(Integer.parseInt(data[0]));
                playlistNames.put(Integer.parseInt(data[0]), data[1]);

                PlaylistIdSongId tmp = new PlaylistIdSongId();
                tmp.setPlaylistId(Integer.parseInt(data[0]));

                for(int i = 2; i < data.length; i++) {
                    tmp.getSongIds().add(Integer.parseInt(data[i]));
                }
                songs.add(tmp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public List<Integer> getPlaylistIds() {
        return playlistIds;
    }

    public void setPlaylistIds(List<Integer> playlistIds) {
        this.playlistIds = playlistIds;
    }

    public HashMap<Integer, String> getPlaylistNames() {
        return playlistNames;
    }

    public void setPlaylistNames(HashMap<Integer, String> playlistNames) {
        this.playlistNames = playlistNames;
    }

    public MusicData setPlaylistData(List<Playlist> playlists) {
        playlistIds.clear();
        playlistNames.clear();
        saveStrings.clear();

        for(Playlist p : playlists) {
            playlistIds.add(p.getId());
            playlistNames.put(p.getId(), p.getName());
            String string = p.getId() + ";" + p.getName();
            for(Song s : p.getSongList()) {
                string += ";" + s.getId();
            }
            string += "\n";
            saveStrings.add(string);
        }
        return this;
    }

    public List<PlaylistIdSongId> getSongs() {
        return songs;
    }
}
