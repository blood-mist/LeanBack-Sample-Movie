package stb.androidtv.moviesleanback.movieCategory;

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
import stb.androidtv.moviesleanback.enitities.MovieDataResponse;
import stb.androidtv.moviesleanback.enitities.MovieDataWrapper;
import stb.androidtv.moviesleanback.retroUtils.ApiManager;
import stb.androidtv.moviesleanback.utils.ApiInterface;
import stb.androidtv.moviesleanback.utils.LinkConfig;
import stb.androidtv.moviesleanback.utils.TimeStamp;

import static stb.androidtv.moviesleanback.splash.SplashRepository.KEY_CATEGORY;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;

public class MovieRepository {
    private static final String KEY_MOVIE = "movie";
    private static MovieRepository mInstance;
    private ApiInterface movieInterface;
    private Context context;

    public MovieRepository(Application application) {
        this.context = application.getApplicationContext();
        Retrofit retrofitInstance = ApiManager.getAdapter();
        movieInterface = retrofitInstance.create(ApiInterface.class);
    }
    public static MovieRepository getInstance(final Application application) {
        if (mInstance == null) {
            synchronized (MovieRepository.class) {
                if (mInstance == null) {
                    mInstance = new MovieRepository(application);
                }
            }
        }
        return mInstance;
    }

    public LiveData<MovieCatWrapper> getMovieCategories(Login login, String macAddress) {
        MediatorLiveData<MovieCatWrapper> responseMediatorLiveData = new MediatorLiveData<>();
        responseMediatorLiveData.setValue(null);
        long utc=TimeStamp.getTimeStamp();
        Gson gson=new Gson();
        MovieCatWrapper wrapper = new MovieCatWrapper();
        io.reactivex.Observable<Response<ResponseBody>> call = movieInterface.getMovieCatList(login.getToken(), utc, String.valueOf(login.getId()),  LinkConfig.getHashCode(String.valueOf(login.getId()), String.valueOf(utc),
                login.getSession()), macAddress);
        call.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> movieCatResponse) {
                        String json = null;

                        if (movieCatResponse.code() == 200) {
                            try {
                                json = movieCatResponse.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (jsonObject.has(KEY_CATEGORY)) {
                                    MovieCategory categoryMoviesData = gson.fromJson(json, MovieCategory.class);
                                    wrapper.setChannelLinkResponse(categoryMoviesData);
                                } else {
                                    ErrorEntity categoryMovieError = gson.fromJson(json, ErrorEntity.class);
                                    wrapper.setException(categoryMovieError);
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

    public LiveData<MovieDataWrapper> getMovieData(int id, Login login, String macAddress) {
        MediatorLiveData<MovieDataWrapper> movieLiveData = new MediatorLiveData<>();
        movieLiveData.setValue(null);
        long utc=TimeStamp.getTimeStamp();
        Gson gson=new Gson();
        MovieDataWrapper wrapper = new MovieDataWrapper();
        io.reactivex.Observable<Response<ResponseBody>> call = movieInterface.getMovieData(login.getToken(), utc, String.valueOf(login.getId()),  LinkConfig.getHashCode(String.valueOf(login.getId()), String.valueOf(utc),
                login.getSession()), macAddress, String.valueOf(id));
        call.subscribeOn(Schedulers.io()).observeOn(Schedulers.newThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new io.reactivex.Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> movieDataResponse) {
                        String json = null;

                        if (movieDataResponse.code() == 200) {
                            try {
                                json = movieDataResponse.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                if (jsonObject.has(KEY_MOVIE)) {
                                    MovieDataResponse movieDataInfo = gson.fromJson(json, MovieDataResponse.class);
                                    wrapper.setMovieDataResponse(movieDataInfo);
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

                        movieLiveData.postValue(wrapper);
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
                        movieLiveData.postValue(wrapper);
//                        responseMediatorLiveData.postValue(wrapper);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return movieLiveData;
    }
}
