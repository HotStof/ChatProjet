package com.example.chted.chatprojet;

import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by chted on 25/10/2017.
 */

interface ReceiveMessagesService {
    @GET("messages")
    Call<List<JsonObject>> receive(@Header("Authorization") String token, @Query("limit") String limit,@Query("offset") String offset,@Query("head") String head);
}
