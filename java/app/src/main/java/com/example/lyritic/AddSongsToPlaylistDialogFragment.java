package com.example.lyritic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class AddSongsToPlaylistDialogFragment extends AppCompatDialogFragment {

    private MusicManager musicManager;
    private LinearLayout llPlaylists;
    private AddSongsToPlaylistListener addSongsToPlaylistListener;
    private AddSongsToPlaylistDialogFragment that = this;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        musicManager = DataManager.getMusicManager();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.add_songs_to_playlist_dialog, null);

        builder.setTitle("Select Playlist").setView(v);

        llPlaylists = v.findViewById(R.id.llAddSongsToPlaylist);

        loadPlaylists();

        return builder.create();
    }

    private void loadPlaylists() {
        llPlaylists.removeAllViews();

        for (final Playlist p : musicManager.getPlaylists()) {
            Button btnName = new Button(getActivity());

            btnName.setText(p.getName());
            btnName.setTextSize(20);

            btnName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSongsToPlaylistListener.onPlaylistClicked(p);
                    that.dismiss();
                }
            });

            llPlaylists.addView(btnName);
        }
    }

    public interface AddSongsToPlaylistListener {
        public void onPlaylistClicked(Playlist p);
    }

    @Override
    public void onAttach(Context context) {
        addSongsToPlaylistListener = (AddSongsToPlaylistListener) context;

        super.onAttach(context);
    }
}
