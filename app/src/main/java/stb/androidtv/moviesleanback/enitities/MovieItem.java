package stb.androidtv.moviesleanback.enitities;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class MovieItem{

	@SerializedName("movies")
	private List<MoviesItem> movies;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("parent_id")
	private int parentId;

	@SerializedName("name")
	private String name;

	@SerializedName("description")
	private String description;

	@SerializedName("logo")
	private String logo;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("is_top")
	private int isTop;

	@SerializedName("status")
	private int status;

	public void setMovies(List<MoviesItem> movies){
		this.movies = movies;
	}

	public List<MoviesItem> getMovies(){
		return movies;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setParentId(int parentId){
		this.parentId = parentId;
	}

	public int getParentId(){
		return parentId;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setLogo(String logo){
		this.logo = logo;
	}

	public String getLogo(){
		return logo;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setIsTop(int isTop){
		this.isTop = isTop;
	}

	public int getIsTop(){
		return isTop;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"MovieItem{" + 
			"movies = '" + movies + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",parent_id = '" + parentId + '\'' + 
			",name = '" + name + '\'' + 
			",description = '" + description + '\'' + 
			",logo = '" + logo + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",id = '" + id + '\'' + 
			",is_top = '" + isTop + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}