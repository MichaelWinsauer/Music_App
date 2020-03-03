package com.example.lyritic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;

public class Home extends AppCompatActivity implements OnNavigationItemSelectedListener, Sort.BottomSheetListener {

    MusicManager musicManager;
    final int external_storage_permission_code = 1;

    DrawerLayout drawer;
    Toolbar toolbar;
    TextView txtTitle;
    TextView txtArtist;
    ImageView imgCover;

    NavigationView navigationView;
    Bundle savedInstanceState;

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
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BrowseFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_browse);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_browse:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BrowseFragment()).commit();
                break;

            case R.id.nav_sort:
                Sort sort = new Sort();
                sort.show(getSupportFragmentManager(), "sort");
                break;

        }
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if(musicManager.getSelectionMode()) {
            musicManager.setSelectionMode(false);

            BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            browseFragment.toggleSelection();

            return;
        }

        super.onBackPressed();
    }

    private void initialize() {


        musicManager = new MusicManager();
        DataManager.setMusicManager(musicManager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if(musicManager.getCurrentSong() != null) {
                    imgCover.setImageBitmap(Tools.createClippingMask(musicManager.getCurrentSong().getCover(), BitmapFactory.decodeResource(getResources(), R.drawable.cover_mask_circle)));
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

    private void checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            musicManager.setSongList(ContentLoader.load(getBaseContext()));
            Stats.loadData();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        }
        ActivityCompat.requestPermissions(Home.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, external_storage_permission_code);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == external_storage_permission_code) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                musicManager.setSongList(ContentLoader.load(getBaseContext()));
                Stats.loadData();
            } else {
                Toast.makeText(this, "Keine Berechtigungen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSelection(Integer selection, Boolean ascending) {
        musicManager.sortSongList(selection, ascending);

        BrowseFragment browseFragment = (BrowseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        browseFragment.refreshSongList();
    }
}
