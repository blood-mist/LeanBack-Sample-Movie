package stb.androidtv.moviesleanback.enitities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ACER on 10/26/2017.
 */

public class SessionData {

    @SerializedName("session")
    @Expose
    private String session;
    @SerializedName("status")
    @Expose
    private Integer status;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
