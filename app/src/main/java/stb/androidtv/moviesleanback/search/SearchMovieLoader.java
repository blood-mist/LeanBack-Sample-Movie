package stb.androidtv.moviesleanback.search;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.enitities.MoviesItem;

public class SearchMovieLoader extends AsyncTaskLoader<ArrayList<MoviesItem>> {
private ArrayList<MovieItem>movieItemsInCat;
private String query;
    public SearchMovieLoader(Context context, ArrayList<MovieItem> movieItemsInCat,String query) {
        super(context);
        this.movieItemsInCat=movieItemsInCat;
        this.query=query;
    }

    @Override
    public ArrayList<MoviesItem> loadInBackground() {
        final ArrayList<MoviesItem> result = new ArrayList<>();
        for (MovieItem movieData : movieItemsInCat) {
            // Main logic of search is here.
            for(MoviesItem movie:movieData.getMovies()) {
                // Just check that "query" is contained in Title or Description or not. (NOTE: excluded studio information here)
                if (movie.getName().toLowerCase(Locale.ENGLISH)
                        .contains(query.toLowerCase(Locale.ENGLISH))) {
                    result.add(movie);
                }
            }
        }
        return result;
    }

    @Override
    public void deliverResult(ArrayList<MoviesItem> data) {
            super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
    }
}
