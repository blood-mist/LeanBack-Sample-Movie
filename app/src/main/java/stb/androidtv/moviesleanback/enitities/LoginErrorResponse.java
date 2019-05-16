package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.SerializedName;


public class LoginErrorResponse{

	@SerializedName("error")
	private LoginError error;

	public void setError(LoginError error){
		this.error = error;
	}

	public LoginError getError(){
		return error;
	}

	@Override
 	public String toString(){
		return 
			"LoginErrorResponse{" + 
			"error = '" + error + '\'' + 
			"}";
		}
}