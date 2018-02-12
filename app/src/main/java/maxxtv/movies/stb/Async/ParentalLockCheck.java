package maxxtv.movies.stb.Async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.Entity.Movie;
import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.LinkConfig;

/**
 * Created by ACER on 6/13/2017.
 */

public   class ParentalLockCheck extends AsyncTask<String, Void, String> {
    Context context;
    int movieId;
    CustomDialogManager progress;
    private String authToken;

    /**
     * gets the movie of which parental lock status is to be ch
     * ecked
     *  @param context
     * @param movieId
     * @param authToken
     */
    public ParentalLockCheck(Context context, int movieId, String authToken) {
        this.movieId = movieId;
        this.context = context;
        this.authToken=authToken;
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub

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
            String parental_url = params[0] + "?" + LinkConfig.getHashCode(utc) + "&movieId=" + movieId;
            DownloadUtil dUtil = new DownloadUtil(parental_url, context,authToken);

            Logger.d("Parential set url", parental_url);

            return dUtil.downloadStringContent();
        } else
            return utc;
    }

    @Override
    protected void onPreExecute() {
        progress = new CustomDialogManager(context, CustomDialogManager.LOADING);
        progress.build();
        progress.show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (progress.isShowing())
            progress.dismiss();
        if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
            CustomDialogManager.ReUsedCustomDialogs.noInternet(context);
        } else {
            Log.d("parental_result", result);
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
