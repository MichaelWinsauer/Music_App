package com.example.lyritic;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        LinearLayout llSongList = findViewById(R.id.llSongList);

        List<Song> songList;

        songList = ContentLoader.load(getBaseContext());

        //Nur temporär
        for(Song s : songList) {
            Button b = new Button(this);
            b.setText(s.getTitle());
            b.setBackgroundColor(this.getColor(R.color.colorSecondaryDark));
            b.setTextColor(this.getColor(R.color.colorSecondary));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 3, 0, 0);

            llSongList.addView(b, layoutParams);
        }
    }
}