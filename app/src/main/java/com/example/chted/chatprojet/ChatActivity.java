package com.example.chted.chatprojet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.getActivity;

public class ChatActivity extends AppCompatActivity {

    private MyAdapter adapter;
    RecyclerView recyclerView;
    private EditText messageForm;
    private JsonObject json;
    private  ArrayList<JsonObject> myDataset;
    private Socket socket;
    private Runnable runnable;
    String token;
    String login;
    String username;
    String uuid;
    String limit = "100";
    String offset = "0";
    String head = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ReceiveMessagesService receiveMessagesService = ((MyApplication) getApplicationContext()).getReceiveMessagesService();
        final SendMessagesService sendMessagesService = ((MyApplication) getApplicationContext()).getSendMessagesService();
        socket = ((MyApplication) getApplicationContext()).getSocket();


        Button sendBtn = (Button) findViewById(R.id.sendbtn);
        Button profileBtn = (Button) findViewById(R.id.edit_profile);
        Button searchProfileBtn = (Button) findViewById(R.id.search_profile);
        messageForm = (EditText) findViewById(R.id.edit_message);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        myDataset = new ArrayList<>();

        token = getIntent().getStringExtra("token");
        username = getIntent().getStringExtra("username");
        callReceiveService(receiveMessagesService, limit, offset, head, token);
        socket.on("inbound_msg", onNewMessage);
        socket.on("post_success_msg ",onNewMessage);
        socket.connect();

        //adapter = new MyAdapter(myDataset);
        //recyclerView.setAdapter(adapter);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login = getIntent().getStringExtra("username");
                json = new JsonObject();
                uuid = UUID.randomUUID().toString();
                json.addProperty("uuid", uuid);
                json.addProperty("login", login);
                json.addProperty("message", messageForm.getText().toString());
                Call<ResponseBody> connect = sendMessagesService.send(token, json);
                connect.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            messageForm.setText("");
                            Toast.makeText(ChatActivity.this, "Message envoyé", Toast.LENGTH_LONG).show();
                            JsonObject jsonresp = new JsonObject();
                            jsonresp.addProperty("login",login);
                            jsonresp.addProperty("token",token);
                            jsonresp.addProperty("uuid",uuid);
                            jsonresp.addProperty("message",messageForm.getText().toString());
                            jsonresp.addProperty("attachments","");
                            socket.emit("outbound_msg",jsonresp);


                        } else {
                            //Closes the connection.
                            String Err_message = response.message();
                            Toast.makeText(ChatActivity.this, Err_message, Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {


                    }
                });
            }

        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });

        searchProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, SearchProfileActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });


    }

    void callReceiveService(ReceiveMessagesService receiveMessagesService, String limit, String offset, String head, String token){
        Call<List<JsonObject>> receive = receiveMessagesService.receive(token,limit, offset,head);
        receive.enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.code() == 200) {
                    myDataset.addAll(response.body());
                    adapter = new MyAdapter(myDataset);
                    recyclerView.setAdapter(adapter);


                } else {
                    //Closes the connection.
                    String Err_message = response.message();
                    //progressBar.setVisibility(View.INVISIBLE );
                    Toast.makeText(ChatActivity.this, Err_message, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {


            }
        });
    }
    Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("login");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    addMessage(username, message);
                }
            });
        }
    };

    private void addMessage(String name,String message){
        System.out.println("Message reçu de :" + name + "\tMessage :" + message);
         JsonObject json = new JsonObject();

        json.addProperty("login",name);
        json.addProperty("message",message);;

        adapter.add(json);

        recyclerView.setAdapter(adapter);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        socket.disconnect();
//    }





}
