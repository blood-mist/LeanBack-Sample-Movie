package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.SerializedName;


public class UserCheckInfo{

	@SerializedName("data")
	private Data data;

	@SerializedName("status")
	private int status;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
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
			"UserCheckInfo{" + 
			"data = '" + data + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}