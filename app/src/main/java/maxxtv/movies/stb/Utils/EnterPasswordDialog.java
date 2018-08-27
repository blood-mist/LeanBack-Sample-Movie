package maxxtv.movies.stb.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import maxxtv.movies.stb.MoviePlayCustomController;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.common.AppConfig;

public class EnterPasswordDialog {

    /**
     * enter password before changing parental status
     * password length should be four
     * password can be only numbers
     * methods
     */
   /* public static void showParentalControlPasswordDialogToChangeLockStatus(
            final Context context, final int movieId) {

        final Dialog passwordDialog = EnterPasswordDialog.buildEnterPasswordInterface(context);

        final EditText passwordField = (EditText) passwordDialog
                .findViewById(R.id.et_buy_passowrd);

        Button btnConfirm = (Button) passwordDialog
                .findViewById(R.id.btn_confirm);


        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                passwordDialog.dismiss();
                String pinCode = getPinCode(context);
                if (pinCode.equals("")) {
                    ParentalLockUtils.changeMovieParentalStatus(context,
                            movieId, authToken);

                } else if (pinCode.equals(passwordField.getText().toString().trim())) {
                    ParentalLockUtils.changeMovieParentalStatus(context,
                            movieId, authToken);
                } else {
                    Toast.makeText(context, "Pin Code does not match", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }*/
    private static String getPinCode(Context context) {
        String pinCode = " ";
        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse(AppConfig.PIN_URL), null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String flg = cursor.getString(0);
                    pinCode = cursor.getString(1);
                    Logger.d("CheckingvlauefoC", flg);
                }
            }
        } catch (Exception e) {
            Logger.printStackTrace(e);
        }
        return pinCode;
    }


    private static Dialog buildEnterPasswordInterface(Context context) {
        final Dialog passwordDialog = new Dialog(context);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.setContentView(R.layout.dialog_enter_password);

        passwordDialog.show();

        if (context.getClass().getName()
                .equals(MoviePlayCustomController.class.getName())) {
            dismissPlayerOnBackPress(passwordDialog, context);
        }


        return passwordDialog;

    }

    private static void dismissPlayerOnBackPress(final Dialog passwordDialog, final Context context) {
        passwordDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        passwordDialog.dismiss();
                        ((MoviePlayCustomController) context).finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public static boolean showParentalControlPasswordDialogToPlayMovie(
            final Context context, final int movieId,
            final boolean flag_to_end_activity, final String authToken) {

        final Dialog passwordDialog = buildEnterPasswordInterface(context);
        final Button confirm = (Button) passwordDialog.findViewById(R.id.btn_confirm);
        final EditText passwordField = (EditText) passwordDialog
                .findViewById(R.id.et_buy_passowrd);

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                passwordDialog.dismiss();
                String pinCode = getPinCode(context);
                if (pinCode.equals(passwordField.getText().toString().trim())) {
                    passwordDialog.dismiss();
                    ParentalLockUtils.changeMovieParentalStatus(context,
                            movieId, authToken);

                } else {
                    Toast.makeText(context, "Pin Code does not match", Toast.LENGTH_SHORT).show();
                    if (context.getClass().getName().equals(MoviePlayCustomController.class.getName()) && flag_to_end_activity)
                        ((MoviePlayCustomController) context).finish();

                }


            }
        });
        return true;

    }

}
