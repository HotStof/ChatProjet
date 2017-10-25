package com.example.chted.chatprojet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText messageForm;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ReceiveMessagesService receiveMessagesService = ((MyApplication) getApplicationContext()).getReceiveMessagesService();
        final SendMessagesService sendMessagesService = ((MyApplication) getApplicationContext()).getSendMessagesService();
        sendBtn = (Button) findViewById(R.id.sendbtn);
        messageForm = (EditText) findViewById(R.id.edit_message);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        final List<JsonObject> myDataset = new ArrayList<>();
        String login = getIntent().getStringExtra("login");
        String password = getIntent().getStringExtra("password");
        Call<List<JsonObject>> receive = receiveMessagesService.receive(login, password);
        receive.enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.code() == 200) {
                    for (JsonObject currentJSON : response.body()) {
                        myDataset.add(currentJSON);
                    }


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

        MyAdapter adapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(adapter);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = getIntent().getStringExtra("login");
                String password = getIntent().getStringExtra("password");
                JsonObject json = new JsonObject();
                json.addProperty("uuid", UUID.randomUUID().toString());
                json.addProperty("login", login);
                json.addProperty("message", messageForm.getText().toString());
                Call<ResponseBody> connect = sendMessagesService.send(login, password, json);
                connect.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            messageForm.setText("");
                            Toast.makeText(ChatActivity.this, "Message envoyé", Toast.LENGTH_LONG).show();
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
    }
}
