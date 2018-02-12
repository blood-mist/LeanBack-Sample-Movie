package maxxtv.movies.stb.Parser;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import maxxtv.movies.stb.Entity.SubCategoryName;
import maxxtv.movies.stb.Utils.Logger;

/**
 * Created by ACER on 5/26/2017.
 */

public class MovieSubCategoryParser {
        private static final String TAG = maxxtv.movies.stb.Parser.MovieCategoryParentParser.class.getSimpleName();

        private ArrayList<SubCategoryName> subCategoryNames;

        private String jsonString;

    public MovieSubCategoryParser(String jsonString) {
            this.jsonString = jsonString;
        subCategoryNames=new ArrayList<>();
        }

        public boolean parse() throws JSONException {
            JSONObject root = null;
            try {
                root = new JSONObject(jsonString);
            } catch (JSONException e) {
                Logger.printStackTrace(e);
                return false;
            }


            JSONArray categoryItems = root.getJSONArray("movie");

            for (int i = 0; i < categoryItems.length(); i++) {
                JSONObject item = categoryItems.getJSONObject(i);
                SubCategoryName subNames = new SubCategoryName();
                subNames.setSubCategoryName(item.getString("name"));
                subNames.setSubCatId(item.getInt("id"));
                subNames.setMovie_details(item.getJSONArray("movies"));
                subCategoryNames.add(subNames);
            }

            return true;
        }

        public ArrayList<SubCategoryName> getSubCategoryList() {
            return this.subCategoryNames;
        }



}
