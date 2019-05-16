
package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topmovie {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("movie_category_id")
    @Expose
    private String movieCategoryId;
    @SerializedName("movie_url")
    @Expose
    private String movieUrl;
    @SerializedName("movie_server_type")
    @Expose
    private String movieServerType;
    @SerializedName("imdbID")
    @Expose
    private Integer imdbID;
    @SerializedName("movie_id")
    @Expose
    private Integer movieId;
    @SerializedName("feature")
    @Expose
    private String feature;
    @SerializedName("is_youtube")
    @Expose
    private String isYoutube;
    @SerializedName("isFav")
    @Expose
    private String isFav;
    @SerializedName("released_date")
    @Expose
    private String releasedDate;
    @SerializedName("mobile_url")
    @Expose
    private String mobileUrl;
    @SerializedName("mobile_ad_url")
    @Expose
    private String mobileAdUrl;
    @SerializedName("preview_url")
    @Expose
    private String previewUrl;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("parental_lock")
    @Expose
    private String parentalLock;
    @SerializedName("mobile_server_type")
    @Expose
    private String mobileServerType;
    @SerializedName("mobile_ad_server_type")
    @Expose
    private String mobileAdServerType;
    @SerializedName("preview_server_type")
    @Expose
    private String previewServerType;
    @SerializedName("movie_logo")
    @Expose
    private String movieLogo;
    @SerializedName("movie_status")
    @Expose
    private String movieStatus;
    @SerializedName("movie_price")
    @Expose
    private String moviePrice;
    @SerializedName("is_content_allow")
    @Expose
    private String isContentAllow;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMovieCategoryId() {
        return movieCategoryId;
    }

    public void setMovieCategoryId(String movieCategoryId) {
        this.movieCategoryId = movieCategoryId;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public String getMovieServerType() {
        return movieServerType;
    }

    public void setMovieServerType(String movieServerType) {
        this.movieServerType = movieServerType;
    }

    public Integer getImdbID() {
        return imdbID;
    }

    public void setImdbID(Integer imdbID) {
        this.imdbID = imdbID;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getIsYoutube() {
        return isYoutube;
    }

    public void setIsYoutube(String isYoutube) {
        this.isYoutube = isYoutube;
    }

    public String getIsFav() {
        return isFav;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getMobileAdUrl() {
        return mobileAdUrl;
    }

    public void setMobileAdUrl(String mobileAdUrl) {
        this.mobileAdUrl = mobileAdUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getParentalLock() {
        return parentalLock;
    }

    public void setParentalLock(String parentalLock) {
        this.parentalLock = parentalLock;
    }

    public String getMobileServerType() {
        return mobileServerType;
    }

    public void setMobileServerType(String mobileServerType) {
        this.mobileServerType = mobileServerType;
    }

    public String getMobileAdServerType() {
        return mobileAdServerType;
    }

    public void setMobileAdServerType(String mobileAdServerType) {
        this.mobileAdServerType = mobileAdServerType;
    }

    public String getPreviewServerType() {
        return previewServerType;
    }

    public void setPreviewServerType(String previewServerType) {
        this.previewServerType = previewServerType;
    }

    public String getMovieLogo() {
        return movieLogo;
    }

    public void setMovieLogo(String movieLogo) {
        this.movieLogo = movieLogo;
    }

    public String getMovieStatus() {
        return movieStatus;
    }

    public void setMovieStatus(String movieStatus) {
        this.movieStatus = movieStatus;
    }

    public String getMoviePrice() {
        return moviePrice;
    }

    public void setMoviePrice(String moviePrice) {
        this.moviePrice = moviePrice;
    }

    public String getIsContentAllow() {
        return isContentAllow;
    }

    public void setIsContentAllow(String isContentAllow) {
        this.isContentAllow = isContentAllow;
    }

}
