package maxxtv.movies.stb.Async;

import android.content.Context;
import android.os.AsyncTask;

import maxxtv.movies.stb.Entity.ImdbPojo;
import maxxtv.movies.stb.Interface.AsyncBack;
import maxxtv.movies.stb.Interface.ImdbCallback;
import maxxtv.movies.stb.Interface.SearchCallback;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;

/**
 * Created by ACER on 6/19/2017.
 */

public class ImdbCredits extends AsyncTask<String,String,String> {
    private Context context;
    private ImdbCallback callback;
    private ImdbPojo imdbpojo;

    public ImdbCredits(Context context, ImdbCallback callback, ImdbPojo imdbpojo) {
        this.context = context;
        this.callback = callback;
        this.imdbpojo=imdbpojo;
    }
    @Override
    protected String doInBackground(String... strings) {
        DownloadUtil dUtil = new DownloadUtil(strings[0], context);
        Logger.d("imdb_api",strings[0]);
        return dUtil.downloadStringContent();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(!isCancelled())
        callback.getImdbData(imdbpojo,s);
    }
}
