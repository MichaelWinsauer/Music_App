package com.example.lyritic;

import android.app.Activity;
import android.util.TypedValue;

public class Tools {
    public static int dpToPx(float dp, Activity activity) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics());

        return Math.round(px);
    }
}
