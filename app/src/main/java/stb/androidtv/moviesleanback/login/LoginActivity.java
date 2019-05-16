package stb.androidtv.moviesleanback.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import stb.androidtv.moviesleanback.MainActivity;
import stb.androidtv.moviesleanback.R;
import stb.androidtv.moviesleanback.enitities.Login;
import stb.androidtv.moviesleanback.enitities.LoginError;
import stb.androidtv.moviesleanback.enitities.LoginResponseWrapper;
import stb.androidtv.moviesleanback.splash.SplashActivity;
import stb.androidtv.moviesleanback.unauthorized.UnauthorizedAccess;
import stb.androidtv.moviesleanback.utils.AppConfig;
import stb.androidtv.moviesleanback.utils.CustomDialogManager;
import stb.androidtv.moviesleanback.utils.GetMac;

import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_HASH;
import static stb.androidtv.moviesleanback.utils.LinkConfig.INVALID_USER;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ERROR_CODE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_ERROR_MESSAGE;
import static stb.androidtv.moviesleanback.utils.LinkConfig.MOVIE_IP;
import static stb.androidtv.moviesleanback.utils.LinkConfig.NO_CONNECTION;
import static stb.androidtv.moviesleanback.utils.LinkConfig.USER_EMAIL;


public class LoginActivity extends FragmentActivity {


    @BindView(R.id.userNamePS)
    TextView txtUsername;

    @BindView(R.id.passWordPS)
    EditText txtPasssword;

    @BindView(R.id.login_loader)
    AVLoadingIndicatorView loginLoader;

    @BindView(R.id.loginButtonPS)
    Button login;

    private LoginViewModel loginViewModel;
    private String username;
    private String macAddress;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        realm = Realm.getDefaultInstance();
        ButterKnife.bind(this);
        Bundle loginBundle = getIntent().getExtras();
        if (loginBundle != null) {
            username = loginBundle.getString(USER_EMAIL, "");
        } else
            username = Objects.requireNonNull(realm.where(Login.class).findFirst()).getEmail();

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        macAddress = AppConfig.isDevelopment() ? AppConfig.getMacAddress() : GetMac.getMac(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        txtUsername.setText(username);
        login.setOnClickListener(view -> initLogin());

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initLogin() {
        loginLoader.smoothToShow();
        LiveData<LoginResponseWrapper> loginResponseData = loginViewModel.performLogin(username, txtPasssword.getText().toString(), macAddress);
        loginResponseData.observe(this, new Observer<LoginResponseWrapper>() {
            @Override
            public void onChanged(@Nullable LoginResponseWrapper loginResponseWrapper) {
                if (loginResponseWrapper != null) {
                    if (loginResponseWrapper.getLoginInfo() != null) {
                        loadMovieCategoryActivity();

                    } else if (loginResponseWrapper.getLoginInvalidResponse() != null) {
                        if (loginResponseWrapper.getLoginInvalidResponse().getLoginInvalidData().getErrorCode().equals("404")) {
                            LoginActivity.this.loadUnauthorized(LoginActivity.this.getString(R.string.mac_not_registered), "N/A");
                        } else {
                            Toast.makeText(LoginActivity.this, loginResponseWrapper.getLoginInvalidResponse().getLoginInvalidData().getMessage(), Toast.LENGTH_LONG).show();
                            loginLoader.smoothToHide();
                            txtPasssword.requestFocus();
                        }

                    } else {
                        LoginActivity.this.showLoginErrorDialog(loginResponseWrapper.getLoginErrorResponse().getError());
                    }
                    loginResponseData.removeObserver(this);


                }
            }
        });

    }

    private void loadUnauthorized(String error_message, String ip) {
        Intent unauthorizedIntent = new Intent(this, UnauthorizedAccess.class);
        unauthorizedIntent.putExtra(MOVIE_ERROR_CODE, "404");
        unauthorizedIntent.putExtra(MOVIE_ERROR_MESSAGE, error_message);
        unauthorizedIntent.putExtra(MOVIE_IP, ip);
        unauthorizedIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(unauthorizedIntent);
        finish();
    }

    private void showLoginErrorDialog(LoginError error) {
        loginLoader.smoothToHide();
        CustomDialogManager.loginErrorDialog(this, error);
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
                    Login login = realm.where(Login.class).findFirst();
                    showLogin(login.getEmail());
                    break;
                case INVALID_USER:
                    showSplash();
                    break;
                case NO_CONNECTION:
                    Intent intent = new Intent(this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

            }

        });
        splashError.show();
    }

    private void showSplash() {
        Intent splashIntent = new Intent(this, SplashActivity.class);
        splashIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(splashIntent);
        finish();

    }

    private void showLogin(String email) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.putExtra(USER_EMAIL, email);
        startActivity(loginIntent);
        finish();

    }

    private void loadMovieCategoryActivity() {
        Intent channelLoadIntent = new Intent(this, MainActivity.class);
        channelLoadIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(channelLoadIntent);
        loginLoader.smoothToHide();
    }
}
