package stb.androidtv.moviesleanback.enitities;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class MovieCategory{

	@SerializedName("favorive_movies")
	private List<FavoriveMoviesItem> favoriveMovies;

	@SerializedName("movies_parent")
	private List<MoviesParentItem> moviesParent;

	@SerializedName("topmovies")
	private List<TopmoviesItem> topmovies;

	@SerializedName("status")
	private int status;

	public void setFavoriveMovies(List<FavoriveMoviesItem> favoriveMovies){
		this.favoriveMovies = favoriveMovies;
	}

	public List<FavoriveMoviesItem> getFavoriveMovies(){
		return favoriveMovies;
	}

	public void setMoviesParent(List<MoviesParentItem> moviesParent){
		this.moviesParent = moviesParent;
	}

	public List<MoviesParentItem> getMoviesParent(){
		return moviesParent;
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
			"MovieCategory{" + 
			"favorive_movies = '" + favoriveMovies + '\'' + 
			",movies_parent = '" + moviesParent + '\'' + 
			",topmovies = '" + topmovies + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}