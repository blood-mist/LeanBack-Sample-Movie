package stb.androidtv.moviesleanback.utils;

import java.util.ArrayList;

import stb.androidtv.moviesleanback.enitities.MovieItem;
import stb.androidtv.moviesleanback.enitities.TopmoviesItem;

public enum MovieEnum {
    INSTANCE;

    private ArrayList<MovieItem> movieSubCatList;
    private ArrayList<TopmoviesItem> topMoviesList;

    public static boolean hasSubListData() {
        return INSTANCE.movieSubCatList != null;
    }

    public static boolean hasTopMovieData() {
        return INSTANCE.topMoviesList != null;
    }

    public static void setMovieSubList(final ArrayList<MovieItem> movieSubCatList) {
        INSTANCE.movieSubCatList = movieSubCatList;
    }

    public static void setTopMovieList(final ArrayList<TopmoviesItem> topMoviesList) {
        INSTANCE.topMoviesList = topMoviesList;
    }

    public static ArrayList<MovieItem> getMovieSublist() {
        final ArrayList<MovieItem> retList = INSTANCE.movieSubCatList;
        INSTANCE.movieSubCatList = null;
        return retList;
    }

    public static ArrayList<TopmoviesItem> getTopMovielist() {
        final ArrayList<TopmoviesItem> retList = INSTANCE.topMoviesList;
        INSTANCE.topMoviesList = null;
        return retList;
    }
}
