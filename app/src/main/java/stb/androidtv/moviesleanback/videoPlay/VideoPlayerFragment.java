/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package stb.androidtv.moviesleanback.videoPlay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.C;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieLinkWrapper;
import stb.androidtv.moviesleanback.enitities.MoviesItem;
import stb.androidtv.moviesleanback.models.Card;

import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_DETAILS;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_NEXT_PREV;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_RESPONSE;


public class VideoPlayerFragment extends VideoSupportFragment {
    private String movieurl;
    private Card movieCard;
    private Realm mRealm;
    private ArrayList<MoviesItem> currentMovieList;
    public static final String TAG = "VideoConsumption";
    private VideoMediaPlayerGlue<ExoPlayerAdapter> mMediaPlayerGlue;
    final VideoSupportFragmentGlueHost mHost = new VideoSupportFragmentGlueHost(this);
    private Login login;
    ExoPlayerAdapter playerAdapter;


    static void playWhenReady(PlaybackGlue glue) {
        if (glue.isPrepared()) {
            glue.play();

        } else {
            glue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
                @Override
                public void onPreparedStateChanged(PlaybackGlue glue) {
                    if (glue.isPrepared()) {
                        glue.removePlayerCallback(this);
                        glue.play();
                    }
                }
            });
        }
    }

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener
            = state -> {
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movieurl = ((VideoPlayActivity) getActivity()).getMovieLink();
        movieCard = ((VideoPlayActivity) getActivity()).getMovieCard();
        currentMovieList = ((VideoPlayActivity) getActivity()).getCurrentMovieList();
        playerAdapter = new ExoPlayerAdapter(getActivity());
        playerAdapter.setAudioStreamType(C.STREAM_TYPE_USE_DEFAULT);
        playerAdapter.mPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mMediaPlayerGlue = new VideoMediaPlayerGlue<>(getActivity(), playerAdapter);
        mMediaPlayerGlue.setHost(mHost);
        AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        if (audioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.w(TAG, "cannot obtain audio focus!");
        }
        mMediaPlayerGlue.setCurrentMovieList(currentMovieList, movieCard.getId(), login);
        mMediaPlayerGlue.setMode(PlaybackControlsRow.RepeatAction.INDEX_NONE);
        MediaMetaData intentMetaData = new MediaMetaData();
        intentMetaData.setMediaTitle(movieCard.getTitle());
        intentMetaData.setMediaAlbumArtUrl(movieCard.getImageUrl());
        mMediaPlayerGlue.setTitle(intentMetaData.getMediaTitle());
        mMediaPlayerGlue.setSubtitle(intentMetaData.getMediaArtistName());
//        movieurl="https://www.radiantmediaplayer.com/media/bbb-360p.mp4";
        mMediaPlayerGlue.getPlayerAdapter().setDataSource(
                Uri.parse(movieurl));
        PlaybackSeekDiskDataProvider.setDemoSeekProvider(mMediaPlayerGlue, movieurl);
        playWhenReady(mMediaPlayerGlue);
        setBackgroundType(BG_LIGHT);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        login = mRealm.where(Login.class).findFirst();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        IntentFilter filter = new IntentFilter(MOVIE_NEXT_PREV);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(movieSwitchedReciever, filter);
    }

    @Override
    public void onPause() {
        if (mMediaPlayerGlue != null) {
            mMediaPlayerGlue.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(movieSwitchedReciever);
        super.onDestroy();
    }

    private BroadcastReceiver movieSwitchedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equals(MOVIE_NEXT_PREV)) {
                playerAdapter.reset();
                MoviesItem changedMovie = intent.getParcelableExtra(MOVIE_DETAILS);
                MovieLinkWrapper changedWrapper = intent.getParcelableExtra(MOVIE_RESPONSE);
                String changedMovieUrl = changedWrapper.getMovieLinkResponse().getMovies().getLink();
//                changedMovieUrl="https://www.radiantmediaplayer.com/media/bbb-360p.mp4";
//                mMediaPlayerGlue.setCurrentMovieList(currentMovieList, changedMovie.getMovieId(), login);
                mMediaPlayerGlue.setMode(PlaybackControlsRow.RepeatAction.INDEX_NONE);
                MediaMetaData intentMetaData = new MediaMetaData();
                intentMetaData.setMediaTitle(changedMovie.getName());
                intentMetaData.setMediaAlbumArtUrl(changedMovie.getMovieLogo());
                mMediaPlayerGlue.setTitle(intentMetaData.getMediaTitle());
                mMediaPlayerGlue.setSubtitle(intentMetaData.getMediaArtistName());
                mMediaPlayerGlue.getPlayerAdapter().setDataSource(
                        Uri.parse(changedMovieUrl));
                PlaybackSeekDiskDataProvider.setDemoSeekProvider(mMediaPlayerGlue, movieurl);
                playWhenReady(mMediaPlayerGlue);
                mMediaPlayerGlue.notifyMoviePlayPause(mMediaPlayerGlue.isPrepared());
            }

        }
    };

}
