/*Note To Developers:
 * Correct the urls at every time use : BASE_URL_DuringDevelopment
 * 										CHECK_IF_SERVER_RECHABLE  in line no 54
 */
package stb.androidtv.moviesleanback.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import stb.androidtv.moviesleanback.R;


public class LinkConfig {
    public static final String LOGIN_FILE_NAME = "prabhutv_mylogin";

//    public static final String ACCOUNT_PACKAGE = "androidtv.myaccount.stb";
public static final String ACCOUNT_PACKAGE = "com.nitv.prabhutv.account";
    public static final String MOVIE_ERROR_CODE = "error_code";
    public static final String MOVIE_ERROR_MESSAGE = "error_message";
    public static final String MOVIE_IP = "ip_address";
    public static final String ALLOW_COUNTRY = "other/geoblock";
    // public static final String BASE_URL = "https://middleware.yourman.info/api/v1/";
//    public static final String BASE_URL = "http://192.168.2.212/api/v1/";
//    public static final String BASE_URL = "http://192.168.1.220/api/v1/";
//    public static final String BASE_URL = "http://59.92.16.3/api/v1/";
//    public static final String BASE_URL = "http://192.168.1.107/api/v1/";
//    public static final String BASE_URL = "http://192.168.9.1/api/v1/";
    public static final String BASE_URL = "https://iptv.prabhutv.com.np/api/v1/";

//    public static final String BASE_URL = "https://demo.newitventure.com/api/v1/";
//   public static final String BASE_URL ="http://demont.newitventure.com/api/v1/" ;

    public static final String CHECK_MAC = "mac/exists";
    public static String CHECK_IF_SERVER_RECHABLE = "maxxtvbox.net";
    public static final String LOGIN_BUTTON_CLICK = "user/login";
    public static final String SEARCH_BUTTON_CLICK = "movies/search";
    public static final String CHECK_BEFORE_LOGIN_URL = "user/checklogin";
    //    public static final String LINK_SEVER_APKs = "http://192.168.1.107/market_app_info/api/market_app_info.php";
//    public static final String LINK_SEVER_APKs = "https://demo.newitventure.com/market_app_info/api/market_app_info.php";
//    public static final String LINK_SEVER_APKs = "http://demont.newitventure.com/market_app_info/api/market_app_info.php";
//    public static final String LINK_SEVER_APKs = "http://192.168.9.1/market_app_info/api/market_app_info.php";
//    public static final String LINK_SEVER_APKs = "https://middleware.yourman.info/market_app_info/api/market_app_info.php";
//    public static final String LINK_SEVER_APKs = "http://192.168.2.212/market_app_info/api/market_app_info.php";
//    public static final String LINK_SEVER_APKs = "http://192.168.1.220/market_app_info/api/market_app_info.php";
    public static final String LINK_SEVER_APKs = "https://iptv.prabhutv.com.np/market_app_info/api/market_app_info.php";
//public static final String LINK_SEVER_APKs = "http://59.92.16.3/market_app_info/api/market_app_info.php";

    public static final String GET_UTC = "other/utc";
    public static final int UPDATE_SESSION = R.string.update_session;
    public static final int GET_GROUP = R.string.get_group_info;
    //  public static final int SET_UNSET_FAV_URL = R.string.set_fav_url;
    public static final int MOVIE_PREVIEW_LINK = R.string.movie_preview;

    public static final String MOVIE_PARENT_CATEGORY_URL = "movies/parent";
    public static final String MOVIE_CATEGORY_DETAIL = "movies/category";
    public static final String MOVIE_PLAY_LINK = "movies/getmovie";
    public static final int MOVIE_FAV_LINK = R.string.movie_fav_link;
    public static final String TOKEN_CONFIG_FILE_NAME = "authCode";
    public static String AUTTHORIZATION_TOKEN = "";
    public static final int NO_CONNECTION = 400;
    public static final int INVALID_HASH = 101;
    public static final int INVALID_USER = 102;
    public static final int USER_NOT_REGISTERED = 304;
    public static final String DOWNLOAD_LINK = "download_link";
    public static final String DOWNLOAD_NAME = "download_name";
    public static final String DOWNLOAD_ID = "download_id";
    public static final String MESSAGE_PROGRESS = "message_progress";
    public static final String MESSAGE_ERROR = "message_error";
    public static final String DOWNLOAD_FRAGMENT = "download_fragment";
    public static final String USER_EMAIL = "user_email";
    public static final String ERROR_TITLE = "error_title";
    public static final String ERROR_MESSAGE = "error_message";
    public static final String SUBCAT_MOVIE_LIST_BYTE = "subcat_movie_list";
    public static final String SUBCAT_TOP_MOVIE_LIST = "top_movie_list";
    public static final String MOVIE_CLICKED_LINK = "clicked_movie_link";
    public static final String SIMILAR_MOVIE_LIST = "current_movie_list";
    public static final String LOADING_FRAGMENT = "loading_fragment";
    public static final String MOVIE_ITEM = "movie_item";
    public static final String KEY_MOVIES = "movies";
    public static final String MOVIE_NEXT_PREV = "movie_intent_filter";
    public static final String MOVIE_RESPONSE = "movie_link_response";
    public static final String MOVIE_DETAILS = "movie_details";
    public static final String LOGIN_DATA = "login_data";


    public static String SCREEN_SAVER = "other/screensaver";

    public static final int MULTI_LOGIN = R.string.multi_login;



  /*  public static void loadMovieLink(Context context, int movieId, boolean flag_to_end_activity) {

        final String media_url = LinkConfig.getString(context, LinkConfig.MOVIE_PLAY_LINK) + "?movieId=" + movieId;
        new MovieLinkLoader(context, movieId, flag_to_end_activity).execute(media_url);

    }*/

    public static String getString(Context context, int resId) {

        return BASE_URL + context.getResources().getString(resId);

    }

    public static void deleteAuthCodeFile() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, LinkConfig.TOKEN_CONFIG_FILE_NAME);
            myFile.delete();
        }
    }

    public static String getHashCode(String userId, String utc, String sessionId) {
        String SecretKey = "123456789";
        String stringToMD5 = SecretKey + sessionId
                + userId + "" + utc;
        return md5(stringToMD5);
    }

    private static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
