package maxxtv.movies.stb.Async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.Interface.AsyncBack;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.common.LinkConfig;

public class LoadMovieAsync extends AsyncTask<String,String,String> {
    private Context context;
    private AsyncBack callback;
    private String authToken;

    public LoadMovieAsync(Context context, AsyncBack callback, String authToken) {
        this.context = context;
        this.callback = callback;
        this.authToken=authToken;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        DownloadUtil getUtc = new DownloadUtil(LinkConfig.getString(context, LinkConfig.GET_UTC), context);
        String utc = "";
        try {
            JSONObject   utcObj = new JSONObject(getUtc.downloadStringContent());
            utc = utcObj.getString("utc");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!utc.equals(DownloadUtil.NotOnline)
                && !utc.equals(DownloadUtil.ServerUnrechable)) {
            String subCategory_url = strings[0] + "&" + LinkConfig.getHashCode(utc);
            Log.d("subCategory_url", subCategory_url);
            DownloadUtil dUtil = new DownloadUtil(subCategory_url, context,authToken);
            return dUtil.downloadStringContent();
        } else
            return utc;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("parent_click", s);
        callback.getResults(s);
    }
}
