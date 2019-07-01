package stb.androidtv.moviesleanback.splash;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import stb.androidtv.moviesleanback.BuildConfig;
import stb.androidtv.moviesleanback.MainActivity;
import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.downloads.DownloadFragment;
import stb.androidtv.moviesleanback.downloads.DownloadService;
import stb.androidtv.moviesleanback.enitities.AppVersionInfo;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.LoginError;
import stb.androidtv.moviesleanback.enitities.LoginInfo;
import stb.androidtv.moviesleanback.enitities.LoginResponseWrapper;
import stb.androidtv.moviesleanback.login.LoginActivity;
import stb.androidtv.moviesleanback.unauthorized.UnauthorizedAccess;
import stb.androidtv.moviesleanback.utils.AppConfig;
import stb.androidtv.moviesleanback.utils.CustomDialogManager;
import stb.androidtv.moviesleanback.utils.GetMac;
import stb.androidtv.moviesleanback.utils.LinkConfig;
import stb.androidtv.moviesleanback.utils.LoginFileUtils;
import stb.androidtv.moviesleanback.utils.MyEncryption;
import stb.androidtv.moviesleanback.utils.PackageUtils;
import stb.androidtv.moviesleanback.utils.PermissionUtils;
import timber.log.Timber;

import static stb.androidtv.moviesleanback.utils.LinkConfig.ACCOUNT_PACKAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.DOWNLOAD_FRAGMENT;
import static stb.androidtv.moviesleanback.utils.LinkConfig.DOWNLOAD_ID;
import static stb.androidtv.moviesleanback.utils.LinkConfig.DOWNLOAD_LINK;
import static stb.androidtv.moviesleanback.utils.LinkConfig.DOWNLOAD_NAME;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_HASH;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_USER;
import static stb.androidtv.moviesleanback.utils.LinkConfig.LOGIN_DATA;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ERROR_CODE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ERROR_MESSAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_IP;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;
import static stb.androidtv.moviesleanback.utils.LinkConfig.USER_EMAIL;
import static stb.androidtv.moviesleanback.utils.LinkConfig.USER_NOT_REGISTERED;

