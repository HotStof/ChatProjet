package com.example.chted.chatprojet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ReceiveMessagesService receiveMessagesService = ((MyApplication) getApplicationContext()).getReceiveMessagesService();
        final SendMessagesService sendMessagesService = ((MyApplication) getApplicationContext()).getSendMessagesService();
        final Socket socket = ((MyApplication) getApplicationContext()).getSocket();


        Button sendBtn = (Button) findViewById(R.id.sendbtn);
        Button profileBtn = (Button) findViewById(R.id.edit_profile);

        messageForm = (EditText) findViewById(R.id.edit_message);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        myDataset = new ArrayList<>();
        token = getIntent().getStringExtra("token");
        username = getIntent().getStringExtra("username");
        String limit = "20";
        String offset = "0";
        String head = "";


        callReceiveService(receiveMessagesService, limit, offset, head, token);


        MyAdapter adapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(adapter);
        socket.connect();

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
                            socket.emit("outbound_msg");

                            messageForm.setText("");
                            socket.on("bad_request_msg ", onNewMessage);
                            //Toast.makeText(ChatActivity.this, "Message envoyé", Toast.LENGTH_LONG).show();

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
            Toast.makeText(ChatActivity.this, "Listener OK !", Toast.LENGTH_LONG).show();

        }
    };


}
