package stb.androidtv.moviesleanback.customFragments;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.enitities.ErrorEntity;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieCatWrapper;
import stb.androidtv.moviesleanback.enitities.MovieCategory;
import stb.androidtv.moviesleanback.enitities.MovieLinkResponse;
import stb.androidtv.moviesleanback.enitities.MovieLinkWrapper;
import stb.androidtv.moviesleanback.movieCategory.MovieRepository;
import stb.androidtv.moviesleanback.retroUtils.ApiManager;
import stb.androidtv.moviesleanback.utils.ApiInterface;
import stb.androidtv.moviesleanback.utils.LinkConfig;
import stb.androidtv.moviesleanback.utils.TimeStamp;

import static stb.androidtv.moviesleanback.splash.SplashRepository.KEY_CATEGORY;
import static stb.androidtv.moviesleanback.utils.LinkConfig.KEY_MOVIES;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;

public class MovielinkRepository {

    private static MovielinkRepository mInstance;
    private final Context context;
    private final ApiInterface movieLinkInterface;

    private MovielinkRepository(Application application) {
        this.context = application.getApplicationContext();
        Retrofit retrofitInstance = ApiManager.getAdapter();
        movieLinkInterface = retrofitInstance.create(ApiInterface.class);
    }
    public static MovielinkRepository getInstance(final Application application) {
        if (mInstance == null) {
            synchronized (MovieRepository.class) {
                if (mInstance == null) {
                    mInstance = new MovielinkRepository(application);
                }
            }
        }
        return mInstance;
    }

    public LiveData<MovieLinkWrapper> getMovieLink(int id, Login login, String macAddress) {
        MediatorLiveData<MovieLinkWrapper> responseMediatorLiveData = new MediatorLiveData<>();
        responseMediatorLiveData.setValue(null);
        long utc= TimeStamp.getTimeStamp();
        Gson gson=new Gson();
        MovieLinkWrapper wrapper = new MovieLinkWrapper();
        io.reactivex.Observable<Response<ResponseBody>> call = movieLinkInterface.getMovieLink(login.getToken(), utc, String.valueOf(login.getId()),  LinkConfig.getHashCode(String.valueOf(login.getId()), String.valueOf(utc),
                login.getSession()), macAddress,id);
        call.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Response<ResponseBody>>() {
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
                                    wrapper.setMovieLinkResponse(movieLinkInfo);
                                } else {
                                    ErrorEntity catChannelError = gson.fromJson(json, ErrorEntity.class);
                                    wrapper.setException(catChannelError);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ErrorEntity movieCatError=new ErrorEntity();
                            movieCatError.setStatus(100);
                            movieCatError.setErrorMessage(context.getResources().getString(R.string.err_unexpected));
                        }

                        responseMediatorLiveData.postValue(wrapper);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ErrorEntity movieCatError=new ErrorEntity();
                        if (e instanceof HttpException || e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                            movieCatError.setStatus(NO_CONNECTION);
                            movieCatError.setErrorMessage(e.getLocalizedMessage());
                        } else {
                            movieCatError.setStatus(500);
                            movieCatError.setErrorMessage(e.getLocalizedMessage());
                        }
                        wrapper.setException(movieCatError);
                        responseMediatorLiveData.postValue(wrapper);
//                        responseMediatorLiveData.postValue(wrapper);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return responseMediatorLiveData;
    }
}
