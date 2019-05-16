package stb.androidtv.moviesleanback.enitities;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class MoviesParentItem{

	@SerializedName("parent_name")
	private String parentName;

	@SerializedName("parent_description")
	private String parentDescription;

	@SerializedName("parent_id")
	private int parentId;

	@SerializedName("parent_logo")
	private String parentLogo;

	@SerializedName("status")
	private int status;

	public void setParentName(String parentName){
		this.parentName = parentName;
	}

	public String getParentName(){
		return parentName;
	}

	public void setParentDescription(String parentDescription){
		this.parentDescription = parentDescription;
	}

	public String getParentDescription(){
		return parentDescription;
	}

	public void setParentId(int parentId){
		this.parentId = parentId;
	}

	public int getParentId(){
		return parentId;
	}

	public void setParentLogo(String parentLogo){
		this.parentLogo = parentLogo;
	}

	public String getParentLogo(){
		return parentLogo;
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
			"MoviesParentItem{" + 
			"parent_name = '" + parentName + '\'' + 
			",parent_description = '" + parentDescription + '\'' + 
			",parent_id = '" + parentId + '\'' + 
			",parent_logo = '" + parentLogo + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}