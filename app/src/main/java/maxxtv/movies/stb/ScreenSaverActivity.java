package maxxtv.movies.stb;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Random;

import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.common.AppConfig;
import maxxtv.movies.stb.Utils.common.GetMac;
import maxxtv.movies.stb.Utils.common.LinkConfig;



public class ScreenSaverActivity extends Activity {
    ImageView imgScreenSaver;
    FrameLayout screenSaverLayout;
    Handler screenSaverHandler;
    Runnable screenSaverRunnable;
    TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);
        getApp().setScreenSaverStarted(true);
        screenSaverLayout = (FrameLayout) findViewById(R.id.layout_screen_saver);
        imgScreenSaver = (ImageView) findViewById(R.id.img_screenSaver);
        txtTime = (TextView) findViewById(R.id.text_time);
        txtTime.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        imgScreenSaver.setImageResource(R.drawable.screensaver);
        if (ApplicationMain.screenSaverImageList.size() <= 1)
            new LoadScreenSaverImageLinks().execute();
//        changePositionForTextView();
        repeatScreenSaver(bundle.getInt("delay"));
        //killBackgroundApps();
//        repeatScreenSaver("",bundle.getInt("delay"));
    }

    public ApplicationMain getApp() {
        return (ApplicationMain) this.getApplication();
    }

    private void repeatScreenSaver(final int milliseconds) {
        final Random random = new Random();
        Thread changeScreenSaverImageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (getApp().getScreenSaverStarted()) {

                    try {
                        Thread.sleep(milliseconds);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ApplicationMain.screenSaverImageList.size() > 1) {
                                    GlideApp.with(ScreenSaverActivity.this)
                                            .load( ApplicationMain.screenSaverImageList.get(random.nextInt(
                                                    ApplicationMain.screenSaverImageList.size()
                                            )))
                                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                            .skipMemoryCache(false)
                                            .error(R.drawable.screensaver)
                                            .into(imgScreenSaver);
                            } else {
                                imgScreenSaver.setImageResource(R.drawable.screensaver);
                            }


                        }
                    });

                }
            }
        });
        changeScreenSaverImageThread.start();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        getApp().active();
        getApp().setScreenSaverStarted(false);
        finish();

    }

    private void changePositionForTextView() {
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(txtTime.getLayoutParams());
        final Random random = new Random();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (getApp().getScreenSaverStarted()) {
                    try {
                        Thread.sleep(8000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    params.setMargins(random.nextInt(500), random.nextInt(500),
                                      random.nextInt(200), random.nextInt(200));
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            txtTime.setLayoutParams(params);
                            Log.d(".ScreenSaverActivity", "layoutParams new set");
                        }
                    }));


                }

            }
        });

        t.start();

    }

    private class LoadScreenSaverImageLinks extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String url = LinkConfig.BASE_URL+LinkConfig.SCREEN_SAVER;
            DownloadUtil downloadUtil = new DownloadUtil(url, getApplicationContext());
            return downloadUtil.downloadStringContent();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject screenObj=new JSONObject(s);
                JSONArray jsonArray = screenObj.getJSONArray("screensaver");
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    String link = jsonArray.getJSONObject(i).getString("banner_image");
                    Log.d("Screen saver link " + i, link);
                    FutureTarget<File> future = Glide.with(ScreenSaverActivity.this)
                            .load(link)
                            .downloadOnly(1920,1080);
                    Glide.with(ScreenSaverActivity.this)
                            .load(link)
                            .preload(1920, 1080);

                    ApplicationMain.screenSaverImageList.add(link);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void killBackgroundApps() {
        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
        //for loop to kill all process except currentApp
        for (ActivityManager.RunningAppProcessInfo process : tasks) {
            if (process.pid != tasks.get(0).pid) {
                android.os.Process.killProcess(process.pid);
            }
        }
        for (ApplicationInfo packageInfo : getPackageManager().getInstalledApplications(0)) {
            if (packageInfo.packageName != getApplicationContext().getPackageName()) {
                am.killBackgroundProcesses(packageInfo.packageName);
            }

        }
    }
}
