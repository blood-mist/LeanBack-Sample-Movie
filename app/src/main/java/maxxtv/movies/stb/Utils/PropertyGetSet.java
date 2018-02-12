package maxxtv.movies.stb.Utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PropertyGetSet {
	public static String PARENTAL_CONTROL = "persist.sys.parental.ctrl";
	public static String SCREEN_SAVER = "persist.sys.screensaver.on";
	public static String PARENTAL_PASSWORD = "persist.sys.parental.pwd";
	public static String LOG_STATUS = "nitv.log.status";
	public static String VIDEO_RUNNING_USAGE = "nitv.video.running";

	public static boolean setProperty(String key, String value) {

		boolean success = false;

		try {

			Class SystemProperty = Class.forName("android.os.SystemProperties");

			Class[] paramTypes = new Class[2];

			paramTypes[0] = String.class;

			paramTypes[1] = String.class;

			Method set = SystemProperty.getMethod("set", paramTypes);

			Object[] params = new Object[2];

			params[0] = new String(key);

			params[1] = new String(value);

			set.invoke(SystemProperty, params);
			Logger.d("samuel---set", params[0].toString());
			Logger.d("samuel---set", params[1].toString());
			success = true;
			return success;

		} catch (ClassNotFoundException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (NoSuchMethodException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (IllegalAccessException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (IllegalArgumentException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (InvocationTargetException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		}

		return success;

	}

	public static String getProperty(String key) {

		String value = null;

		try {

			Class SystemProperty = Class.forName("android.os.SystemProperties");

			Class[] paramTypes = new Class[1];

			paramTypes[0] = String.class;

			Method get = SystemProperty.getMethod("get", paramTypes);

			Object[] params = new Object[1];

			params[0] = new String(key);

			value = (String) get.invoke(SystemProperty, params);
			Logger.d("value --> sadip ", value + "");
		} catch (NoSuchMethodException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (ClassNotFoundException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (IllegalAccessException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (IllegalArgumentException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		} catch (InvocationTargetException e) {

			// TODO Auto-generated catch block

			Logger.printStackTrace(e);

		}

		Logger.e("winwin", "value >>>>>>>>>>>>>>>>>>>>>> " + value);

		// Log.e("value >>>>>>>>>>>>>>>>>>>>>> ", value + "");

		return value;

	}

	public static void showToast(Context context, String string) {
		// TODO Auto-generated method stub
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();

	}

}