public class SplashActivity extends FragmentActivity implements PermissionUtils.PermissionResultCallback, DownloadFragment.OnDismissInteraction {
    private static final String MAC_REGISTERED = "yes";
    private static final String MAC_NOT_REGISTERED = "no";
    private static final String GEO_ACCESS_ENABLED = "true";
    private static final int UPDATE_ID = 1;
    private static final int FORCE_ID = 2;
    private static final int DOWNLOAD_ACCOUNT = 3;
    private static final int CHECK_USER = 4;
    @BindView(R.id.txt_version)
    TextView appVersion;
    private SplashViewModel splashViewModel;
    private String macAddress;
    private AppVersionInfo appVersionInfo;
    private PermissionUtils  permissionutils;
    private String accountDownloadLink = "";
    ArrayList<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        permissionutils = new PermissionUtils(this);
        macAddress = AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(this);
        permissions = new ArrayList<>(
                Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE));
        splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);


    }






    @Override
    protected void onStart() {
        super.onStart();
        appVersion.setText(BuildConfig.VERSION_NAME);
        checkForValidMacAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }




    private void showErrorDialog(int errorCode, String message) {
        CustomDialogManager splashError = new CustomDialogManager(this, CustomDialogManager.ALERT);
        splashError.build();
        splashError.showMacAndVersion();
        splashError.setMessage(String.valueOf(errorCode), message);
        splashError.setExtraButton(v -> {
            splashError.dismiss();
            finish();
        });
        splashError.setNeutralButton(getString(R.string.btn_retry), view -> {
            splashError.dismiss();
            switch (errorCode) {
                case INVALID_HASH:
                    splashViewModel.deleteloginData();
                        checkForValidMacAddress();
                    break;
                case INVALID_USER:
                    splashViewModel.deleteloginData();
                    splashViewModel.deleteLoginFile();
                    checkValidUser();
                    break;
                case USER_NOT_REGISTERED:
                    splashViewModel.deleteloginData();
                    splashViewModel.deleteLoginFile();
                    openAccountApk(ACCOUNT_PACKAGE, "");
                    break;
                case NO_CONNECTION:
                    try {
                        Intent i = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } catch (Exception ignored) {
                    }
            }

        });
        splashError.show();
    }




    @Override
    protected void onPause() {
        super.onPause();

    }

    private void loadChannelActivity() {
     /*   Intent channelLoadIntent = new Intent(this, VideoPlayActivity.class);
        channelLoadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(channelLoadIntent);
        finish();*/
    }

    /**
     * check if the device is registered or not, if no connection found check d for offline
     */
    private void checkForValidMacAddress() {
        splashViewModel.checkIfValidMacAddress(macAddress).observe(this, macInfo -> {
            if (macInfo != null)
                if (macInfo.getResponseCode() != NO_CONNECTION)
                    switch (macInfo.getMacExists()) {
                        case MAC_REGISTERED:
                            checkGeoAccessibility();
                            break;
                        case MAC_NOT_REGISTERED:
                            loadUnauthorized(macInfo.getCode(), macInfo.getMessage(), "N/A");
                            break;
                        default:
                            CustomDialogManager.dataNotFetched(this);
                            break;

                    }
                else
                    CustomDialogManager.ReUsedCustomDialogs.noInternet(this);


        });
    }

    private void loadUnauthorized(String error_code, String error_message, String ip) {
        Intent unauthorizedIntent = new Intent(this, UnauthorizedAccess.class);
        unauthorizedIntent.putExtra(MOVIE_ERROR_CODE, error_code);
        unauthorizedIntent.putExtra(MOVIE_ERROR_MESSAGE, error_message);
        unauthorizedIntent.putExtra(MOVIE_IP, ip);
        unauthorizedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(unauthorizedIntent);
        finish();
    }

    private void checkGeoAccessibility() {
        splashViewModel.checkIfGeoAccessEnabled().observe(this, geoAccessInfo -> {
            if (geoAccessInfo != null) {
                if (geoAccessInfo.getAllow().getAllow().equalsIgnoreCase(GEO_ACCESS_ENABLED))
                    initVersionCheck();
                else
                    loadUnauthorized(geoAccessInfo.getAllow().getCode(), geoAccessInfo.getError(), geoAccessInfo.getAllow().getIp());
            }
        });
    }

    private void initVersionCheck() {
        splashViewModel.checkVersion(macAddress, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME, BuildConfig.APPLICATION_ID).observe(this, versionResponseWrapper -> {
            if (versionResponseWrapper != null) {
                if (versionResponseWrapper.getAppVersionInfo() != null && !versionResponseWrapper.getAppVersionInfo().isEmpty())
                    for (AppVersionInfo appVersionInfo : versionResponseWrapper.getAppVersionInfo()) {
                        if (appVersionInfo.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
                            compareVersion(appVersionInfo);
                        }
                        if (appVersionInfo.getPackageName().equalsIgnoreCase(ACCOUNT_PACKAGE)) {
                            accountDownloadLink = appVersionInfo.getApkDownloadLink();
                        }
                    }

                else {
                    Toast.makeText(SplashActivity.this, versionResponseWrapper.getVersionErrorResponse().getMessage(), Toast.LENGTH_LONG).show();
                    permissionutils.check_permission(permissions, getString(R.string.request_permissions), CHECK_USER);
                }
            }
        });
    }

    private void compareVersion(AppVersionInfo appVersionInfo) {
        this.appVersionInfo = appVersionInfo;
        if (appVersionInfo.getUpdate()) {
            permissionutils.check_permission(permissions, getString(R.string.request_permissions), UPDATE_ID);
        } else {
            permissionutils.check_permission(permissions, getString(R.string.request_permissions), CHECK_USER);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionutils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void downloadApk(String apkDownloadLink, String appName, String message, int statusId) {
        if(statusId==FORCE_ID) {
            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(DOWNLOAD_LINK, apkDownloadLink);
            intent.putExtra(DOWNLOAD_NAME, appName);
            intent.putExtra(DOWNLOAD_ID, statusId);
            startService(intent);
        }
        Fragment downloadFragment=DownloadFragment.newInstance(getString(R.string.update), message, statusId, apkDownloadLink);
        getSupportFragmentManager().beginTransaction().replace(R.id.splash_container,downloadFragment , DOWNLOAD_FRAGMENT).commit();


    }

    @Override
    public void PermissionGranted(int request_code) {
        switch (request_code) {
            case UPDATE_ID:
                apkVersionCheck();
                break;
            case CHECK_USER:
                checkValidUser();
                break;
        }
    }

    private void apkVersionCheck() {
        switch (appVersionInfo.getUpdateType()) {
            case "force":
                downloadApk(appVersionInfo.getApkDownloadLink(), getString(R.string.app_name), "Connecting...", FORCE_ID);
                break;
            case "normal":
                downloadApk(appVersionInfo.getApkDownloadLink(), getString(R.string.app_name), getString(R.string.msg_update), UPDATE_ID);
                break;
            default:
                checkValidUser();
                break;
        }
    }

    private void checkValidUser() {
        splashViewModel.checkIfUserRegistered(macAddress).observe(this, userCheckWrapper -> {
                    if (userCheckWrapper != null) {
                        if (userCheckWrapper.getUserCheckInfo() != null) {
                            if ((userCheckWrapper.getUserCheckInfo().getData().getActivationStatus() == 1 && userCheckWrapper.getUserCheckInfo().getData().getIsActive() == 1)) {
                                if (LoginFileUtils.checkIfFileExists(LinkConfig.LOGIN_FILE_NAME)) {
                                    proceedToLoginViaFile();
                                } else {
                                    showLogin(userCheckWrapper.getUserCheckInfo().getData().getUserName());
                                }
                            }
                        } else {
                            if (userCheckWrapper.getUserErrorInfo().getStatus() == 401 || userCheckWrapper.getUserErrorInfo().getStatus() == 402)
                            {
                                openAccountApk(ACCOUNT_PACKAGE, accountDownloadLink);
                            } else
                                loadUnauthorized(String.valueOf(userCheckWrapper.getUserErrorInfo().getStatus()), userCheckWrapper.getUserErrorInfo().getMessage(), "N/A");

                        }
                    }
                }

        );
    }

    private void proceedToLoginViaFile() {
        if (LoginFileUtils.readFromFile(AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(this))) {
            try {
                String encrypted_password = LoginFileUtils.getUserPassword();

                String userEmail = LoginFileUtils.getUserEmail();
                Timber.d("File:", encrypted_password + "");

                MyEncryption sUtils = new MyEncryption();
                Timber.d("Decrypting " + encrypted_password);
                String decrypted_password = sUtils.getDecryptedToken(encrypted_password);
                Timber.d("CheckingPassword", decrypted_password);
                LiveData<LoginResponseWrapper> loginfildeData = splashViewModel.loginFromFile(userEmail, decrypted_password, macAddress);
//                LiveData<LoginResponseWrapper> loginfildeData = splashViewModel.loginFromFile("demoiptv12@nitv.com","123456", macAddress);
                loginfildeData.observe(this, new Observer<LoginResponseWrapper>() {
                    @Override
                    public void onChanged(@Nullable LoginResponseWrapper loginResponseWrapper) {
                        if (loginResponseWrapper != null) {
                            if (loginResponseWrapper.getLoginInfo() != null) {
                                loadMovieCategoryActivity(loginResponseWrapper.getLoginInfo());
                            //Login Completed do ,send to annother activity or fetch movie dtaa first and send to another activity// TODO: 11/15/2018
                            } else if (loginResponseWrapper.getLoginInvalidResponse() != null) {
                                if (loginResponseWrapper.getLoginInvalidResponse().getLoginInvalidData().getErrorCode().equals("404")) {
                                    SplashActivity.this.loadUnauthorized("404", SplashActivity.this.getString(R.string.mac_not_registered), "N/A");
                                } else {
                                    SplashActivity.this.showErrorDialog(Integer.parseInt(loginResponseWrapper.getLoginInvalidResponse().getLoginInvalidData().getErrorCode()), loginResponseWrapper.getLoginInvalidResponse().getLoginInvalidData().getMessage());
                                }

                            } else {
                                SplashActivity.this.showLoginErrorDialog(loginResponseWrapper.getLoginErrorResponse().getError(), userEmail);
                                LoginFileUtils.deleteLoginFile();
                            }
                            loginfildeData.removeObserver(this);

                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.err_autologin), Toast.LENGTH_LONG).show();
                LoginFileUtils.deleteLoginFile();
                checkForValidMacAddress();
            }
        } else {
            LoginFileUtils.deleteLoginFile();
            checkForValidMacAddress();
        }
    }
    private void loadMovieCategoryActivity(LoginInfo loginInfo) {
        Intent channelLoadIntent = new Intent(this, MainActivity.class);
        channelLoadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(channelLoadIntent);
        finish();
    }
    private void showLoginErrorDialog(LoginError error, String userEmail) {
        CustomDialogManager loginError = new CustomDialogManager(this, CustomDialogManager.ALERT);
        loginError.build();
        loginError.showMacAndVersion();
        loginError.setMessage(String.valueOf(error.getErrorCode()), error.getMessage());
        loginError.setExtraButton(v -> {
            loginError.dismiss();
            finish();
        });
        loginError.setNeutralButton(getString(R.string.btn_retry), view -> {
            loginError.dismiss();
            showLogin(userEmail);
        });
        loginError.show();
    }

    private void openAccountApk(String accountPackage, String accountDownloadLink) {
        if (PackageUtils.isPackageInstalled(this, accountPackage)) {
            Intent openApk = getPackageManager()
                    .getLaunchIntentForPackage(accountPackage);
            startActivity(openApk);
            finish();
        } else {
            downloadApk(accountDownloadLink, getString(R.string.myaccount), getString(R.string.download_myaccount), UPDATE_ID);
        }
    }

    private void showLogin(String userEmail) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.putExtra(USER_EMAIL, userEmail);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> pending_permissions) {
        permissionutils.check_permission(pending_permissions, getString(R.string.request_permissions), request_code);

    }

    @Override
    public void PermissionDenied(int request_code) {
        finish();
    }

    @Override
    public void NeverAskAgain(int request_code) {
        switch (request_code) {
            case UPDATE_ID:
                apkVersionCheck();
                break;
            case CHECK_USER:
                checkValidUser();
                break;

        }

    }

    @Override
    public void onDismissBtnClicked() {
        checkValidUser();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
