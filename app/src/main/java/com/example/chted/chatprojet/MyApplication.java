package com.example.chted.chatprojet;

import android.app.Application;
import io.socket.client.IO;
import java.net.URISyntaxException;
import io.socket.client.Socket;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chted on 25/10/2017.
 */

public class MyApplication extends Application {

    private LoginService loginService;
    private ProfileService profileService;
    private RegisterService registerService;
    private ReceiveMessagesService receiveMessagesService;
    private SendMessagesService sendMessagesService;
    private SearchProfileService searchProfileService;
    private Socket socket;
    {
        try {
            socket = IO.socket("https://training.loicortola.com/chat-rest/2.0/ws");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://training.loicortola.com/chat-rest/2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();




        loginService = retrofit.create(LoginService.class);
        profileService = retrofit.create(ProfileService.class);
        registerService = retrofit.create(RegisterService.class);
        receiveMessagesService = retrofit.create(ReceiveMessagesService.class);
        sendMessagesService = retrofit.create(SendMessagesService.class);
        searchProfileService = retrofit.create(SearchProfileService.class);

    }

    public LoginService getLoginService() {
        return loginService;
    }

    public ProfileService getProfileService() {
        return profileService;
    }

    public RegisterService getRegisterService() {
        return registerService;
    }

    public ReceiveMessagesService getReceiveMessagesService() {
        return receiveMessagesService;
    }

    public SendMessagesService getSendMessagesService() {
        return sendMessagesService;
    }

    public SearchProfileService getSearchProfileService() {return searchProfileService; }

    public Socket getSocket() {
        return socket;
    }

}
