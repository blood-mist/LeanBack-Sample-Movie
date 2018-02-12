package maxxtv.movies.stb.Async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import maxxtv.movies.stb.BuildConfig;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.GetMac;

/**
 * Created by NITV on 27/02/2017.
 */

public class SendingCrashReport extends AsyncTask<String,String,String> {

    String errorMessage;
    Context context;


    public SendingCrashReport(Context context, String errorMessage){
        this.context = context;
        this.errorMessage = errorMessage;
    }

    @Override
    protected String doInBackground(String... params) {
        String response="";
        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("error", errorMessage)
                    .appendQueryParameter("macId", GetMac.getMac(context))
                    .appendQueryParameter("appVersion", BuildConfig.VERSION_CODE+"")
                    .appendQueryParameter("appVersionName", BuildConfig.VERSION_NAME)
                    .appendQueryParameter("boxVersion", "1.0.4");
            String query = builder.build().getEncodedQuery();

            Logger.d("CheckingALlappData",query.toString());
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
        } catch (Exception e){
            System.exit(0);
        }
        System.exit(0);
        return response;

    }

    @Override
    protected void onPostExecute(String s) {
        Logger.d("CheckingResponseOfErrorSend",s + "   wffdsf");
        super.onPostExecute(s);
    }
}