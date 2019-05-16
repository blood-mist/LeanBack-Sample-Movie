package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.SerializedName;


public class LoginInvalidResponse {

	public LoginInvalidData getLoginInvalidData() {
		return loginInvalidData;
	}

	public void setLoginInvalidData(LoginInvalidData loginInvalidData) {
		this.loginInvalidData = loginInvalidData;
	}

	@SerializedName("login")
	private LoginInvalidData loginInvalidData;



	@Override
 	public String toString(){
		return 
			"LoginInvalidResponse{" +
			"login = '" + loginInvalidData + '\'' +
			"}";
		}
}