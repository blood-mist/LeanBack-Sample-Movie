package stb.androidtv.moviesleanback.customFragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.ErrorSupportFragment;
import android.support.v17.leanback.app.RowsSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseOnItemViewClickedListener;
import android.support.v17.leanback.widget.BaseOnItemViewSelectedListener;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.cards.presenters.CardPresenterSelector;
import stb.androidtv.moviesleanback.cards.presenters.GridListRow;
import stb.androidtv.moviesleanback.cards.presenters.GridRowItemPresenter;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.enitities.MovieLinkResponse;
import stb.androidtv.moviesleanback.enitities.MovieLinkWrapper;
import stb.androidtv.moviesleanback.enitities.Movies;
import stb.androidtv.moviesleanback.enitities.MoviesItem;
import stb.androidtv.moviesleanback.enitities.TopmoviesItem;
import stb.androidtv.moviesleanback.leanDialog.DialogExampleFragment;
import stb.androidtv.moviesleanback.loadingSpinner.LoadingFragment;
import stb.androidtv.moviesleanback.models.Card;
import stb.androidtv.moviesleanback.movieDetailsCustom.MovieCustomBrowseActivity;
import stb.androidtv.moviesleanback.utils.AppConfig;
import stb.androidtv.moviesleanback.utils.GetMac;
import stb.androidtv.moviesleanback.utils.GlideApp;
import stb.androidtv.moviesleanback.videoPlay.VideoPlayActivity;

import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_MESSAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_TITLE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_HASH;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_USER;
import static stb.androidtv.moviesleanback.utils.LinkConfig.LOADING_FRAGMENT;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_CLICKED_LINK;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ITEM;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;
import static stb.androidtv.moviesleanback.utils.LinkConfig.SIMILAR_MOVIE_LIST;

/**
 * Created by Sebastiano Gottardo on 08/11/14.
 */
public class CustomRowsFragment extends RowsSupportFragment implements BaseOnItemViewClickedListener, BaseOnItemViewSelectedListener {
    private ArrayObjectAdapter rowsAdapter;
    private static final int HEADERS_FRAGMENT_SCALE_SIZE = 300;
    List<MovieItem> subCatMovieList;
    List<TopmoviesItem> topmoviesItems;
    private MovieLinkViewModel movieLinkViewModel;
    private String mBackgroundUri;
    private Timer mBackgroundTimer;
    private Handler mHandler= new Handler();;
    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private DisplayMetrics mMetrics;
    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private String macAddress;

    private Realm realm;
    private List<MoviesItem> currentMovies;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm=Realm.getDefaultInstance();
        movieLinkViewModel= ViewModelProviders.of(this).get(MovieLinkViewModel.class);
        macAddress= AppConfig.isDevelopment()?AppConfig.getMacAddress(): GetMac.getMac(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        int marginOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEADERS_FRAGMENT_SCALE_SIZE, getResources().getDisplayMetrics());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) Objects.requireNonNull(v).getLayoutParams();
        params.rightMargin -= marginOffset;
        v.setLayoutParams(params);
