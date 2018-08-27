package maxxtv.movies.stb.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import maxxtv.movies.stb.Utils.common.LinkConfig;

public class DownloadUtil {

	private static final String TAG = "com.newitventure.smartvision.movies.util.DownloadUtil";
	public static String NotOnline = "1";
	public static String ServerUnrechable = "0";
	private String link;
	private Context context;
	private String encoding = "utf-8";
	private boolean isTokenNeed=false;
	private String access_token;


	public DownloadUtil(String link, Context context) {
		this.link = link;
		this.context = context;
		isTokenNeed=false;
	}
	public DownloadUtil(String link, Context context, String access_token) {
		this.link = link;
		this.context = context;
		this.access_token = access_token;
		isTokenNeed = true;
	}

	public String downloadStringContent() {
		if (isOnline()) {

			StringBuilder result = new StringBuilder();

			try {
				URL url = new URL(link);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				if (isTokenNeed)
					connection.setRequestProperty("Authorization", "Bearer " + access_token);
				connection.connect();
				Log.d("response_code", connection.getResponseCode() + "" + connection.getResponseMessage());
				InputStream stream = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
				Log.d("the json data is ", result.toString());
				return result.toString();
			} catch (Exception e) {
                e.printStackTrace();
				return ServerUnrechable;
            }


		} else
			return NotOnline;

	}


	private boolean isServerReachable(String value)
	// To check if server is reachable
	{
		/*try {
			InetAddress.getByName(value).isReachable(3000); // Replace
															// with
															// your
															// name
			// InetAddress.getByName("asdksjdjkshdd.askjdhasjk.sakd").isReachable(3000);
			// //Replace with your name
			Logger.d(TAG, "connection established");
			return true;
		} catch (Exception e) {
			Logger.d(TAG, "connection lost");
			return false;
		}*/
		return true;
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}


	public String postLoginData(String email, String password, String macAddress) {
		try {
            StringBuilder result = new StringBuilder();
			URL urlToRequest = new URL(link);
			HttpURLConnection urlConnection = (HttpURLConnection) urlToRequest.openConnection();
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			String postParamaters = "uname="+email+"&"+"pswd="+password+"&boxId="+macAddress;
			urlConnection.setFixedLengthStreamingMode(postParamaters.getBytes().length);
			urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
			out.print(postParamaters);
			out.close();
			urlConnection.connect();
			InputStream in =
					new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
					result.append(line);
			}
			Log.d("the json data is ", result.toString());
			return result.toString();
		}catch (ProtocolException e) {
			e.printStackTrace();
			return ServerUnrechable;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return ServerUnrechable;
		} catch (IOException e) {
			e.printStackTrace();
			return NotOnline;
		}
	}
}
