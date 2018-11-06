package maxxtv.movies.stb.Utils.common;


import maxxtv.movies.stb.BuildConfig;

public class AppConfig {
    public static final int CACHE_HOLD_DURATION = 60 * 1000; // 1 min
    public static final int randomdialogshowrange = 2 * 60 * 60 * 1000;
    private static final boolean isInDevelopment = BuildConfig.DEBUG;
    public static boolean featureParentalLock = true;

    public static boolean isDevelopment() {
        return isInDevelopment;
    }

    public static final String PROVIDER_NAME = "com.newitventure.maxtv";
    public static final String SCREEN_URL = "content://" + PROVIDER_NAME + "/screensaver";
    public static final String PIN_URL = "content://" + PROVIDER_NAME + "/pincode";
    public static final String TOKEN_URL = "content://maxxtv.launcher.stb/firebase_token";

    /**
     * @return fixed mac for testing in development mode
     */
    public static String getMacAddress() {
//		return "78c2c0928760";//isp registered mac
//		return "789654123321";
//		return "ccd3e222652e";
      //  return "ccd3e2226503";
       //return "aabbccddeeff";
         return "78c2c096ef97";
    }

}
