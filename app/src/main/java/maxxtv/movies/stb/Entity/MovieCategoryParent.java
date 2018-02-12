package maxxtv.movies.stb.Entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MovieCategoryParent implements Parcelable {
	private int parentId;
	private String categoryName;
	private String categoryDescription;
	private String categoryImageLink;
	private String recommendedGroup;
	private int status;
	
	public void setParentId( int parentId ) {
		this.parentId = parentId;
	}
	
	public void setCategoryName( String categoryName ) {
		this.categoryName = categoryName;
	}
	
	public void setCategoryDescription( String categoryDescription ) {
		this.categoryDescription = categoryDescription;
	}
	
	public void setCategoryImageLink( String categoryImageLink ) {
		this.categoryImageLink = categoryImageLink;
	}
	
	public void setRecommendedGroup( String recommendedGroup ) {
		this.recommendedGroup = recommendedGroup;
	}
	
	public void setStaus( int status ) {
		this.status = status;
	}
	
	public int getParentId() {
		return this.parentId;
	}
	
	public String getCategoryName() {
		return this.categoryName;
	}
	
	public String getCategoryDescription() {
		return this.categoryDescription;
	}
	
	public String getCategoryImageLink() {
		return this.categoryImageLink;
	}
	
	public String getRecommendedGroup() {
		return this.recommendedGroup;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append( "MovieCategoryParent[" )
			.append( "\n\tParent ID: ").append( this.parentId )
			.append( "\n\tCategory Name: ").append( this.categoryName )
			.append( "\n\tCategory Description: ").append( this.categoryDescription )
			.append( "\n\tCategory Image: ").append( this.categoryImageLink )
			.append( "\n\tRecommended Group: ").append( this.recommendedGroup )
			.append( "\n\tStatus: ").append( this.status )
			.append( "\n]" );
		
		return sb.toString();
			
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.parentId);
		dest.writeString(this.categoryName);
		dest.writeString(this.categoryDescription);
		dest.writeString(this.categoryImageLink);
		dest.writeString(this.recommendedGroup);
		dest.writeInt(this.status);
	}

	public MovieCategoryParent() {
	}

	protected MovieCategoryParent(Parcel in) {
		this.parentId = in.readInt();
		this.categoryName = in.readString();
		this.categoryDescription = in.readString();
		this.categoryImageLink = in.readString();
		this.recommendedGroup = in.readString();
		this.status = in.readInt();
	}

	public static final Parcelable.Creator<MovieCategoryParent> CREATOR = new Parcelable.Creator<MovieCategoryParent>() {
		@Override
		public MovieCategoryParent createFromParcel(Parcel source) {
			return new MovieCategoryParent(source);
		}

		@Override
		public MovieCategoryParent[] newArray(int size) {
			return new MovieCategoryParent[size];
		}
	};
}
