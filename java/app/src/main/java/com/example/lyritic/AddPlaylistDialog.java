package com.example.lyritic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

public class AddPlaylistDialog extends AppCompatDialogFragment {

    private EditText txtNewPlaylistName;
    private MusicManager musicManager;
    private AddPlaylistDialogListener addPlaylistDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        musicManager = DataManager.getMusicManager();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.add_playlist_dialog, null);

        txtNewPlaylistName = v.findViewById(R.id.txtNewPlaylistName);

        builder.setView(v).setTitle("new Playlist").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(txtNewPlaylistName.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please enter a name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(Playlist p : musicManager.getPlaylists()) {
                    if(p.getName().equals(txtNewPlaylistName.getText().toString())) {
                        Toast.makeText(getActivity(), "Name already in use!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                Playlist p = new Playlist(txtNewPlaylistName.getText().toString());

                musicManager.addPlaylist(p);
                addPlaylistDialogListener.onPlaylistAdded(p);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        addPlaylistDialogListener = (AddPlaylistDialogListener) context;

        super.onAttach(context);
    }

    public interface AddPlaylistDialogListener {
        public void onPlaylistAdded(Playlist p);
    }
}
