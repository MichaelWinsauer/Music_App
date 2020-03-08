package com.example.lyritic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.File;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;

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

        if(image.getWidth() != image.getHeight()) {
            image = cropBitmapToSquare(image);
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

    public static Bitmap cropBitmapToSquare(Bitmap src) {

        if(src == null) {
            return null;
        }

        Bitmap result = src;

        if(src.getWidth() >= src.getHeight()) {
            result = Bitmap.createBitmap(
                    src,
                    src.getWidth()/2 - src.getHeight()/2,
                    0,
                    src.getHeight(),
                    src.getHeight()
            );

        } else if (src.getWidth() < src.getHeight()) {
            result = Bitmap.createBitmap(
                    src,
                    0,
                    src.getHeight()/2 - src.getWidth()/2,
                    src.getWidth(),
                    src.getWidth()
            );
        }

        return result;
    }

    @SuppressLint("DefaultLocale")
    public static String millisToMinSecFormat(long millis) {
        long min = (millis % 3600) / 60;
        long sec = millis % 60;

        return String.format("%02d:%02d",  min, sec);
    }

    public static void convertVideoToAudio(File video, String title, String artist) {
        File target = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + "/" + artist + " - " + title + ".mp3");

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("libmp3lame");
        audio.setBitRate(128000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);

        Encoder encoder = new Encoder();


        try {
            encoder.encode(video, target, attrs);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }
}
