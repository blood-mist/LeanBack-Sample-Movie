package maxxtv.movies.stb.Async;

/**
 * Downloads and installs the the application from provided link.
 * <p>
 * if this class is run from entrypoint class then the application will exit after it runs successfully
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.view.View;



import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import maxxtv.movies.stb.BuildConfig;
import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.Logger;

public class ApkDownloader extends AsyncTask<String, Integer, File> {
    private ProgressDialog mProgressDialog;
    InputStream is = null;
    private WeakReference<Context> weakReference;
    private PowerManager.WakeLock mWakeLock;
    private String appName;

    public ApkDownloader( WeakReference<Context> weakReference, String appName) {
        this.weakReference = weakReference;
        this.appName=appName;
    }
    private Context getContext(){
        return weakReference.get();
    }

    @Override
    protected File doInBackground(String... params) {
        // TODO Auto-generated method stub
        File file = null;
        Logger.d("APK DOwnload do in background", "returns file");
        try {
            // set the download URL, a url that points to a file on the internet
            // this is the file to be downloaded
            String updateLink = params[0];

            URL url = new URL(updateLink);

            // create the new connection
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            // set up some things on the connection
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);

            // and connect!
            urlConnection.connect();

            File SDCardRoot = Environment.getExternalStorageDirectory();
            String fileName = updateLink.substring(updateLink.lastIndexOf("/"));

            file = new File(SDCardRoot, fileName);

            // this will be used to write the downloaded data into the file
            // we created
            FileOutputStream fileOutput = new FileOutputStream(file);

            // this will be used in reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            // this is the total size of the file
            int totalSize = urlConnection.getContentLength();
            // variable to store total downloaded bytes
            int downloadedSize = 0;

            // create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0; // used to store a temporary size of the
            // buffer

            // now, read through the input buffer and write the contents to
            // the file
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                // add the data in the buffer to the file in the file output
                // stream (the file on the sd card
                fileOutput.write(buffer, 0, bufferLength);
                // add up the size so we know how much is downloaded
                downloadedSize += bufferLength;
                // Log.d( "com.newitventure.smartvision.EntryPoint",
                // downloadedSize + "" );

                // this is where you would do something to report the
                // prgress, like this maybe
                // updateProgress(downloadedSize, totalSize);

                if (totalSize > 0) {
                    publishProgress((downloadedSize * 100 / totalSize));
                }

            }
            // close the output stream when done
            fileOutput.close();
        } catch (final Exception e) {
            // Log.e("Buffer Error", "Error converting result " +
            // e.toString());
            try {
                mProgressDialog.dismiss();
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final CustomDialogManager error_dialog = new CustomDialogManager(
                                getContext(), CustomDialogManager.ALERT);
                        error_dialog.build();
                        error_dialog.setMessage("Error: " , e.toString());
                        error_dialog.setPositiveButton("OK",
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        error_dialog.dismiss();

                                    }
                                });
                        error_dialog.show();

                    }
                });
            } catch (Exception de) {
                Logger.printStackTrace(de);
//                PropertyGetSet.showToast(context, "Sorry! Couldnot download the update!!!");
            }

        }

        return file;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle(appName.toUpperCase());
        mProgressDialog.setMessage("Downloading the update");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);


        PowerManager pm = (PowerManager) getContext()
                .getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass()
                .getName());
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);

        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(File savedFile) {
        super.onPostExecute(savedFile);

        mWakeLock.release();
        mProgressDialog.dismiss();

        // Log.d("com.newitventure.smartvision.EntryPoint",
        // "Saved File at: " + savedFile.toString());
        if (savedFile.exists()) {
            try {
                if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(savedFile),
                            "application/vnd.android.package-archive");
                    getContext().startActivity(intent);
                    ((Activity) getContext()).finish();
                }else{
                    Uri apkUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", savedFile);
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getContext().startActivity(intent);
                }


            } catch (Exception e) {
                Logger.printStackTrace(e);
                Logger.e("APK DOWNLOADER:", "Exception: " + e.getMessage());
                CustomDialogManager downloadException = new CustomDialogManager(
                        getContext(), CustomDialogManager.ALERT);
                downloadException.build();
                downloadException.setTitle("Download");
                downloadException.setMessage("Exception: " , e.getMessage());
                downloadException.addDissmissButtonToDialog();

                try {
                    downloadException.show();
                }catch(Exception dialogexception){
//                    PropertyGetSet.showToast(context,dialogexception.getMessage());
                }

            }
        }
        if (getContext().getClass().getName()
                .equals(EntryPoint.class.getName()))
            ((Activity) getContext()).finish();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
    }

}