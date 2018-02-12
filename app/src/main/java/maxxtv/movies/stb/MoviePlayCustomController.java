package maxxtv.movies.stb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmQuery;
import maxxtv.movies.stb.Async.ApkDownloader;
import maxxtv.movies.stb.Async.ImdbCredits;
import maxxtv.movies.stb.Async.ImdbDetails;
import maxxtv.movies.stb.Entity.ImdbPojo;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Entity.ShowBgProgress;
import maxxtv.movies.stb.Entity.ShowFragmentEntity;
import maxxtv.movies.stb.Fragments.InfoFragment;
import maxxtv.movies.stb.Fragments.OptionsFragment;
import maxxtv.movies.stb.Interface.AsyncBack;
import maxxtv.movies.stb.Interface.ImdbCallback;
import maxxtv.movies.stb.Parser.MarketAppDetailParser;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.EnterPasswordDialog;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.LoginFileUtils;
import maxxtv.movies.stb.Utils.Mcrypt;
import maxxtv.movies.stb.Utils.MovieInFile;
import maxxtv.movies.stb.Utils.PackageUtils;
import maxxtv.movies.stb.Utils.ParentalLockUtils;
import maxxtv.movies.stb.Utils.PropertyGetSet;
import maxxtv.movies.stb.Utils.common.AppConfig;
import maxxtv.movies.stb.Utils.common.LinkConfig;

