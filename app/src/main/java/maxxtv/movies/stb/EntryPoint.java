package maxxtv.movies.stb;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Stack;

import maxxtv.movies.stb.Async.ApkDownloader;
import maxxtv.movies.stb.Async.ApkInServer;
import maxxtv.movies.stb.Async.MergedJson;
import maxxtv.movies.stb.Async.UpdateSession;
import maxxtv.movies.stb.Async.ValidMacAddress;
import maxxtv.movies.stb.Entity.MarketApp;
import maxxtv.movies.stb.Entity.VersionCheckEvent;
import maxxtv.movies.stb.Parser.MarketAppDetailParser;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.DownloadUtil;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.LoginFileUtils;
import maxxtv.movies.stb.Utils.MyEncryption;
import maxxtv.movies.stb.Utils.PackageUtils;
import maxxtv.movies.stb.Utils.common.AppConfig;
import maxxtv.movies.stb.Utils.common.GetMac;
import maxxtv.movies.stb.Utils.common.LinkConfig;


public class EntryPoint extends Activity implements PermissionUtils.PermissionResultCallback {


    private static final int VERSION_CHECK = 1;
    private static final int VALID_USER = 2;
    /**
     * Everthing that is needed for user verification is done here before the app loads
     */
    String uname;
    private static final String TAG = EntryPoint.class.getSimpleName();
    private static final String platform = "android-stb";
    public static String macAddress; /*used in LinkConfig*/
    private LinearLayout loginLayout;
    private LinearLayout loadingLayout;
    //    private TextView userMacAddressView;
    private Button btn_login;
    public EditText et_password;
    public TextView et_username;
    public static String decrypted_password;
    int RETRY_COUNT = 0;
    public static String password;
    private ArrayList<String> permissions;
    PermissionUtils permissionUtils;
    private WeakReference<Context> weakReference;
    public static final int TODO_DOWNLOAD_ACCOUNT = 100;
    public static final int TODO_VERSION_CHECK = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        permissionUtils = new PermissionUtils(this);
        EventBus.getDefault().register(this);
        macAddress = AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(EntryPoint.this);
        permissions = new ArrayList<String>(
                Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE));

    }

    @Override
    protected void onStart() {
        super.onStart();
        loginLayout = (LinearLayout) findViewById(R.id.loginLinearyLayout);
        loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
//        userMacAddressView = (TextView) findViewById(R.id.user_macaddress);
        btn_login = (Button) findViewById(R.id.loginButtonPS);
        et_username = (TextView) findViewById(R.id.userNamePS);
        et_password = (EditText) findViewById(R.id.passWordPS);
        et_password.setTypeface(Typeface.DEFAULT);
        et_password.setTransformationMethod(new PasswordTransformationMethod());

        decrypted_password = et_password.getText().toString().trim();
        btn_login.requestFocus();


        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            String version = pInfo.versionName;
            ((TextView) findViewById(R.id.txt_version_loading)).setText(version + "");
        } catch (NameNotFoundException e) {
            Logger.printStackTrace(e);
        }

        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                uname = et_username.getText().toString().trim();
                String pword = et_password.getText().toString().trim();
                Logger.d("CheckingEmailaddress", uname);
                if (uname.length() > 0 && pword.length() > 0) {
                    loadingLayout.setVisibility(View.VISIBLE);
                    loginLayout.setVisibility(View.GONE);
                    String URL = LinkConfig.getString(EntryPoint.this,
                            LinkConfig.LOGIN_BUTTON_CLICK);

                    Logger.d(TAG + ": URL login check", URL);
                    password = pword;
                    new LoginTask(uname, pword).execute(URL);


                } else {
                    final CustomDialogManager login_error = new CustomDialogManager(
                            EntryPoint.this, "Login Failed..!",
                            "Please Enter the Username and Password",
                            CustomDialogManager.ALERT);
                    login_error.build();
                    login_error.dismissDialogOnBackPressed();
                    login_error.setNeutralButton(getString(R.string.btn_retry), new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            login_error.dismiss();
                            et_password.requestFocus();
                        }
                    });
                    login_error.setExtraButton("", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            login_error.dismiss();
                        }
                    });
                    login_error.show();
                }

            }
        });

        deleteOlderFiles();

    }

    public void checkPermissionBeforeapkVerSionCheck(int request_code) {
        permissionUtils.check_permission(permissions, getString(R.string.message_permissions), request_code);


    }

    public void deleteOlderFiles() {
        File dir = new File(getExternalFilesDir(null).getPath());
        if (dir != null) {
            Stack<File> dirlist = new Stack<File>();
            dirlist.clear();
            dirlist.push(dir);

            while (!dirlist.isEmpty()) {
                File dirCurrent = dirlist.pop();

                File[] fileList = dirCurrent.listFiles();
                if (fileList != null) {
                    for (File aFileList : fileList) {
                        if (!aFileList.getName().equalsIgnoreCase(
                                LoginFileUtils.APP_CONFIG_FILE_NAME)) {
                            if (aFileList.isDirectory())
                                dirlist.push(aFileList);
                            else {
                                Calendar c = Calendar.getInstance();
                                long date = c.get(Calendar.DATE);
                                Date today = new Date(date);

                                int diffInDays = (int) ((today.getTime() - aFileList
                                        .lastModified()) / (1000 * 60 * 60 * 24 * 7));
                                Logger.d(aFileList.getName(),
                                        (int) (aFileList.lastModified() / (1000 * 60 * 60 * 24 * 7))
                                                + "");
                                if (diffInDays > 7) {
                                    Logger.d(TAG + ": File is one week old",
                                            aFileList.getName() + "");
                                    aFileList.delete();
                                }
                            }
                        }
                    }
                } else
                    Logger.d(TAG, "dirlist is empty");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = LinkConfig.getString(EntryPoint.this, LinkConfig.CHECK_MAC) + "?mac=" + (AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(EntryPoint.this));
        new ValidMacAddress(EntryPoint.this, url).execute(url);
//        new MergedJson().execute();

    }

    /**
     * Checks the login procedure
     *
     * @param displayName
     */
    public void proceedLogin(String displayName, String userName) {

       /* userMacAddressView.setText(displayName
                                           + " | "
                                           + (AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(EntryPoint.this))
        );*/

        File externalStorageDir = Environment.getExternalStorageDirectory();
        File myFile = new File(externalStorageDir,
                LoginFileUtils.APP_CONFIG_FILE_NAME);

        // try to login from file
        Logger.e(myFile.getName(), "exists");

        /*
         * if (!FileAuthentication.checkMountAndFile(macAddress))
         * FileAuthentication.setUserNameandId();
         */

        if (LoginFileUtils.readFromFile(AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(EntryPoint.this))) {
            String encrypted_password = LoginFileUtils.getUserPassword() + "";
            Logger.d(TAG, LoginFileUtils.getUserEmail() + "");
            // display_name = FileAuthentication.getUserName();
            Logger.d(TAG, encrypted_password + "");

            MyEncryption sUtils = new MyEncryption();
            Logger.d(TAG, "Decrypting " + encrypted_password);
            decrypted_password = sUtils.getDecryptedToken(encrypted_password);
            password = decrypted_password;
            Logger.d("CheckingPasswotrd", decrypted_password);
            Logger.d("CheckingPasswotre", encrypted_password);
            Logger.d(TAG, decrypted_password);
            Logger.d(TAG, "##");
            String URL = LinkConfig.getString(EntryPoint.this, LinkConfig.LOGIN_BUTTON_CLICK)
                    /*+ "?uname="
                    + et_username.getText()
                    + "&pswd="
                    + decrypted_password
                    + "&boxId="
                    + (AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(EntryPoint.this))
                    + "&platform=" + platform*/;

            new LoginTask(userName, decrypted_password).execute(URL);
//            new PerformTask(encrypted_password).execute(URL);

        } else {
            // file not found so login from details provided by user
            loadingLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            et_password.requestFocus();
        }


    }

    private WeakReference<Context> getWeaakReference() {
        if (weakReference == null)
            weakReference = new WeakReference<Context>(this);

        return weakReference;
    }

    public void apkVerSionCheck() {
        int version;
        try {
            final MarketApp apk = MarketAppDetailParser.APKs.get(getPackageName());
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

            int versionFromJson = apk.getVersionCode();
            Logger.d("CheckingVersion", version + "   " + versionFromJson);
            if (versionFromJson > version) {
                final CustomDialogManager newVerSionAvailable = new CustomDialogManager(EntryPoint.this, CustomDialogManager.WARNING);
                newVerSionAvailable.build();
                newVerSionAvailable.setTitle(getString(R.string.update_found));
                newVerSionAvailable.setMessage("", getString(R.string.update_message));
                newVerSionAvailable.dismissDialogOnBackPressed();
                newVerSionAvailable.setPositiveButton(getString(R.string.btn_update), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newVerSionAvailable.dismiss();
                        new ApkDownloader(getWeaakReference(), getString(R.string.app_name)).execute(apk.getAppDownloadLink());
                    }
                });
                newVerSionAvailable.setNegativeButton(getString(R.string.btn_dismiss), new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newVerSionAvailable.dismiss();
                        new MergedJson(EntryPoint.this).execute();
                    }
                });
                newVerSionAvailable.show();
                newVerSionAvailable.getInnerObject().setCancelable(true);
                newVerSionAvailable.getInnerObject().setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        new MergedJson(EntryPoint.this).execute();
                    }
                });

            } else {

                new MergedJson(EntryPoint.this).execute();
            }
        } catch (NameNotFoundException e) {
            Toast.makeText(EntryPoint.this, "Could not check version!!!", Toast.LENGTH_SHORT).show();
            Logger.printStackTrace(e);

            new MergedJson(EntryPoint.this).execute();
        } catch (Exception e) {
            Toast.makeText(EntryPoint.this, "Could not check version!!!", Toast.LENGTH_SHORT).show();

            new MergedJson(EntryPoint.this).execute();
        }

    }

    public static String getPassword() {
        if (LoginFileUtils.readFromFile(macAddress)) {
            String encrypted_password = LoginFileUtils.getUserPassword() + "";
            Logger.d(TAG, LoginFileUtils.getUserEmail() + "");
            // display_name = FileAuthentication.getUserName();
            Logger.d(TAG, encrypted_password + "");

            MyEncryption sUtils = new MyEncryption();
            Logger.d(TAG, "Decrypting " + encrypted_password);
            decrypted_password = sUtils.getDecryptedToken(encrypted_password);
            password = decrypted_password;
            Logger.d("CheckingPasswotrd", decrypted_password);
            Logger.d("CheckingPasswotre", encrypted_password);
            Logger.d(TAG, decrypted_password);
        }
        return password.trim();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void PermissionGranted(int request_code) {
        performOperations(request_code);
    }

    private void performOperations(int request_code) {
        switch (request_code) {
            case TODO_VERSION_CHECK:
                apkVerSionCheck();
                break;
            case TODO_DOWNLOAD_ACCOUNT:
                MarketAppDetailParser.openApk(this, MarketAppDetailParser.MyAccountAppPackage);
        }
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> pending_permissions) {
        permissionUtils.check_permission(pending_permissions, getString(R.string.message_permissions), request_code);
    }

    @Override
    public void PermissionDenied(int request_code) {
        finish();
    }

    @Override
    public void NeverAskAgain(int request_code) {
        performOperations(request_code);
    }


    private class LoginTask extends AsyncTask<String, Void, String> {
        private String email, password;

        public LoginTask(String userEmail, String decrypted_password) {
            this.email = userEmail;
            this.password = decrypted_password;
        }

        @Override
        protected String doInBackground(String... params) {
            String loginLink = params[0];
            DownloadUtil dUtil = new DownloadUtil(loginLink, EntryPoint.this);
            Logger.d("CheckingLoginURL", loginLink);
            return dUtil.postLoginData(email, password, macAddress);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
                final CustomDialogManager noInternet = new CustomDialogManager(EntryPoint.this, CustomDialogManager.ALERT);
                noInternet.build();
                noInternet.setTitle(getString(R.string.no_internet_title));
                noInternet.setMessage("", getString(R.string.no_internet_body));
                if (loginLayout.getVisibility() == View.GONE)
                    noInternet.exitApponBackPress();
                else
                    noInternet.dismissDialogOnBackPressed();
                noInternet.getInnerObject().setCancelable(false);
                noInternet.setExtraButton(getString(R.string.btn_dismiss), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noInternet.dismiss();
                        if (loginLayout.getVisibility() == View.GONE)
                            noInternet.exitApponBackPress();
                        else
                            finish();
                    }
                });
                noInternet.setPositiveButton("Re-Connect", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        noInternet.dismiss();
                        startActivity(i);


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
                    Logger.d("CheckingLoginResult", result);
                    JSONObject value = new JSONObject(result);
                    JSONObject root = value.getJSONObject("login");
                    if (root.getString("is_active").equals("1")) {

                        String authToken = root.getString("token");
                        writeAuthTokenToFile(authToken);

                        String url = LinkConfig.getString(EntryPoint.this, LinkConfig.UPDATE_SESSION) + "?platform=android-stb&uname=" + email + "&boxId=" + macAddress;
                        new UpdateSession(EntryPoint.this, root.getString("id"), authToken).execute(url);
                    } else {
                        final CustomDialogManager loginError = new CustomDialogManager(EntryPoint.this, "Login Error", root.getString("error_code") + "\n" + root.getString("message"), CustomDialogManager.ALERT);
                        loginError.build();
                        loginError.setPositiveButton(getString(R.string.btn_retry), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginError.dismiss();
                            }
                        });
                        loginError.setNegativeButton(getString(R.string.btn_exit), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginError.dismiss();
                                finish();
                            }
                        });
                        loginError.setExtraButton("", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginError.dismiss();
                            }
                        });
                        loginError.show();
                        loadingLayout.setVisibility(View.GONE);
                        loginLayout.setVisibility(View.VISIBLE);
                        et_password.requestFocus();
                    }
                   /* new GroupDataParser(result).parse();
                    LoginFileUtils.reWriteLoginDetailsToFile(AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(EntryPoint.this),
                                                             email, new MyEncryption().getEncryptedToken(decrypted_password), GroupDataParser.groupData.getSession());
                    String movieCategoryUrl = LinkConfig.getString(
                            EntryPoint.this, LinkConfig.MOVIE_PARENT_CATEGORY_URL);
                    movieCategoryUrl += "?groupId="
                            + GroupDataParser.groupData.getGroupId();

                    new MovieCategoryJsonParser().execute(movieCategoryUrl);*/

                } catch (JSONException e) {
                    Logger.printStackTrace(e);
                    loadingLayout.setVisibility(View.GONE);
                    loginLayout.setVisibility(View.VISIBLE);
                    try {
                        JSONObject value = new JSONObject(result);
                        JSONObject errorObj = value.getJSONObject("error");
                        /* if (errorObj.getInt("is_active") == -4) {*/
                        final CustomDialogManager loginError = new CustomDialogManager(EntryPoint.this, "Login Error", errorObj.getString("error_code") + "\n" + errorObj.getString("message"), CustomDialogManager.ALERT);
                        loginError.build();
                        loginError.dismissDialogOnBackPressed();
                        loginError.setPositiveButton(getString(R.string.btn_retry), new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginError.dismiss();
                            }
                        });
                        loginError.setExtraButton("", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loginError.dismiss();
                                finish();
                            }
                        });
                        loginError.show();
                      /*  } else {
                            MarketAppDetailParser.openApk(EntryPoint.this, MarketAppDetailParser.MyAccountAppPackage);
                        }*/
                    } catch (JSONException je2) {
                        Logger.printStackTrace(je2);
                        CustomDialogManager.ReUsedCustomDialogs.showDataNotFetchedAlert(EntryPoint.this);
                    }
                }
            }
        }
    }

    private void writeAuthTokenToFile(String authToken) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Logger.i("STRING_TO_WRITE", authToken);

            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, LinkConfig.TOKEN_CONFIG_FILE_NAME);

            try {
                FileOutputStream fOut1 = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut1);
                myOutWriter.append(authToken);
                myOutWriter.close();
                fOut1.close();
            } catch (Exception e) {
                Logger.printStackTrace(e);
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VersionCheckEvent event) {
       checkForAccountOrVersion();

    }

    private void checkForAccountOrVersion() {
        if (PackageUtils.isPackageInstalled(EntryPoint.this, MarketAppDetailParser.MyAccountAppPackage)) {
            String url = LinkConfig.LINK_SEVER_APKs + "?macAddress=" + macAddress
                    + "&versionCode=" + BuildConfig.VERSION_CODE
                    + "&versionName=" + BuildConfig.VERSION_NAME
                    + "&packageName=" + BuildConfig.APPLICATION_ID;
            Logger.d("CheckingMarketAppInfoUrl", url);
            new ApkInServer(TODO_VERSION_CHECK,this).execute(url);
        } else {
            String url = LinkConfig.LINK_SEVER_APKs + "?macAddress=" + macAddress
                    + "&packageName=" + MarketAppDetailParser.MyAccountAppPackage;
            new ApkInServer(TODO_DOWNLOAD_ACCOUNT,this).execute(url);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void openSetting() {
        try {
            try {

                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.rk_itvui.settings", "com.rk_itvui.settings.Settings"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                try {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.giec.settings");
                    startActivity(LaunchIntent);
                    finish();
                } catch (Exception c) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }


        } catch (Exception a) {
            startActivity(
                    new Intent(Settings.ACTION_SETTINGS));
            finish();
        }
    }
