package maxxtv.movies.stb.Utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.MovieCategoryActivity;
import maxxtv.movies.stb.MovieListActivity;
import maxxtv.movies.stb.MoviePlayCustomController;
import maxxtv.movies.stb.R;
import maxxtv.movies.stb.Utils.common.AppConfig;
import maxxtv.movies.stb.Utils.common.GetMac;


public class CustomDialogManager {

    public static final int DEFAULT = 0;
    public static final int LOADING = 1;
    public static final int PROGRESS = 2;
    public static final int ALERT = 3;
    public static final int MESSAGE = 4;
    public static final int WARNING = 5;

    private Context context = null;
    private String version = "", macAddress = "", title = "", message = "";

    private Dialog d;
    private TextView alertTitle, errorCodeTextView, MacTextView, versionTextView, messageTextView;
    private LinearLayout macAndVersion;
    private LinearLayout buttonLayout;
    private Button positive, neutral, negative, extra;
    private ImageView error_image;
    private ProgressBar progressBar;
    private View viewBelowMac, viewAboveButtons;
    String error_code = "";

    FrameLayout custom_dialog_layout;
    ImageButton close_btn;
    Typeface light, medium, semibold, regular;
    AVLoadingIndicatorView progressBarLayout;
    TextView MacTextViewFixed, versionTextViewFixed;

    private int type = DEFAULT;

    public CustomDialogManager(Context context, int type) {
        this.context = context;
        this.type = type;
        this.title = context.getString(R.string.app_name);
        this.message = context.getString(R.string.err_unexpected);
    }

    /**
     * Dialog with app name as title
     *
     * @param context
     * @param message
     * @param type
     */
    public CustomDialogManager(Context context, String message, int type) {
        this.context = context;
        this.type = type;
        this.message = message;
        this.title = context.getString(R.string.app_name);
    }

