package stb.androidtv.moviesleanback.customFragments;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.MovieLinkWrapper;

public class MovieLinkViewModel extends AndroidViewModel {
    private MovielinkRepository movielinkRepository;
    public MovieLinkViewModel(@NonNull Application application) {
        super(application);
        movielinkRepository = MovielinkRepository.getInstance(application);
    }

    public LiveData<MovieLinkWrapper> getMovieLink(int id, Login login, String macAddress) {
        return movielinkRepository.getMovieLink(id,login,macAddress);
    }

}
