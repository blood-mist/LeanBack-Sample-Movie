package stb.androidtv.moviesleanback.enitities;

public class MovieCatWrapper {

    private MovieCategory movieCatResponse;
    private ErrorEntity exception;

    public MovieCategory getMovieCatResponse() {
        return movieCatResponse;
    }

    public void setChannelLinkResponse(MovieCategory movieCatResponse) {
        this.movieCatResponse = movieCatResponse;
    }

    public ErrorEntity getException() {
        return exception;
    }

    public void setException(ErrorEntity exception) {
        this.exception = exception;
    }
}