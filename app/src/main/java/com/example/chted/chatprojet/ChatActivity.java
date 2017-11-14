package com.example.chted.chatprojet;

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
import com.google.gson.JsonParser;

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
    private Button sendBtn;
    private JsonObject json;
    private  ArrayList<JsonObject> myDataset;
    private String token;
    private String limit;
    private String offset;
    private String head;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final ReceiveMessagesService receiveMessagesService = ((MyApplication) getApplicationContext()).getReceiveMessagesService();
        final SendMessagesService sendMessagesService = ((MyApplication) getApplicationContext()).getSendMessagesService();
        final Socket socket = ((MyApplication) getApplicationContext()).getSocket();


        sendBtn = (Button) findViewById(R.id.sendbtn);
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
        limit = "20";
        offset="0";
        head="";


        callReceiveService(receiveMessagesService,limit,offset,head,token);


        MyAdapter adapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(adapter);

        socket.on("inbound_msg",onNewMessage);
        socket.connect();



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = getIntent().getStringExtra("token");
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


    }

    void callReceiveService(ReceiveMessagesService receiveMessagesService, String limit, String offset, String head, String token){
        Call<List<JsonObject>> receive = receiveMessagesService.receive(token,limit, offset,head);
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




                JsonParser jsonParser = new JsonParser();
                JsonObject jsonToView = (JsonObject)jsonParser.parse(json.toString());
                finish();
                startActivity(getIntent());

            } catch (JSONException e) {
                Log.e("onNewMessage","ERROR !");

            }

        }
    };


}
