package maxxtv.movies.stb.Utils;

import java.util.ArrayList;
import java.util.List;

import maxxtv.movies.stb.Entity.Movie;

public enum MovieEnum {
    INSTANCE;

    private ArrayList<Movie> mObjectList;

    public static boolean hasData() {
        return INSTANCE.mObjectList != null;
    }

    public static void setData(final ArrayList<Movie> objectList) {
        INSTANCE.mObjectList = objectList;
    }

    public static ArrayList<Movie> getData() {
        final ArrayList<Movie> retList = INSTANCE.mObjectList;
        INSTANCE.mObjectList = null;
        return retList;
    }
}
