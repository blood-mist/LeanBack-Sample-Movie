package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.SerializedName;



public class TimeStampEntity{

	@SerializedName("utc")
	private int utc;


	@SerializedName("status")
	private int status;

	public void setUtc(int utc){
		this.utc = utc;
	}

	public int getUtc(){
		return utc;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"TimeStampEntity{" + 
			"utc = '" + utc + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}