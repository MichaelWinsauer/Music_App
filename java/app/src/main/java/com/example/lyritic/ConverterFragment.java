package com.example.lyritic;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

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

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;


public class ConverterFragment extends Fragment {
    private ConverterListener converterListener;

    private EditText txtLink;
    private Button btnDownload;
    private EditText txtTitle;
    private EditText txtArtist;

    private File src;

    public ConverterFragment() {
        // Required empty public constructor
    }

    public static ConverterFragment newInstance(String param1, String param2) {
        ConverterFragment fragment = new ConverterFragment();



        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.converter, container, false);
        btnDownload = v.findViewById(R.id.btnDownloadConverter);
        txtLink = v.findViewById(R.id.txtLinkConverter);
        txtTitle = v.findViewById(R.id.txtTitleConverter);
        txtArtist = v.findViewById(R.id.txtArtistConverter);


        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtLink.getText().toString().equals("")) {
                    downloadVideo();
                    getContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }
            }
        });

        AndroidAudioConverter.load(getContext(), new ILoadCallback() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void onFailure(Exception error) {

            }
        });

        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadVideo() {
        new YouTubeExtractor(getContext()) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                if(ytFiles != null) {
                    int itag = 18;
                    String downloadUrl = ytFiles.get(itag).getUrl();
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, txtArtist.getText().toString() + " - " + txtTitle.getText().toString() + ".mp4");
                    DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                    request.allowScanningByMediaScanner();
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                    downloadManager.enqueue(request);
                }
            }
        }.execute(txtLink.getText().toString());
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            src = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),txtArtist.getText().toString() + " - " + txtTitle.getText().toString() + ".mp4");
            convertVideo();
            converterListener.onConvertionStarted();
        }
    };

    private void convertVideo() {
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                Toast.makeText(getContext(), "File converted successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception error) {
                error.printStackTrace();
                Toast.makeText(getContext(), "An error ocurred whilst converting the File", Toast.LENGTH_SHORT).show();
            }
        };

        AndroidAudioConverter.with(getContext()).setFile(src).setFormat(AudioFormat.MP3).setCallback(callback).convert();

    }

    @Override
    public void onAttach(Context context) {
        converterListener = (ConverterListener) context;
        super.onAttach(context);
    }

    public interface ConverterListener {
        public void onVideoDownloaded();
        public void onConvertionStarted();
    }
}
