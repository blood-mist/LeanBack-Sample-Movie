package stb.androidtv.moviesleanback.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.LoginResponseWrapper;


public class LoginViewModel extends AndroidViewModel {
    private  LoginRepository loginRepository;
    private MediatorLiveData<Login> loginData;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepository=LoginRepository.getInstance(application);
    }
    public LiveData<LoginResponseWrapper> performLogin(String userEmail, String userPassword, String macAddress) {
       return loginRepository.signIn(userEmail,userPassword,macAddress);

    }




}
