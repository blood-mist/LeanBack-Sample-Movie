package maxxtv.movies.stb.Utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import maxxtv.movies.stb.Utils.common.LinkConfig;

public class LoginFileUtils {
	public static final String APP_CONFIG_FILE_NAME = "maxtv_mylogin";

	public static String getDisplayName() {
		return displayName;
	}

	public static String getUserId() {
		return userId;
	}

	private static String displayName;
	private static String userEmail;
	private static String userPassword;
	private static String userId;
	private static String sessionId;

	public static String getUserEmail() {
		return userEmail;
	}

	public static String getUserPassword() {
		return userPassword;
	}

	public static String getSessionId() {
		return sessionId;
	}

	public static boolean reWriteLoginDetailsToFile(String macAddress,
													String userEmai, String userPassword, String sessionId, String userId) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			String st = macAddress + "\n" + userEmai + "\n" + userPassword + "\n" + sessionId + "\n" + userId;

			Logger.i("STRING_TO_WRITE", st);

			File externalStorageDir = Environment.getExternalStorageDirectory();
			File myFile = new File(externalStorageDir, APP_CONFIG_FILE_NAME);

			try {
				FileWriter fileWriter = new FileWriter(myFile);
				PrintWriter printWriter = new PrintWriter(fileWriter);
				printWriter.print(st);
				printWriter.close();
			} catch (Exception e) {
				Logger.printStackTrace(e);
			}

			return true;
		} else {
			return false;
		}

	}

	public static boolean readFromFile(String macAddress) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File externalStorageDir = Environment.getExternalStorageDirectory();
			File myFile = new File(externalStorageDir, APP_CONFIG_FILE_NAME);
			if (myFile.exists()) {

				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream(myFile)));

					String data;
					StringBuffer sb = new StringBuffer();
					while ((data = br.readLine()) != null) {
						sb.append(data + ",");
					}
					Logger.d("FILE CONTAINS", sb.toString());
					int i = 1;
					for (String s : sb.toString().split(","))
						Logger.i("LOOP", s + (i++));

					String str = sb.toString().replace(",,", ",");
					Logger.i("AA", str);
					String[] both = str.split(",");
					if (both[0].toString().equals(macAddress)) {
						userEmail = both[1].toString();
						userPassword = both[2].toString();
						try {
							sessionId = both[3].toString();
						} catch (Exception e) {
							sessionId = "";
						}
						try {
							userId = both[4].toString();
						} catch (Exception e) {
							userId = "";
						}

						return true;
					}
					// Toast.makeText(MovieCategoryActivity.this,both[0].toString(),
					// Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {
					Logger.printStackTrace(e);
					return false;
				} catch (IOException e) {
					Logger.printStackTrace(e);
					return false;
				}
			}
			return false;

		} else
			return false;
	}
    public static String getAuthTokenFromFile() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File externalStorageDir = Environment.getExternalStorageDirectory();
            File myFile = new File(externalStorageDir, LinkConfig.TOKEN_CONFIG_FILE_NAME);
            if (myFile.exists()) {

                try {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(new FileInputStream(myFile)));

                    String data;
                    StringBuffer sb = new StringBuffer();
                    while ((data = br.readLine()) != null) {
                        sb.append(data + ",");
                    }
                    Logger.d("FILE CONTAINS", sb.toString());
                    String str = sb.toString();
                    Logger.i("AA", str);
                    // Toast.makeText(MovieCategoryActivity.this,both[0].toString(),
                    // Toast.LENGTH_SHORT).show();
                    return str;
                } catch (FileNotFoundException e) {
                    Logger.printStackTrace(e);
                    return "";
                } catch (IOException e) {
                    Logger.printStackTrace(e);
                    return "";
                }
            }
            return "";

        } else
            return "";
    }

	public static boolean checkIfFileExists(String filename) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File externalStorageDir = Environment.getExternalStorageDirectory();
			File myFile = new File(externalStorageDir, filename);
			if (myFile.exists()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}
}