public class MoviePlayCustomController extends AppCompatActivity implements
        SurfaceHolder.Callback, VideoControllerView.MediaPlayerControl,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, OptionsFragment.OnFragmentInteractionListener, AsyncBack, ImdbCallback {
    private static final String API_KEY = "91ac7578ede4905ea0004aa546ce21c7";

    private static final String TAG = MoviePlayCustomController.class.getSimpleName();
    private static final int NEXT_MOVIE = 1;
    private static final int PREV_MOVIE = 2;
    private TextView movieTitle, bgCurrrentTime, bgEndTime;
    private LinearLayout bgSeekLayout;
    final Context mContext = this;
    Uri video;
    ArrayList<Movie> moviesList;
    String username, macAddress = "";
    SurfaceView videoSurface;
    MediaPlayer player;
    private Handler myHandler = new Handler();
    ;
    VideoControllerView controller;
    SurfaceHolder videoHolder;
    private CustomDialogManager loading_dialog;
    private Bundle extras;
    private String final_video_url;
    private Movie thisMovie;
    private int currentMovieId;
    private Handler handler;
    private Runnable r;
    private Realm realm;
    private String movieName, overview;
    private ProgressBar bgSeekBar;

    private ImdbPojo imdbGlobal;
    private String authToken;
//    Timer timer;

    //    private Handler handlerChangeChannelFromNumbers = new Handler();
//
//    private Runnable runnableChangeChannelFromNumbers = new Runnable() {
//        @Override
//        public void run() {
//            mac_check_link = getString(R.string.mac_check_link);
//            new CheckIfValidMacAddress().execute(mac_check_link + "?mac=" + EntryPoint.macAddress);
//            Toast.makeText(MoviePlayCustomController.this, "Handler Started", Toast.LENGTH_SHORT).show();
//
//        }
//    };
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (player == null) {
                return;
            }

            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = player.getDuration();
            long newposition = (duration * progress) / 1000L;
            player.seekTo((int) newposition);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            setProgress();
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
        }
    };

    /*TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {

            try {
                MovieInFile movieinfile = new MovieInFile(thisMovie,
                        player.getCurrentPosition());
                write(currentMovieId + "", movieinfile);
            } catch (Exception e) {
                Logger.e("ExceptionSaving",e.getMessage());
            }

        }

    };*/

    private void setProgress() {
        if (player == null) {
            return;
        }
        int position = player.getCurrentPosition();
        int duration = player.getDuration();
        if (bgSeekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                bgSeekBar.setProgress((int) pos);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_player);
        authToken = LoginFileUtils.getAuthTokenFromFile();
        realm = Realm.getDefaultInstance();
        moviesList = getIntent().getParcelableArrayListExtra("movie_list");
        movieTitle = (TextView) findViewById(R.id.movie_title);
        bgSeekBar = (ProgressBar) findViewById(R.id.background_seekbar);
        bgSeekLayout = (LinearLayout) findViewById(R.id.background_seekbar_layout);
        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        bgSeekBar = (SeekBar) findViewById(R.id.background_seekbar);
        if (bgSeekBar != null) {
            if (bgSeekBar instanceof SeekBar) {
                SeekBar seeker = (SeekBar) bgSeekBar;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            bgSeekBar.setMax(1000);
            bgSeekBar.getProgressDrawable().setColorFilter(
                    Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
            videoHolder = videoSurface.getHolder();
            videoHolder.addCallback(this);
            player = new MediaPlayer();
            controller = new VideoControllerView(this);
            extras = getIntent().getExtras();
            handler = new Handler();
            r = new Runnable() {

                @Override
                public void run() {
                }
            };
            startHandler();

            int currentMovieId = extras.getInt("currentMovieId");
            checkToLoadMovieLink(MoviePlayCustomController.this, currentMovieId, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ShowFragmentEntity showFragmentEntity) {
        /* Do something */
        if (showFragmentEntity.isShowfrag()) {
            movieTitle.setVisibility(View.VISIBLE);
            OptionsFragment optionsFragment = OptionsFragment.newInstance(currentMovieId, "dfgdfgfd");
            FragmentTransaction optionTransaction = getSupportFragmentManager().beginTransaction();
            optionTransaction.replace(R.id.controller_fragment_container, optionsFragment, "optionsFragment").commit();

        } else {
            movieTitle.setVisibility(View.GONE);
            Fragment optionsFragment = getSupportFragmentManager().findFragmentByTag("optionsFragment");
            if (optionsFragment != null) {
                FragmentTransaction optTransation = getSupportFragmentManager().beginTransaction();
                optTransation.remove(optionsFragment).commit();
            }


        }

    }

    ;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ShowBgProgress showBgProgress) {
        /* Do something */
        if (showBgProgress.isShowbgProgress()) {
            bgSeekLayout.setVisibility(View.VISIBLE);

        } else {
            bgSeekLayout.setVisibility(View.INVISIBLE);
        }

    }

    public void startHandler() {
        handler.postDelayed(r, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showInfoFragment() {
        final InfoFragment infoFragment = InfoFragment.newInstance("infoFragment", this.imdbGlobal);
        final FragmentTransaction showinfo = getSupportFragmentManager().beginTransaction();
        showinfo.replace(R.id.controller_fragment_container, infoFragment, "infoFragment").addToBackStack("optionsFragment").commit();
        Handler hideHandler = new Handler();
        hideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment infoFrag = getSupportFragmentManager().findFragmentByTag("infoFragment");
                FragmentTransaction infoTransation = getSupportFragmentManager().beginTransaction();
                infoTransation.remove(infoFrag).commit();
            }
        }, 3000);


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onOptionFragmentInteraction() {
        controller.show(5000);
    }


    public void checkToLoadMovieLink(final Context context,
                                     final int movieId, final boolean flag_to_end_activity) {
        this.imdbGlobal = null;

        Movie toLoadMovie= realm.where(Movie.class).equalTo("movie_id", movieId).findFirst();

        movieName = toLoadMovie.getMovie_name();
        overview = toLoadMovie.getMovie_description();

        if (player.isPlaying()) {
            player.stop();
            player.reset();
        }
        if (toLoadMovie.getIs_Imdb() == 1) {
            new ImdbDetails(MoviePlayCustomController.this, MoviePlayCustomController.this).execute(getResources().getString(R.string.imdb_details) + toLoadMovie.getImdb_id() + "?api_key=" + API_KEY + "&language=en-US");
        } else {
            imdbGlobal = new ImdbPojo();
            imdbGlobal.setWriters("N/A");
            imdbGlobal.setCasts("N/A");
            imdbGlobal.setDirector("N/A");
            imdbGlobal.setMovie_name(toLoadMovie.getMovie_name());
            imdbGlobal.setOverview(toLoadMovie.getMovie_description());
            imdbGlobal.setRelease_date("N/A");
        }
//        if (!ParentalLockUtils.isMovieParentallyLocked(movie)) {
        if (toLoadMovie.getParental_lock() == 0) {
            movieTitle.setText(toLoadMovie.getMovie_name());
            final String media_url = LinkConfig.getString(context,
                    LinkConfig.MOVIE_PLAY_LINK) + "?movieId=" + movieId;
            new MovieLinkLoader(context, movieId, flag_to_end_activity, authToken)
                    .execute(media_url);
        } else {

            Logger.d("movie parental locked", "do not let let play channel");
            final CustomDialogManager parentalWarning = new CustomDialogManager(context, "Parental Lock", "Sorry this movie is parentially locked",
                    CustomDialogManager.WARNING);
            parentalWarning.build();
            parentalWarning.show();
            parentalWarning.dismissDialogOnBackPressed(MoviePlayCustomController.this);
            parentalWarning.setExtraButton(
                    context.getString(R.string.btn_dismiss),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            parentalWarning.dismiss();
                            finish();

                        }
                    });
            parentalWarning.setNeutralButton(
                    context.getString(R.string.btn_watch),
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            parentalWarning.dismiss();
                            if (checkPinStatus()) {
                                EnterPasswordDialog.showParentalControlPasswordDialogToPlayMovie(context,
                                        movieId, flag_to_end_activity, authToken);
                            } else {
                                ParentalLockUtils.changeMovieParentalStatus(MoviePlayCustomController.this, movieId, authToken);
                            }

                        }
                    });
//            parentalWarning.getNeutralButton().requestFocus();
//
//        }


        }
    }

    public boolean checkPinStatus() {
        boolean flag = false;
        try {
            Cursor cursor = this.getContentResolver().query(Uri.parse(AppConfig.PIN_URL), null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String flg = cursor.getString(0);
                    Logger.d("CheckingvlauefoC", flg);
                    flag = flg.equals("true");
                }
            }
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
        return flag;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player.isPlaying()) {
            try {
                if (player.getCurrentPosition() > 0) {
                    MovieInFile movieinfile = new MovieInFile(thisMovie,
                            player.getCurrentPosition());
                    write(currentMovieId + "", movieinfile);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            player.stop();
        }
        EventBus.getDefault().unregister(this);
        try {
//            timer.cancel();
        }catch (NullPointerException ignored){}
        myHandler.removeCallbacks(null);
        finish();

    }

    @Override
    protected void onDestroy() {

        // if (player != null) {
        try {
            player.stop();

            try {
                player.reset();
            } catch (IllegalStateException ex) {
                Logger.printStackTrace(ex);
            }

            player.release();
            player = null;
        } catch (Exception e) {
            Logger.d("Exception at onDestroy Method", e.getMessage() + "");
        }
        super.onDestroy();
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            Logger.d("This Is written at file when key pressed",
                    player.getCurrentPosition() + "");
            MovieInFile movieinfile = new MovieInFile(thisMovie,
                    player.getCurrentPosition());
            write(currentMovieId + "", movieinfile);
        } catch (Exception e) {
            Logger.e("Exception at onBackPressed Method", e.getMessage() + "");
        }

        switch (keyCode) {
            /*** USED ONLY FOR TEST ****************************************************************************************************/
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!controller.isShowing()) {
                    controller.show();
                    return true;
                } else
                    return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!controller.isShowing()) {
                    controller.show();
                    return true;
                } else
                    return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_DPAD_CENTER:
                controller.show();
                return true;

            /*******************************************************************************************************/
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!controller.isShowing()) {
                    startHandler();
                    player.seekTo(player.getCurrentPosition() - 10000);
                    return true;
                } else return super.onKeyDown(keyCode, event);


            case KeyEvent.KEYCODE_DPAD_RIGHT:
                startHandler();
                if (!controller.isShowing()) {
                    player.seekTo(player.getCurrentPosition() + 10000);
                    return true;
                } else return super.onKeyDown(keyCode, event);


            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                startHandler();
                controller.show();

                if (player.isPlaying())
                    player.pause();
                else {
                    player.start();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                startHandler();
                controller.show();
                player.pause();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                startHandler();
                controller.show();
                player.start();
                return true;

            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                startHandler();
                player.seekTo(player.getCurrentPosition() + 10000);
                controller.show();
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                startHandler();
                player.seekTo(player.getCurrentPosition() - 10000);
                controller.show();
                return true;

            case KeyEvent.KEYCODE_INFO:
            case 83:
                controller.hide();

                return true;

            case 87: // ch+ next from remote control
            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_PAGE_UP:

                Logger.d("ENTERED INSIDE", "KEYCODE_PAGE_UP");
                playNextorPrevMovie(NEXT_MOVIE);

                return true;

            case 88:
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_PAGE_DOWN:
                playNextorPrevMovie(PREV_MOVIE);
                return true;

            case KeyEvent.KEYCODE_MENU:
                startHandler();
                controller.hide();
                return true;

            case 44:
                controller.show();
                player.start();
                return true;
            case 7:
                controller.show();
                player.pause();
                return true;


            case KeyEvent.KEYCODE_BACK:
                try {
                    player.stop();
                } catch (Exception e) {
                    Logger.e("Exception at key event of case 4", e.getMessage()
                            + "");
                }
//                PropertyGetSet.setProperty(PropertyGetSet.VIDEO_RUNNING_USAGE,
//                                           "0");
//                PropertyGetSet.setProperty(PropertyGetSet.SCREEN_SAVER, "1");

                finish();
                this.finishFromChild(getParent());
                return true;
            default:
                return super.onKeyDown(keyCode, event);

        }
    }

    private void playNextorPrevMovie(int NEXTorPREV) {
        try {
            if (moviesList.size() > 0) {
                Movie temp;
                try {
                    temp = prevnextlisteners(moviesList, NEXTorPREV,
                            thisMovie.getMovie_id());
                    player.stop();
                    player.reset();
                    checkToLoadMovieLink(MoviePlayCustomController.this, temp.getMovie_id(),
                            true);

                } catch (Exception e) {

                    Logger.printStackTrace(e);
                }


            }
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
    }

    // end of handler

    public Movie prevnextlisteners(ArrayList<Movie> moviesList, int prevnext,
                                   int movieid) {
        Movie prevmovie, nextmovie;
        prevmovie = new Movie();
        nextmovie = new Movie();
        Logger.e("MovieList size at prevnextlisteners", moviesList.size() + "");
        for (int i = 0; i < moviesList.size(); i++) {
            int temp = moviesList.get(i).getMovie_id();
            if (temp == movieid) {
                if (i > 0)
                    prevmovie = moviesList.get(i - 1);
                else
                    prevmovie = moviesList.get(moviesList.size() - 1);

                if (i < moviesList.size() - 1)
                    nextmovie = moviesList.get(i + 1);
                else
                    nextmovie = moviesList.get(0);
            }

        }

        Logger.d(TAG, prevnext + "");
        Logger.d(TAG, prevmovie.toString());
        Logger.d(TAG, nextmovie.toString());

        if (prevnext == PREV_MOVIE) {
            return prevmovie;
        } else if (prevnext == NEXT_MOVIE) {
            return nextmovie;
        } else
            return thisMovie;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    // HANDLER THAT HANDLES USER INACTIVITY AND HIDES SIMILAR MOVIES AFTER 5
    // SECONDS OF INACTIVITY
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        getApp().active();
        stopHandler();
        startHandler();
    }

    public ApplicationMain getApp() {
        return (ApplicationMain) this.getApplication();
    }


    public void stopHandler() {
        handler.removeCallbacks(r);
    }

    public void write(String fname, MovieInFile movieinfile) {
        try {
            // String fpath = "/sdcard/.Movies_wod/"+fname+".txt";
            File file = new File(getExternalFilesDir(null), currentMovieId + "");
            Logger.d("File Stored in", getExternalFilesDir(null).getPath()
                    + currentMovieId + "");

            // new File(fpath);
            if (file.exists())
                file.delete();
            // If file does not exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            // int movieId = movieinfile.getmovie().getId();
            // String movieName = movieinfile.getmovie().getName();
            // String movieLogoLink =
            // movieinfile.getmovie().getImageLink();

            bw.write(movieinfile.getMovieSaveDuration() + "\n"
                    + movieinfile.getmovie().getMovieInString());
            bw.close();
            Logger.d("Suceess", movieinfile.getmovie().getMovieInString());
        } catch (IOException e) {
            Logger.printStackTrace(e);
            Toast.makeText(this, "failed to save datas", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void playMovie(Context context, int currentMovieId, String final_video_url) {
        if (context == null) {
            Logger.d("NullContext ", "   YEs");

        }
        this.currentMovieId = currentMovieId;
        this.final_video_url = final_video_url;


        Logger.d("currentMovieId", currentMovieId + "   " + final_video_url);

        loading_dialog = new CustomDialogManager(
                context, CustomDialogManager.LOADING);
        loading_dialog.build();
        loading_dialog.setMessage("", "Streaming...");
        loading_dialog.show();
        loading_dialog.getInnerObject().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == keyEvent.KEYCODE_BACK) {
                    loading_dialog.dismiss();
                    finish();
                }
                return false;
            }
        });
        loading_dialog.setExtraButton(context.getResources().getString(R.string.btn_dismiss), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Logger.d(TAG, "streamin dialog shown");

        /****************************************************************************/


        Logger.d(TAG, "START PLAYING VIDEO");
        if (player == null) player = new MediaPlayer();
        try {
            player.setDataSource(context, Uri.parse(final_video_url));
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            try {
                player.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
                this.finish();
            }


        } catch (IllegalArgumentException e) {
            Logger.e(TAG, e.getMessage() + "");
            Logger.printStackTrace(e);

        } catch (SecurityException e) {
            Logger.printStackTrace(e);
        } catch (IllegalStateException e) {
            Logger.printStackTrace(e);
        } catch (IOException e) {
            Logger.printStackTrace(e);
        } catch (NullPointerException e) {
            Logger.printStackTrace(e);
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            player.setDisplay(holder);
        } catch (Exception e) {
            Logger.e("Exception At surfaceCreated Method", e.getMessage() + "");
            Logger.printStackTrace(e);
        }
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    // End SurfaceHolder.Callback
    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        loading_dialog.dismiss();
        // progressDialog.dismiss();
        // full screen control for video
        videoSurface.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent service = new Intent();
                service.setComponent(new ComponentName("com.mytv.MyTVHome",
                        "com.mytv.utils.FullScreenVideoService"));
                Logger.e(TAG + ": on prepared", "call full screen");
                service.putExtra("fullscreen", true);
                startService(service);

            }
        }, 1000);
        // end of full screen control for video

        controller.setMediaPlayer(this);
        controller
                .setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        controller.setPrevNextListeners(new View.OnClickListener() {
            // next
            @Override
            public void onClick(View arg0) {
                playNextorPrevMovie(NEXT_MOVIE);

            }

        }, new View.OnClickListener() {
            // prev
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                playNextorPrevMovie(PREV_MOVIE);

            }

        });

        try {
            final String last_duration;

            last_duration = read(thisMovie.getMovie_name());
            Logger.d(TAG, "check if can resume " + last_duration);

            // show dialog only if stored time is greater than 5 minutes
            if (Integer.parseInt(last_duration.trim()) > 3 * 60 * 1000
                    && Integer.parseInt(last_duration.trim()) < player.getDuration() - 3 * 60 * 1000) {
                Logger.d("PALO Alert dialog ko", Integer.parseInt(last_duration)
                        + "");
                // player.pause();
                loading_dialog = new CustomDialogManager(
                        MoviePlayCustomController.this,
                        getString(R.string.msg_resume_movie),
                        CustomDialogManager.MESSAGE);
                loading_dialog.build();
                loading_dialog.show();
                loading_dialog.dismissDialogOnBackPressed();
                loading_dialog.setPositiveButton("Resume",
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                player.seekTo(Integer.parseInt(last_duration));
                                player.start();
                                loading_dialog.dismiss();

                            }
                        });
                loading_dialog.setNegativeButton("Dismiss",
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                loading_dialog.dismiss();

                            }
                        });
                loading_dialog.setExtraButton("", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loading_dialog.dismiss();
                    }
                });

            }
        } catch (NumberFormatException e) {
            Logger.d("NumberFormatException", e.getMessage() + "");

        } catch (Exception e) {
            Logger.d("Exception at onPrepared of mediaplayer", e.getMessage() + "");
            Logger.printStackTrace(e);
        }

        mp.start();
        getApp().setVideoPlaying(true);
        bgSeekBar.setMax(player.getDuration());
        bgSeekBar.setProgress(mp.getCurrentPosition());
        myHandler.postDelayed(UpdateSeekbar, 100);
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        Runnable saveDuration=new Runnable() {
            @Override
            public void run() {
                if (player.isPlaying()) {
                    try {
                        if (player.getCurrentPosition() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MovieInFile movieinfile = new MovieInFile(thisMovie,
                                            player.getCurrentPosition());
                                    write(currentMovieId + "", movieinfile);
                                }
                            });


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        service.scheduleAtFixedRate(saveDuration, 0, 10, TimeUnit.SECONDS);
      /*  timer = new Timer();
        timer.schedule(timerTask, 2*60*1000,30*60*1000);*/



    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public String read(String fname) {
        BufferedReader br = null;
        String response = null;
        Logger.d("File read from ", getExternalFilesDir(null).getPath()
                + currentMovieId + "");

        File file = new File(getExternalFilesDir(null), currentMovieId + "");

        BufferedReader inputReader2;
        try {
            inputReader2 = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));

            String inputString2;
            StringBuffer stringBuffer2 = new StringBuffer();

            try {
                while ((inputString2 = inputReader2.readLine()) != null) {
                    stringBuffer2.append(inputString2 + "\n");
                    response = stringBuffer2.toString().trim() + "";
                }
                String movieData[] = stringBuffer2.toString().split("\n");
                // String movieDuration = movieData[0];
                // String movieId = movieData[1];

                System.out.println("movie id from file in movie play activity"
                        + movieData[0]);
                response = movieData[0];
                inputReader2.close();
            } catch (IOException e) {
                response = 0 + "";
                Logger.printStackTrace(e);
            }

        } catch (FileNotFoundException e) {
            response = 0 + "";
            Logger.printStackTrace(e);
        }

        Logger.d("WHAT IS READ?", response);
        return response;

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        loading_dialog.dismiss();
        if (what == -38) {
            Logger.d("CheckingAMIHere", "Yes");
        } else {

            Logger.d("onError of MOVIEPLAYCUSTOMCONTROLLER", "OnErrorBlock");
            PropertyGetSet.setProperty(PropertyGetSet.VIDEO_RUNNING_USAGE, "0");
            PropertyGetSet.setProperty(PropertyGetSet.SCREEN_SAVER, "1");
            Toast.makeText(MoviePlayCustomController.this, "ERROR PLAYING THE MEDIA",
                    Toast.LENGTH_LONG).show();
            myHandler.removeCallbacks(null);
            bgSeekBar.setProgress(0);
            finish();

        }
        getApp().setVideoPlaying(false);
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        try {
            File file = new File(getExternalFilesDir(null), currentMovieId + "");
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            Logger.e("Exception at onCompletion method", e.getMessage() + "");
        }
        getApp().setVideoPlaying(false);
        Toast.makeText(MoviePlayCustomController.this, "Playback complete",
                Toast.LENGTH_LONG).show();
        PropertyGetSet.setProperty(PropertyGetSet.VIDEO_RUNNING_USAGE, "0");
        PropertyGetSet.setProperty(PropertyGetSet.SCREEN_SAVER, "1");
        finish();

    }

    @Override
    public void start() {
        try {
            player.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Unknown Error!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public int getDuration() {
        try {
            return player.getDuration();
        } catch (Exception e) {
            Logger.printStackTrace(e);
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        try {

            Logger.d("the current position is ", player.getCurrentPosition() + "");
            return player.getCurrentPosition();

        } catch (Exception e) {
            Logger.printStackTrace(e);
            return 0;
        }

    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public boolean isPlaying() {

        try {
            return player.isPlaying();
        } catch (Exception pe) {
            Logger.printStackTrace(pe);
        }
        return false;

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isLIVE() {
        return false;
    }

    @Override
    public String videoTitle() {

        return thisMovie.getMovie_name();
    }

    @Override
    public String videoLogo() {
        return thisMovie.getMovie_logo();
    }

    @Override
    public void getResults(String s) {
        ImdbPojo movieDetails = new ImdbPojo();
        try {

            if (!s.equals("")) {
                JSONObject imdbObj = new JSONObject(s);
                movieDetails.setMovie_name(imdbObj.getString("original_title"));
                movieDetails.setOverview(imdbObj.getString("overview"));
                movieDetails.setPopularity(imdbObj.getDouble("popularity"));
                movieDetails.setRelease_date(imdbObj.getString("release_date"));
                getMovieCredentials(imdbObj.getString("imdb_id"), movieDetails);

            } else {
                movieDetails.setMovie_name(movieName);
                movieDetails.setOverview(overview);
                movieDetails.setPopularity(0);
                getMovieCredentials("", movieDetails);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            movieDetails.setMovie_name(movieName);
            movieDetails.setOverview(overview);
            movieDetails.setPopularity(0);
            movieDetails.setRelease_date("N/A");
            getMovieCredentials("", movieDetails);
        }
    }

    private void getMovieCredentials(String imdb_id, ImdbPojo movieDetails) {
        if (imdb_id.equals("")) {
            movieDetails.setDirector("N/A");
            movieDetails.setProducers("N/A");
            movieDetails.setWriters("N/A");
            this.imdbGlobal = movieDetails;
        } else {
            new ImdbCredits(MoviePlayCustomController.this, MoviePlayCustomController.this, movieDetails).execute(getResources().getString(R.string.imdb_details) + imdb_id + "/credits?api_key=" + API_KEY);
        }
    }

    @Override
    public void getImdbData(ImdbPojo imdbData, String s) {
        try {
            JSONObject imdbObj = new JSONObject(s);
            JSONArray castArray = imdbObj.getJSONArray("cast");
            String stars = "";
            for (int i = 0; i < 4; i++) {
                JSONObject castObj = castArray.getJSONObject(i);
                if (stars.equals("")) {
                    stars = castObj.getString("name");
                } else {
                    stars = stars + "," + castObj.getString("name");
                }
            }
            imdbData.setCasts(stars);

            JSONArray crewArray = imdbObj.getJSONArray("crew");
            String director = "";
            String producers = "";
            String writers = "";
            for (int i = 0; i < crewArray.length(); i++) {
                JSONObject creditObj = crewArray.getJSONObject(i);
                String job = creditObj.getString("job");
                switch (job) {
                    case "Director":
                        if (director.equals(""))
                            director = creditObj.getString("name");
                        else
                            director = director + "," + creditObj.getString("name");
                        break;
                    case "Producer":
                        if (producers.equals(""))
                            producers = creditObj.getString("name");
                        else
                            producers = producers + "," + creditObj.getString("name");
                        break;
                    case "Story":
                        if (writers.equals(""))
                            writers = creditObj.getString("name");
                        else
                            writers = writers + "," + creditObj.getString("name");
                        break;
                    case "Screenplay":
                        if (writers.equals(""))
                            writers = creditObj.getString("name");
                        else
                            writers = writers + "," + creditObj.getString("name");
                        break;
                    case "Characters":
                        if (writers.equals(""))
                            writers = creditObj.getString("name");
                        else
                            writers = writers + "," + creditObj.getString("name");
                        break;
                }
                imdbData.setDirector(director);
                imdbData.setProducers(producers);
                imdbData.setWriters(writers);

                this.imdbGlobal = imdbData;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Runnable UpdateSeekbar = new Runnable() {
        public void run() {
            if (player != null) {
                int startTime = player.getCurrentPosition();
                bgSeekBar.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);
            }
        }
    };

    private class MovieLinkLoader extends AsyncTask<String, Void, String> {

        private final String TAG = MovieLinkLoader.class.getSimpleName();
        String tvUrl, serverType, decryptedUrl, data1;
        int currentMovieId;
        String media_url;
        String success;
        private CustomDialogManager loading_dialog;
        private Context context;
        private boolean ServerError = false;
        private boolean flag_to_close_activity;
        private String authToken;

        /**
         * gets the link of movie to play and checks when to let user go back to previous screen
         *
         * @param context
         * @param movieId
         * @param flag_to_close_activity
         * @param authToken
         */
        public MovieLinkLoader(Context context, int movieId,
                               boolean flag_to_close_activity, String authToken) {
            this.context = context;
            this.currentMovieId = movieId;
            this.flag_to_close_activity = flag_to_close_activity;
            this.authToken = authToken;


            media_url = LinkConfig.getString(context, LinkConfig.MOVIE_PLAY_LINK)
                    + "?movieId=" + currentMovieId;
            Movie result = realm.where(Movie.class).equalTo("movie_id", currentMovieId).findFirst();
            Log.e("Movie", result + "");
            thisMovie = result;

        }

        @Override
        protected String doInBackground(String... params) {
            DownloadUtil getUtc = new DownloadUtil(LinkConfig.getString(MoviePlayCustomController.this, LinkConfig.GET_UTC), context);
            String result = getUtc.downloadStringContent();
            String utc = "";
            try {
                JSONObject resultObj = new JSONObject(result);
                utc = resultObj.getString("utc");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Logger.printStackTrace(e);
            }
            if (!utc.equals(DownloadUtil.NotOnline)
                    && !utc.equals(DownloadUtil.ServerUnrechable)) {
                DownloadUtil dUtil = new DownloadUtil(params[0] + "&"
                        + LinkConfig.getHashCode(utc), context, authToken);

                Logger.d(TAG, params[0] + "&" + LinkConfig.getHashCode(utc));

                return dUtil.downloadStringContent();
            } else
                return utc;
        }


        @Override
        protected void onPreExecute() {
            loading_dialog = new CustomDialogManager(context,
                    CustomDialogManager.LOADING);
            loading_dialog.build();
            loading_dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (loading_dialog.isShowing())
                loading_dialog.dismiss();
            if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
                final CustomDialogManager noInternet = new CustomDialogManager(context, CustomDialogManager.ALERT);
                noInternet.build();
                noInternet.setTitle(getString(R.string.no_internet_title)  );
                noInternet.setMessage("", getString(R.string.no_internet_body));
                noInternet.getInnerObject().setCancelable(false);
                noInternet.finishActivityonDismissPressed(MoviePlayCustomController.this);
                noInternet.finishActivityOnBackPressed(MoviePlayCustomController.this);
                noInternet.setPositiveButton(context.getString(R.string.btn_dismiss), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noInternet.dismiss();
                        ((Activity) context).finish();
                    }
                });

                noInternet.setNegativeButton("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openSetting();
                        noInternet.dismiss();

                    }
                });
                noInternet.show();

            } else {

                try {
                    JSONObject root = new JSONObject(result);
                    JSONObject movieObj = root.getJSONObject("movies");
                    tvUrl = movieObj.getString("link");
                    serverType = movieObj.getString("servertype");
                  /*  if (serverType.equals("1")) {
                        // Decryption DOne here
                        Mcrypt mCrypt = new Mcrypt();

                        try {
                            decryptedUrl = new String(mCrypt.decrypt(tvUrl));
                            data1 = decryptedUrl.trim();
                        } catch (Exception e) {
                            Logger.printStackTrace(e);
                        }

                    }*/
                    if (serverType.equals("6")) {
                        flag_to_close_activity = true;

                        String YT_KEY = LinkConfig.YOUTUBE_API_KEY;
                        Uri uri = Uri.parse(data1);
                        String videoId = uri.getQueryParameter("v");
                        Logger.d("YouTube videoId", videoId);
                        if (PackageUtils.isPackageInstalled(context, MarketAppDetailParser.Youtube)) {
                            if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(context).equals(YouTubeInitializationResult.SUCCESS)) {
                                Intent videoIntent = YouTubeStandalonePlayer
                                        .createVideoIntent((Activity) context, YT_KEY,
                                                videoId, 0, true, false);
                                if (flag_to_close_activity) {
                                    videoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                }
                                ((Activity) context).startActivityForResult(videoIntent, 1001
                                            /*STANDALONE_PLAYER_REQUEST_CODE*/);
                            } else {
                                Toast.makeText(context, "Player is not supported in this device", Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } else {
                            checkIfLatestYoutubeInstalled();
                        }


                    } else {

                        data1 = tvUrl;
                        playMovie(context, currentMovieId, data1);
                    }

                } catch (JSONException je) {
                    Logger.printStackTrace(je);
                    String errorCode, errorMessage;
                    Logger.d(TAG + "error json", result);
                    try {
                        JSONObject Jobj = new JSONObject(result);
                        errorCode = Jobj.getString("error_code");
                        errorMessage = Jobj.getString("error_message");

                        final CustomDialogManager movieLoadError = new CustomDialogManager(
                                context, CustomDialogManager.ALERT);
                        movieLoadError.build();
                        movieLoadError.setMessage(errorCode, errorMessage);


                        movieLoadError.getInnerObject().setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                ((Activity) context).finish();
                            }
                        });

                        if (context
                                .getClass()
                                .getName()
                                .equals(MoviePlayCustomController.class.getName())) {
                            movieLoadError.finishActivityOnBackPressed(context);
                            movieLoadError.finishActivityonDismissPressed(context);

                        }
                        Logger.d(TAG + MovieLinkLoader.class.getSimpleName() + "error json", "parsing success");
                        movieLoadError.show();
                    } catch (JSONException je2) {
                        Logger.printStackTrace(je2);
                        try {
                            JSONObject root = new JSONObject(result);
                            if (root.getString("error_code").equals("405")) {
                                LinkConfig.deleteAuthCodeFile();
                                final CustomDialogManager invalidTokenDialog = new CustomDialogManager(context, CustomDialogManager.ALERT);
                                invalidTokenDialog.build();
                                invalidTokenDialog.setTitle("Invalid Token");
                                invalidTokenDialog.setMessage("", root.getString("message") + ",please re-login");
                                invalidTokenDialog.getInnerObject().setCancelable(false);
                                invalidTokenDialog.exitApponBackPress();
                                invalidTokenDialog.setPositiveButton("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent entryPointIntent = new Intent(context, EntryPoint.class);
                                        entryPointIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        invalidTokenDialog.dismiss();
                                        context.startActivity(entryPointIntent);


                                    }
                                });
                                invalidTokenDialog.show();


                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(context);
                        }

                    }




                }

            }

        }

        private boolean checkIfLatestYoutubeInstalled() {
            String installedYoutubeVersion;
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(MarketAppDetailParser.Youtube, PackageManager.GET_META_DATA);
                installedYoutubeVersion = pInfo.versionName.replace(".", "");
                int requiredVersion = Integer.parseInt(MarketAppDetailParser.APKs.get(MarketAppDetailParser.Youtube).getVersion().replace(".", ""));

                Logger.d("Required--Installed", requiredVersion + "---/t" + installedYoutubeVersion);
                if (requiredVersion > Integer.parseInt(installedYoutubeVersion)) {
                    final CustomDialogManager youtubeUpdateDailog = new CustomDialogManager(context, CustomDialogManager.WARNING);
                    youtubeUpdateDailog.build();
                    youtubeUpdateDailog.setMessage("", "Older version of Youtube Found! \n Please update Youtube App for Better Experience!!!");
                    youtubeUpdateDailog.setPositiveButton(context.getString(R.string.btn_update), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            youtubeUpdateDailog.dismiss();
                            new ApkDownloader(context, MarketAppDetailParser.APKs.get(MarketAppDetailParser.Youtube).getName()).execute(MarketAppDetailParser.APKs.get(MarketAppDetailParser.Youtube).getAppDownloadLink());
                        }
                    });
                    youtubeUpdateDailog.setNegativeButton("LATER", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            youtubeUpdateDailog.dismiss();
                        }
                    });
                    youtubeUpdateDailog.show();
                    youtubeUpdateDailog.showMacAndVersion();
                    youtubeUpdateDailog.setTitle("Update Youtube!!!");
                    youtubeUpdateDailog.getInnerObject().setCancelable(true);

                    return true;
                } else {
                    return true;
                }

            } catch (PackageManager.NameNotFoundException e) {
                Logger.printStackTrace(e);
                final CustomDialogManager youtubeNotInstalled = new CustomDialogManager(context, CustomDialogManager.ALERT);
                youtubeNotInstalled.build();
                youtubeNotInstalled.setMessage("", "Please install Youtube app from Play Store to view play YoutubePlus contents.");
                youtubeNotInstalled.setTitle("Youtube App Not Installed");
                youtubeNotInstalled.showMacAndVersion();
                youtubeNotInstalled.show();
                youtubeNotInstalled.setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        youtubeNotInstalled.dismiss();
                        new ApkDownloader(context, MarketAppDetailParser.APKs.get(MarketAppDetailParser.Youtube).getName()).execute(MarketAppDetailParser.APKs.get(MarketAppDetailParser.Youtube).getAppDownloadLink());
                    }
                });
                youtubeNotInstalled.getInnerObject().setCancelable(true);
                return false;
            }

        }
    }


    public void openSetting() {
        try {
            try {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.rk_itvui.settings", "com.rk_itvui.settings.Settings"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                try {
                    Intent LaunchIntent = (getPackageManager().getLaunchIntentForPackage("com.giec.settings"));
                    startActivity(LaunchIntent);
                    finish();
                } catch (Exception c) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }


        } catch (Exception a) {
            startActivity(
                    new Intent(Settings.ACTION_SETTINGS));
            finish();
        }
    }

}