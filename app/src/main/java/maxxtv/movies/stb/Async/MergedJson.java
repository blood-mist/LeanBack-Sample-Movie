package maxxtv.movies.stb.Async;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Parser.MarketAppDetailParser;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.UnauthorizedAccess;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.LoginFileUtils;
import maxxtv.movies.stb.Utils.common.AppConfig;
import maxxtv.movies.stb.Utils.common.GetMac;
import maxxtv.movies.stb.Utils.common.LinkConfig;

import static maxxtv.movies.stb.EntryPoint.macAddress;

/**
 * Created by NITV on 21/09/2016.
 */
public class MergedJson extends AsyncTask<Void, Void, String> {

    private static int RETRY_COUNT = 0;
    private EntryPoint entryPoint;
    String url;

    public MergedJson(EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    protected String doInBackground(Void... params) {

        String link = LinkConfig.getString(entryPoint, LinkConfig.CHECK_BEFORE_LOGIN_URL) + "?boxId=" + (AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(entryPoint));
        Logger.d("URL", link);
        return new DownloadUtil(link, entryPoint).downloadStringContent();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Logger.d("result", result);
        if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
            final CustomDialogManager noInternet = new CustomDialogManager(entryPoint, CustomDialogManager.ALERT);
            noInternet.build();
            noInternet.exitApponBackPress();
            noInternet.setTitle(entryPoint.getString(R.string.no_internet_title));
            noInternet.setMessage("", entryPoint.getString(R.string.no_internet_body));
            noInternet.getInnerObject().setCancelable(false);
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
                JSONObject jObj = new JSONObject(result);
                try {
                    JSONObject dataObj = jObj.getJSONObject("data");
                    String uname = dataObj.getString("user_name");
                    String displayName = dataObj.getString("display_name");

                    entryPoint.et_username.setText(uname);
                    String isapproved = dataObj.getString("activation_status");
                    String isactive = dataObj.getString("is_active");
                    if (isapproved.equals("1") && isactive.equals("1")) {
                       /* if (LoginFileUtils.checkIfFileExists(LinkConfig.LOGIN_FILE) &&
                                LoginFileUtils.checkIfFileExists(LinkConfig.TOKEN_CONFIG_FILE_NAME)) {
                            String authToken = LoginFileUtils.getAuthTokenFromFile();
                            String user_id = "", user_email = "";
                            if (LoginFileUtils.readFromFile(macAddress))
                                user_id = LoginFileUtils.getUserId();

                            if (LoginFileUtils.readFromFile(macAddress))
                                user_email = LoginFileUtils.getUserEmail();

                            String session_url = LinkConfig.getString(entryPoint, LinkConfig.UPDATE_SESSION) + "?platform=android-stb&uname=" + user_email + "&boxId=" + macAddress;
                            new UpdateSession(entryPoint, user_id, authToken).execute(session_url);*/
                            entryPoint.proceedLogin(displayName, uname);
                    } else {
                        int error_code = jObj.getInt("error");
                        String error_message = jObj.getString("message");
                        LoginFileUtils.deleteLoginFile();
                        Intent i3 = new Intent(entryPoint, UnauthorizedAccess.class);
                        i3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i3.putExtra("error_code", error_code);
                        i3.putExtra("error_message", error_message);

                        entryPoint.startActivity(i3);
                        entryPoint.finish();
                    }

                } catch (JSONException je) {
                    je.printStackTrace();
                    //mac or geo block
//                    MarketAppDetailParser.openApk(entryPoint, MarketAppDetailParser.MyAccountAppPackage);
                    int error_code = jObj.getInt("error");
                    String error_message = jObj.getString("message");

                    if (error_code == 401 || error_code == 402) {
                        MarketAppDetailParser.openApk(entryPoint, MarketAppDetailParser.MyAccountAppPackage);
                    } else {
                        Intent i3 = new Intent(entryPoint, UnauthorizedAccess.class);
                        i3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i3.putExtra("error_code", error_code + "");
                        i3.putExtra("error_message", error_message);
                        LoginFileUtils.deleteLoginFile();
                        entryPoint.startActivity(i3);
                        entryPoint.finish();
                    }
                }
            } catch (JSONException je) {
                Logger.printStackTrace(je);
                CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(entryPoint);

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
