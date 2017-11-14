package com.example.chted.chatprojet;

import android.app.Application;
import android.provider.SyncStateContract;

import io.socket.client.IO;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.Manager;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chted on 25/10/2017.
 */

public class MyApplication extends Application {

    private LoginService loginService;
    private RegisterService registerService;
    private ReceiveMessagesService receiveMessagesService;
    private SendMessagesService sendMessagesService;
    private Socket socket;
    private IO.Options opts;

    {
        try {


            Manager.Options options = new Manager.Options();
            options.path = "/chat-rest/socket.io";
            Manager mManager = new Manager(new URI("https://training.loicortola.com"), options);
            socket = mManager.socket("/2.0/ws");

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://training.loicortola.com/chat-rest/2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();




        loginService = retrofit.create(LoginService.class);
        registerService = retrofit.create(RegisterService.class);
        receiveMessagesService = retrofit.create(ReceiveMessagesService.class);
        sendMessagesService = retrofit.create(SendMessagesService.class);

    }

    public LoginService getLoginService() {
        return loginService;
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

}
