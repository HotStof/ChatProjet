package com.example.chted.chatprojet;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.http.Path;

/**
 * Created by chted on 25/10/2017.
 */
public interface LoginService {
    @GET("connect/{username}/{password}")
    Call<ResponseBody> connect(@Path("username") String username, @Path("password") String password);

}
