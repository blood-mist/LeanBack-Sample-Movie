package maxxtv.movies.stb.Parser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import maxxtv.movies.stb.Entity.GroupData;
import maxxtv.movies.stb.Utils.Logger;

public class GroupDataParser {

    private static final String TAG = "com.newitventure.totalcable.account.parser.GroupDataParser";

    public static GroupData groupData;
    private String jsonString;

    public GroupDataParser(String jsonString) {
        this.jsonString = jsonString;
    }

    public void parse() throws JSONException {
        JSONObject value = new JSONObject(jsonString);
        JSONArray jsonArray = value.getJSONArray("login");
        JSONObject root = jsonArray.getJSONObject(0);
        Logger.d("CheckingLoginResult", jsonString);
        groupData = new GroupData();
        groupData.setGroupId(root.getString("id"));
        groupData.setGroupName(root.getString("name"));
        groupData.setGroupLogoLink(root.getString("logo"));
        groupData.setGroupEmail(root.getString("email"));
        groupData.setGroupContact(root.getString("contact_no"));
        groupData.setSignUpDate(root.getString("created_at"));
        groupData.setPassword(root.getString("password"));

        groupData.setUserId(root.getString("user_id"));
        //    groupData.setDisplayName(root.getString("display_name"));
        groupData.setActivationCode(root.getString("activation_code"));
        groupData.setIsActive(root.getString("is_active"));
        //    groupData.setGroupId(root.getString("user_group"));

//        groupData.setUserEmail(root.getString("user_email"));
        groupData.setUserName(root.getString("user_name"));
        groupData.setSession(root.getString("session"));


    }

}
