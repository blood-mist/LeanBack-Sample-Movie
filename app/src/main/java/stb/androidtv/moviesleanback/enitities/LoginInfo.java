package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.SerializedName;


public class LoginInfo{
	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	private String responseCode;

	private String errorMessage;

	@SerializedName("login")
	private Login login;

	public void setLogin(Login login){
		this.login = login;
	}

	public Login getLogin(){
		return login;
	}

	@Override
 	public String toString(){
		return 
			"LoginInfo{" + 
			"login = '" + login + '\'' + 
			"}";
		}
}