package maxxtv.movies.stb.Entity;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ACER on 5/26/2017.
 */

public class SubCategoryName {
    public String getSubCategory() {
        return subCategoryname;
    }

    public void setSubCategoryName(String subCategoryname) {
        this.subCategoryname = subCategoryname;
    }

    private String subCategoryname;

    public JSONArray getMovie_details() {
        return movie_details;
    }

    public void setMovie_details(JSONArray movie_details) {
        this.movie_details = movie_details;
    }

    private JSONArray movie_details;

    public int getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(int subCatId) {
        this.subCatId = subCatId;
    }

    private int subCatId;

}
