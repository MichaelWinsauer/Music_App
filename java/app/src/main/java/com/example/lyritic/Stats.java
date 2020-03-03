package com.example.lyritic;

import org.cmc.music.metadata.IMusicMetadata;

import java.util.HashMap;

public class Stats {
    private static MusicManager musicManager;
    private static int durationListend;
    private static HashMap<String, Integer> artists = new HashMap<>();
    private static HashMap<Integer, Integer> titles = new HashMap<>();


    public static int getSongCount(int key) {
        return titles.get(key);
    }

    public static void loadData() {
        musicManager = DataManager.getMusicManager();
        for(Song s : DataManager.getMusicManager().getSongList()) {
            artists.put(s.getInterpret(), 0);
            titles.put(s.getId(), 0);
        }
    }

    public static void listend(Song s) {
        if(artists.get(s.getInterpret()) != null) {
            int tmp = artists.get(s.getInterpret());
            tmp++;
            artists.put(s.getInterpret(), tmp);
        }

        if(titles.get(s.getInterpret()) != null) {
            int tmp = titles.get(s.getInterpret());
            tmp++;
            artists.put(s.getInterpret(), tmp);
        }
    }

    public static String getHighesTitle() {
        String title = "";
        int value = 0;

    //TODO: Currently returning ""
        for(int k : titles.keySet() ) {
            if(titles.get(k) > value) {
                value = titles.get(k);
                title = musicManager.getSongById(k).getTitle();
            }
        }

        return title;
    }

    public static int getHighestTitleCount() {
        int value = 0;

        //TODO: Currently returning ""
        for(int k : titles.keySet() ) {
            if(titles.get(k) > value) {
                value = titles.get(k);
            }
        }

        return value;
    }

    //TODO: Currently returning ""
    public static String getHighesArtist() {
        String key = "";
        int value = 0;

        for(String k : artists.keySet() ) {
            if(artists.get(k) > value) {
                value = artists.get(k);
                key = k;
            }
        }

        return key;
    }


    public static int getHighesArtistCount() {
        String key = "";
        int value = 0;

        for(String k : artists.keySet() ) {
            if(artists.get(k) > value) {
                value = artists.get(k);
                key = k;
            }
        }

        return value;
    }

    public static int getDurationListend() {
        return durationListend;
    }

    public static void setDurationListend(int durationListend) {
        Stats.durationListend = durationListend;
    }
}
