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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.app.ErrorSupportFragment;
import android.support.v17.leanback.app.VerticalGridSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import stb.androidtv.moviesleanback.cards.presenters.CardPresenterSelector;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieCatWrapper;
import stb.androidtv.moviesleanback.enitities.MovieCategory;
import stb.androidtv.moviesleanback.enitities.MovieDataResponse;
import stb.androidtv.moviesleanback.enitities.MovieDataWrapper;
import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.enitities.MoviesParentItem;
import stb.androidtv.moviesleanback.enitities.TopmoviesItem;
import stb.androidtv.moviesleanback.leanDialog.DialogExampleFragment;
import stb.androidtv.moviesleanback.loadingSpinner.LoadingFragment;
import stb.androidtv.moviesleanback.models.Card;
import stb.androidtv.moviesleanback.movieCategory.MovieViewModel;
import stb.androidtv.moviesleanback.movieDetailsCustom.MovieCustomBrowseActivity;
import stb.androidtv.moviesleanback.movieList.MovieListActivity;
import stb.androidtv.moviesleanback.search.SearchActivity;
import stb.androidtv.moviesleanback.utils.AppConfig;
import stb.androidtv.moviesleanback.utils.GetMac;
import stb.androidtv.moviesleanback.utils.GlideApp;
import stb.androidtv.moviesleanback.utils.MovieDataRepo;
import stb.androidtv.moviesleanback.utils.MovieEnum;

import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_MESSAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_TITLE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_HASH;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_USER;
import static stb.androidtv.moviesleanback.utils.LinkConfig.LOADING_FRAGMENT;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;

public class MainFragment extends VerticalGridSupportFragment implements OnItemViewClickedListener,OnItemViewSelectedListener{
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int NUM_ROWS = 4;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;


    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;
    private MovieViewModel mainViewModel;
    private Realm realm;
    private String macAddress;

