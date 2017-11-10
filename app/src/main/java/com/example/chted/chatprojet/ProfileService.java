package com.example.chted.chatprojet;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


/**
 * Created by chted on 25/10/2017.
 */

interface ProfileService {
    @POST("profile/")
    Call<ResponseBody> edit(@Header("Authorization") String token, @Body JsonObject profileInfo);
}
