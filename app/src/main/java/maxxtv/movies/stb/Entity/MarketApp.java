package maxxtv.movies.stb.Entity;

public class MarketApp {
    String name;
    String version;
    String appDownloadLink;
    String appImageLink;
    String packageName;
    int versionCode;

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