package com.example.lyritic;

import android.media.MediaPlayer;
import android.os.Handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicManager implements Serializable {
    private Song currentSong;
    private Song prevSong;
    private Song nextSong;
    private MediaPlayer player;
    private List<Song> songList;
    private Handler handler;
    private Runnable update;
    private Comparator<Song> comparator;
    private Boolean isPrepared = false;

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
            isPrepared = true;
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
                if(isPrepared) {
                    player.start();
                } else {
                    play();
                }
            }
        } else {
            player.reset();
            setCurrentSong(newSong);
            play();
        }

        toggleSeekBarProgress(handler, update);
    }

    public void toggleSong() {

        if(!isPrepared) {
            changeSong(currentSong);
            return;
        }

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

    public void skipSong() {

        currentSong = nextSong;
        setSongsByCurrentSong();

        if(player.isPlaying()) {
            player.reset();
            play();
        }
    }

    public void backSong() {

        currentSong = prevSong;
        setSongsByCurrentSong();


        if(player.isPlaying()) {
            player.reset();
            play();
        }
    }

    public void setSongsByCurrentSong() {
        if(currentSong == null) {
            return;
        }

        if(getSongListIndex(currentSong) > 0) {
            prevSong = songList.get(getSongListIndex(currentSong) - 1);
        } else {
            prevSong = songList.get(songList.size() - 1);
        }

        if(getSongListIndex(currentSong) + 1 < songList.size()) {
            nextSong = songList.get(getSongListIndex(currentSong) + 1);
        } else {
            nextSong = songList.get(0);
        }
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

    public Integer getSongListIndex(Song song) {
        if(songList != null && songList.size() > 0) {
            for(int i = 0; i < songList.size(); i++) {
                if(song.getId() == songList.get(i).getId()) {
                    return i;
                }
            }
        }

        return -1;
    }

    public List<Song> sortSongList(Integer id) {
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
        sortSongList(R.id.sortDate);
        Collections.reverse(songList);
        if(currentSong == null) {
            currentSong = songList.get(0);
            setSongsByCurrentSong();
        }
    }

    public List<Song> getSongList() {
        return this.songList;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Runnable getUpdate() {
        return update;
    }

    public void setUpdate(Runnable update) {
        this.update = update;
    }

    public Comparator<Song> getComparator() {
        return comparator;
    }

    public void setComparator(Comparator<Song> comparator) {
        this.comparator = comparator;
    }
}
