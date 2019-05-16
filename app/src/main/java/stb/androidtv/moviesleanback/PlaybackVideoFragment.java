/*
 * Copyright (C) 2017 The Android Open Source Project
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

package stb.androidtv.moviesleanback;

import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.VideoSupportFragment;
import android.support.v17.leanback.app.VideoSupportFragmentGlueHost;
import android.support.v17.leanback.media.MediaPlayerAdapter;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;
import android.support.v17.leanback.widget.PlaybackControlsRow;

/**
 * Handles video playback with media controls.
 */
public class PlaybackVideoFragment extends VideoSupportFragment {

    private PlaybackTransportControlGlue<MediaPlayerAdapter> mTransportControlGlue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Movie movie =
                (Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);

        VideoSupportFragmentGlueHost glueHost =
                new VideoSupportFragmentGlueHost(PlaybackVideoFragment.this);

        MediaPlayerAdapter playerAdapter = new MediaPlayerAdapter(getActivity());
//        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE);

        mTransportControlGlue = new PlaybackTransportControlGlue<>(getActivity(), playerAdapter);
        mTransportControlGlue.setHost(glueHost);
        mTransportControlGlue.setTitle(movie.getTitle());
        mTransportControlGlue.setSubtitle(movie.getDescription());
        mTransportControlGlue.play();
        playerAdapter.setDataSource(Uri.parse("https://ottstream.prabhutv.com.np/live-abr/SonyMaxHD-abr.stream/playlist.m3u8"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mTransportControlGlue != null) {
            mTransportControlGlue.pause();
        }
    }
}