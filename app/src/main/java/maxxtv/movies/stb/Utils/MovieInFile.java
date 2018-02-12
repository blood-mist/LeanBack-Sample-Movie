package maxxtv.movies.stb.Utils;


import maxxtv.movies.stb.Entity.Movie;

public class MovieInFile {
	private Movie movie;
	private int moviesaveduration;
	public MovieInFile(Movie movie, int moviesaveduration) {
		this.movie = movie;
		this.moviesaveduration=moviesaveduration;
	}
	public Movie getmovie(){
		return this.movie;
	}
	public int getMovieSaveDuration(){
		return moviesaveduration;
	}
	
}
