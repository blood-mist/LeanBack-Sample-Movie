package maxxtv.movies.stb.Async;

import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;



import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.UnauthorizedAccess;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;

/**
 * Created by NITV on 21/09/2016.
 */
public class ValidMacAddress extends AsyncTask<String, Void, String> {

    private static int RETRY_COUNT = 0;
    EntryPoint entryPoint;
    String url;


    public ValidMacAddress(EntryPoint entryPoint, String url) {
        this.entryPoint = entryPoint;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... params) {
        DownloadUtil dUtil = new DownloadUtil(url, entryPoint);
        Logger.d("CheckibngURkshjdf",url);
        return dUtil.downloadStringContent();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
                final CustomDialogManager noInternet = new CustomDialogManager(entryPoint, CustomDialogManager.ALERT);
                noInternet.build();
                noInternet.setTitle(entryPoint.getString(R.string.no_internet_title));
                noInternet.setMessage("",entryPoint.getString(R.string.no_internet_body));
                noInternet.getInnerObject().setCancelable(false);
                noInternet.exitApponBackPress();
                noInternet.finishActivityonDismissPressed(entryPoint);
               noInternet.setExtraButton("", new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       noInternet.dismiss();
                       entryPoint.finish();
                   }
               });
                noInternet.setPositiveButton("Re-Connect", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = entryPoint.getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(entryPoint.getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        noInternet.dismiss();
                        entryPoint.startActivity(i);


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
            try {
                JSONObject jsonObject = new JSONObject(result);

                String status = jsonObject.getString("mac_exists");
                if (status.equals("yes")) {
                    new AllowCountry(entryPoint).execute();
                } else {
                    try {
                        String error_message = jsonObject.getString("message");
                        String error_code = jsonObject.getString("code");
                        Intent i1 = new Intent(entryPoint,   UnauthorizedAccess.class);
                        i1.putExtra("error_code", error_code);
                        // i1.putExtra("username", username);
                        i1.putExtra("error_message", error_message);
                        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        entryPoint.startActivity(i1);
                        entryPoint.finish();
                    } catch (JSONException e) {
                        Logger.printStackTrace(e);
                    }
                }
            } catch (JSONException e) {
                Logger.printStackTrace(e);

            }
            Logger.d("CheckingSessionResult", result);

        }
    }


    public void openSetting() {
        try {
            try {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.rk_itvui.settings", "com.rk_itvui.settings.Settings"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                entryPoint.startActivity(intent);
                entryPoint.finish();
            } catch (Exception e) {
                try {
                    Intent LaunchIntent = entryPoint.getPackageManager().getLaunchIntentForPackage("com.giec.settings");
                    entryPoint.startActivity(LaunchIntent);
                    entryPoint.finish();
                } catch (Exception c) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    entryPoint.startActivity(intent);
                    entryPoint.finish();
                }
            }


        } catch (Exception a) {
            entryPoint.startActivity(
                    new Intent(Settings.ACTION_SETTINGS));
            entryPoint.finish();
        }
    }

}
