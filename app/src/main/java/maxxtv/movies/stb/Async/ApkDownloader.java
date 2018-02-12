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
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;



import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import maxxtv.movies.stb.EntryPoint;
import maxxtv.movies.stb.Utils.CustomDialogManager;
import maxxtv.movies.stb.Utils.Logger;

public class ApkDownloader extends AsyncTask<String, Integer, File> {
    ProgressDialog mProgressDialog;
    InputStream is = null;
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private String appName;

    public ApkDownloader(Context context, String appName) {
        this.context = context;this.appName=appName;
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
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final CustomDialogManager error_dialog = new CustomDialogManager(
                                context, CustomDialogManager.ALERT);
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

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle(appName);
        mProgressDialog.setMessage("Downloading the update");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass()
                .getName());
        mWakeLock.acquire();

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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(savedFile),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                /**
                 * If this class is run from the loading class then exit the
                 * application
                 */


            } catch (Exception e) {
                Logger.printStackTrace(e);
                Logger.e("APK DOWNLOADER:", "Exception: " + e.getMessage());
                CustomDialogManager downloadException = new CustomDialogManager(
                        context, CustomDialogManager.ALERT);
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
        if (context.getClass().getName()
                .equals(EntryPoint.class.getName()))
            ((Activity) context).finish();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(values[0]);
    }

}