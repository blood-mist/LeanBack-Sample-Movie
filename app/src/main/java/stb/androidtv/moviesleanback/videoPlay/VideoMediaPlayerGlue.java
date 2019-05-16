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
 *
 */

package stb.androidtv.moviesleanback.videoPlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v17.leanback.media.PlaybackTransportControlGlue;
import android.support.v17.leanback.media.PlayerAdapter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.enitities.ErrorEntity;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieLinkResponse;
import stb.androidtv.moviesleanback.enitities.MovieLinkWrapper;
import stb.androidtv.moviesleanback.enitities.MoviesItem;
import stb.androidtv.moviesleanback.models.Card;
import stb.androidtv.moviesleanback.retroUtils.ApiManager;
import stb.androidtv.moviesleanback.utils.ApiInterface;
import stb.androidtv.moviesleanback.utils.AppConfig;
import stb.androidtv.moviesleanback.utils.GetMac;
import stb.androidtv.moviesleanback.utils.LinkConfig;
import stb.androidtv.moviesleanback.utils.TimeStamp;

import static android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction.INDEX_PAUSE;
import static android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction.INDEX_PLAY;
import static stb.androidtv.moviesleanback.utils.LinkConfig.KEY_MOVIES;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_CLICKED_LINK;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_DETAILS;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ITEM;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_NEXT_PREV;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_RESPONSE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;
import static stb.androidtv.moviesleanback.utils.LinkConfig.SIMILAR_MOVIE_LIST;

/**
 * PlayerGlue for video playback
 *
 * @param <T>
 */
public class VideoMediaPlayerGlue<T extends PlayerAdapter> extends PlaybackTransportControlGlue<T> {

    private PlaybackControlsRow.SkipNextAction mNext;
    private PlaybackControlsRow.SkipPreviousAction mPrevious;
    private PlaybackControlsRow.PlayPauseAction mPlayPause;
    private ArrayList<MoviesItem> currentMovieList;
    private int current_movieId;
    private Context context;
    private Login login;

    public VideoMediaPlayerGlue(Activity context, T impl) {
        super(context, impl);
        this.context = context;
        mNext = new PlaybackControlsRow.SkipNextAction(context);
        mPrevious = new PlaybackControlsRow.SkipPreviousAction(context);
        mPlayPause = new PlaybackControlsRow.PlayPauseAction(context);
//        setSeekEnabled(true);
    }

    @Override
    protected void onCreateSecondaryActions(ArrayObjectAdapter adapter) {
       /* adapter.add(mThumbsUpAction);
        adapter.add(mThumbsDownAction);
        if (android.os.Build.VERSION.SDK_INT > 23) {
            adapter.add(mPipAction);
        }*/
    }

    @Override
    protected void onCreatePrimaryActions(ArrayObjectAdapter adapter) {
        super.onCreatePrimaryActions(adapter);
        adapter.add(mPrevious);
        adapter.add(mNext);
        /*adapter.add(mRepeatAction);
        adapter.add(mClosedCaptioningAction);*/
    }

    @Override
    public void onActionClicked(Action action) {
        if (shouldDispatchAction(action) && currentMovieList!=null && currentMovieList.size() > 0) {
            dispatchAction(action);
            return;
        }
        super.onActionClicked(action);
    }

    private boolean shouldDispatchAction(Action action) {
        return action == mPrevious || action == mNext;
    }

    private void dispatchAction(Action action) {
        int currentPosition = getCurrentMoviePosition();
        if (action == mPrevious) {
            if (currentPosition >= 1) {
                currentPosition--;
                MoviesItem toLoadMovie=currentMovieList.get(currentPosition);
                loadMovie(toLoadMovie);
            } else
                Toast.makeText(context, "No Movie found to load", Toast.LENGTH_SHORT).show();
        } else if (action == mNext) {
            if (currentPosition < currentMovieList.size() - 1) {
                currentPosition++;
                MoviesItem toLoadMovie=currentMovieList.get(currentPosition);
                loadMovie(toLoadMovie);
            } else
                Toast.makeText(context, "No Movie found to load", Toast.LENGTH_SHORT).show();
        }
    }



