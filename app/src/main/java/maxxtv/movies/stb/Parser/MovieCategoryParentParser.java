package maxxtv.movies.stb.Parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Entity.MovieCategoryParent;
import maxxtv.movies.stb.Utils.Logger;

public class MovieCategoryParentParser {

    private static final String TAG = MovieCategoryParentParser.class.getSimpleName();

    private static final String PARENT_ID = "parent_id";
    private static final String CATEGORY_NAME = "parent_name";
    private static final String CATEGORY_DESC = "parent_description";
    private static final String CATEGORY_IMAGE = "parent_logo";
    private static final String STATUS = "status";
    private Realm realm;
    public static ArrayList<MovieCategoryParent> movieParentCategoryList;
    public ArrayList<Movie> topMovieList;
    public ArrayList<Movie> myWatchList;
    private String jsonString;
    private Context context;

    public MovieCategoryParentParser(String jsonString, Context context) {
        this.jsonString = jsonString;
        this.context = context;

        movieParentCategoryList = new ArrayList<>();
        topMovieList = new ArrayList<>();
        myWatchList = new ArrayList<>();
    }

    public boolean parse() throws JSONException {
        JSONObject root = null;
        realm = Realm.getDefaultInstance();
        try {
            root = new JSONObject(jsonString);
        } catch (JSONException e) {
            Logger.printStackTrace(e);
            return false;
        }


        JSONArray categoryItems = root.getJSONArray("movies_parent");

        for (int i = 0; i < categoryItems.length(); i++) {
            JSONObject item = categoryItems.getJSONObject(i);

            MovieCategoryParent parent = new MovieCategoryParent();

            parent.setParentId(item.getInt(PARENT_ID));
            parent.setCategoryName(item.getString(CATEGORY_NAME));
            parent.setCategoryDescription(item.getString(CATEGORY_DESC));
            parent.setCategoryImageLink(item.getString(CATEGORY_IMAGE));
            parent.setStaus(item.getInt(STATUS));
            movieParentCategoryList.add(parent);
        }

        JSONArray topMovieItems = root.getJSONArray("topmovies");
        if (topMovieItems.length() > 0) {
            for (int i = 0; i < topMovieItems.length(); i++) {
                realm.beginTransaction();
                JSONObject item = topMovieItems.getJSONObject(i);
                Movie top_movie = new Movie();
                top_movie.setMovie_name(item.getString("name"));
                top_movie.setMovie_id(item.getInt("id"));
                top_movie.setIs_Imdb(Integer.parseInt(item.getString("imdbID")));
                top_movie.setImdb_id(String.valueOf(item.getInt("movie_id")));
                top_movie.setMovie_category_id(Integer.parseInt(item.getString("movie_category_id")));
                top_movie.setMovie_url(item.getString("movie_url"));
                top_movie.setIs_youtube(Integer.parseInt(item.getString("is_youtube")));
                top_movie.setMovie_description(item.getString("description"));
                top_movie.setPreview_url(item.getString("preview_url"));
                top_movie.setParental_lock(Integer.parseInt(item.getString("parental_lock")));
                Movie movie = realm.where(Movie.class).equalTo("movie_id", item.getInt("id")).findFirst();
                if (movie != null) {
                    if (movie.getParental_lock() == 1)
                        top_movie.setParental_lock(1);
                }
                top_movie.setMovie_logo(item.getString("movie_logo"));
                top_movie.setIsFav(item.getInt("isFav"));
                topMovieList.add(top_movie);
                realm.insertOrUpdate(top_movie);
                realm.commitTransaction();
            }
        }

        JSONArray favoriteMovieItems = root.getJSONArray("favorive_movies");
        if (favoriteMovieItems.length() > 0) {
            for (int i = 0; i < favoriteMovieItems.length(); i++) {
                realm.beginTransaction();
                JSONObject item = favoriteMovieItems.getJSONObject(i);
                Movie favorite_movie = new Movie();
                favorite_movie.setMovie_name(item.getString("name"));
                favorite_movie.setMovie_id(item.getInt("id"));
                favorite_movie.setIs_Imdb(Integer.parseInt(item.getString("imdbID")));
                favorite_movie.setImdb_id(String.valueOf(item.getInt("movie_id")));
                favorite_movie.setMovie_category_id(Integer.parseInt(item.getString("movie_category_id")));
                favorite_movie.setMovie_url(item.getString("movie_url"));
                favorite_movie.setIs_youtube(Integer.parseInt(item.getString("is_youtube")));
                favorite_movie.setMovie_description(item.getString("description"));
                favorite_movie.setPreview_url(item.getString("preview_url"));
                favorite_movie.setParental_lock(Integer.parseInt(item.getString("parental_lock")));
                Movie movie = realm.where(Movie.class).equalTo("movie_id", item.getInt("id")).findFirst();
                if (movie != null) {
                    if (movie.getParental_lock() == 1)
                        favorite_movie.setParental_lock(1);
                }
                favorite_movie.setMovie_logo(item.getString("movie_logo"));
                favorite_movie.setIsFav(item.getInt("isFav"));
                myWatchList.add(favorite_movie);
                realm.insertOrUpdate(favorite_movie);
                realm.commitTransaction();
            }
        }

        return true;
    }

    public ArrayList<Movie> getTopMovieList() {
        return this.topMovieList;
    }

    public ArrayList<MovieCategoryParent> getMovieCategoryParentList() {
        return this.movieParentCategoryList;
    }

    public ArrayList<Movie> getFavMovieList() {
        return this.myWatchList;
    }

}
