package maxxtv.movies.stb.Entity;


import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Movie extends RealmObject implements Parcelable {
    @PrimaryKey
    private int movie_id;

    private int is_Imdb;

    public int getIs_Imdb() {
        return is_Imdb;
    }

    public void setIs_Imdb(int is_Imdb) {
        this.is_Imdb = is_Imdb;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    private String imdb_id;

    private String movie_name;

    private String movie_description;

    private int movie_category_id;

    private String movie_url;

    private String preview_url;

    private int is_youtube;

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    private int isFav;

    private String movie_logo;

    private int parental_lock;

    public Movie() {
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getMovie_description() {
        return movie_description;
    }

    public void setMovie_description(String movie_description) {
        this.movie_description = movie_description;
    }

    public int getMovie_category_id() {
        return movie_category_id;
    }

    public void setMovie_category_id(int movie_categoru_id) {
        this.movie_category_id = movie_categoru_id;
    }

    public String getMovie_url() {
        return movie_url;
    }

    public void setMovie_url(String movie_url) {
        this.movie_url = movie_url;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public int getIs_youtube() {
        return is_youtube;
    }

    public void setIs_youtube(int is_youtube) {
        this.is_youtube = is_youtube;
    }

    public String getMovie_logo() {
        return movie_logo;
    }

    public void setMovie_logo(String movie_logo) {
        this.movie_logo = movie_logo;
    }
    public int getParental_lock() {
        return parental_lock;
    }

    public void setParental_lock(int parental_lock) {
        this.parental_lock = parental_lock;
    }

    public String getMovieInString() {
        StringBuilder sb = new StringBuilder();

        sb.append(movie_id).append("\n").append(movie_name).append("\n")
                .append(movie_description).append("\n")
                .append(movie_category_id).append("\n")
                .append(movie_url).append("\n")
                .append(preview_url).append("\n")
                .append(is_youtube).append("\n")
                .append(movie_logo).append("\n")
                .append(is_Imdb).append("\n")
                .append(imdb_id).append("\n")
                .append(isFav).append("\n")
                .append("\n");
        return sb.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movie_id);
        dest.writeInt(this.is_Imdb);
        dest.writeString(this.imdb_id);
        dest.writeString(this.movie_name);
        dest.writeString(this.movie_description);
        dest.writeInt(this.movie_category_id);
        dest.writeString(this.movie_url);
        dest.writeString(this.preview_url);
        dest.writeInt(this.is_youtube);
        dest.writeInt(this.isFav);
        dest.writeString(this.movie_logo);
        dest.writeInt(this.parental_lock);
    }

    protected Movie(Parcel in) {
        this.movie_id = in.readInt();
        this.is_Imdb = in.readInt();
        this.imdb_id = in.readString();
        this.movie_name = in.readString();
        this.movie_description = in.readString();
        this.movie_category_id = in.readInt();
        this.movie_url = in.readString();
        this.preview_url = in.readString();
        this.is_youtube = in.readInt();
        this.isFav = in.readInt();
        this.movie_logo = in.readString();
        this.parental_lock = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
