package stb.androidtv.moviesleanback.enitities;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class ErrorEntity implements Parcelable {

	@SerializedName("error_message")
	private String errorMessage;

	@SerializedName("status")
	private int status;

	public void setErrorMessage(String errorMessage){
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage(){
		return errorMessage;
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
			"ErrorEntity{" + 
			"error_message = '" + errorMessage + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.errorMessage);
		dest.writeInt(this.status);
	}

	public ErrorEntity() {
	}

	protected ErrorEntity(Parcel in) {
		this.errorMessage = in.readString();
		this.status = in.readInt();
	}

	public static final Parcelable.Creator<ErrorEntity> CREATOR = new Parcelable.Creator<ErrorEntity>() {
		@Override
		public ErrorEntity createFromParcel(Parcel source) {
			return new ErrorEntity(source);
		}

		@Override
		public ErrorEntity[] newArray(int size) {
			return new ErrorEntity[size];
		}
	};
}