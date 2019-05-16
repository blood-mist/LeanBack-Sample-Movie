
package stb.androidtv.moviesleanback.enitities;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieDetails {

    @SerializedName("movies_parent")
    @Expose
    private List<MoviesParent> moviesParent = null;
    @SerializedName("topmovies")
    @Expose
    private List<Topmovie> topmovies = null;
    @SerializedName("favorive_movies")
    @Expose
    private List<Object> favoriveMovies = null;
    @SerializedName("status")
    @Expose
    private Integer status;

    public List<MoviesParent> getMoviesParent() {
        return moviesParent;
    }

    public void setMoviesParent(List<MoviesParent> moviesParent) {
        this.moviesParent = moviesParent;
    }

    public List<Topmovie> getTopmovies() {
        return topmovies;
    }

    public void setTopmovies(List<Topmovie> topmovies) {
        this.topmovies = topmovies;
    }

    public List<Object> getFavoriveMovies() {
        return favoriveMovies;
    }

    public void setFavoriveMovies(List<Object> favoriveMovies) {
        this.favoriveMovies = favoriveMovies;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
