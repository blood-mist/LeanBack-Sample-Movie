
package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoviesParent {

    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("parent_name")
    @Expose
    private String parentName;
    @SerializedName("parent_description")
    @Expose
    private String parentDescription;
    @SerializedName("parent_logo")
    @Expose
    private String parentLogo;
    @SerializedName("status")
    @Expose
    private String status;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentDescription() {
        return parentDescription;
    }

    public void setParentDescription(String parentDescription) {
        this.parentDescription = parentDescription;
    }

    public String getParentLogo() {
        return parentLogo;
    }

    public void setParentLogo(String parentLogo) {
        this.parentLogo = parentLogo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
