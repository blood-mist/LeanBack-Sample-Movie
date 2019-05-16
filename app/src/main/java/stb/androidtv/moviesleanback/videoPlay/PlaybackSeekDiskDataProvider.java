/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stb.androidtv.moviesleanback.videoPlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaDataSource;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v17.leanback.media.PlaybackGlue;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;
import android.support.v17.leanback.widget.PlaybackSeekDataProvider;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Sample PlaybackSeekDataProvider that reads bitmaps stored on disk.
 * e.g. new PlaybackSeekDiskDataProvider(duration, 1000, "/sdcard/frame_%04d.jpg")
 * Expects the seek positions are 1000ms interval, snapshots are stored at
 * /sdcard/frame_0001.jpg, ...
 */
public class PlaybackSeekDiskDataProvider extends PlaybackSeekAsyncDataProvider {

    //    final Paint mPaint;
//    private final String mPathPattern;
    private final String uri;

    private PlaybackSeekDiskDataProvider(long duration, long interval, String pathPattern, String movie_url) {
        Log.d("duration", duration + "");
//        mPathPattern = pathPattern;
        uri = movie_url;
        int size = (int) (duration / interval) + 1;
        long[] pos = new long[size];
        for (int i = 0; i < pos.length; i++) {
            pos[i] = i * interval;

        }
        setSeekPositions(pos);
    }


    /**
     * Helper function to set a demo seek provider on PlaybackTransportControlGlue based on
     * duration.
     */
    public static void setDemoSeekProvider(final PlaybackTransportControlGlue glue, String movieUrl) {
        if (glue.isPrepared()) {
            glue.setSeekProvider(new PlaybackSeekDiskDataProvider(
                    glue.getDuration(),
                    glue.getDuration() / 100,
                    "/sdcard/seek/frame_%04d.jpg", movieUrl));
        } else {
            glue.addPlayerCallback(new PlaybackGlue.PlayerCallback() {
                @Override
                public void onPreparedStateChanged(PlaybackGlue glue) {
                    if (glue.isPrepared()) {
                        glue.removePlayerCallback(this);
                        PlaybackTransportControlGlue transportControlGlue =
                                (PlaybackTransportControlGlue) glue;
                        transportControlGlue.setSeekEnabled(true);
                        transportControlGlue.setSeekProvider(new PlaybackSeekDiskDataProvider(
                                transportControlGlue.getDuration(),
                                transportControlGlue.getDuration() / 100,
                                "/sdcard/seek/frame_%04d.jpg", movieUrl));

                    }
                }
            });
        }
    }

    @Override
    protected Bitmap doInBackground(Object task, int index, long position) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // Thread might be interrupted by cancel() call.
        }
        if (isCancelled(task)) {
            return null;
        }
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(uri,new HashMap<>());
            Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(position*1000 , MediaMetadataRetriever.OPTION_CLOSEST);
            mediaMetadataRetriever.release();
            return bmFrame;
    }
}
