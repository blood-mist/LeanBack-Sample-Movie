package stb.androidtv.moviesleanback.utils;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import stb.androidtv.moviesleanback.enitities.GeoAccessInfo;
import stb.androidtv.moviesleanback.enitities.MacInfo;
import stb.androidtv.moviesleanback.enitities.TimeStampEntity;

public interface ApiInterface {
    @GET(LinkConfig.CHECK_MAC)
    Observable<Response<MacInfo>> checkMacValidation(@Query("mac") String macAddress);

    @GET(LinkConfig.ALLOW_COUNTRY)
    Observable<Response<GeoAccessInfo>> checkGeoAccess();

    @GET(LinkConfig.LINK_SEVER_APKs)
    Observable<Response<ResponseBody>> checkForAppVersion(@Query("macAddress") String macAddress, @Query("versionCode") int versionCode
            , @Query("versionName") String versionName, @Query("packageName") String packageName);

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @GET(LinkConfig.CHECK_BEFORE_LOGIN_URL)
    Observable<Response<ResponseBody>> checkUserStatus(@Query("boxId") String macAddress);

    @POST(LinkConfig.LOGIN_BUTTON_CLICK)
    @FormUrlEncoded
    Observable<Response<ResponseBody>> signIn(@Field("uname") String userEmail, @Field("pswd") String userPassword, @Field("boxId") String boxId);


    @GET(LinkConfig.GET_UTC)
    Observable<Response<TimeStampEntity>> getTimestamp();

    @GET(LinkConfig.MOVIE_PARENT_CATEGORY_URL)
    Observable<Response<ResponseBody>> getMovieCatList(@Header("Authorization") String token, @Query("utc") long utc, @Query("userId") String userId, @Query("hash")String hash,@Query("mac_id")String macAddress);

    @GET(LinkConfig.MOVIE_CATEGORY_DETAIL)
    Observable<Response<ResponseBody>> getMovieData(@Header("Authorization")String token,@Query("utc") long utc,@Query("userId") String userId,@Query("hash") String hashCode,
                                                    @Query("mac_id") String macAddress,@Query("parentId") String categoryId);
    @GET(LinkConfig.MOVIE_PLAY_LINK)
    Observable<Response<ResponseBody>> getMovieLink(@Header("Authorization")String token,@Query("utc") long utc,@Query("userId") String userId,@Query("hash") String hashCode,
                                                    @Query("mac_id") String macAddress,@Query("movieId") int movieId);
}
