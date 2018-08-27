package maxxtv.movies.stb.Async;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.View;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.BuildConfig;
import maxxtv.movies.stb.Entity.VersionCheckEvent;
import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.UnauthorizedAccess;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.LinkConfig;

/**
 * Created by NITV on 02/06/2016.
 */
public class AllowCountry extends AsyncTask<Void, Void, String> {

    EntryPoint activity;

    public AllowCountry(EntryPoint activity){
        this.activity = activity;
    }
    @Override
    protected String doInBackground(Void... params) {
        String link = LinkConfig.getString(activity, LinkConfig.ALLOW_COUNTRY);
        System.out.println(link);
        return new DownloadUtil(link, activity).downloadStringContent();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals(DownloadUtil.NotOnline) || result.equals(DownloadUtil.ServerUnrechable)) {
            final CustomDialogManager noInternet = new CustomDialogManager(activity, CustomDialogManager.ALERT);
            noInternet.build();
            noInternet.setTitle(activity.getString(R.string.no_internet_title));
            noInternet.setMessage("",activity.getString(R.string.no_internet_body));
            noInternet.getInnerObject().setCancelable(false);
            noInternet.exitApponBackPress();
            noInternet.finishActivityonDismissPressed(activity);
            noInternet.setExtraButton("", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noInternet.dismiss();
                    activity.finish();
                }
            });
            noInternet.setPositiveButton("Re-Connect", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = activity.getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
                    assert i != null;
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    noInternet.dismiss();
                    activity.startActivity(i);


                }
            });
            noInternet.setNegativeButton("Settings", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.openSetting();
                    noInternet.dismiss();

                }
            });
        } else {
            try {
                JSONObject jo = new JSONObject(result);
                JSONObject allow= jo.getJSONObject("allow");
                if (allow.getBoolean("allow")) {
                    Logger.d("AllowCountry","TRUE");
                    EventBus.getDefault().post(new VersionCheckEvent());

//                    new ApkInServer(activity).execute(LinkConfig.LINK_SEVER_APKs+"?macAddress="+ EntryPoint.macAddress,(activity).getApplicationContext().getPackageName(), String.valueOf(BuildConfig.VERSION_CODE));


                } else {

                    Intent i = new Intent(activity, UnauthorizedAccess.class);
                    i.putExtra("error_code", allow.getString("code"));
                    i.putExtra("error_message", allow.getString("message"));
                    i.putExtra("ipAddress", allow.getString("ip"));
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(i);
                    activity.finish();

                }
            } catch (JSONException je) {
                Logger.printStackTrace(je);
                CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(activity);
            }
        }
    }
}
