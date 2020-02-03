package com.example.lyritic;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse);

        LinearLayout llSongList = (LinearLayout) findViewById(R.id.llSongList);

        List<Song> songList = new ArrayList<>();


        for(int i = 0; i < 30; i++) {
            songList.add(new Song());
        }

        for(Song s : songList) {
            Button b = new Button(this);
            b.setText("Button " + s.getId());
            b.setBackgroundColor(Color.rgb(40, 53, 64));
            b.setTextColor(Color.rgb(230, 230, 230));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 5, 0, 5);

            llSongList.addView(b, layoutParams);
        }

    }
}
