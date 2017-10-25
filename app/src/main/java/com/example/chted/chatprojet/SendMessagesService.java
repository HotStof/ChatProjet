package com.example.chted.chatprojet;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by chted on 25/10/2017.
 */

interface SendMessagesService {
    @POST("messages/{username}/{password}")
    Call<ResponseBody> send(@Path("username") String username, @Path("password") String password, @Body JsonObject messageSent);
}
