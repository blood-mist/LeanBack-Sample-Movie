package stb.androidtv.moviesleanback.enitities;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Movies implements Parcelable {

	@SerializedName("servertype")
	private int servertype;

	@SerializedName("link")
	private String link;

	public void setServertype(int servertype){
		this.servertype = servertype;
	}

	public int getServertype(){
		return servertype;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	@Override
 	public String toString(){
		return 
			"Movies{" + 
			"servertype = '" + servertype + '\'' + 
			",link = '" + link + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.servertype);
		dest.writeString(this.link);
	}

	public Movies() {
	}

	protected Movies(Parcel in) {
		this.servertype = in.readInt();
		this.link = in.readString();
	}

	public static final Parcelable.Creator<Movies> CREATOR = new Parcelable.Creator<Movies>() {
		@Override
		public Movies createFromParcel(Parcel source) {
			return new Movies(source);
		}

		@Override
		public Movies[] newArray(int size) {
			return new Movies[size];
		}
	};
}