    private void loadMovie(MoviesItem moviesItem) {
        long utc = TimeStamp.getTimeStamp();
        MovieLinkWrapper movieLinkWrapper = new MovieLinkWrapper();
        String macAddress = AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(context);
        Retrofit retrofit = ApiManager.getAdapter();
        Gson gson = new Gson();
        final ApiInterface movieApiInterface = retrofit.create(ApiInterface.class);
        Observable<Response<ResponseBody>> observable = movieApiInterface.getMovieLink(login.getToken(), utc, String.valueOf(login.getId()), LinkConfig.getHashCode(String.valueOf(login.getId()), String.valueOf(utc),
                login.getSession()), macAddress, moviesItem.getId());
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> movieLinkResponse) {
                        String json = null;

                        if (movieLinkResponse.code() == 200) {
                            try {
                                json = movieLinkResponse.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (jsonObject.has(KEY_MOVIES)) {
                                    MovieLinkResponse movieLinkInfo = gson.fromJson(json, MovieLinkResponse.class);
                                    movieLinkWrapper.setMovieLinkResponse(movieLinkInfo);
                                } else {
                                    ErrorEntity catChannelError = gson.fromJson(json, ErrorEntity.class);
                                    movieLinkWrapper.setException(catChannelError);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ErrorEntity movieCatError = new ErrorEntity();
                            movieCatError.setStatus(100);
                            movieCatError.setErrorMessage(context.getResources().getString(R.string.err_unexpected));
                        }
                        sendBroadcast(movieLinkWrapper, moviesItem);

                    }


                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ErrorEntity movieCatError = new ErrorEntity();
                        if (e instanceof HttpException || e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                            movieCatError.setStatus(NO_CONNECTION);
                            movieCatError.setErrorMessage(e.getLocalizedMessage());
                        } else {
                            movieCatError.setStatus(500);
                            movieCatError.setErrorMessage(e.getLocalizedMessage());
                        }
                        movieLinkWrapper.setException(movieCatError);
                        sendBroadcast(movieLinkWrapper, moviesItem);
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    private void sendBroadcast(MovieLinkWrapper movieLinkWrapper, MoviesItem moviesItem) {
      /*  Intent nextPrevIntent=new Intent(MOVIE_NEXT_PREV);
        nextPrevIntent.putExtra(MOVIE_RESPONSE,movieLinkWrapper);
        nextPrevIntent.putExtra(MOVIE_DETAILS,moviesItem);
        LocalBroadcastManager.getInstance(context).sendBroadcast(nextPrevIntent);*/
      if(movieLinkWrapper.getMovieLinkResponse()!=null) {
          Card movieCard = new Card();
          movieCard.setId(moviesItem.getId());
          movieCard.setDescription(moviesItem.getDescription());
          movieCard.setTitle(moviesItem.getName());
          movieCard.setImageUrl(moviesItem.getMovieLogo());
          movieCard.setType(Card.Type.GRID_SQUARE);
          if (context instanceof VideoPlayActivity) {

              Intent moviePlayIntent = new Intent(context, VideoPlayActivity.class);
              moviePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              moviePlayIntent.putExtra(MOVIE_ITEM, movieCard);
              moviePlayIntent.putExtra(MOVIE_CLICKED_LINK, movieLinkWrapper.getMovieLinkResponse().getMovies().getLink());
              moviePlayIntent.putParcelableArrayListExtra(SIMILAR_MOVIE_LIST, currentMovieList);
              context.startActivity(moviePlayIntent);
              ((Activity) context).finish();
          }
      }else{
          Toast.makeText(getContext(),"Error fetching Movie",Toast.LENGTH_SHORT).show();
      }
    }

    private int getCurrentMoviePosition() {
        for (MoviesItem movies : currentMovieList) {
            if (movies.getId() == current_movieId) {
                Log.d("current_movie_position", currentMovieList.indexOf(movies) + "");
                return currentMovieList.indexOf(movies);
            }
        }
        return 0;
    }

    private void notifyActionChanged(PlaybackControlsRow.MultiAction action) {
        int index = -1;
        if (getPrimaryActionsAdapter() != null) {
            index = getPrimaryActionsAdapter().indexOf(action);
        }
        if (index >= 0) {
            getPrimaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
        } else {
            if (getSecondaryActionsAdapter() != null) {
                index = getSecondaryActionsAdapter().indexOf(action);
                if (index >= 0) {
                    getSecondaryActionsAdapter().notifyArrayItemRangeChanged(index, 1);
                }
            }
        }
    }

    private ArrayObjectAdapter getPrimaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getPrimaryActionsAdapter();
    }

    private ArrayObjectAdapter getSecondaryActionsAdapter() {
        if (getControlsRow() == null) {
            return null;
        }
        return (ArrayObjectAdapter) getControlsRow().getSecondaryActionsAdapter();
    }

    Handler mHandler = new Handler();

    @Override
    protected void onPlayCompleted() {
        super.onPlayCompleted();
        mHandler.post(() -> dispatchAction(mNext));
    }


    public void notifyMoviePlayPause(boolean isPlaying) {
        notifyActionChanged(mPlayPause);
    }

    public void setMode(int mode) {
//        mRepeatAction.setIndex(mode);
        if (getPrimaryActionsAdapter() == null) {
            return;
        }
//        notifyActionChanged(mRepeatAction);
    }


    public void setCurrentMovieList(ArrayList<MoviesItem> currentMovieList, int current_movieId, Login login) {
        this.currentMovieList = currentMovieList;
        this.current_movieId = current_movieId;
        this.login = login;
    }
}