package stb.androidtv.moviesleanback.enitities;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class MovieLinkResponse implements Parcelable {

	@SerializedName("movies")
	private Movies movies;

	@SerializedName("mgs")
	private String mgs;

	@SerializedName("status")
	private int status;

	public void setMovies(Movies movies){
		this.movies = movies;
	}

	public Movies getMovies(){
		return movies;
	}

	public void setMgs(String mgs){
		this.mgs = mgs;
	}

	public String getMgs(){
		return mgs;
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
			"MovieLinkResponse{" + 
			"movies = '" + movies + '\'' + 
			",mgs = '" + mgs + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.movies, flags);
		dest.writeString(this.mgs);
		dest.writeInt(this.status);
	}

	public MovieLinkResponse() {
	}

	protected MovieLinkResponse(Parcel in) {
		this.movies = in.readParcelable(Movies.class.getClassLoader());
		this.mgs = in.readString();
		this.status = in.readInt();
	}

	public static final Parcelable.Creator<MovieLinkResponse> CREATOR = new Parcelable.Creator<MovieLinkResponse>() {
		@Override
		public MovieLinkResponse createFromParcel(Parcel source) {
			return new MovieLinkResponse(source);
		}

		@Override
		public MovieLinkResponse[] newArray(int size) {
			return new MovieLinkResponse[size];
		}
	};
}