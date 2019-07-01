package stb.androidtv.moviesleanback.videoPlay;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.BuildCompat;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.enitities.MoviesItem;
import stb.androidtv.moviesleanback.models.Card;

import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_CLICKED_LINK;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ITEM;
import static stb.androidtv.moviesleanback.utils.LinkConfig.SIMILAR_MOVIE_LIST;

public class VideoPlayActivity extends FragmentActivity {
    public static final String TAG = "VideoExampleWithExoPlayerActivity";
    public static final String SHARED_ELEMENT_NAME ="Play Video";

    private String movieLink;
    private Card movieCard;
    private ArrayList<MoviesItem> currentMovieList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_example);
        movieLink = getIntent().getStringExtra(MOVIE_CLICKED_LINK);
        movieCard = getIntent().getParcelableExtra(MOVIE_ITEM);
        currentMovieList = getIntent().getParcelableArrayListExtra(SIMILAR_MOVIE_LIST);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.videoFragment, new VideoPlayerFragment(),
                VideoPlayerFragment.TAG);
        ft.commit();
    }

    public ArrayList<MoviesItem> getCurrentMovieList() {
        return currentMovieList;
    }

    public String getMovieLink() {
        return movieLink;
    }

    public Card getMovieCard() {
        return movieCard;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // This part is necessary to ensure that getIntent returns the latest intent when
        // VideoExampleActivity is started. By default, getIntent() returns the initial intent
        // that was set from another activity that started VideoExampleActivity. However, we need
        // to update this intent when for example, user clicks on another video when the currently
        // playing video is in PIP mode, and a new video needs to be started.
        setIntent(intent);
    }

    public static boolean supportsPictureInPicture(Context context) {
        return BuildCompat.isAtLeastN() &&
                context.getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_PICTURE_IN_PICTURE);
    }

}