    private ArrayObjectAdapter mAdapter;
    private Login login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        mainViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        macAddress = AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(getContext());
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(NUM_ROWS);
        setGridPresenter(gridPresenter);
    }

    private void getMovieCategories() {
        login = realm.where(Login.class).findFirst();
        if(login!=null) {
            LiveData<MovieCatWrapper> movieCategoryLiveData = mainViewModel.getMovieCat(login, macAddress);
            movieCategoryLiveData.observe(this, new Observer<MovieCatWrapper>() {
                @Override
                public void onChanged(@Nullable MovieCatWrapper movieCategoryWrapper) {
                    if (movieCategoryWrapper != null) {
                        if (movieCategoryWrapper.getMovieCatResponse() != null) {

                            loadRows(movieCategoryWrapper.getMovieCatResponse());

                            setupEventListeners();
                        } else {
                            ErrorSupportFragment fragment = new DialogExampleFragment();
                            Bundle bundle = new Bundle();
                            switch (movieCategoryWrapper.getException().getStatus()) {
                                case INVALID_HASH:
                                    bundle.putString(ERROR_TITLE, getString(R.string.invalid_hash));
                                    bundle.putString(ERROR_MESSAGE, movieCategoryWrapper.getException().getErrorMessage());
                                    break;
                                case INVALID_USER:
                                    bundle.putString(ERROR_TITLE, getString(R.string.invalid_user));
                                    bundle.putString(ERROR_MESSAGE, movieCategoryWrapper.getException().getErrorMessage());
                                    break;
                                case NO_CONNECTION:
                                    bundle.putString(ERROR_TITLE, getString(R.string.no_internet_title));
                                    bundle.putString(ERROR_MESSAGE, movieCategoryWrapper.getException().getErrorMessage());
                                    break;
                                default:
                                    bundle.putString(ERROR_TITLE, getString(R.string.err_unexpected));
                                    bundle.putString(ERROR_MESSAGE, getString(R.string.unexpctd_err_body));
                                    break;
                            }
                            fragment.setArguments(bundle);
                            Objects.requireNonNull(getFragmentManager()).beginTransaction().add(R.id.main_browse_fragment, fragment).commit();
                        }
                        movieCategoryLiveData.removeObserver(this);
                    }


                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);
        prepareBackgroundManager();
        setupUIElements();
        getMovieCategories();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void loadRows(MovieCategory movieCatResponse) {
        PresenterSelector cardPresenterSelector = new CardPresenterSelector(getActivity());
        mAdapter = new ArrayObjectAdapter(cardPresenterSelector);
        setAdapter(mAdapter);
        prepareEntranceTransition();
        new Handler().postDelayed(() -> {
            createRows(movieCatResponse);
            startEntranceTransition();
        }, 1000);


    }

    private void createRows(MovieCategory movieCatResponse) {
        ArrayList<Card> cardList = new ArrayList<>();
        for (MoviesParentItem moviesParent : movieCatResponse.getMoviesParent()) {
            Card card = new Card();
            card.setId(moviesParent.getParentId());
            card.setDescription(moviesParent.getParentDescription());
            card.setTitle(moviesParent.getParentName());
            card.setImageUrl(moviesParent.getParentLogo());
            card.setType(Card.Type.GRID_SQUARE);
            cardList.add(card);
        }
        mAdapter.addAll(0, cardList);

    }

        private void prepareBackgroundManager() {
            mBackgroundManager = BackgroundManager.getInstance(Objects.requireNonNull(getActivity()));
            mBackgroundManager.attach(getActivity().getWindow());

            mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
            mMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.movie_category));
        // Badge, when set, takes precedent
        // set search icon color
//        setSearchAffordanceColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.search_opaque));
    }

    private void setupEventListeners() {
       /* setOnSearchClickedListener(view -> {
            Intent intent=new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });
*/
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);

    }

    private void getMoviesofClickedCategory(int id) {
        login = realm.where(Login.class).findFirst();
        Fragment loadingFragment = new LoadingFragment();
        Objects.requireNonNull(getFragmentManager()).beginTransaction().add(R.id.main_browse_fragment, loadingFragment, LOADING_FRAGMENT).commit();
        LiveData<MovieDataWrapper> movieCategoryLiveData = mainViewModel.getMovieData(id, login, macAddress);
        movieCategoryLiveData.observe(this, new Observer<MovieDataWrapper>() {
            @Override
            public void onChanged(@Nullable MovieDataWrapper movieDataWrapper) {
                if (movieDataWrapper != null) {
                    getFragmentManager().beginTransaction().remove(loadingFragment).commit();
                    if (movieDataWrapper.getMovieDataResponse() != null) {

                        MovieDataResponse movieDataResponse = movieDataWrapper.getMovieDataResponse();
                        ArrayList<MovieItem> movieSubcat = (ArrayList<MovieItem>) movieDataResponse.getMovie();
                        MovieDataRepo.allMovieData=movieSubcat;
                        ArrayList<TopmoviesItem> topMovies = (ArrayList<TopmoviesItem>) movieDataResponse.getTopmovies();
                        Intent intent = new Intent(getContext(), MovieCustomBrowseActivity.class);
                        MovieEnum.setMovieSubList(movieSubcat);
//                        MovieEnum.setTopMovieList(topMovies);
                        startActivity(intent);

                    } else {
                        ErrorSupportFragment fragment = new DialogExampleFragment();
                        Bundle bundle = new Bundle();
                        switch (movieDataWrapper.getException().getStatus()) {
                            case INVALID_HASH:
                                bundle.putString(ERROR_TITLE, getString(R.string.invalid_hash));
                                bundle.putString(ERROR_MESSAGE, movieDataWrapper.getException().getErrorMessage());
                                break;
                            case INVALID_USER:
                                bundle.putString(ERROR_TITLE, getString(R.string.invalid_user));
                                bundle.putString(ERROR_MESSAGE, movieDataWrapper.getException().getErrorMessage());
                                break;
                            case NO_CONNECTION:
                                bundle.putString(ERROR_TITLE, getString(R.string.no_internet_title));
                                bundle.putString(ERROR_MESSAGE, movieDataWrapper.getException().getErrorMessage());
                                break;
                            default:
                                bundle.putString(ERROR_TITLE, getString(R.string.err_unexpected));
                                bundle.putString(ERROR_MESSAGE, getString(R.string.unexpctd_err_body));
                                break;
                        }
                        fragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().add(R.id.main_browse_fragment, fragment).commit();
                    }
                    movieCategoryLiveData.removeObserver(this);
                }

            }
        });


    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        GlideApp.with(Objects.requireNonNull(getActivity()))
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<Drawable>(width, height) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder viewHolder, Object o, RowPresenter.ViewHolder viewHolder1, Row row) {
        Card card = (Card) o;
        getMoviesofClickedCategory(card.getId());
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder viewHolder, Object o, RowPresenter.ViewHolder viewHolder1, Row row) {
        if (o instanceof Card) {
            mBackgroundUri = ((Card) o).getImageUrl();
            startBackgroundTimer();
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(() -> updateBackground(mBackgroundUri));
        }
    }

}
