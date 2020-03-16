package com.example.lyritic;

import android.media.MediaPlayer;
import android.os.Handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MusicManager implements MediaPlayer.OnCompletionListener {
    private MediaPlayer player;

    private Song currentSong;
    private Song prevSong;
    private Song nextSong;

    private List<Song> songList; //songlist, which gets changed depend. on context (not android context)
    private List<Song> backup;  //shuffle Backup for temp
    private List<Song> selectionBackup;  //selection Backup for multiselect
    private List<Song> tmp = new ArrayList<>(); //temporäre Playlist, für das Hinzufügen von Liedern in eine Playlist
    private List<Song> originalSongList = new ArrayList<>();

    private List<Playlist> playlists;
    private List<SongListener> songListeners = new ArrayList<>();

    private Handler handler;
    private Runnable update;

    private Comparator<Song> comparator;

    private Boolean isPrepared = false;
    private Boolean isPlaying = false;
    private Boolean isShuffled = false;
    private Boolean isSelectionMode = false;
    private Boolean selectionModeChanged = false;
    private Boolean isInit = true;

    private int position;

    public MusicManager() {
        songList = new ArrayList<>();
        playlists = new ArrayList<>();
        player = new MediaPlayer();
        playlists.add(new Playlist("Favorites"));
        player.setOnCompletionListener(this);
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

        Stats.listend(currentSong);
        player.start();
        isPlaying = true;
    }

    public void changeSong(Song newSong) {

        List<Song> tmp = songList;

        if(isShuffled) {
            songList = backup;
        }

        if(selectionBackup != null) {
            songList = selectionBackup;
        }

        if(currentSong == null) {
            setCurrentSong(newSong);
            play();

            return;
        }

        if(currentSong.getId() == newSong.getId()) {
            if(player.isPlaying()) {
                try {
                    isPlaying = false;
                    player.pause();
                }catch (IllegalStateException e) {
                    handler.removeCallbacks(update);
                }
            } else {
                if(isPrepared) {
                    player.start();
                    isPlaying = true;
                } else {
                    play();
                }
            }
        } else {
            player.reset();
            setCurrentSong(newSong);
            setSongsByCurrentSong();
            play();
        }

        if(isShuffled) {
            songList = tmp;
        }

        toggleSeekBarProgress(handler, update);
    }

    public void toggleSong() {

        if(currentSong == null) {
            return;
        }

        if(!isPrepared) {
            changeSong(currentSong);
            return;
        }

        if(!player.isPlaying()) {
            player.start();
            isPlaying = true;
        } else {
            try {
                isPlaying = false;
                player.pause();
            } catch (IllegalStateException e) {
                handler.removeCallbacks(update);
            }
        }

        toggleSeekBarProgress(handler, update);
    }

    private void toggleSeekBarProgress(Handler handler, Runnable update) {
        if(handler == null || update == null) {
            return;
        }

        if(player.isPlaying()) {
            handler.postDelayed(update, 50);
        } else {
            handler.removeCallbacks(update);
        }
    }

    public void skipTo(int progress) {
        if(currentSong != null) {
            player.seekTo((int)Math.round(currentSong.getDuration() / 100 * progress * 1000));
        }
    }

    public void toggleLoop() {

        if(player.isLooping()) {
            player.setLooping(false);
        } else {
            player.setLooping(true);
        }
    }

    public void toggleShuffle() {
        if(!isShuffled) {
            backup = songList;
            List<Song> tmp = new ArrayList<>();
            isShuffled = true;

            if(songList.size() > 1) {
                for(Song s : songList) {
                    tmp.add(songList.get(new Random().nextInt(songList.size() - 1)));
                }
            }

            songList = tmp;
        } else {
            isShuffled = false;
            songList = backup;
        }

        setSongsByCurrentSong();
    }

    public void skipSong() {

        currentSong = nextSong;
        setSongsByCurrentSong();


        player.reset();
        play();

//        if(player.isPlaying()) {
//            player.reset();
//            play();
//        }
    }

    public void backSong() {

        currentSong = prevSong;
        setSongsByCurrentSong();


        player.reset();
        play();

//        if(player.isPlaying()) {
//            player.reset();
//            play();
//        }
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

    public Boolean toggleFavorite(Song s) {
        if(!playlists.get(0).getSongList().contains(s)) {
            playlists.get(0).addSong(s);
            return true;
        } else {
            playlists.get(0).removeSong(s);
            return false;
        }
    }

    public Boolean getFav(Song s) {
        if(playlists.get(0).getSongList().contains(s)) {
           return true;
        }
        
        return false;
    }


    public int getPercentageProgress() {
        return (int)Math.round( (player.getCurrentPosition() / 1000) / currentSong.getDuration() * 100) ;
    }

    public Song getSongById(int id) {
        List<Song> tmp = songList;

        if(isShuffled) {
            songList = backup;
        }

        if(selectionBackup != null) {
            songList = selectionBackup;
        }


        if(songList != null && songList.size() > 0) {
            for (Song s : songList) {
                if (s.getId() == id) {
                    return s;
                }
            }
        }

        if(isShuffled || selectionBackup != null) {
            songList = tmp;
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

    public List<Song> sortSongList(Integer id, Boolean asc) {
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

        if(!asc) {
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

        for(SongListener sl : songListeners) {
            sl.songChanged(currentSong);
        }
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

    public void setSongSelection(List<Song> selection) {
        selectionBackup = songList;
        songList = selection;

        //changeSong(songList.get(0));
        currentSong = selection.get(0);
        setSongsByCurrentSong();

        play();
    }

    public boolean addSongsToPlaylist(Playlist p) {
        if(tmp == null || tmp.size() <= 0) {
            return false;
        }

        for(Song s : tmp) {
            if(!p.getSongList().contains(s)) {
                p.addSong(s);
            }
        }

        tmp.clear();
        return true;
    }

    private void defaultSorting() {
        sortSongList(R.id.sortDate, false);
        if(currentSong == null && songList != null && songList.size() > 0) {
            currentSong = songList.get(0);
            setSongsByCurrentSong();
        }
    }

    public Playlist getPlaylistById(int id) {
        for(Playlist p : playlists) {
            if(p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void addPlaylist(Playlist p) {
        playlists.add(p);
    }

    public void removePlaylist(Playlist p) {
        playlists.remove(p);
    }

    public void addSongListener(SongListener sl) {
        songListeners.add(sl);
    }

    public void setTempSongs(List<Song> tmp) {
        this.tmp = tmp;
    }

    public List<Song> getTempSongs() {
        return tmp;
    }

    public void deselectAllSongs() {
        for(Song s : songList) {
            s.setSelected(false);
        }
    }

    public void setSongListFromPlaylist(List<Song> songList) {
        this.songList = songList;
        setSongsByCurrentSong();
    }

    public void restoreSongList() {
        this.songList = originalSongList;
        setSongsByCurrentSong();
    }

    public void pauseBeforeActivity() {
        try {
            isPlaying = false;
            player.pause();
        }catch (IllegalStateException e) {
            handler.removeCallbacks(update);
        }
    }

    public void resumeInActivity() {
        player.start();
        isPlaying = true;
    }

    public List<Song> getOriginalSongList() {
        return originalSongList;
    }

    public void setOriginalSongList(List<Song> originalSongList) {
        this.originalSongList = originalSongList;
    }

    public Boolean getPlaying() {
        return isPlaying;
    }

    public void setPlaying(Boolean playing) {
        isPlaying = playing;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        changeSong(getNextSong());
        setSongsByCurrentSong();
        if(!isInit) {
            play();
        }
    }

    public void setInit(boolean isInit) {
        this.isInit = isInit;
    }

    public interface SongListener {
        public void songChanged(Song s);
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

    public Boolean getSelectionMode() {
        return isSelectionMode;
    }

    public void setSelectionMode(Boolean selectionMode) {
        isSelectionMode = selectionMode;
    }

    public Boolean getSelectionModeChanged() {
        return selectionModeChanged;
    }

    public void setSelectionModeChanged(Boolean selectionModeChanged) {
        this.selectionModeChanged = selectionModeChanged;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Boolean getShuffled() {
        return isShuffled;
    }
}
