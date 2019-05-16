package stb.androidtv.moviesleanback.utils;

import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

public class TimeStamp {
    public static long getTimeStamp(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return  Instant.now().getEpochSecond();
        }else{
            return System.currentTimeMillis()/1000L;
        }
    }
}
