package com.example.lyritic;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

public class Home extends AppCompatActivity implements OnNavigationItemSelectedListener, Sort.BottomSheetListener, SongFragment.SongFragmentListener, AddPlaylistDialog.AddPlaylistDialogListener, AddSongsToPlaylistDialogFragment.AddSongsToPlaylistListener, PlaylistItemFragment.PlaylistItemListener, ConverterFragment.ConverterListener, BrowseFragment.BrowseListener, PlayerFragment.PlayerListener {

    private final String SHARED_PREFS = "sharedPrefs";
    private final String CURRENT_SONG_ID = "current_song_id";
    private final String CURRENT_POSITION = "currentPosition";
    private final String IS_SHUFFLE = "is_shuffle";
    private final String IS_REPEAT = "is_repeat";

    private MusicManager musicManager;
    private final int external_storage_permission_code = 1;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TextView txtTitle;
    private TextView txtArtist;
    private ImageView imgCover;

    private NavigationView navigationView;
    private SearchView searchView;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        this.savedInstanceState = savedInstanceState;

        initialize();
        checkAppPermissions();
        loadFragment();
    }

    private void loadFragment() {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BrowseFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_browse);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.nav_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setFocusable(true);
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment) {
                    BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    browseFragment.searchSong(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment) {
                    BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    browseFragment.searchSong(newText);
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        searchView.requestFocus();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_browse:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new BrowseFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_browse);
                break;

            case R.id.nav_playlists:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new PlaylistsFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_playlists);
                break;

            case R.id.nav_converter:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left).replace(R.id.fragment_container, new ConverterFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_converter);
                break;

            case R.id.nav_settings:
                navigationView.setCheckedItem(R.id.nav_settings);
                break;

            case R.id.nav_stats:
                navigationView.setCheckedItem(R.id.nav_stats);
                break;

            case R.id.nav_sort:
                Sort sort = new Sort();
                sort.show(getSupportFragmentManager(), "sort");
                break;
        }

        if (item.getItemId() != R.id.nav_search) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return false;
    }

    @Override
    protected void onResume() {
        if(musicManager.getPlayer().isPlaying()) {
            musicManager.toggleSong();
            musicManager.toggleSong();
        }

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment) {
            BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (musicManager.getCurrentSong() != null) {
                browseFragment.refreshData();
                browseFragment.setSeekBarData();
            }
        }


        super.onResume();
    }

    @Override
    public void onBackPressed() {
        saveData();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (musicManager.getSelectionMode()) {
            musicManager.deselectAllSongs();
            musicManager.setSelectionMode(false);
            if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment) {
                BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                browseFragment.toggleSelection();
                return;
            }
        }

        if (searchView.hasFocus()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
        }

        if (!(getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BrowseFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_browse);
            return;
        }


        super.onBackPressed();
    }

    private void initialize() {

        if (DataManager.getMusicManager() == null) {
            musicManager = new MusicManager();
            DataManager.setMusicManager(musicManager);
        } else {
            musicManager = DataManager.getMusicManager();
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (musicManager.getCurrentSong() != null) {
                    if (musicManager.getCurrentSong().getCover() != null) {
                        imgCover.setImageBitmap(Tools.createClippingMask(musicManager.getCurrentSong().getCover(), BitmapFactory.decodeResource(getResources(), R.drawable.cover_mask_circle)));
                    } else {
                        imgCover.setImageBitmap(Tools.createClippingMask(BitmapFactory.decodeResource(getResources(), R.drawable.missing_img), BitmapFactory.decodeResource(getResources(), R.drawable.cover_mask_circle)));
                    }
                    txtTitle.setText(musicManager.getCurrentSong().getTitle());
                    txtArtist.setText(musicManager.getCurrentSong().getInterpret());
                }
                super.onDrawerStateChanged(newState);
            }
        };


        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View nav_header = navigationView.getHeaderView(0);

        txtTitle = nav_header.findViewById(R.id.txtDrawerTitle);
        txtArtist = nav_header.findViewById(R.id.txtDrawerArtist);
        imgCover = nav_header.findViewById(R.id.imgDrawerCover);

    }

    private void refreshPlaylists() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof PlaylistsFragment) {
            PlaylistsFragment playlistsFragment = (PlaylistsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            playlistsFragment.loadPlaylists();
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(CURRENT_SONG_ID, musicManager.getCurrentSong().getId());
        editor.putBoolean(IS_REPEAT, musicManager.getPlayer().isLooping());
        editor.putBoolean(IS_SHUFFLE, musicManager.getShuffled());
        editor.putInt(CURRENT_POSITION, musicManager.getPlayer().getCurrentPosition());
        editor.apply();

        MusicData ms = new MusicData();
        ms.setPlaylistData(musicManager.getPlaylists()).save(this);

    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        musicManager.setSongList(musicManager.getOriginalSongList());
//        musicManager.play();
//        musicManager.getPlayer().pause();
        musicManager.setCurrentSong(musicManager.getSongById(sharedPreferences.getInt(CURRENT_SONG_ID, musicManager.getSongList().get(0).getId())));
        musicManager.setSongsByCurrentSong();
        musicManager.getPlayer().seekTo(sharedPreferences.getInt(CURRENT_POSITION, 0));

        if (sharedPreferences.getBoolean(IS_SHUFFLE, false)) {
            musicManager.toggleShuffle();
        }

        if (sharedPreferences.getBoolean(IS_REPEAT, false)) {
            musicManager.toggleLoop();
        }

        MusicData ms = new MusicData();
        if (ms.read(this).getPlaylistIds() != null && ms.read(this).getPlaylistIds().size() > 0) {
            musicManager.getPlaylists().clear();
            ms.read(this);

            for (int i : ms.getPlaylistIds()) {
                Playlist p = new Playlist();
                p.setId(i);
                p.setName(ms.getPlaylistNames().get(i));
                musicManager.addPlaylist(p);
            }

            for (PlaylistIdSongId i : ms.getSongs()) {
                for (int j : i.getSongIds()) {
                    if (!musicManager.getPlaylistById(i.getPlaylistId()).getSongList().contains(musicManager.getSongById(j))) {
                        musicManager.getPlaylistById(i.getPlaylistId()).addSong(musicManager.getSongById(j));
                    }
                }
            }
        }
    }

    private void checkAppPermissions() {
        if(savedInstanceState != null) {
            return;
        }

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Home.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Home.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        ) {
            musicManager.setSongList(ContentLoader.loadSongs(getBaseContext()));
            musicManager.setOriginalSongList(musicManager.getSongList());
            DataManager.setMusicManager(musicManager);
            Stats.loadData();
            loadData();
            new LocationTracker(this);
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

        }
        ActivityCompat.requestPermissions(Home.this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        }, external_storage_permission_code);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean denied = false;

        if (requestCode == external_storage_permission_code) {
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        denied = true;
                    }
                }
            } else {
                Toast.makeText(this, "Keine Berechtigungen", Toast.LENGTH_SHORT).show();
            }
        }

        if (!denied) {
            musicManager.setSongList(ContentLoader.loadSongs(getBaseContext()));
            musicManager.setOriginalSongList(musicManager.getSongList());
            DataManager.setMusicManager(musicManager);
            Stats.loadData();
            loadData();
            new LocationTracker(this);
        } else {
            Toast.makeText(this, "Keine Berechtigungen", Toast.LENGTH_SHORT).show();
        }
    }

    public static MusicManager getMusicManager() {
        return DataManager.getMusicManager();
    }

    @Override
    public void onSelection(Integer selection, Boolean ascending) {
        musicManager.sortSongList(selection, ascending);

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment) {
            BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            browseFragment.refreshSongList();
        }
    }

    @Override
    public void onSongClicked(View v, Song s) {
        musicManager.changeSong(s);
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment) {
            BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            browseFragment.setSongData(s);
        }
    }

    @Override
    public void onFavClicked(View v, Song s) {
    }

    @Override
    public void onSongHold(View v, Song s) {
        musicManager.setSelectionMode(true);
    }

    @Override
    public void onSongSelected(View v, Song s) {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) instanceof BrowseFragment) {
            BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            browseFragment.toggleSelection();
        }
    }

    @Override
    public void onPlaylistAdded(Playlist p) {
        refreshPlaylists();
        saveData();
    }

    @Override
    public void onPlaylistClicked(Playlist p) {
        musicManager.addSongsToPlaylist(p);
    }

    @Override
    public void onDelete() {
        refreshPlaylists();
        saveData();
    }

    @Override
    public void onVideoDownloaded() {
        Toast.makeText(this, "File Converted", Toast.LENGTH_SHORT).show();
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_left, R.anim.enter_from_bottom, R.anim.exit_to_left).replace(R.id.fragment_container, new BrowseFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_browse);
    }

    @Override
    public void onConvertionStarted() {
        Toast.makeText(this, "Convertion started... ", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
//        musicManager.pauseBeforeActivity();
        super.onPause();
    }

    @Override
    public void onPlayerClicked() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom).replace(R.id.fragment_container, new PlayerFragment()).commit();
    }

    @Override
    public void onPlayClicked() {
        musicManager.toggleSong();
    }
}
