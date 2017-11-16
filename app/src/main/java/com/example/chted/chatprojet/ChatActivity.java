package com.example.chted.chatprojet;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;
import java.util.UUID;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText messageForm;
    private JsonObject json;
    private  ArrayList<JsonObject> myDataset;
    String token;
    String username;
    String limit = "100";
    String offset = "0";
    String head = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ReceiveMessagesService receiveMessagesService = ((MyApplication) getApplicationContext()).getReceiveMessagesService();
        final SendMessagesService sendMessagesService = ((MyApplication) getApplicationContext()).getSendMessagesService();
        final Socket socket = ((MyApplication) getApplicationContext()).getSocket();


        Button sendBtn = (Button) findViewById(R.id.sendbtn);
        Button profileBtn = (Button) findViewById(R.id.edit_profile);
        Button searchProfileBtn = (Button) findViewById(R.id.search_profile);
        messageForm = (EditText) findViewById(R.id.edit_message);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        myDataset = new ArrayList<>();
        token = getIntent().getStringExtra("token");
        username = getIntent().getStringExtra("username");

        socket.connect();
        socket.on("inbound_msg",onNewMessage);
        MyAdapter adapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(adapter);



        callReceiveService(receiveMessagesService, limit, offset, head, token);



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = getIntent().getStringExtra("username");
                json = new JsonObject();
                json.addProperty("uuid", UUID.randomUUID().toString());
                json.addProperty("login", login);
                json.addProperty("message", messageForm.getText().toString());
                Call<ResponseBody> connect = sendMessagesService.send(token, json);
                connect.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            messageForm.setText("");
                            Toast.makeText(ChatActivity.this, "Message envoyé", Toast.LENGTH_LONG).show();
                            socket.emit("outbound_msg");

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
                socket.close();

            }
        });

        searchProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, SearchProfileActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("username", username);
                startActivity(intent);
                socket.close();

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
            Log.i("onNewMessage","Message reçu");

            try {
                JSONObject json = (JSONObject) args[0];
                String login;
                String uuid;
                String message;
                //String images;

                login = json.getString("login");
                uuid = json.getString("uuid");
                message = json.getString("message");
                //pas sûre que le get JSON marche ainsi :o
                //attachement = json.getJSONObject("attachement");
                //images = json.getString("images");

                Log.i("onNewMessage",json.getString("message"));
                Log.i("onNewMessage",json.toString());
                Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("username", username);
                startActivity(intent);
                //finish();
                //startActivity(getIntent());

            } catch (JSONException e) {
                Log.e("onNewMessage","ERROR !");

            }

        }
    };


}
