package maxxtv.movies.stb.Entity;

/**
 * Created by ACER on 6/13/2017.
 */

public class AddDataToFav {
    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    private int movie_id;

    public AddDataToFav(int movie_id) {
        this.movie_id = movie_id;
    }
}
