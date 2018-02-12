package maxxtv.movies.stb.Parser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import maxxtv.movies.stb.Async.ApkDownloader;
import maxxtv.movies.stb.Entity.MarketApp;
import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.PackageUtils;


public class MarketAppDetailParser {
    public static String MyAccountAppPackage = "maxxtv.myaccount.stb"
            .trim();
    public static String Youtube = "com.google.android.youtube.tv";
    public static HashMap<String, MarketApp> APKs;
    public static ArrayList<String> packageNames;
    private Context context;
    private String jsonstr;

    public MarketAppDetailParser(Context context, String jsonstr) {
        this.context = context;
        this.jsonstr = jsonstr;
        APKs = new HashMap<String, MarketApp>();
        packageNames = new ArrayList<String>();
    }

    public static ArrayList<MarketApp> ApkListWithoutCurrentApk(Context context) {
        ArrayList<MarketApp> apklist = new ArrayList<MarketApp>();

        if (packageNames != null) {
            for (int i = 0; i < MarketAppDetailParser.packageNames.size(); i++) {
                String packageNamefromApk = packageNames.get(i);
                MarketApp ma = MarketAppDetailParser.APKs
                        .get(packageNamefromApk);

                Logger.d("apk name", ma.getName() + "");
                String packagename = "";
                try {
                    PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                            context.getPackageName(), 0);
                    packagename = pInfo.packageName;
                } catch (NameNotFoundException e) {
                    Logger.printStackTrace(e);
                }
                if (!ma.getVisibility() || packageNames.get(i).equalsIgnoreCase(packagename))
                    continue;
                else
                    apklist.add(ma);
                // MarketAppDetailParser.packageNames.get(i);
            }
        }
        return apklist;
    }

    public static void openApk(Context context, String packageName) {
        Logger.d("inside", "open apk");
        if (PackageUtils.isPackageInstalled(context, packageName)) {
            Intent openApk = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
            openApk.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(openApk);
            ((EntryPoint) context).finish();
        } else {
            try {
                Logger.d("package to install", packageName);
                Logger.d("app to install", APKs.get(packageName).getName());
                MarketApp ma = APKs.get(packageName);
                new ApkDownloader(context, APKs.get(packageName).getName()).execute(APKs.get(packageName)
                                                                                            .getAppDownloadLink());


            } catch (Exception e2) {
                Logger.printStackTrace(e2);
                CustomDialogManager apkNotFound = new CustomDialogManager(
                        context, CustomDialogManager.ALERT);
                apkNotFound.build();
                apkNotFound
                        .setMessage("","App Not Found! \n Contact your Service Provider!!!");


                apkNotFound.addDissmissButtonToDialog();
                try {
                    apkNotFound.show();
                } catch (Exception e) {
                    Toast.makeText(context, "App Not Found! Contact your Service Provider!", Toast.LENGTH_SHORT).show();
                }

            }

        }

    }

    public void parse() throws JSONException {

        JSONArray jObj = new JSONArray(jsonstr);
        for (int i = 0; i < jObj.length(); i++) {
            JSONObject apkObj = jObj.getJSONObject(i);
            MarketApp marketApp = new MarketApp();

            marketApp.setName(apkObj.getString("display_name"));
            marketApp.setAppPackageName(apkObj.getString("package_name"));
            marketApp.setAppDownloadLink(apkObj.getString("apk_download_link"));
            marketApp.setAppImageLink(apkObj.getString("myapp_image"));
            marketApp.setVisibility(apkObj.getBoolean("visibility"));
            marketApp.setVersion(apkObj.getString("version_name"));
            marketApp.setVersionCode(apkObj.getInt("version_code"));
            packageNames.add(marketApp.getAppPackageName());
            APKs.put(marketApp.getAppPackageName(), marketApp);
        }


    }

}
