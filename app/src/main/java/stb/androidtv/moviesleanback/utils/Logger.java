package stb.androidtv.moviesleanback.utils;

import android.util.Log;


/**
 * Created by NITV on 02/05/2016.
 */
public class Logger {

    private static final boolean isDebugEnabled = AppConfig.isDevelopment();

    public static void d(String tag, String msg) {
        if (isDebugEnabled) {
            Log.d(tag, "Debug: " + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebugEnabled) {
            Log.e(tag, "Error: " + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebugEnabled) {
            Log.i(tag, "Info: " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebugEnabled) {
            Log.w(tag, "Verbose: " + msg);
        }
    }
    public static void v(String tag, String msg) {
        if (isDebugEnabled) {
            Log.v(tag, "Verbose: " + msg);
        }
    }


    public static void printStackTrace( Exception msg) {
        if( isDebugEnabled ) {
            msg.printStackTrace();
        }
    }
}
