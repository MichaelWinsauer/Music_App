package com.example.lyritic;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagOptionSingleton;

import java.io.File;
import java.io.IOException;

public class Details extends AppCompatActivity {

    MusicManager musicManager;
    EditText txtTitle;
    EditText txtArtist;
    EditText txtAlbum;
    EditText txtGenre;
    Button btnSave;
    ImageView imgCover;
    TextView txtSongTitleCover;
    TextView txtSongArtistCover;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        initializeReferences();
        loadCurrentSongData();
        initializeListener();
    }

    private void initializeReferences() {

        if(DataManager.getMusicManager() != null) {
            musicManager = DataManager.getMusicManager();
        }

        txtTitle = findViewById(R.id.txtTitle);
        txtArtist = findViewById(R.id.txtArtist);
        txtAlbum = findViewById(R.id.txtAlbum);
        txtGenre = findViewById(R.id.txtGenre);
        imgCover = findViewById(R.id.imgCover);
        txtSongTitleCover = findViewById(R.id.txtTitleCover);
        txtSongArtistCover = findViewById(R.id.txtArtistCover);
        btnSave = findViewById(R.id.btnSave);
    }

    private void loadCurrentSongData() {
        txtTitle.setText(musicManager.getCurrentSong().getTitle());
        txtArtist.setText(musicManager.getCurrentSong().getInterpret());
        txtAlbum.setText(musicManager.getCurrentSong().getAlbum());
        txtGenre.setText(musicManager.getCurrentSong().getGenre());
        if(musicManager.getCurrentSong().getCover() != null) {
            imgCover.setImageBitmap(Tools.cropBitmapToSquare(musicManager.getCurrentSong().getCover()));
        } else {
            imgCover.setImageBitmap(Tools.cropBitmapToSquare(BitmapFactory.decodeResource(getResources(), R.drawable.missing_img)));
        }
        txtSongTitleCover.setText(musicManager.getCurrentSong().getTitle());
        txtSongArtistCover.setText(musicManager.getCurrentSong().getInterpret());
    }

    private void initializeListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMetadata();
                onBackPressed();
            }
        });
    }

    private void saveMetadata() {
        File src = new File(musicManager.getCurrentSong().getAbsolutePath());
//        MusicMetadataSet mms;
//
//        try {
//            mms = new MyID3().read(src);
//
//            File dst = new File(musicManager.getCurrentSong().getAbsolutePath());
//            MusicMetadata metadata = (MusicMetadata) mms.getSimplified();
//            metadata.setSongTitle(txtTitle.getText().toString());
//            metadata.setArtist(txtArtist.getText().toString());
//            metadata.setAlbum(txtAlbum.getText().toString());
//            metadata.setGenre(txtGenre.getText().toString());
//
//            new MyID3().write(src, dst, mms, metadata);
//        } catch(ID3WriteException | IOException e) {
//            e.printStackTrace();
//        }


        try {
            TagOptionSingleton.getInstance().setAndroid(true);
            AudioFile file = AudioFileIO.read(src);
            Tag tag = file.getTag();

            tag.setField(FieldKey.TITLE, txtTitle.getText().toString());
            tag.setField(FieldKey.ARTIST, txtArtist.getText().toString());
            tag.setField(FieldKey.ALBUM, txtAlbum.getText().toString());
            tag.setField(FieldKey.GENRE, txtGenre.getText().toString());
            AudioFileIO.write(file);

        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }

        musicManager.setSongList(ContentLoader.loadSongs(this));

    }
}
