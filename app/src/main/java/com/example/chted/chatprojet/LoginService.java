package com.example.chted.chatprojet;

        import okhttp3.ResponseBody;
        import retrofit2.Call;
        import retrofit2.http.GET;
        import retrofit2.http.Header;


interface LoginService {
    @GET("connect/")
    Call<ResponseBody> connect(@Header("Authorization") String token) ;

}
