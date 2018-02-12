package maxxtv.movies.stb.Async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.Interface.AsyncBack;
import maxxtv.movies.stb.Interface.SearchCallback;
import maxxtv.movies.stb.MovieCategoryActivity;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.common.LinkConfig;

/**
 * Created by ACER on 6/9/2017.
 */

public class SearchAsync extends AsyncTask<String,String,String> {
    private Context context;
    private SearchCallback asyncback;
    private String movieName;
    private CustomDialogManager loading;
    private String authToken;
    public SearchAsync(Context context, SearchCallback asyncback, String movieName, String authToken) {
        this.context=context;
        this.asyncback=asyncback;
        this.movieName=movieName;
        this.authToken=authToken;
        this.loading=new CustomDialogManager(context,CustomDialogManager.LOADING);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading.build();
        loading.show();
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
            String subCategory_url = strings[0] + "?" + LinkConfig.getHashCode(utc)+"&movieName="+movieName;
            Log.d("subCategory_url", subCategory_url);
            DownloadUtil dUtil = new DownloadUtil(subCategory_url, context,authToken);
            return dUtil.downloadStringContent();
        } else
            return utc;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        loading.dismiss();
        asyncback.getSearchMovies(s);
    }
}
