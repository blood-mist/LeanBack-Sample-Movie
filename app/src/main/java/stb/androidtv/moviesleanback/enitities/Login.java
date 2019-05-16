package stb.androidtv.moviesleanback.enitities;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Login extends RealmObject implements Parcelable {
    @SerializedName("parent")
    private int parent;

    @SerializedName("role")
    private String role;

    @Ignore
    private String activationCode;

    @SerializedName("session")
    private String session;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("package_id")
    private int packageId;

    @SerializedName("mac_id")
    private int macId;

    @SerializedName("lname")
    private String lname;

    @SerializedName("community_id")
    private int communityId;

    @SerializedName("signup_date")
    private String signupDate;

    @SerializedName("updated_at")
    private String updatedAt;

    @Ignore
    private String cardLastFour;

    @SerializedName("subscription_status")
    private int subscriptionStatus;

    @Ignore
    private String cardBrand;

    @SerializedName("app_info")
    private String appInfo;


    @SerializedName("id")
    @Expose
    @PrimaryKey
    private int id;

    @SerializedName("email")
    private String email;

    @Ignore
    private String stripeId;

    @SerializedName("fname")
    private String fname;

    @SerializedName("is_active")
    private int isActive;

    @SerializedName("expiry_date")
    private String expiryDate;

    @SerializedName("payment_status")
    private int paymentStatus;

    @Ignore
    private String firebaseToken;

    @SerializedName("token")
    private String token;

    @SerializedName("phone")
    private String phone;

    @Ignore
    private String updatedBy;

    @SerializedName("subscription_period")
    private String subscriptionPeriod;

    @Ignore
    private String signupIp;

    @Ignore
    private String subscriptionType;

    @SerializedName("currency_id")
    private String currencyId;

    @SerializedName("status")
    private int status;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    private String userPassword;

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getParent() {
        return parent;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setMacId(int macId) {
        this.macId = macId;
    }

    public int getMacId() {
        return macId;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getLname() {
        return lname;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setSignupDate(String signupDate) {
        this.signupDate = signupDate;
    }

    public String getSignupDate() {
        return signupDate;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public Object getCardLastFour() {
        return cardLastFour;
    }

    public void setSubscriptionStatus(int subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public int getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public Object getCardBrand() {
        return cardBrand;
    }

    public void setAppInfo(String appInfo) {
        this.appInfo = appInfo;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setStripeId(String stripeId) {
        this.stripeId = stripeId;
    }

    public Object getStripeId() {
        return stripeId;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFname() {
        return fname;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public Object getFirebaseToken() {
        return firebaseToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setSubscriptionPeriod(String subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public String getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setSignupIp(String signupIp) {
        this.signupIp = signupIp;
    }

    public Object getSignupIp() {
        return signupIp;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Object getSubscriptionType() {
        return subscriptionType;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return
                "Login{" +
                        "parent = '" + parent + '\'' +
                        ",role = '" + role + '\'' +
                        ",activation_code = '" + activationCode + '\'' +
                        ",session = '" + session + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",package_id = '" + packageId + '\'' +
                        ",mac_id = '" + macId + '\'' +
                        ",lname = '" + lname + '\'' +
                        ",community_id = '" + communityId + '\'' +
                        ",signup_date = '" + signupDate + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",card_last_four = '" + cardLastFour + '\'' +
                        ",subscription_status = '" + subscriptionStatus + '\'' +
                        ",card_brand = '" + cardBrand + '\'' +
                        ",app_info = '" + appInfo + '\'' +
                        ",id = '" + id + '\'' +
                        ",email = '" + email + '\'' +
                        ",stripe_id = '" + stripeId + '\'' +
                        ",fname = '" + fname + '\'' +
                        ",is_active = '" + isActive + '\'' +
                        ",expiry_date = '" + expiryDate + '\'' +
                        ",payment_status = '" + paymentStatus + '\'' +
                        ",firebase_token = '" + firebaseToken + '\'' +
                        ",token = '" + token + '\'' +
                        ",phone = '" + phone + '\'' +
                        ",updated_by = '" + updatedBy + '\'' +
                        ",subscription_period = '" + subscriptionPeriod + '\'' +
                        ",signup_ip = '" + signupIp + '\'' +
                        ",subscription_type = '" + subscriptionType + '\'' +
                        ",currency_id = '" + currencyId + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.parent);
        dest.writeString(this.role);
        dest.writeString(this.activationCode);
        dest.writeString(this.session);
        dest.writeString(this.createdAt);
        dest.writeInt(this.packageId);
        dest.writeInt(this.macId);
        dest.writeString(this.lname);
        dest.writeInt(this.communityId);
        dest.writeString(this.signupDate);
        dest.writeString(this.updatedAt);
        dest.writeString(this.cardLastFour);
        dest.writeInt(this.subscriptionStatus);
        dest.writeString(this.cardBrand);
        dest.writeString(this.appInfo);
        dest.writeInt(this.id);
        dest.writeString(this.email);
        dest.writeString(this.stripeId);
        dest.writeString(this.fname);
        dest.writeInt(this.isActive);
        dest.writeString(this.expiryDate);
        dest.writeInt(this.paymentStatus);
        dest.writeString(this.firebaseToken);
        dest.writeString(this.token);
        dest.writeString(this.phone);
        dest.writeString(this.updatedBy);
        dest.writeString(this.subscriptionPeriod);
        dest.writeString(this.signupIp);
        dest.writeString(this.subscriptionType);
        dest.writeString(this.currencyId);
        dest.writeInt(this.status);
        dest.writeString(this.userPassword);
    }

    public Login() {
    }

    protected Login(Parcel in) {
        this.parent = in.readInt();
        this.role = in.readString();
        this.activationCode = in.readParcelable(Object.class.getClassLoader());
        this.session = in.readString();
        this.createdAt = in.readString();
        this.packageId = in.readInt();
        this.macId = in.readInt();
        this.lname = in.readString();
        this.communityId = in.readInt();
        this.signupDate = in.readString();
        this.updatedAt = in.readString();
        this.cardLastFour = in.readParcelable(Object.class.getClassLoader());
        this.subscriptionStatus = in.readInt();
        this.cardBrand = in.readParcelable(Object.class.getClassLoader());
        this.appInfo = in.readString();
        this.id = in.readInt();
        this.email = in.readString();
        this.stripeId = in.readParcelable(Object.class.getClassLoader());
        this.fname = in.readString();
        this.isActive = in.readInt();
        this.expiryDate = in.readString();
        this.paymentStatus = in.readInt();
        this.firebaseToken = in.readParcelable(Object.class.getClassLoader());
        this.token = in.readString();
        this.phone = in.readString();
        this.updatedBy = in.readParcelable(Object.class.getClassLoader());
        this.subscriptionPeriod = in.readString();
        this.signupIp = in.readParcelable(Object.class.getClassLoader());
        this.subscriptionType = in.readParcelable(Object.class.getClassLoader());
        this.currencyId = in.readString();
        this.status = in.readInt();
        this.userPassword = in.readString();
    }

    public static final Parcelable.Creator<Login> CREATOR = new Parcelable.Creator<Login>() {
        @Override
        public Login createFromParcel(Parcel source) {
            return new Login(source);
        }

        @Override
        public Login[] newArray(int size) {
            return new Login[size];
        }
    };
}