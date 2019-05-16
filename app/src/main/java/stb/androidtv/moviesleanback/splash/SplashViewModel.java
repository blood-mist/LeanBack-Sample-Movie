package stb.androidtv.moviesleanback.splash;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import stb.androidtv.moviesleanback.enitities.GeoAccessInfo;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.LoginResponseWrapper;
import stb.androidtv.moviesleanback.enitities.MacInfo;
import stb.androidtv.moviesleanback.enitities.UserCheckWrapper;
import stb.androidtv.moviesleanback.enitities.VersionResponseWrapper;


public class SplashViewModel extends AndroidViewModel {


    private SplashRepository splashRepository;
    private MediatorLiveData<GeoAccessInfo> geoAccessInfoLiveData;

    private MediatorLiveData<VersionResponseWrapper> appInfoLiveData;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        splashRepository = SplashRepository.getInstance(application);
        geoAccessInfoLiveData = new MediatorLiveData<>();
        appInfoLiveData = new MediatorLiveData<>();
        geoAccessInfoLiveData.setValue(null);
        appInfoLiveData.setValue(null);
    }


    public LiveData<MacInfo> checkIfValidMacAddress(String macAddress) {
        return splashRepository.isMacRegistered(macAddress);

    }


    public LiveData<GeoAccessInfo> checkIfGeoAccessEnabled() {
        return splashRepository.isGeoAccessEnabled();
    }

    public LiveData<VersionResponseWrapper> checkVersion(String macAddress, int versionCode, String versionName, String applicationId) {
        return splashRepository.isNewVersionAvailable(macAddress, versionCode, versionName, applicationId);

    }


    public LiveData<UserCheckWrapper> checkIfUserRegistered(String macAddress) {
        return splashRepository.isUserRegistered(macAddress);
    }


    public LiveData<LoginResponseWrapper> loginFromFile(String userEmail, String userPassword, String macAddress) {
        return splashRepository.getLoginResponse(userEmail, userPassword, macAddress);

    }

    public void deleteloginData() {
        splashRepository.deleteLoginFromDB();
    }

    public void deleteLoginFile() {
        splashRepository.deleteLoginFile();
    }
}
