package stb.androidtv.moviesleanback.splash;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import stb.androidtv.moviesleanback.enitities.AppVersionInfo;
import stb.androidtv.moviesleanback.enitities.GeoAccessInfo;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.LoginError;
import stb.androidtv.moviesleanback.enitities.LoginErrorResponse;
import stb.androidtv.moviesleanback.enitities.LoginInfo;
import stb.androidtv.moviesleanback.enitities.LoginInvalidResponse;
import stb.androidtv.moviesleanback.enitities.LoginResponseWrapper;
import stb.androidtv.moviesleanback.enitities.MacInfo;
import stb.androidtv.moviesleanback.enitities.UserCheckInfo;
import stb.androidtv.moviesleanback.enitities.UserCheckWrapper;
import stb.androidtv.moviesleanback.enitities.UserErrorInfo;
import stb.androidtv.moviesleanback.enitities.VersionErrorResponse;
import stb.androidtv.moviesleanback.enitities.VersionResponseWrapper;
import stb.androidtv.moviesleanback.retroUtils.ApiManager;
import stb.androidtv.moviesleanback.utils.ApiInterface;
import stb.androidtv.moviesleanback.utils.LoginFileUtils;
import stb.androidtv.moviesleanback.utils.MyEncryption;

import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;


public class SplashRepository {
    private static final String IS_JSON_ARRAY = "is_json_array";
    private static final String IS_JSON_OBJECT = "is_json_object";
    private static final String UNEXPECTED_JSON = "json_unexpected";
    private static final String ON_SUCCESS_KEY = "data";
    private static final String KEY_LOGIN_SUCCESS = "login";
    public static final String KEY_CATEGORY = "movies_parent";
    private static final String KEY_MAC_INVALID = "error_code";
    private static SplashRepository sInstance;
    private MediatorLiveData<MacInfo> macInfoMediatorLiveData;
    private MediatorLiveData<GeoAccessInfo> geoAccessLiveData;
    private MediatorLiveData<VersionResponseWrapper> appInfoLiveData;
    private ApiInterface apiInterface;
    private MediatorLiveData<UserCheckWrapper> userCheckLiveData;
    private MediatorLiveData<Login> userCredentialData;
    private MediatorLiveData<LoginResponseWrapper> loginInfoLiveData;
    private Realm realm = Realm.getDefaultInstance();

    SplashRepository(Application application) {
        Retrofit retrofitInstance = ApiManager.getAdapter();
        userCredentialData = new MediatorLiveData<>();
        userCredentialData.setValue(null);
        apiInterface = retrofitInstance.create(ApiInterface.class);
    }

    public static SplashRepository getInstance(final Application application) {
        if (sInstance == null) {
            synchronized (SplashRepository.class) {
                if (sInstance == null) {
                    sInstance = new SplashRepository(application);
                }
            }
        }
        return sInstance;
    }

