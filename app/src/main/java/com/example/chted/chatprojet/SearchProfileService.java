package com.example.chted.chatprojet;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;


/**
 * Created by chted on 25/10/2017.
 */

interface SearchProfileService {
    @GET("profile/{login}")
    Call<ResponseBody> search(@Header("Authorization") String token, @Path("login") String login);
}
