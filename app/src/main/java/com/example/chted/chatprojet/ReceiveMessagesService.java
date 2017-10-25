package com.example.chted.chatprojet;

import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by chted on 25/10/2017.
 */

interface ReceiveMessagesService {
    @GET("messages/{username}/{password}")
    Call<List<JsonObject>> receive(@Path("username") String username, @Path("password") String password);
}
