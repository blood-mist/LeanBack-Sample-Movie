package stb.androidtv.moviesleanback.enitities;

public class MovieDataWrapper {
    private MovieDataResponse movieDataResponse;
    private ErrorEntity exception;

    public MovieDataResponse getMovieDataResponse() {
        return movieDataResponse;
    }

    public void setMovieDataResponse(MovieDataResponse movieDataResponse) {
        this.movieDataResponse = movieDataResponse;
    }

    public ErrorEntity getException() {
        return exception;
    }

    public void setException(ErrorEntity exception) {
        this.exception = exception;
    }
}
