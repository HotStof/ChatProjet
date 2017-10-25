package com.example.chted.chatprojet;

        import okhttp3.Credentials;
        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.Body;
        import retrofit2.http.GET;
        import retrofit2.http.Path;
        import retrofit2.http.Query;

/**
 * Created by chted on 25/10/2017.
 */
public interface LoginService {
    @GET("connect/")
    Call<ResponseBody> connect(@Query("identification") String token) ;

}
