/*Note To Developers:
 * Correct the urls at every time use : BASE_URL_DuringDevelopment
 * 										CHECK_IF_SERVER_RECHABLE  in line no 54
 */
package maxxtv.movies.stb.Utils.common;

import android.content.Context;
import android.os.Environment;


import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Parser.GroupDataParser;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.LoginFileUtils;

public class LinkConfig {
    public static final int ALLOW_COUNTRY = R.string.allow_country;
    public static final int PARENTAL_URL = R.string.parental_url;
    public static final String LOGIN_FILE ="androidtv_mylogin" ;
    public static String BASE_URL = "https://middleware02.yourman.info/api/v1/";
    public static int CHECK_MAC = R.string.check_mac;
    public static String CHECK_IF_SERVER_RECHABLE = "https://middleware02.yourman.info";
    public static final int LOGIN_BUTTON_CLICK = R.string.login_click ;
    public static final int SEARCH_BUTTON_CLICK=R.string.search_url;
    public static final int CHECK_BEFORE_LOGIN_URL = R.string.check_user ;
    public static String LINK_SEVER_APKs = "https://middleware02.yourman.info/market_app_info/api/market_app_info.php";
    public static final int GET_UTC = R.string.get_utc;
    public static final int UPDATE_SESSION = R.string.update_session;
    public static final int GET_GROUP = R.string.get_group_info;
  //  public static final int SET_UNSET_FAV_URL = R.string.set_fav_url;
    public static final int MOVIE_PREVIEW_LINK = R.string.movie_preview;

    public static final int MOVIE_PARENT_CATEGORY_URL = R.string.movie_category_list;
    public static final int MOVIE_CATEGORY_DETAIL = R.string.movie_list_detail;
    public static final int MOVIE_PLAY_LINK = R.string.movie_play_link;
    public static final int MOVIE_FAV_LINK=R.string.movie_fav_link;
    public static final String TOKEN_CONFIG_FILE_NAME ="authCode" ;
    public static final String YOUTUBE_API_KEY="AIzaSyAzPx34vZSmQ9uyJ4TaSSXpjoYdwBWTZKE";

    public static String SCREEN_SAVER ="other/screensaver";

    public static final int MULTI_LOGIN = R.string.multi_login;
    public static String getHashCode(String utc) {

        String sessionId = null;

        if (LoginFileUtils.readFromFile(EntryPoint.macAddress)) {

            sessionId = LoginFileUtils.getSessionId();
            Logger.e("session id in hash", sessionId.trim());

            String SecretKey = "123456789";

/*
				  Calendar calendar = Calendar.getInstance(); long currentTime
				  = calendar.getTimeInMillis();

				  String utc = ((int) (currentTime / 1000)) + "";
				 /*//**/


            Logger.i("utc time ", utc);
            String stringToMD5 = SecretKey + sessionId + GroupDataParser.groupData.getUserId() + "".trim()+ utc;
            String hexString = md5(stringToMD5);
            return "utc=" + utc.trim() + "&userId=" + GroupDataParser.groupData.getUserId() + "".trim()
                    + "&hash=" + hexString;

        }
        return null;

    }

    private static String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Logger.printStackTrace(e);
        }
        return "";
    }


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

}
