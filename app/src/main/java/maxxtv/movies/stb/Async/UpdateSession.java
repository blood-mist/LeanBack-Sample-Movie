package maxxtv.movies.stb.Async;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.View;


import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.Entity.Session;
import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.UnauthorizedAccess;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.LinkConfig;

/**
 * Created by NITV on 20/09/2016.
 */
public class UpdateSession extends AsyncTask<String, Void, String> {

    private static int RETRY_COUNT = 0;
    Context context;
    String userId;
    private String authToken;

    public UpdateSession(Context context, String userId, String authToken) {
        this.context = context;
        this.userId = userId;
        this.authToken = authToken;
    }

    @Override
    protected String doInBackground(String... params) {
        DownloadUtil dUtil = new DownloadUtil(params[0], context, authToken);
        Logger.d("CheckingSessionResult", params[0]);

        return dUtil.downloadStringContent();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
            final CustomDialogManager noInternet = new CustomDialogManager(context, CustomDialogManager.ALERT);
            noInternet.build();
            noInternet.setTitle(context.getString(R.string.no_internet_title));
            noInternet.setMessage("", context.getString(R.string.no_internet_body));
            noInternet.exitApponBackPress();
            noInternet.getInnerObject().setCancelable(false);
            noInternet.setExtraButton(context.getString(R.string.btn_dismiss), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noInternet.dismiss();
                    ((Activity) context).finish();
                }
            });
            noInternet.setPositiveButton("Re-Connect", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = ((Activity) context).getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(((Activity) context).getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    noInternet.dismiss();
                    context.startActivity(i);


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
                JSONObject root = new JSONObject(result);
                Session session = new Session();
                session.setSessionId(root.getString("session"));
                String url = LinkConfig.getString(context, LinkConfig.GET_GROUP) + "?userId=" + userId;
                Logger.d("CheckingSessionResult", url);

                new GetGroupInfo(context, authToken).execute(url);

            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    JSONObject root = new JSONObject(result);
                    if (root.getString("error_code").equals("405")) {
                        LinkConfig.deleteAuthCodeFile();
                        final CustomDialogManager invalidTokenDialog = new CustomDialogManager(context, CustomDialogManager.ALERT);
                        invalidTokenDialog.build();
                        invalidTokenDialog.setTitle("Invalid Token");
                        invalidTokenDialog.setMessage("", root.getString("message")+",please re-login");
                        invalidTokenDialog.getInnerObject().setCancelable(false);
                        invalidTokenDialog.finishActivityOnBackPressed(context);
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


    public void openSetting() {
        try {
            try {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.rk_itvui.settings", "com.rk_itvui.settings.Settings"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
            } catch (Exception e) {
                try {
                    Intent LaunchIntent = ((Activity) context).getPackageManager().getLaunchIntentForPackage("com.giec.settings");
                    context.startActivity(LaunchIntent);
                    ((Activity) context).finish();
                } catch (Exception c) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }


        } catch (Exception a) {
            context.startActivity(
                    new Intent(Settings.ACTION_SETTINGS));
            ((Activity) context).finish();
        }
    }

}