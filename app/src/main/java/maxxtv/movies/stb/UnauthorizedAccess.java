package maxxtv.movies.stb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import maxxtv.movies.stb.Utils.Logger;
import maxxtv.movies.stb.Utils.common.AppConfig;
import maxxtv.movies.stb.Utils.common.GetMac;


public class UnauthorizedAccess extends Activity {

    private TextView messageView, txt_username, boxid, ipAddress_txt, errorCode_txt;
    private Button retry, finishBut;
    private String username, macAddress = "", error_code = "",error_message, ipAddress = "";
    private StringBuilder mb = new StringBuilder();

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_unauthorized);
        error_code = getIntent().getStringExtra("error_code");
        error_message = getIntent().getStringExtra("error_message");
        messageView = (TextView) findViewById(R.id.txt_msg);
        txt_username = (TextView) findViewById(R.id.username_txt);
        boxid = (TextView) findViewById(R.id.box_id_txt);
        ipAddress_txt = (TextView) findViewById(R.id.ip_address_txtvw);
        errorCode_txt = (TextView) findViewById(R.id.error_code_txt);
        retry = (Button) findViewById(R.id.exit_button);

        retry.requestFocus();

        error_message=getAppropiateErrorMessage(error_code);
        if (AppConfig.isDevelopment()) {
            macAddress = AppConfig.getMacAddress();
        } else {
            macAddress = GetMac.getMac(this); // Getting mac addresss
        }
        boxid.setText("Box id: " + macAddress);
        errorCode_txt.setText("ERROR CODE: " + error_code);
        messageView.setText(error_message);
        Logger.e(error_code + "code", error_message + "");
        try {
            username = getIntent().getStringExtra("username");
            System.out.println(username);
            if (username == null || username.equals("null")) {
                System.out.println("I am here");
                txt_username.setVisibility(View.INVISIBLE);

            } else {
                txt_username.setVisibility(View.VISIBLE);
                txt_username.setText("username: " + username);

            }
//			txt_username.setVisibility(View.VISIBLE);
            Logger.e("username", username);
        } catch (Exception e) {
            txt_username.setVisibility(View.INVISIBLE);
        }

        try {
            ipAddress = getIntent().getStringExtra("ipAddress");
            if (ipAddress != null) {
//                mb.append("Ip Adress:\t").append(ipAddress).append("\n\n");
                ipAddress_txt.setVisibility(View.VISIBLE);
                ipAddress_txt.setText("IP Address: "+ipAddress);}
        } catch (Exception e) {
            Logger.printStackTrace(e);
            ipAddress = "";
            ipAddress_txt.setVisibility(View.INVISIBLE);
        }

        if (error_code.equals(""))
           errorCode_txt.setVisibility(View.INVISIBLE);

        else
            /*mb.append("Box ID: " + macAddress.toUpperCase())
                    .append("\n")
                    .append("Error Code: " + error_code)
                    .append("\n")
                    .append(error_message);*/
            errorCode_txt.setText("Error Code: "+error_code);

//        messageView.setText(mb.toString());
        retry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private String getAppropiateErrorMessage(String error_code) {
        switch(error_code){
            case "401":
                error_message=getString(R.string.user_not_registered);
                break;
            case "402":
                error_message=getString(R.string.user_not_active);
                break;
            case "403":
                error_message=getString(R.string.user_not_approved);
                break;
            case "404":
                error_message=getString(R.string.mac_not_registered);
                break;
        }
        return  error_message;
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }
}
