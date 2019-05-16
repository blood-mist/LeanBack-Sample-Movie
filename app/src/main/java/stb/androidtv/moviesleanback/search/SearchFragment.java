package stb.androidtv.moviesleanback.search;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.ErrorSupportFragment;
import android.support.v17.leanback.app.SearchSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.CursorObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import stb.androidtv.moviesleanback.CardPresenter;
import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.cards.presenters.CardPresenterSelector;
import stb.androidtv.moviesleanback.enitities.ErrorEntity;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.enitities.MovieLinkResponse;
import stb.androidtv.moviesleanback.enitities.MovieLinkWrapper;
import stb.androidtv.moviesleanback.enitities.MoviesItem;
import stb.androidtv.moviesleanback.leanDialog.DialogExampleFragment;
import stb.androidtv.moviesleanback.models.Card;
import stb.androidtv.moviesleanback.retroUtils.ApiManager;
import stb.androidtv.moviesleanback.utils.ApiInterface;
import stb.androidtv.moviesleanback.utils.AppConfig;
import stb.androidtv.moviesleanback.utils.GetMac;
import stb.androidtv.moviesleanback.utils.LinkConfig;
import stb.androidtv.moviesleanback.utils.MovieDataRepo;
import stb.androidtv.moviesleanback.utils.PermissionUtils;
import stb.androidtv.moviesleanback.utils.TimeStamp;
import stb.androidtv.moviesleanback.videoPlay.VideoPlayActivity;

import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_MESSAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.ERROR_TITLE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_HASH;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_USER;
import static stb.androidtv.moviesleanback.utils.LinkConfig.KEY_MOVIES;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_CLICKED_LINK;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ITEM;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;
import static stb.androidtv.moviesleanback.utils.LinkConfig.SIMILAR_MOVIE_LIST;

public class SearchFragment extends SearchSupportFragment implements SearchSupportFragment.SearchResultProvider, PermissionUtils.PermissionResultCallback, LoaderManager.LoaderCallbacks<ArrayList<MoviesItem>>, OnItemViewClickedListener {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int CHECK_SPEECH_ENABLED = 4;
    private static final int REQUEST_SPEECH = 0x00000010;
    private ArrayObjectAdapter mRowsAdapter;
    private static final boolean FINISH_ON_RECOGNIZER_CANCELED = true;
    private String mQuery;
    PermissionUtils permissionUtils;
    private Login login;

    private ArrayList<MovieItem> movieItems = MovieDataRepo.allMovieData;
    private Realm mRealm;
    private ArrayList<MoviesItem> searchList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        login = mRealm.where(Login.class).findFirst();
        permissionUtils = new PermissionUtils(getActivity(), this);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setSearchResultProvider(this);
        setOnItemViewClickedListener(this);
        permissionUtils.check_permission(new ArrayList<>(Collections.singletonList(Manifest.permission.RECORD_AUDIO)), getString(R.string.record_permissions), CHECK_SPEECH_ENABLED);
        Log.v(TAG, "no permission RECORD_AUDIO");

        // SpeechRecognitionCallback is not required and if not provided recognition will be handled
        // using internal speech recognizer, in which case you must have RECORD_AUDIO permission

    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        Log.d(TAG, "getResultsAdapter");
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        mQuery = newQuery;
        mRowsAdapter.clear();
        loadMovieRows();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mQuery = query;
//       loadRows();
        mRowsAdapter.clear();
        loadMovieRows();

        return true;
    }

    private void loadMovieRows() {
        if (!TextUtils.isEmpty(mQuery)) {
            if (getLoaderManager().getLoader(0) == null)
                getLoaderManager().initLoader(0, null, this);
            else
                getLoaderManager().restartLoader(0, null, this);
        }
    }


    @Override
    public void PermissionGranted(int request_code) {
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> pending_permissions) {

    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.d(TAG, "Does not have RECORD_AUDIO, using SpeechRecognitionCallback");
        setSpeechRecognitionCallback(() -> {
            try {
                startActivityForResult(getRecognizerIntent(), REQUEST_SPEECH);
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Cannot find activity for speech recognizer", e);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SPEECH:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setSearchQuery(data, true);
                        break;
                    default:
                        // If recognizer is canceled or failed, keep focus on the search orb
                        if (FINISH_ON_RECOGNIZER_CANCELED) {
                            if (!hasResults()) {
                                Objects.requireNonNull(getView()).findViewById(R.id.lb_search_bar_speech_orb).requestFocus();
                            }
                        }
                        break;
                }
                break;
        }
    }


    @Override
    public void NeverAskAgain(int request_code) {

    }

    public boolean hasResults() {
        return mRowsAdapter.size() > 0;
    }

    @Override
    public Loader<ArrayList<MoviesItem>> onCreateLoader(int id, Bundle args) {
        SearchMovieLoader searchMovieLoader = new SearchMovieLoader(getActivity(), movieItems, mQuery);
        searchMovieLoader.forceLoad();
        return searchMovieLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MoviesItem>> loader, ArrayList<MoviesItem> data) {
        new Handler().postDelayed(() -> {
            createRows(data);
        }, 1000);
    }


    private void createRows(ArrayList<MoviesItem> searchData) {
        this.searchList = searchData;
        PresenterSelector cardPresenterSelector = new CardPresenterSelector(getActivity());
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenterSelector);
        ArrayList<Card> cardList = new ArrayList<>();
        for (MoviesItem movieItem : searchData) {
            Card card = new Card();
            card.setId(movieItem.getId());
            card.setDescription(movieItem.getDescription());
            card.setTitle(movieItem.getName());
            card.setImageUrl(movieItem.getMovieLogo());
            card.setType(Card.Type.GRID_SQUARE);
            cardList.add(card);
        }
        listRowAdapter.addAll(0, cardList);
        HeaderItem header = new HeaderItem("Search Results");
        ListRow row = new ListRow(header, listRowAdapter);
        mRowsAdapter.clear();
        mRowsAdapter.add(row);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MoviesItem>> loader) {
        mRowsAdapter.clear();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Card) {
            Card video = (Card) item;
            loadMovie(video);
        } else {
            Toast.makeText(getActivity(), "Soon to be implemented", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMovie(Card video) {
        long utc = TimeStamp.getTimeStamp();
        MovieLinkWrapper movieLinkWrapper = new MovieLinkWrapper();
        String macAddress = AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(getContext());
        Retrofit retrofit = ApiManager.getAdapter();
        Gson gson = new Gson();
        final ApiInterface movieApiInterface = retrofit.create(ApiInterface.class);
        Observable<Response<ResponseBody>> observable = movieApiInterface.getMovieLink(login.getToken(), utc, String.valueOf(login.getId()), LinkConfig.getHashCode(String.valueOf(login.getId()), String.valueOf(utc),
                login.getSession()), macAddress, video.getId());
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
                            movieCatError.setErrorMessage(getActivity().getResources().getString(R.string.err_unexpected));
                        }
                        Intent moviePlayIntent = new Intent(getActivity(), VideoPlayActivity.class);
                        moviePlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        moviePlayIntent.putExtra(MOVIE_ITEM, video);
                        moviePlayIntent.putExtra(MOVIE_CLICKED_LINK, movieLinkWrapper.getMovieLinkResponse().getMovies().getLink());
                        moviePlayIntent.putParcelableArrayListExtra(SIMILAR_MOVIE_LIST, searchList);
                        getActivity().startActivity(moviePlayIntent);

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
                        showError(movieLinkWrapper);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void showError(MovieLinkWrapper movieLinkWrapper) {
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
}
