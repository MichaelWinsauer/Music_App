package com.example.lyritic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Home extends AppCompatActivity {

    MusicManager musicManager;
    final int external_storage_permission_code = 1;
    ImageButton imgButtonBrowse;
    ImageButton imgButtonPlaylists;
    ImageButton imgButtonSettings;
    ImageButton imgButtonStats;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        initialize();
        checkAppPermissions();
        initializeListener();
    }

    private void initialize() {
        musicManager = new MusicManager();

        imgButtonBrowse = findViewById(R.id.imgBtnHomeBrowse);
        imgButtonPlaylists = findViewById(R.id.imgBtnHomePlaylists);
        imgButtonSettings = findViewById(R.id.imgBtnHomeSettings);
        imgButtonStats = findViewById(R.id.imgBtnHomeStats);
    }

    private void initializeListener() {

        imgButtonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Browse.class);
                DataManager.setMusicManager(musicManager);
                startActivity(intent);
            }
        });
    }

    private void checkAppPermissions() {
        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            musicManager.setSongList(ContentLoader.load(getBaseContext()));

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
            } else {
                Toast.makeText(this, "Keine Berechtigungen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
