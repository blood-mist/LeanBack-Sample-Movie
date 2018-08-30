package maxxtv.movies.stb.Utils.common;

import java.util.ArrayList;

import maxxtv.movies.stb.Entity.SubCategoryName;

public class DataUtils {
    public static ArrayList<SubCategoryName> getFilteredSubCatList(ArrayList<SubCategoryName> subcategoryList) {
        ArrayList<SubCategoryName> categoryNames = new ArrayList<>();
        for (SubCategoryName subCatName:subcategoryList) {
            if(subCatName.getMovie_details().length()>0){
                categoryNames.add(subCatName);
            }
        }

        return categoryNames;
    }
}
