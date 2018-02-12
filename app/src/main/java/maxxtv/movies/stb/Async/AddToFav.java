package maxxtv.movies.stb.Async;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.Fragments.OptionsFragment;
import maxxtv.movies.stb.Interface.IsFavCallback;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.LinkConfig;

/**
 * Created by ACER on 6/13/2017.
 */

public class AddToFav extends AsyncTask<String, String, String> {
    private Context context;
    private int movie_id;
    private IsFavCallback favCallback;
    private String authToken;

    public AddToFav(Context context, int movie_id, IsFavCallback favCallback, String authToken) {
        this.context=context;
        this.movie_id=movie_id;
        this.favCallback=favCallback;
        this.authToken=authToken;
    }

    @Override
    protected String doInBackground(String... strings) {
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
            String setfav_url = strings[0] + "?" + LinkConfig.getHashCode(utc)+"&movieId="+movie_id;
            Log.d("setfav_url", setfav_url);
            DownloadUtil dUtil = new DownloadUtil(setfav_url, context,authToken);
            return dUtil.downloadStringContent();
        } else
            return utc;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Logger.d("fav_result",s);
        favCallback.setMovieAsFav(s);
    }
}
