package com.example.lyritic;

import android.media.MediaPlayer;
import android.os.Handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicManager {
    private Song currentSong;
    private Song prevSong;
    private Song nextSong;
    private MediaPlayer player;
    private List<Song> songList;
    private Handler handler;
    private Runnable update;
    private Comparator<Song> comparator;


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

        toggleSeekBarProgress(handler, update);
    }

    public void toggleSong() {
        if(!player.isPlaying()) {
            player.start();
        } else {
            player.pause();
        }

        toggleSeekBarProgress(handler, update);
    }

    private void toggleSeekBarProgress(Handler handler, Runnable update) {
        if(player.isPlaying()) {
            handler.postDelayed(update, 0);
        } else {
            handler.removeCallbacks(update);
        }
    }

    public void skipTo(int progress) {
        player.seekTo((int)Math.round(currentSong.getDuration() / 100 * progress * 1000));
    }

    public void toggleLoop() {

        if(player.isLooping()) {
            player.setLooping(false);
        } else {
            player.setLooping(true);
        }
    }

    public void toggleRandom() {
        //TODO: später wenn Playlists
    }

    public int getPercentageProgress() {
        return (int)Math.round( (player.getCurrentPosition() / 1000) / currentSong.getDuration() * 100) ;
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

    public List<Song> sortSongList(Integer id, boolean ascending) {
        switch (id) {
            case R.id.sortName:
                comparator = new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                };
                break;

            case R.id.sortArtist:
                comparator = new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        return o1.getInterpret().compareTo(o2.getInterpret());
                    }
                };
                break;

            case R.id.sortDate:
                comparator = new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        return o1.getDateAdded().compareTo(o2.getDateAdded());
                    }
                };
                break;

            case R.id.sortAlbum:
                comparator = new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        return o1.getAlbum().compareTo(o2.getAlbum());
                    }
                };
                break;

            case R.id.sortLength:
                comparator = new Comparator<Song>() {
                    @Override
                    public int compare(Song o1, Song o2) {
                        return ((Integer) Math.round((long) o1.getDuration())).compareTo(Math.round((long) o2.getDuration()));
                    }
                };
                break;
        }

        Collections.sort(songList, comparator);

        if(!ascending) {
            Collections.reverse(songList);
        }

        return songList;
    }

    public void setSeekBarData(Handler handler, Runnable update) {
        this.handler = handler;
        this.update = update;
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
        defaultSorting();
    }

    private void defaultSorting() {
        sortSongList(R.id.sortDate, false);
    }

    public List<Song> getSongList() {
        return this.songList;
    }

}
