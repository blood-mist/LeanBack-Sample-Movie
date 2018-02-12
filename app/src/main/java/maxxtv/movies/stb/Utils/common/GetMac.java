package maxxtv.movies.stb.Utils.common;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import maxxtv.movies.stb.Utils.Logger;

public class GetMac {

    private static final String TAG = "com.newitventure.utils.common.GetMac";


    public static String getMac(Context context) {
        String mac = readMacFromFilePath("/sys/class/efuse/mac");
        if (mac == null || mac.equalsIgnoreCase("NotFound"))
            mac = readMacFromFilePath("/sys/class/net/eth0/address");

        if (mac == null || mac.equalsIgnoreCase("NotFound")) {

            mac = readMacFromWifi(context);

        }
        return mac;

//		try {
//			File file = new File("/sys/class/efuse/mac");
//			i=1;
//			if( ! file.exists() ) {
//				file = new File( "/sys/class/net/eth0/address" );
//				i=2;
//
//				//Toast.makeText(context, "File not found at /sys/class/efuse/mac", Toast.LENGTH_LONG ).show();
//			}
//
//
//			if( file.exists() ) {
//				result = readMacFromFile( file );
//			}else {
//				//Toast.makeText(context, "File not found at /sys/class/net/eth0/address", Toast.LENGTH_LONG ).show();
//
//
//				/* for phone and tablet having wifi adapter */
//				WifiManager wifiManager = (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
//				WifiInfo wInfo = wifiManager.getConnectionInfo();
//
//				result = wInfo.getMacAddress();
//				result = result.replaceAll(":", "");
//			}
//		} catch (Exception e) {
//			Log.e( TAG, e.getMessage() );
//			return "mac not found";
//		}
//		Log.d("mac read from "+ result,i+"");
    }

    private static String readMacFromFilePath(String filepath) {
        String result = "NotFound";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(new File(filepath));
            br = new BufferedReader(fr);
            result = br.readLine();
            if (result != null)
                result = result.replaceAll(":", "");
        } catch (Exception e) {
            return result;
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                }
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                }
        }
        return result;
    }


    /**
     * for phone and tablet having wifi adapter
     *
     * @param context
     * @return wifi-MacAddress
     */
    private static String readMacFromWifi(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();

        String mac = wInfo.getMacAddress();
        mac = mac.replaceAll(":", "");
        return mac;
    }

    public static String getMacSingle(Context context) {
        String result = "NotFound";
        try {
            FileReader fr = new FileReader(new File("/sys/class/efuse/mac"));
            BufferedReader br = new BufferedReader(fr);
            result = br.readLine();
            fr.close();
            br.close();
            result = result.replaceAll(":", "");
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    private static String readMacFromFile(File file) {
        String result = "NotFound";

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            result = br.readLine();
            fr.close();
            br.close();
            result = result.replaceAll(":", "");
        } catch (Exception e) {
            Logger.e(TAG, "readMacFromFile " + e.getMessage());
        }

        return result;
    }

}
