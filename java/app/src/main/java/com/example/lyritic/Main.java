package com.example.lyritic;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.View;
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

        final List<Song> songList;

        songList = ContentLoader.load(getBaseContext());

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
                            s.play();
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
}