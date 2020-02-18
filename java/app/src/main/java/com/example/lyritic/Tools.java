package com.example.lyritic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

public class Tools {
    public static int dpToPx(float dp, Activity activity) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, activity.getResources().getDisplayMetrics());

        return Math.round(px);
    }

    public static Bitmap createClippingMask(Bitmap image, Bitmap mask) {
        Bitmap result;
        Bitmap bitmapMask;

        if(image == null || mask == null) {
            return null;
        }

        result = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmapMask = Bitmap.createScaledBitmap(mask, image.getWidth(), image.getHeight(), true);

        Canvas c = new Canvas(result);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        c.drawBitmap(image, 0, 0, null);
        c.drawBitmap(bitmapMask, 0, 0, paint);

        paint.setXfermode(null);
        paint.setStyle(Paint.Style.STROKE);

        return result;
    }

    @SuppressLint("DefaultLocale")
    public static String millisToMinSecFormat(long millis) {
        long min = (millis % 3600) / 60;
        long sec = millis % 60;

        return String.format("%02d:%02d",  min, sec);
    }
}