/*

    private class ParentalLock extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            DownloadUtil getUtc = new DownloadUtil(LinkConfig.getString(EntryPoint.this,LinkConfig.GET_UTC), EntryPoint.this);
            String utc = getUtc.downloadStringContent();
            if (!utc.equals(DownloadUtil.NotOnline)
                    && !utc.equals(DownloadUtil.ServerUnrechable)) {
                DownloadUtil dUtil = new DownloadUtil(params[0] + "&"
                                                              + LinkConfig.getHashCode(utc), EntryPoint.this);

                Logger.d(TAG, params[0] + "&" + LinkConfig.getHashCode(utc));

                return dUtil.downloadStringContent();
            } else
                return utc;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Logger.d(TAG + "parental lock result-->", result);
            if (result.equalsIgnoreCase(DownloadUtil.NotOnline) || result.equalsIgnoreCase(DownloadUtil.ServerUnrechable)) {
                if (RETRY_COUNT < 3) {
                    new MergedJson().execute();
                    RETRY_COUNT++;
                } else {
                    CustomDialogManager.ReUsedCustomDialogs.noInternet(EntryPoint.this);
                    RETRY_COUNT = 0;
                }
            } else {

                ParentalControlParser parser = new ParentalControlParser(
                        EntryPoint.this, result);
                parser.parse();
            }

        }
    }*/
}
