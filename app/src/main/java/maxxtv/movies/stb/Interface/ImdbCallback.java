package maxxtv.movies.stb.Interface;

import maxxtv.movies.stb.Entity.ImdbPojo;

/**
 * Created by ACER on 6/19/2017.
 */

public interface ImdbCallback {
    public void getImdbData(ImdbPojo imdbData,String s);
}
