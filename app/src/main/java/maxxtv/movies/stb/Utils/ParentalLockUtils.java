package maxxtv.movies.stb.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;


import io.realm.Realm;
import io.realm.RealmQuery;
import maxxtv.movies.stb.Async.ParentalLockCheck;
import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.MoviePlayCustomController;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.common.AppConfig;
import maxxtv.movies.stb.Utils.common.LinkConfig;

/**
 * Created by sadip_000 on 26/01/2016.
 */
public class ParentalLockUtils {

    public static boolean isMovieParentallyLocked(final Movie movie) {
        if (AppConfig.featureParentalLock) {
            // parental lock feature is disabled
            return false;
        } else {
            // parental lock feature is enabled
            if (movie.getParental_lock() == 0) {
                // movie is not parentially locked
                return false;
            } else {
                // movie is parentially locked
                Logger.d("movie parental locked", "do not let let play movie");
                return true;
            }
        }
    }

    /**
     * change the parental status of given
     * movie
     *  @param context
     * @param movieId
     * @param authToken
     */
   /* public static void changeMovieParentalStatus(final Context context,
                                                 int movieId) {
        Movie movie = GenreAndMoviesParser.getMovieFromId(movieId);
        // turn on or of parental control
        String lock;
        if (movie.getParentalLock().equals("0"))
            lock = "1";
        else
            lock = "0";
        String setParentalLockUrl = LinkConfig.getString(context,
                LinkConfig.PARENTAL_LOCK_URL)
                + "?type=setMovie"
                + "&lock="
                + lock + "&productId=" + movie.getId();

        new ParentalLockCheck(context, movie).execute(setParentalLockUrl);

    }*/
    public static void changeMovieParentalStatus(final Context context,
                                                 int movieId, String authToken) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Movie> parentalLockquery = realm.where(Movie.class).equalTo("movie_id", movieId);
        Movie movie = parentalLockquery.findFirst();
        //   if (movie.getParentalLock().equals("1")) {
        Log.d("movie_lock_status", movie.getParental_lock() + "");
        if (movie.getParental_lock() == 1) {
            realm.beginTransaction();
            movie.setParental_lock(0);
            showDialog(context, movie, "Parental Lock is successfully unset");
            realm.insertOrUpdate(movie);
            realm.commitTransaction();
          /*  new ParentalLockCheck(context,movie.getMovie_id(),authToken).execute(LinkConfig.getString(context,LinkConfig.PARENTAL_URL));*/
        } else {
            realm.beginTransaction();
            movie.setParental_lock(1);
            showDialog(context, movie, "parental Lock is successfully set");
            realm.insertOrUpdate(movie);
            realm.commitTransaction();
//            new ParentalLockCheck(context,movie.getMovie_id(), authToken).execute(LinkConfig.getString(context,LinkConfig.PARENTAL_URL));

        }
    }

    private static void showDialog(final Context context, final Movie movie, String message) {
        final CustomDialogManager parentialStatusSuccessDialog = new CustomDialogManager(
                context, movie.getMovie_name(), message,
                CustomDialogManager.MESSAGE);
        parentialStatusSuccessDialog.build();
        parentialStatusSuccessDialog.show();
        if (context
                .getClass()
                .getName()
                .equals(MoviePlayCustomController.class.getName())) {
            parentialStatusSuccessDialog.addDissmissButtonToDialogandPlay(context, movie.getMovie_id(), false);
            parentialStatusSuccessDialog.dismissDialogOnBackPressedandPlay(context, movie.getMovie_id(), false);
        } else {
            parentialStatusSuccessDialog.addDissmissButtonToDialog();
        }


        parentialStatusSuccessDialog.setNegativeButton("OK",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        parentialStatusSuccessDialog.dismiss();
                        if (context
                                .getClass()
                                .getName()
                                .equals(MoviePlayCustomController.class.getName())) {
                            ((MoviePlayCustomController) context).checkToLoadMovieLink(context, movie.getMovie_id(), false);
                        }
                    }
                });
    }

    /**
     * checks if the movie is parentially locked
     */

}
