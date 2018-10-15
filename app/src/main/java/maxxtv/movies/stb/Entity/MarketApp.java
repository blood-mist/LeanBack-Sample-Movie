package maxxtv.movies.stb.Entity;

public class MarketApp {
    /**
     "id":5,
     "display_name":"Movies",
     "package_name":"androidtv.movies.stb",
     "image":"https:\/\/middleware.yourman.info\/market_app_info\/uploads\/images\/app17-07-05-03-55-37.png",
     "myapp_image":"https:\/\/middleware.yourman.info\/market_app_info\/uploads\/images\/17-07-06-10-49-56.png",
     "version_name":"1.0.8",
     "description":"Movies",
     "version_code":8,
     "visibility":true,
     "launcher_display":true,
     "apk_download_link":"https:\/\/middleware.yourman.info\/market_app_info\/uploads\/apk\/test_androidtv.movies.stb-2018-10-15-06-13-33.apk",
     "update_type":"force",
     "update":false,
     "is_allow":true,
     "priority":2
     */
    String name;
    String version;
    String appDownloadLink;
    String appImageLink;
    String packageName;
    int versionCode;
    boolean update;
    String updateType;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    boolean visibility;

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppDownloadLink() {
        return appDownloadLink;
    }

    public void setAppDownloadLink(String appDownloadLink) {
        this.appDownloadLink = appDownloadLink;
    }

    public String getAppImageLink() {
        return appImageLink;
    }

    public void setAppImageLink(String appImageLink) {
        this.appImageLink = appImageLink;
    }

    public String getAppPackageName() {
        return packageName;
    }

    public void setAppPackageName(String packageName) {
        this.packageName = packageName;
    }

}