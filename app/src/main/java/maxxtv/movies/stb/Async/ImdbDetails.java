package maxxtv.movies.stb.Async;

import android.content.Context;
import android.os.AsyncTask;

import maxxtv.movies.stb.Interface.AsyncBack;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;

/**
 * Created by ACER on 6/19/2017.
 */

public class ImdbDetails extends AsyncTask<String,String,String>{
    private Context context;
    private AsyncBack callback;

    public ImdbDetails(Context context, AsyncBack callback) {
        this.context = context;
        this.callback = callback;
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
        Logger.d("imdb_result",s);
        if(!isCancelled())
        callback.getResults(s);
    }
}

