package stb.androidtv.moviesleanback.enitities;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieLinkWrapper implements Parcelable {
    private MovieLinkResponse movieLinkResponse;
    private ErrorEntity exception;

    public MovieLinkResponse getMovieLinkResponse() {
        return movieLinkResponse;
    }

    public void setMovieLinkResponse(MovieLinkResponse movieLinkResponse) {
        this.movieLinkResponse = movieLinkResponse;
    }

    public ErrorEntity getException() {
        return exception;
    }

    public void setException(ErrorEntity exception) {
        this.exception = exception;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.movieLinkResponse, flags);
        dest.writeParcelable(this.exception, flags);
    }

    public MovieLinkWrapper() {
    }

    protected MovieLinkWrapper(Parcel in) {
        this.movieLinkResponse = in.readParcelable(MovieLinkResponse.class.getClassLoader());
        this.exception = in.readParcelable(ErrorEntity.class.getClassLoader());
    }

    public static final Parcelable.Creator<MovieLinkWrapper> CREATOR = new Parcelable.Creator<MovieLinkWrapper>() {
        @Override
        public MovieLinkWrapper createFromParcel(Parcel source) {
            return new MovieLinkWrapper(source);
        }

        @Override
        public MovieLinkWrapper[] newArray(int size) {
            return new MovieLinkWrapper[size];
        }
    };
}
