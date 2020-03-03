package com.example.lyritic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Sort extends BottomSheetDialogFragment {
    private BottomSheetListener bottomSheetListener;

    private RadioGroup rgSelection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.bottom_sheet_sort, container, false);

        RadioButton rbAscending = view.findViewById(R.id.sortAscending);
        RadioButton rbDecending = view.findViewById(R.id.sortDescending);

        rgSelection = view.findViewById(R.id.sortGroupSelection);

        rbAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onSelection(rgSelection.getCheckedRadioButtonId(), true);
                dismiss();
            }
        });

        rbDecending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetListener.onSelection(rgSelection.getCheckedRadioButtonId(), false);
                dismiss();
            }
        });

        return view;
    }

    public interface BottomSheetListener {
        void onSelection(Integer selection, Boolean ascending);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        bottomSheetListener = (BottomSheetListener) context;
    }


}
