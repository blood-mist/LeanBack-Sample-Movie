package stb.androidtv.moviesleanback.movieCategory;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieCatWrapper;
import stb.androidtv.moviesleanback.enitities.MovieCategory;
import stb.androidtv.moviesleanback.enitities.MovieDataWrapper;

public class MovieViewModel extends AndroidViewModel {
    private MovieRepository movieRepository;
    public MovieViewModel(@NonNull Application application) {
        super(application);
            movieRepository = MovieRepository.getInstance(application);
    }

    public LiveData<MovieCatWrapper> getMovieCat(Login login, String macAddress) {
        return movieRepository.getMovieCategories(login,macAddress);
    }

    public LiveData<MovieDataWrapper> getMovieData(int id, Login login, String macAddress) {
        return movieRepository.getMovieData(id,login,macAddress);
    }
}
