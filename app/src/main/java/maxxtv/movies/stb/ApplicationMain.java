package maxxtv.movies.stb;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import maxxtv.movies.stb.Async.SendingCrashReport;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.AppConfig;

/*import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;*/


/**
 * Created by sadip on 21/03/2016.
 * Point to change before using the sample:
 * open build:gradle(Module:app)
 * paste "  useLibrary 'org.apache.http.legacy' " inside android tag
 * paste "    compile 'com.koushikdutta.ion:ion:2.+'  " inside dependency tag
 */

public class ApplicationMain extends Application {
    private static final String TAG = ApplicationMain.class.getName();
    private static final int DELAY_TO_START_SCREENSAVER = 5 * 60 * 1000;
    private static final int DELAY_BETWEEN_CHANGE_IMAGE_IN_SCREENSAVER = 1 * 60 * 1000;
    private static final int USER_INACTIVITY_CHECK_FOR_SCREENSAVER = 5 * 60 * 1000;
    public static ArrayList<String> screenSaverImageList;

    private UserInteractionThread waiter;
    private boolean screenSaverStarted = false; // flag that knows if screensaver is on top or not
    private boolean videoPlaying = false;// flag that knows if video is being played in application so that screensaver should not be started
    private String userAgent;


    public boolean getVideoPlaying() {
        return videoPlaying;
    }


    public void setVideoPlaying(boolean isVideoPlaying) {
        this.videoPlaying = isVideoPlaying;
    }

    public boolean getScreenSaverStarted() {
        return screenSaverStarted;
    }

    public void setScreenSaverStarted(boolean isScreenSaverOn) {
        screenSaverStarted = isScreenSaverOn;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting Application" + this.toString());
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(0) // Must be bumped when the schema changes
//                .migration(new MyMigration()) // Migration to run instead of throwing an exception
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
        userAgent = Util.getUserAgent(this, getString(R.string.app_name));

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread
                    , Throwable e) {
                handleUncaughtException(thread, e);
            }
        });

        waiter = new UserInteractionThread(DELAY_TO_START_SCREENSAVER);// starts checking after 5 minutes
        screenSaverImageList = new ArrayList<String>();
        screenSaverImageList.add("");
        waiter.start();


    }

    public void handleUncaughtException(Thread thread, Throwable e) {

      /*  e.printStackTrace();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        String s = writer.toString();
        Logger.d("CheckingErrorStatus", s);

        new SendingCrashReport(getApplicationContext(), s).execute(getResources().getString(R.string.send_crash_url));
*/

        try {
            e.printStackTrace();
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();
            Logger.d("CheckingErrorStatus", s);
            // String fpath = "/sdcard/.Movies_wod/"+fname+".txt";
            File file = new File(getExternalFilesDir(null), "crash_report");
            Logger.d("File Stored in", getExternalFilesDir(null).getPath()
                    +"crash_report");
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(s);
            bw.close();
        } catch (IOException e1) {
            Logger.printStackTrace(e1);
            Toast.makeText(this, "failed to save datas", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * this method must be called from every activity in onUserInteraction() to find user inactivity
     */
    public void active() {
        waiter.active();
        setScreenSaverStarted(false);
    }

    /**
     * @return the flag that recognizes if app is on top of any other application
     */
    private boolean currentAppInForeground() {
        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        return currentApp.equalsIgnoreCase(getApplicationContext().getPackageName());


    }

    /**
     * it is the main thread that finds user interation whole over the app
     * it keeps running even if the app is closed pressing back, couldnt figure that why?
     * but can be stopped if within the running perion the flag stop is set to true
     * shows screen saver only if the app is in foregorund
     */
    private class UserInteractionThread extends Thread {
        private long lastUsed;
        private long period;
        private boolean stop;


        public UserInteractionThread(long period) {
            this.period = period;
            stop = false;
        }

        public void run() {
            long idle = 0;
            this.active();
            Looper.prepare();
            while (!stop) {
                try {
                    Thread.sleep(USER_INACTIVITY_CHECK_FOR_SCREENSAVER); //checks to show screensaver after every 1 minute
                } catch (InterruptedException e) {
                    Log.d(TAG, "Waiter interrupted!");
                }
                idle = System.currentTimeMillis() - lastUsed;
                Log.d(TAG, "Application is idle for " + idle + " ms");
                if (idle > period) {
                    idle = 0;

                    /**
                     * show screenSaver only
                     * if the app is running on foreground
                     * if screensaver is not already shown
                     * if video is not being played
                     */

                    if (currentAppInForeground() && !getScreenSaverStarted() && !getVideoPlaying()) {
                        startScreenSaver();
                        //onTrimMemory(TRIM_MEMORY_BACKGROUND);
                    }

                }

            }
            /**
             * if needed to stop thread running set the flag stop here to true
             */

        }

        private synchronized void active() {
            lastUsed = System.currentTimeMillis();
        }

        /**
         * it is to show the screen saver which is shown as activity
         */
        private void startScreenSaver() {

            if (checkScreenStatus()) {
                Toast.makeText(ApplicationMain.this, "Displaying.....", Toast.LENGTH_SHORT).show();
                Log.d("CheckingScreenStaus", "Displaying....");
                Intent i = new Intent(getApplicationContext(), ScreenSaverActivity.class);
                i.putExtra("delay", DELAY_BETWEEN_CHANGE_IMAGE_IN_SCREENSAVER);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
            } else {
                Toast.makeText(ApplicationMain.this, "Displaying.....", Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), "Screen Saver is off", Toast.LENGTH_SHORT).show();
                Log.d("CheckingScreenStaus", "Not Displaying....");
            }
        }

        // for soft stopping of thread within the loop that keeps running thread on background too
        public void stopThread() {
            stop = true;
        }

    }

    private boolean checkScreenStatus() {
        boolean flag = true;
        try {
            Cursor cursor = getContentResolver().query(Uri.parse(AppConfig.SCREEN_URL), null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String flg = cursor.getString(0);
                    Logger.d("CheckingvlauefoS", flg);
                    if (flg.equals("true")) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
            } else {

            }
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }

        return flag;
    }
    public DataSource.Factory buildDataSourceFactory(TransferListener<? super DataSource> listener) {
        return new DefaultDataSourceFactory(this, listener, buildHttpDataSourceFactory(listener));
    }

    /** Returns a {@link HttpDataSource.Factory}. */
    public HttpDataSource.Factory buildHttpDataSourceFactory(
            TransferListener<? super DataSource> listener) {
        return new DefaultHttpDataSourceFactory(userAgent, listener);
    }
}

