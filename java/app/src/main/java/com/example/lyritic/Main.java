package com.example.lyritic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {
    int external_storage_permission_code;
    LinearLayout llSongList;
    List<Song> songList;
    final MediaPlayer player = new MediaPlayer();
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        llSongList = findViewById(R.id.llSongList);
        context = getBaseContext();

        if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            songList = ContentLoader.load(getBaseContext());
            createSongs(llSongList);
        } else {
            requestPermission();
        }
    }

    private void createSongs(LinearLayout llSongList) {
        //Nur temporär
        for(Song s : songList) {
            final Button b = new Button(this);
            b.setText(s.getTitle());
            b.setBackgroundColor(this.getColor(R.color.colorSecondaryDark));
            b.setTextColor(this.getColor(R.color.colorSecondary));
            b.setTag(s.getId());

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for (Song s : songList) {
                        if(s.getId() != (Integer)b.getTag()) {
                            continue;
                        }
                        if(!s.getIsPlaying()) {
                            s.play(context);
//                            MediaPlayer player = new MediaPlayer();
//
//                            try {
//                                player.setDataSource(context, Uri.parse(s.getAbsolutePath()));
//                                player.prepare();
//                            } catch(IOException e) {
//                                e.printStackTrace();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            player.start();
                        } else {
                            s.pause();
                        }

                        break;
                    }
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 3, 0, 0);

            llSongList.addView(b, layoutParams);
        }
    }


    private void requestPermission() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        }
        ActivityCompat.requestPermissions(Main.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, external_storage_permission_code);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == external_storage_permission_code) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                songList = ContentLoader.load(getBaseContext());
                createSongs(llSongList);
            } else {
                Toast.makeText(this, "Keine Berechtigungen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}