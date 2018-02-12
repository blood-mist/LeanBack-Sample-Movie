package maxxtv.movies.stb.Async;

import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;



import org.json.JSONException;

import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Parser.MarketAppDetailParser;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;

/**
 * Created by NITV on 21/09/2016.
 */
public class ApkInServer extends AsyncTask<String, String, String> {


    private static int RETRY_COUNT = 0;
    String url;
    EntryPoint entryPoint;

    public ApkInServer(EntryPoint entryPoint){
        this.entryPoint = entryPoint;
    }

    @Override
    protected String doInBackground(String... params) {
        Logger.d(".APKInServer", params[0]);
        DownloadUtil dUtil = new DownloadUtil(params[0], entryPoint);
        url = params[0];
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
                noInternet.setExtraButton(entryPoint.getString(R.string.btn_dismiss), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                MarketAppDetailParser parser = new MarketAppDetailParser(
                        entryPoint, result);
                parser.parse();
                entryPoint.apkVerSionCheck();

            } catch (JSONException e1) {
                Logger.printStackTrace(e1);
                MarketAppDetailParser.APKs = null;
                MarketAppDetailParser.packageNames = null;
                Toast.makeText(entryPoint, "Could not check version!!!", Toast.LENGTH_SHORT).show();

                new MergedJson(entryPoint).execute();
            }
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