//        v.setBackgroundColor(getRandomColor());
        return v;
    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(Objects.requireNonNull(getActivity()));
       if( !mBackgroundManager.isAttached()) {
           mBackgroundManager.attach(getActivity().getWindow());
       }

        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareBackgroundManager();
        loadRows();
        setCustomPadding();
        setUpEventListeners();

    }

    private void setUpEventListeners() {
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    private void setCustomPadding() {
        Objects.requireNonNull(getView()).setPadding(dipToPixels(-24), dipToPixels(128), dipToPixels(48), 0);
    }

    public int dipToPixels(int dipValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private void loadRows() {
        rowsAdapter = new ArrayObjectAdapter(new GridRowItemPresenter());
        PresenterSelector cardPresenterSelector = new CardPresenterSelector(getContext());
        subCatMovieList = ((MovieCustomBrowseActivity) getActivity()).getSubCatMovieList();
        topmoviesItems=((MovieCustomBrowseActivity) getActivity()).getTopmovieList();
        int currentRow = ((MovieCustomBrowseActivity) getActivity()).getCurrentRowPosition();
        currentMovies=subCatMovieList.get(currentRow).getMovies();
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenterSelector);
        for (MoviesItem movies :currentMovies ) {
            Card card = new Card();
            card.setId(movies.getId());
            card.setDescription(movies.getDescription());
            card.setTitle(movies.getName());
            card.setImageUrl(movies.getMovieLogo());
            card.setType(Card.Type.GRID_SQUARE);
            listRowAdapter.add(card);
        }

        HeaderItem header = new HeaderItem(0, subCatMovieList.get(currentRow).getName());
        GridListRow headersRow = new GridListRow(header, listRowAdapter);
        headersRow.setNumRows(2);
       /* for(TopmoviesItem topmoviesItem:topmoviesItems){
            Card card = new Card();
            card.setId(topmoviesItem.getMovieId());
            card.setDescription(topmoviesItem.getDescription());
            card.setTitle(topmoviesItem.getName());
            card.setImageUrl(topmoviesItem.getMovieLogo());
            card.setType(Card.Type.GRID_SQUARE);
            listRowAdapter.add(card);
        }*/
        rowsAdapter.add(headersRow);

        setAdapter(rowsAdapter);
    }

    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    public void refresh() {
            Objects.requireNonNull(getView()).setPadding(dipToPixels(-24), dipToPixels(128), dipToPixels(300), 0);
    }


    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Object row) {
        Card movieCard=(Card)item;
        getMovieLinkofClickItem(movieCard);
    }

    private void getMovieLinkofClickItem(Card movieCard) {
       Login login = realm.where(Login.class).findFirst();
        Fragment loadingFragment = new LoadingFragment();
        Objects.requireNonNull(getFragmentManager()).beginTransaction().add(R.id.loading_container, loadingFragment, LOADING_FRAGMENT).commit();
        LiveData<MovieLinkWrapper> movieCategoryLiveData = movieLinkViewModel.getMovieLink(movieCard.getId(), login, macAddress);
        movieCategoryLiveData.observe(this, new Observer<MovieLinkWrapper>() {
            @Override
            public void onChanged(@Nullable MovieLinkWrapper movieLinkWrapper) {
                if (movieLinkWrapper != null) {
                    getFragmentManager().beginTransaction().remove(loadingFragment).commit();
                    if (movieLinkWrapper.getMovieLinkResponse() != null) {
                        MovieLinkResponse movieLinkResponse = movieLinkWrapper.getMovieLinkResponse();
                        Movies movielinkData =  movieLinkResponse.getMovies();
                        Intent moviePlayIntent=new Intent(getActivity().getBaseContext(),VideoPlayActivity.class);
                        moviePlayIntent.putExtra(MOVIE_ITEM,movieCard);
                        moviePlayIntent.putExtra(MOVIE_CLICKED_LINK,movielinkData.getLink());
                        moviePlayIntent.putParcelableArrayListExtra(SIMILAR_MOVIE_LIST, (ArrayList<? extends Parcelable>) currentMovies);
                        getActivity().startActivity(moviePlayIntent);

                    } else {
                        ErrorSupportFragment fragment = new DialogExampleFragment();
                        Bundle bundle = new Bundle();
                        switch (movieLinkWrapper.getException().getStatus()) {
                            case INVALID_HASH:
                                bundle.putString(ERROR_TITLE, getString(R.string.invalid_hash));
                                bundle.putString(ERROR_MESSAGE, movieLinkWrapper.getException().getErrorMessage());
                                break;
                            case INVALID_USER:
                                bundle.putString(ERROR_TITLE, getString(R.string.invalid_user));
                                bundle.putString(ERROR_MESSAGE, movieLinkWrapper.getException().getErrorMessage());
                                break;
                            case NO_CONNECTION:
                                bundle.putString(ERROR_TITLE, getString(R.string.no_internet_title));
                                bundle.putString(ERROR_MESSAGE, movieLinkWrapper.getException().getErrorMessage());
                                break;
                            default:
                                bundle.putString(ERROR_TITLE, getString(R.string.err_unexpected));
                                bundle.putString(ERROR_MESSAGE, getString(R.string.unexpctd_err_body));
                                break;
                        }
                        fragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().add(R.id.loading_container, fragment).commit();
                    }
                    movieCategoryLiveData.removeObserver(this);
                }
            }
        });

    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Object row) {
        //update background here
        if (item!=null && item instanceof Card) {
            mBackgroundUri = ((Card) item).getImageUrl();
            startBackgroundTimer();
        }
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(() -> updateBackground(mBackgroundUri));
        }
    }

    private void updateBackground(String mBackgroundUri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        if(getActivity()!=null) {
            GlideApp.with(getContext())
                    .load(mBackgroundUri)
                    .centerCrop()
                    .error(mDefaultBackground)
                    .into(new SimpleTarget<Drawable>(width, height) {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            mBackgroundManager.setDrawable(resource);
                        }
                    });
        }
        mBackgroundTimer.cancel();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
    }
}