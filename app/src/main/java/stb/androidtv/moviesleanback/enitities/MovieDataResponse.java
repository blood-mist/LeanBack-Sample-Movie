package stb.androidtv.moviesleanback.enitities;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class MovieDataResponse{

	@SerializedName("movie")
	private List<MovieItem> movie;

	@SerializedName("topmovies")
	private List<TopmoviesItem> topmovies;

	@SerializedName("status")
	private int status;

	public void setMovie(List<MovieItem> movie){
		this.movie = movie;
	}

	public List<MovieItem> getMovie(){
		return movie;
	}

	public void setTopmovies(List<TopmoviesItem> topmovies){
		this.topmovies = topmovies;
	}

	public List<TopmoviesItem> getTopmovies(){
		return topmovies;
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
			"MovieDataResponse{" + 
			"movie = '" + movie + '\'' + 
			",topmovies = '" + topmovies + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}