    /**
     * @param context
     * @param title
     * @param message
     * @param type
     */
    public CustomDialogManager(Context context, String title, String message,
                               int type) {
        this.context = context;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public void build() {
        d = new Dialog(context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.getWindow().setDimAmount(0.8f);
        d.setContentView(R.layout.custom_dialog);
        d.setCancelable(true);
        light = Typeface.createFromAsset(context.getAssets(), "fonts/Exo2-Light.otf");
        medium = Typeface.createFromAsset(context.getAssets(), "fonts/Exo2-Medium_0.otf");
        semibold = Typeface.createFromAsset(context.getAssets(), "fonts/Exo2-SemiBold.otf");
        regular = Typeface.createFromAsset(context.getAssets(), "fonts/Exo2-Regular.otf");
        findViewbyId();

        hidePriorDialogUI();


        setDialogTypeSetting(type);


    }

    private void findViewbyId() {
        custom_dialog_layout = (FrameLayout) d.findViewById(R.id.custom_dialog_layout);


        alertTitle = (TextView) d.findViewById(R.id.dialog_heading);
        alertTitle.setText(title);
        alertTitle.setTypeface(semibold);
        macAndVersion = (LinearLayout) d.findViewById(R.id.mac_version);
        MacTextView = (TextView) d.findViewById(R.id.macaddress_variable);
        MacTextViewFixed = (TextView) d.findViewById(R.id.macaddress_fixed);
        versionTextView = (TextView) d.findViewById(R.id.app_version_variable);
        versionTextViewFixed = (TextView) d.findViewById(R.id.app_version_fixed);
        MacTextViewFixed.setTypeface(semibold);
        versionTextViewFixed.setTypeface(semibold);
        MacTextView.setTypeface(light);
        versionTextView.setTypeface(light);

        viewBelowMac = d.findViewById(R.id.view_below_mac);
        viewAboveButtons = d.findViewById(R.id.view_above_button);


        error_image = (ImageView) d.findViewById(R.id.error_image);
        messageTextView = (TextView) d.findViewById(R.id.message);

        buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);
        positive = (Button) d.findViewById(R.id.positive);
        neutral = (Button) d.findViewById(R.id.neutral);
//        extra = (Button) d.findViewById(R.id.extra);
        negative = (Button) d.findViewById(R.id.negative);
        close_btn = (ImageButton) d.findViewById(R.id.closeButton);
        progressBar = (ProgressBar) d.findViewById(R.id.progressBar);
        progressBarLayout = (AVLoadingIndicatorView) d.findViewById(R.id.custom_progress_bar);
    }

    private void hidePriorDialogUI() {
        macAndVersion.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        error_image.setVisibility(View.GONE);
        viewAboveButtons.setVisibility(View.GONE);
        //viewBelowMac.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        positive.setVisibility(View.GONE);
        neutral.setVisibility(View.GONE);
        // extra.setVisibility(View.GONE);
        negative.setVisibility(View.GONE);
    }

    private void setDialogTypeSetting(int type) {
        if (type == LOADING) {
       /*     alertTitle.setVisibility(View.GONE);
            error_image.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            viewBelowMac.setVisibility(View.GONE);
            this.message = userAccountActivity.getString(R.string.txt_loading);
            messageTextView.setText(this.message + "");
            setErrorCode("null");*/
            progressBarLayout.setVisibility(View.VISIBLE);
            custom_dialog_layout.setVisibility(View.INVISIBLE);
        } else if (type == PROGRESS) {
            progressBarLayout.setVisibility(View.INVISIBLE);
            custom_dialog_layout.setVisibility(View.VISIBLE);
            alertTitle.setVisibility(View.VISIBLE);
            macAndVersion.setVisibility(View.GONE);
            viewAboveButtons.setVisibility(View.GONE);
            error_image.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            messageTextView.setText(this.message + "");
            setErrorCode(this.error_code);
        } else if (type == ALERT) {
            progressBarLayout.setVisibility(View.INVISIBLE);
            custom_dialog_layout.setVisibility(View.VISIBLE);
            alertTitle.setVisibility(View.VISIBLE);
            error_image.setImageResource(R.drawable.error);
            error_image.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            messageTextView.setText(this.message + "");
            setErrorCode(this.error_code);
        } else if (type == WARNING) {
            progressBarLayout.setVisibility(View.INVISIBLE);
            custom_dialog_layout.setVisibility(View.VISIBLE);
            alertTitle.setVisibility(View.VISIBLE);
            error_image.setImageResource(R.drawable.warning);
            error_image.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            messageTextView.setText(this.message + "");
            setErrorCode(this.error_code);
        } else if (type == MESSAGE) {
            progressBarLayout.setVisibility(View.INVISIBLE);
            custom_dialog_layout.setVisibility(View.VISIBLE);
            alertTitle.setVisibility(View.VISIBLE);
            macAndVersion.setVisibility(View.GONE);
            error_image.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            messageTextView.setText(this.message + "");
            setErrorCode(this.error_code);
        }
    }

    public void dismissDialogOnBackPressed() {
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        d.dismiss();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    public void dismissDialogOnBackPressed(final Context context) {
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        d.dismiss();
                        ((Activity) context).finish();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    public void addDissmissButtonToDialogandPlay(final Context context, final int id, final boolean b) {
        setExtraButton(
                context.getString(R.string.btn_dismiss),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        ((MoviePlayCustomController) context).checkToLoadMovieLink((Activity) context, id, b);
                    }
                });

    }

    public void dismissDialogOnBackPressedandPlay(final Context context, final int id, final boolean b) {
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        d.dismiss();
                        ((MoviePlayCustomController) context).checkToLoadMovieLink((Activity) context, id, b);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    public Dialog getInnerObject() {
        return d;
    }

    public void finishActivityOnBackPressed(final Context context) {
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        d.dismiss();
                        ((Activity) context).finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void exitApponBackPress() {
        d.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        d.dismiss();
                        System.exit(0);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public Button setPositiveButton(String btn_text,
                                    View.OnClickListener onClickListener) {
        viewAboveButtons.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);
        positive.setText(btn_text);
        positive.setVisibility(View.VISIBLE);
        positive.setOnClickListener(onClickListener);
        return positive;

    }

    public Button setNeutralButton(String btn_text,
                                   View.OnClickListener onClickListener) {
        viewAboveButtons.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);
        neutral.setText(btn_text);
        neutral.setVisibility(View.VISIBLE);
        neutral.setOnClickListener(onClickListener);
        return neutral;

    }

    public Button setNegativeButton(String btn_text,
                                    View.OnClickListener onClickListener) {
        viewAboveButtons.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);
        negative.setText(btn_text);
        negative.setVisibility(View.VISIBLE);
        negative.setOnClickListener(onClickListener);
        return negative;

    }

    public String getTitle() {
        return alertTitle.getText() + "";
    }

    public void setTitle(String title) {
        this.title = title;
        /**
         * if set title is done before build it should be found again
         */
        alertTitle = (TextView) d.findViewById(R.id.dialog_heading);
        alertTitle.setText(title);
        alertTitle.setVisibility(View.VISIBLE);
    }

    public void setMessage(String error_code, String message) {
        this.message = message;
        this.error_code = error_code;
        /**
         * if set message is done before build it should be found again
         */

        messageTextView = (TextView) d.findViewById(R.id.message);
        messageTextView.setText(message);
        messageTextView.setTypeface(light);
        setErrorCode(error_code);

    }

    public void showMacAndVersion() {
        if (AppConfig.isDevelopment()) {
            macAddress = AppConfig.getMacAddress();
        } else {
            macAddress = GetMac.getMac(context);
        }
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (NameNotFoundException e) {
            version = "N/A";
            e.printStackTrace();
        }
        MacTextView.setText(macAddress);
        versionTextView.setText(version);

        macAndVersion.setVisibility(View.VISIBLE);
        viewBelowMac.setVisibility(View.VISIBLE);
    }

    public void show() {
        d.show();
    }

    public void hide() {
        d.hide();
    }

    public boolean isShowing() {
        /**
         * if build is not done then handle exception for dialog
         */
        try {
            return d.isShowing();
        } catch (Exception e) {
            return false;
        }
    }

    public Button getNeutralButton() {
        return neutral;
    }

    public Button getExtraButton() {
        return extra;
    }

    public void finishActivityonDismissPressed(final Context context) {

        setExtraButton(
                context.getString(R.string.btn_dismiss),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                        ((Activity) context).finish();

                    }
                });
    }

    public ImageButton setExtraButton(String btn_text,
                                      View.OnClickListener onClickListener) {
        close_btn.setVisibility(View.VISIBLE);
        close_btn.setOnClickListener(onClickListener);
        return close_btn;

    }

    // end of re used custom Dialog

    public void addDissmissButtonToDialog() {
        setExtraButton(
                context.getString(R.string.btn_dismiss),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (context.getClass().getName()
                                .equals(
                                        MovieCategoryActivity.class.getName()) || context.getClass().getName()
                                .equals(MoviePlayCustomController.class.getName())
                                )

                            ((Activity) context).finish();
                    }
                });

    }

    public void dismiss() {
        d.dismiss();
    }

    public void setErrorCode(String error_codesdf) {
        this.error_code = error_codesdf;
        errorCodeTextView = (TextView) d.findViewById(R.id.txt_error_code);
        if (error_code.equalsIgnoreCase("null") || error_code.equals("")) {
            errorCodeTextView.setVisibility(View.GONE);
        } else {
            errorCodeTextView.setText("Error Code:" + error_code);
        }
    }


    public static class ReUsedCustomDialogs {
        // Dialog to show when required data not available
        public static CustomDialogManager showDataNotFetchedAlert(final Context context) {
            final CustomDialogManager error = new CustomDialogManager(context,
                    CustomDialogManager.ALERT);
            error.build();
            error.showMacAndVersion();
            error.setMessage("null", context.getString(R.string.err_json_exception));
            error.addDissmissButtonToDialog();
            error.dismissDialogOnBackPressed();
            error.setNegativeButton(context.getString(R.string.btn_relaunch), new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    error.dismiss();
                    Intent i = ((ContextWrapper) context)
                            .getBaseContext()
                            .getPackageManager()
                            .getLaunchIntentForPackage(
                                    ((ContextWrapper) context).getBaseContext()
                                            .getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);

                    ((Activity) context).finish();

                }
            });
            error.show();
            return error;
        }


        public static CustomDialogManager noInternet(final Context context) {
            final CustomDialogManager noInternet = new CustomDialogManager(context, CustomDialogManager.WARNING);
            noInternet.build();
            noInternet.getInnerObject().setCancelable(true);
            noInternet.setTitle(context.getString(R.string.app_name));
            noInternet.setMessage("E 025", context.getString(R.string.err_server_unreachable));
            noInternet.show();

            noInternet.addDissmissButtonToDialog();

            noInternet.setNeutralButton(context.getString(R.string.btn_settings), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noInternet.dismiss();
                    try {
                        Intent LaunchIntent = context.getPackageManager()
                                .getLaunchIntentForPackage("com.newitventure.wod.mynitvsetting");
                        context.startActivity(LaunchIntent);
                    } catch (Exception e) {
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_SETTINGS);
                        context.startActivity(intent);
                    }
                }
            });


            if (context.getClass().getName().equals(MovieCategoryActivity.class.getName())
                    || context.getClass().getName().equals(MovieListActivity.class.getName())) {

                noInternet.finishActivityonDismissPressed(context);
                noInternet.finishActivityOnBackPressed(context);

                noInternet.setPositiveButton(context.getString(R.string.btn_relaunch), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        noInternet.dismiss();
                        context.startActivity(i);
                        ((Activity) context).finish();

                    }
                });

                noInternet.getInnerObject().setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                        ((Activity) context).finish();
                    }

                });
            }
            return noInternet;
        }

        public static CustomDialogManager featureNotAvailable(Context context) {
            CustomDialogManager featureNotAvailable = new CustomDialogManager(context, CustomDialogManager.MESSAGE);
            featureNotAvailable.build();
            featureNotAvailable.showMacAndVersion();
            featureNotAvailable.setMessage("null", context.getString(R.string.msg_feature_not_available));
            featureNotAvailable.addDissmissButtonToDialog();
            featureNotAvailable.dismissDialogOnBackPressed();
            featureNotAvailable.getInnerObject().setCancelable(true);
            featureNotAvailable.show();
            return featureNotAvailable;
        }


        public static CustomDialogManager parseAndShowErrorMessage(String result, Context context) throws JSONException {
            JSONObject jo = new JSONObject(result);
            StringBuilder sb = new StringBuilder();
            sb.append("Error Code:\t\t")
                    .append(jo.getString("error_code"))
                    .append("\n").append(jo.getString("error_message"));
            CustomDialogManager parseError = new CustomDialogManager(context, CustomDialogManager.MESSAGE);
            parseError.build();
            // parseError.setMessage(sb.toString());
            parseError.setMessage(jo.getString("error_code"), jo.getString("error_message"));
            parseError.addDissmissButtonToDialog();
            parseError.dismissDialogOnBackPressed();
            parseError.getInnerObject().setCancelable(true);
            parseError.show();
            return parseError;


        }
    }

}