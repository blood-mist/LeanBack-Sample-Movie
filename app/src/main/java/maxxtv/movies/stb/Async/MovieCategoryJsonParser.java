package maxxtv.movies.stb.Async;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.Entity.MovieCategoryParent;
import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.MovieCategoryActivity;
import maxxtv.movies.stb.Parser.MovieCategoryParentParser;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.LinkConfig;

/**
 * Created by NITV on 21/09/2016.
 */
public class MovieCategoryJsonParser extends
        AsyncTask<String, Void, String> {

    private static final String TAG = "MovieCategoryJsonParser";
    private static int RETRY_COUNT = 0;
    Context context;
    String url;
    private String authToken;

    public MovieCategoryJsonParser(Context context, String authToken) {
        this.context = context;
        this.authToken=authToken;
    }

    @Override
    protected String doInBackground(String... params) {

        DownloadUtil getUtc = new DownloadUtil(LinkConfig.getString(context, LinkConfig.GET_UTC), context);
        JSONObject utcObj = null;
        String utc = "";
        try {
            utcObj = new JSONObject(getUtc.downloadStringContent());
            utc = utcObj.getString("utc");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!utc.equals(DownloadUtil.NotOnline)
                && !utc.equals(DownloadUtil.ServerUnrechable)) {
            DownloadUtil dUtil = new DownloadUtil(params[0] + "&"
                    + LinkConfig.getHashCode(utc), context,authToken);

            Logger.d(TAG, params[0] + "&" + LinkConfig.getHashCode(utc));
            url = params[0] + "&" + LinkConfig.getHashCode(utc);
            Logger.d("CheckingMovieResult", url);

            return dUtil.downloadStringContent();
        } else
            return utc;
    }

    @Override
    protected void onPostExecute(String result) {
        Logger.d("CheckingMovieResult", result);

        if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
            final CustomDialogManager noInternet = new CustomDialogManager(context, CustomDialogManager.ALERT);
            noInternet.build();
            noInternet.setTitle(context.getString(R.string.no_internet_title));
            noInternet.setMessage("", context.getString(R.string.no_internet_body));
            noInternet.exitApponBackPress();
            noInternet.getInnerObject().setCancelable(false);
            noInternet.setExtraButton(context.getString(R.string.btn_dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noInternet.dismiss();
                    ((Activity) context).finish();
                }
            });
            noInternet.setPositiveButton("Re-Connect", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = ((Activity) context).getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(((Activity) context).getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    noInternet.dismiss();
                    context.startActivity(i);


                }
            });
            noInternet.setNegativeButton("Settings", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSetting();
                    noInternet.dismiss();

                }
            });
            noInternet.show();

        } else {
            MovieCategoryParentParser parser = new MovieCategoryParentParser(result, context);
            try {
                boolean success = parser.parse();
                if (success) {
                    String parentalLockIdUrl = LinkConfig.getString(context, LinkConfig.MOVIE_PARENT_CATEGORY_URL)
                            + "?type=getMovie";

//                        new ParentalLock().execute(parentalLockIdUrl);
                    ArrayList<MovieCategoryParent> movieParentList = parser.getMovieCategoryParentList();
                    ArrayList<Movie> topMovieList = parser.getTopMovieList();
                    ArrayList<Movie> myWatchList = parser.getFavMovieList();
                    Intent i1 = new Intent(context, MovieCategoryActivity.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i1.putParcelableArrayListExtra("movieParentList", movieParentList);
                    i1.putParcelableArrayListExtra("topMovieList", topMovieList);
                    i1.putParcelableArrayListExtra("watchList", myWatchList);
//                        i1.putExtra("version", version);
//                        i1.putExtra("platform", platform);
                    context.startActivity(i1);
                    ((Activity) context).finish();
                } else {
                    CustomDialogManager noMoviesAlert = new CustomDialogManager(context, CustomDialogManager.MESSAGE);
                    noMoviesAlert.build();
                    noMoviesAlert.setMessage("", context.getString(R.string.msg_no_movies));
                    noMoviesAlert.addDissmissButtonToDialog();
                    noMoviesAlert.dismissDialogOnBackPressed();
                    noMoviesAlert.finishActivityOnBackPressed(context);
                    noMoviesAlert.show();
                }
            } catch (JSONException e) {
                Logger.printStackTrace(e);
                try {
                    JSONObject root = new JSONObject(result);
                    if (root.getString("error_code").equals("405")) {
                        LinkConfig.deleteAuthCodeFile();
                        final CustomDialogManager invalidTokenDialog = new CustomDialogManager(context, CustomDialogManager.ALERT);
                        invalidTokenDialog.build();
                        invalidTokenDialog.setTitle("Invalid Token");
                        invalidTokenDialog.setMessage("", root.getString("message")+",please re-login");
                        invalidTokenDialog.getInnerObject().setCancelable(false);
                        invalidTokenDialog.exitApponBackPress();
                        invalidTokenDialog.setPositiveButton("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent entryPointIntent = new Intent(context, EntryPoint.class);
                                entryPointIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                invalidTokenDialog.dismiss();
                                context.startActivity(entryPointIntent);


                            }
                        });
                        invalidTokenDialog.show();


                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(context);
                }
            }
        }
    }

    public void openSetting() {
        try {
            try {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.rk_itvui.settings", "com.rk_itvui.settings.Settings"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
            } catch (Exception e) {
                try {
                    Intent LaunchIntent = ((Activity) context).getPackageManager().getLaunchIntentForPackage("com.giec.settings");
                    context.startActivity(LaunchIntent);
                    ((Activity) context).finish();
                } catch (Exception c) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }


        } catch (Exception a) {
            context.startActivity(
                    new Intent(Settings.ACTION_SETTINGS));
            ((Activity) context).finish();
        }
    }
}
