package com.example.chted.chatprojet;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
/**
 * Created by chted on 25/10/2017.
 */

public interface RegisterService {

    @POST("register")
    Call<ResponseBody> register(@Body JsonObject userRegister);

}
