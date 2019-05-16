package stb.androidtv.moviesleanback.enitities;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class MoviesItem implements Parcelable {

	@SerializedName("preview_server_type")
	private int previewServerType;

	@SerializedName("is_content_allow")
	private int isContentAllow;

	@SerializedName("imdbID")
	private int imdbID;

	@SerializedName("mobile_ad_url")
	private String mobileAdUrl;

	@SerializedName("mobile_url")
	private String mobileUrl;

	@SerializedName("description")
	private String description;

	@SerializedName("device_type")
	private String deviceType;

	@SerializedName("movie_price")
	private String moviePrice;

	@SerializedName("mobile_server_type")
	private int mobileServerType;

	@SerializedName("movie_logo")
	private String movieLogo;

	@SerializedName("movie_id")
	private int movieId;

	@SerializedName("parental_lock")
	private int parentalLock;

	@SerializedName("movie_status")
	private int movieStatus;

	@SerializedName("isFav")
	private int isFav;

	@SerializedName("released_date")
	private String releasedDate;

	@SerializedName("movie_url")
	private String movieUrl;

	@SerializedName("movie_server_type")
	private int movieServerType;

	@SerializedName("feature")
	private String feature;

	@SerializedName("preview_url")
	private String previewUrl;

	@SerializedName("name")
	private String name;

	@SerializedName("movie_category_id")
	private int movieCategoryId;

	@SerializedName("id")
	private int id;

	@SerializedName("mobile_ad_server_type")
	private int mobileAdServerType;

	@SerializedName("is_youtube")
	private int isYoutube;

	public void setPreviewServerType(int previewServerType){
		this.previewServerType = previewServerType;
	}

	public int getPreviewServerType(){
		return previewServerType;
	}

	public void setIsContentAllow(int isContentAllow){
		this.isContentAllow = isContentAllow;
	}

	public int getIsContentAllow(){
		return isContentAllow;
	}

	public void setImdbID(int imdbID){
		this.imdbID = imdbID;
	}

	public int getImdbID(){
		return imdbID;
	}

	public void setMobileAdUrl(String mobileAdUrl){
		this.mobileAdUrl = mobileAdUrl;
	}

	public Object getMobileAdUrl(){
		return mobileAdUrl;
	}

	public void setMobileUrl(String mobileUrl){
		this.mobileUrl = mobileUrl;
	}

	public Object getMobileUrl(){
		return mobileUrl;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setDeviceType(String deviceType){
		this.deviceType = deviceType;
	}

	public String getDeviceType(){
		return deviceType;
	}

	public void setMoviePrice(String moviePrice){
		this.moviePrice = moviePrice;
	}

	public Object getMoviePrice(){
		return moviePrice;
	}

	public void setMobileServerType(int mobileServerType){
		this.mobileServerType = mobileServerType;
	}

	public int getMobileServerType(){
		return mobileServerType;
	}

	public void setMovieLogo(String movieLogo){
		this.movieLogo = movieLogo;
	}

	public String getMovieLogo(){
		return movieLogo;
	}

	public void setMovieId(int movieId){
		this.movieId = movieId;
	}

	public int getMovieId(){
		return movieId;
	}

	public void setParentalLock(int parentalLock){
		this.parentalLock = parentalLock;
	}

	public int getParentalLock(){
		return parentalLock;
	}

	public void setMovieStatus(int movieStatus){
		this.movieStatus = movieStatus;
	}

	public int getMovieStatus(){
		return movieStatus;
	}

	public void setIsFav(int isFav){
		this.isFav = isFav;
	}

	public int getIsFav(){
		return isFav;
	}

	public void setReleasedDate(String releasedDate){
		this.releasedDate = releasedDate;
	}

	public Object getReleasedDate(){
		return releasedDate;
	}

	public void setMovieUrl(String movieUrl){
		this.movieUrl = movieUrl;
	}

	public String getMovieUrl(){
		return movieUrl;
	}

	public void setMovieServerType(int movieServerType){
		this.movieServerType = movieServerType;
	}

	public int getMovieServerType(){
		return movieServerType;
	}

	public void setFeature(String feature){
		this.feature = feature;
	}

	public Object getFeature(){
		return feature;
	}

	public void setPreviewUrl(String previewUrl){
		this.previewUrl = previewUrl;
	}

	public Object getPreviewUrl(){
		return previewUrl;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setMovieCategoryId(int movieCategoryId){
		this.movieCategoryId = movieCategoryId;
	}

	public int getMovieCategoryId(){
		return movieCategoryId;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setMobileAdServerType(int mobileAdServerType){
		this.mobileAdServerType = mobileAdServerType;
	}

	public int getMobileAdServerType(){
		return mobileAdServerType;
	}

	public void setIsYoutube(int isYoutube){
		this.isYoutube = isYoutube;
	}

	public int getIsYoutube(){
		return isYoutube;
	}

	@Override
 	public String toString(){
		return 
			"MoviesItem{" + 
			"preview_server_type = '" + previewServerType + '\'' + 
			",is_content_allow = '" + isContentAllow + '\'' + 
			",imdbID = '" + imdbID + '\'' + 
			",mobile_ad_url = '" + mobileAdUrl + '\'' + 
			",mobile_url = '" + mobileUrl + '\'' + 
			",description = '" + description + '\'' + 
			",device_type = '" + deviceType + '\'' + 
			",movie_price = '" + moviePrice + '\'' + 
			",mobile_server_type = '" + mobileServerType + '\'' + 
			",movie_logo = '" + movieLogo + '\'' + 
			",movie_id = '" + movieId + '\'' + 
			",parental_lock = '" + parentalLock + '\'' + 
			",movie_status = '" + movieStatus + '\'' + 
			",isFav = '" + isFav + '\'' + 
			",released_date = '" + releasedDate + '\'' + 
			",movie_url = '" + movieUrl + '\'' + 
			",movie_server_type = '" + movieServerType + '\'' + 
			",feature = '" + feature + '\'' + 
			",preview_url = '" + previewUrl + '\'' + 
			",name = '" + name + '\'' + 
			",movie_category_id = '" + movieCategoryId + '\'' + 
			",id = '" + id + '\'' + 
			",mobile_ad_server_type = '" + mobileAdServerType + '\'' + 
			",is_youtube = '" + isYoutube + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.previewServerType);
		dest.writeInt(this.isContentAllow);
		dest.writeInt(this.imdbID);
		dest.writeString(this.mobileAdUrl);
		dest.writeString(this.mobileUrl);
		dest.writeString(this.description);
		dest.writeString(this.deviceType);
		dest.writeString(this.moviePrice);
		dest.writeInt(this.mobileServerType);
		dest.writeString(this.movieLogo);
		dest.writeInt(this.movieId);
		dest.writeInt(this.parentalLock);
		dest.writeInt(this.movieStatus);
		dest.writeInt(this.isFav);
		dest.writeString(this.releasedDate);
		dest.writeString(this.movieUrl);
		dest.writeInt(this.movieServerType);
		dest.writeString(this.feature);
		dest.writeString(this.previewUrl);
		dest.writeString(this.name);
		dest.writeInt(this.movieCategoryId);
		dest.writeInt(this.id);
		dest.writeInt(this.mobileAdServerType);
		dest.writeInt(this.isYoutube);
	}

	public MoviesItem() {
	}

	protected MoviesItem(Parcel in) {
		this.previewServerType = in.readInt();
		this.isContentAllow = in.readInt();
		this.imdbID = in.readInt();
		this.mobileAdUrl = in.readString();
		this.mobileUrl = in.readString();
		this.description = in.readString();
		this.deviceType = in.readString();
		this.moviePrice = in.readString();
		this.mobileServerType = in.readInt();
		this.movieLogo = in.readString();
		this.movieId = in.readInt();
		this.parentalLock = in.readInt();
		this.movieStatus = in.readInt();
		this.isFav = in.readInt();
		this.releasedDate = in.readString();
		this.movieUrl = in.readString();
		this.movieServerType = in.readInt();
		this.feature = in.readString();
		this.previewUrl = in.readString();
		this.name = in.readString();
		this.movieCategoryId = in.readInt();
		this.id = in.readInt();
		this.mobileAdServerType = in.readInt();
		this.isYoutube = in.readInt();
	}

	public static final Parcelable.Creator<MoviesItem> CREATOR = new Parcelable.Creator<MoviesItem>() {
		@Override
		public MoviesItem createFromParcel(Parcel source) {
			return new MoviesItem(source);
		}

		@Override
		public MoviesItem[] newArray(int size) {
			return new MoviesItem[size];
		}
	};
}