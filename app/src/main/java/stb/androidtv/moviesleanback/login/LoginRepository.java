package stb.androidtv.moviesleanback.login;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.LoginError;
import stb.androidtv.moviesleanback.enitities.LoginErrorResponse;
import stb.androidtv.moviesleanback.enitities.LoginInfo;
import stb.androidtv.moviesleanback.enitities.LoginInvalidResponse;
import stb.androidtv.moviesleanback.enitities.LoginResponseWrapper;
import stb.androidtv.moviesleanback.retroUtils.ApiManager;
import stb.androidtv.moviesleanback.utils.ApiInterface;
import stb.androidtv.moviesleanback.utils.LinkConfig;
import stb.androidtv.moviesleanback.utils.LoginFileUtils;
import stb.androidtv.moviesleanback.utils.MyEncryption;
import timber.log.Timber;

import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;

public class LoginRepository {
    private static final String KEY_LOGIN_SUCCESS = "login";
    private static LoginRepository loginInstance;
    private ApiInterface loginApiInterface;
    private MediatorLiveData<LoginResponseWrapper> loginInfoLiveData;
    private MediatorLiveData<Login> loginData;
    private static final String KEY_MAC_INVALID = "error_code";
    private Realm realm;


    private LoginRepository(Application application) {
        realm = Realm.getDefaultInstance();
        Retrofit retrofitInstance = ApiManager.getAdapter();
        loginApiInterface = retrofitInstance.create(ApiInterface.class);
        loginData = new MediatorLiveData<>();

    }

    public static LoginRepository getInstance(final Application application) {
        if (loginInstance == null) {
            synchronized (LoginRepository.class) {
                if (loginInstance == null) {
                    loginInstance = new LoginRepository(application);
                }
            }
        }
        return loginInstance;
    }

    public LiveData<LoginResponseWrapper> signIn(String userEmail, String userPassword, String macAddress) {
        loginInfoLiveData = new MediatorLiveData<>();
        loginInfoLiveData.setValue(null);
        LoginResponseWrapper loginResponseWrapper = new LoginResponseWrapper();
        Gson gson = new Gson();
        Observable<Response<ResponseBody>> geoAccess = loginApiInterface.signIn(userEmail, userPassword, macAddress);
        geoAccess.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<Response<ResponseBody>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<ResponseBody> loginInfoResponse) {
                        String json = null;
                        try {
                            json = loginInfoResponse.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                                    loginInfo.getLogin().setUserPassword(userPassword);
                                    realm.executeTransaction(realm ->{
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

    /**
     * Insert login data from api response into database
     *
     * @param login
     * @param userPassword
     */
    private void insertLoginData(Login login, String userPassword, String macAddress) {
        //TODO insert into database

//        new insertAsyncTask(mLoginDao).execute(login);
        Completable.fromRunnable(() -> {
            writeLoginDataToFile(login, userPassword, macAddress);
            writeAuthTokenToFile(login);
        }).subscribeOn(Schedulers.io()).subscribe();


    }

    private void writeAuthTokenToFile(Login login) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Timber.d("Token:" + login.getToken());

            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, LinkConfig.TOKEN_CONFIG_FILE_NAME);

            try {
                FileOutputStream fOut1 = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut1);
                myOutWriter.append(login.getToken());
                myOutWriter.close();
                fOut1.close();
            } catch (Exception e) {
                Timber.wtf(e);
            }

        }
    }

    private void writeLoginDataToFile(Login login, String userPassword, String macAddress) {
        LoginFileUtils.reWriteLoginDetailsToFile(macAddress,
                login.getEmail(), new MyEncryption().getEncryptedToken(userPassword), login.getSession(), String.valueOf(login.getId()));
    }
}