    public LiveData<MacInfo> isMacRegistered(String macAddress) {
        macInfoMediatorLiveData = new MediatorLiveData<>();
        macInfoMediatorLiveData.setValue(null);
        Observable<Response<MacInfo>> macObserver = apiInterface.checkMacValidation(macAddress);
        macObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<MacInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<MacInfo> macInfoResponse) {
                        if (macInfoResponse.code() == 200) {
                            MacInfo macInfo = macInfoResponse.body();
                            macInfo.setResponseCode(macInfoResponse.code());
                            macInfoMediatorLiveData.postValue(macInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        MacInfo macInfo = new MacInfo();
                        if (e instanceof HttpException || e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                            macInfo.setResponseCode(NO_CONNECTION);
                        } else {
                            macInfo.setResponseCode(0);
                        }
                        macInfo.setMacExists("");
                        macInfo.setMessage(e.getMessage());
                        macInfoMediatorLiveData.postValue(macInfo);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return macInfoMediatorLiveData;

    }

    public LiveData<GeoAccessInfo> isGeoAccessEnabled() {
        geoAccessLiveData = new MediatorLiveData<>();
        geoAccessLiveData.setValue(null);
        Observable<Response<GeoAccessInfo>> geoAccess = apiInterface.checkGeoAccess();
        geoAccess.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<GeoAccessInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<GeoAccessInfo> geoAccessInfoResponse) {
                        if (geoAccessInfoResponse.code() == 200) {
                            GeoAccessInfo geoAccessInfo = geoAccessInfoResponse.body();
                            geoAccessInfo.setResponseCode(geoAccessInfoResponse.code());

                            geoAccessLiveData.postValue(geoAccessInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        GeoAccessInfo geoAccessInfo = new GeoAccessInfo();
                        if (e instanceof HttpException || e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                            geoAccessInfo.setResponseCode(NO_CONNECTION);
                        } else {
                            geoAccessInfo.setResponseCode(0);
                        }
                        geoAccessInfo.getAllow().setAllow("false");
                        geoAccessLiveData.postValue(geoAccessInfo);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return geoAccessLiveData;
    }


    public LiveData<VersionResponseWrapper> isNewVersionAvailable(String macAddress, int versionCode, String versionName, String applicationId) {
        appInfoLiveData = new MediatorLiveData<>();
        appInfoLiveData.setValue(null);
        VersionResponseWrapper versionResponseWrapper = new VersionResponseWrapper();
        Gson gson = new Gson();

        Observable<Response<ResponseBody>> checkVersion = apiInterface.checkForAppVersion(macAddress, versionCode, versionName, applicationId);
        checkVersion.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> versionInfoResponse) {
                        String json = null;
                        try {
                            json = versionInfoResponse.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        switch (isResponseArray(json)) {
                            case IS_JSON_ARRAY:
                                List<AppVersionInfo> appDataList = new ArrayList<>();
                                JSONArray appArray = null;
                                try {
                                    appArray = new JSONArray(json);
                                    for (int i = 0; i < appArray.length(); i++) {
                                        JSONObject appobject = appArray.getJSONObject(i);
                                        String appObjToString = appobject.toString();
                                        AppVersionInfo appData = gson.fromJson(appObjToString, AppVersionInfo.class);
                                        appDataList.add(appData);
                                        versionResponseWrapper.setAppVersionInfo(appDataList);
                                        appInfoLiveData.postValue(versionResponseWrapper);

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case IS_JSON_OBJECT:
                                VersionErrorResponse errorResponse = gson.fromJson(json, VersionErrorResponse.class);
                                versionResponseWrapper.setVersionErrorResponse(errorResponse);
                                appInfoLiveData.postValue(versionResponseWrapper);
                                break;
                            default:
                                onError(new Throwable("ERR_RESPONSE_UNEXPECTED"));
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        VersionErrorResponse versionInfo = new VersionErrorResponse();
                        versionInfo.setMessage(e.getLocalizedMessage());
                        versionInfo.setStatus(401);
                        versionResponseWrapper.setVersionErrorResponse(versionInfo);
                        appInfoLiveData.postValue(versionResponseWrapper);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return appInfoLiveData;
    }

    private String isResponseArray(String json) {
        try {
            new JSONArray(json);
            return IS_JSON_ARRAY;
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                new JSONObject(json);
                return IS_JSON_OBJECT;
            } catch (JSONException e1) {
                e1.printStackTrace();
                return UNEXPECTED_JSON;
            }

        }
    }

    public LiveData<UserCheckWrapper> isUserRegistered(String macAddress) {
        userCheckLiveData = new MediatorLiveData<>();
        userCheckLiveData.setValue(null);
        UserCheckWrapper userCheckWrapper = new UserCheckWrapper();
        Gson gson = new Gson();
        Observable<Response<ResponseBody>> userCheckObserver = apiInterface.checkUserStatus(macAddress);
        userCheckObserver.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> userCheckInfoResponse) {
                        String json = null;
                        try {
                            json = userCheckInfoResponse.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject.has(ON_SUCCESS_KEY)) {
                                UserCheckInfo userCheckInfo = gson.fromJson(json, UserCheckInfo.class);
                                userCheckWrapper.setUserCheckInfo(userCheckInfo);
                            } else {
                                UserErrorInfo userErrorInfo = gson.fromJson(json, UserErrorInfo.class);
                                userCheckWrapper.setUserErrorInfo(userErrorInfo);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        userCheckLiveData.postValue(userCheckWrapper);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //set Extra Code for error Handeling
                        UserErrorInfo userErrorInfo = new UserErrorInfo();
                        if (e instanceof HttpException || e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                            userErrorInfo.setStatus(NO_CONNECTION);
                            userErrorInfo.setMessage(e.getLocalizedMessage());
                        } else {
                            userErrorInfo.setStatus(500);
                            userErrorInfo.setMessage(e.getLocalizedMessage());
                        }
                        userCheckWrapper.setUserErrorInfo(userErrorInfo);
                        userCheckLiveData.postValue(userCheckWrapper);


                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return userCheckLiveData;

    }

    public LiveData<LoginResponseWrapper> getLoginResponse(String userEmail, String userPassword, String macAddress) {
        loginInfoLiveData = new MediatorLiveData<>();
        loginInfoLiveData.setValue(null);
        LoginResponseWrapper loginResponseWrapper = new LoginResponseWrapper();
        Gson gson = new Gson();
        Observable<Response<ResponseBody>> login = apiInterface.signIn(userEmail, userPassword, macAddress);
        login.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> loginInfoResponse) {
                        String json = null;
                        try {
                            json = loginInfoResponse.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject.has(KEY_LOGIN_SUCCESS)) {
                                JSONObject loginobject = jsonObject.getJSONObject(KEY_LOGIN_SUCCESS);
                                if (loginobject.has(KEY_MAC_INVALID)) {
                                    LoginInvalidResponse loginInvalid = gson.fromJson(json, LoginInvalidResponse.class);
                                    loginResponseWrapper.setLoginInvalidResponse(loginInvalid);
                                } else {
                                    LoginInfo loginInfo = gson.fromJson(json, LoginInfo.class);
                                    loginResponseWrapper.setLoginInfo(loginInfo);
                                    realm.executeTransaction(realm -> {
                                        realm.where(Login.class).findAll().deleteAllFromRealm();
                                        realm.insert(loginInfo.getLogin());
                                    });
                                    insertLoginData(loginInfo.getLogin(), userPassword, macAddress);
                                }
                            } else {
                                LoginErrorResponse loginErrorResponse = gson.fromJson(json, LoginErrorResponse.class);
                                loginResponseWrapper.setLoginErrorResponse(loginErrorResponse);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            LoginErrorResponse unknownErrorResponse = new LoginErrorResponse();
                            LoginError errorType = new LoginError();
                            errorType.setErrorCode(1);
                            errorType.setMessage(e.getLocalizedMessage());
                            unknownErrorResponse.setError(errorType);
                            loginResponseWrapper.setLoginErrorResponse(unknownErrorResponse);
                        }
                        loginInfoLiveData.postValue(loginResponseWrapper);
                        } catch (Exception e) {
                            onError(new HttpException(loginInfoResponse));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LoginErrorResponse loginErrorResponse = new LoginErrorResponse();
                        LoginError loginError = new LoginError();
                        if (e instanceof HttpException || e instanceof ConnectException || e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                            loginError.setErrorCode(NO_CONNECTION);
                            loginError.setMessage(e.getLocalizedMessage());
                            loginErrorResponse.setError(loginError);
                        } else {
                            loginError.setErrorCode(500);
                            loginError.setMessage(e.getLocalizedMessage());
                        }
                        loginResponseWrapper.setLoginErrorResponse(loginErrorResponse);
                        loginInfoLiveData.postValue(loginResponseWrapper);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return loginInfoLiveData;
    }

    private void insertLoginData(Login login, String userPassword, String macAddress) {
        login.setUserPassword(userPassword);
        writeLoginDataToFile(login, userPassword, macAddress);

    }

    private void writeLoginDataToFile(Login login, String userPassword, String macAddress) {
        LoginFileUtils.reWriteLoginDetailsToFile(macAddress,
                login.getEmail(), new MyEncryption().getEncryptedToken(userPassword), login.getSession(), String.valueOf(login.getId()));
    }

    public void deleteLoginFile() {
        Completable.fromRunnable(LoginFileUtils::deleteLoginFile).subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteLoginFromDB() {
        RealmResults<Login> loginRealmResults = realm.where(Login.class).findAllAsync();
        if (loginRealmResults.size() > 0)
            realm.executeTransactionAsync(realm -> loginRealmResults.deleteAllFromRealm());
    }

}

