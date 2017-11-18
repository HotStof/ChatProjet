package com.example.chted.chatprojet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    String tokenSocket;
    String username;
    TextView notifText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        final ReceiveMessagesService receiveMessagesService = ((MyApplication) getApplicationContext()).getReceiveMessagesService();
        final SendMessagesService sendMessagesService = ((MyApplication) getApplicationContext()).getSendMessagesService();
        final Socket socket = ((MyApplication) getApplicationContext()).getSocket();
        socket.connect();

        notifText = (TextView) findViewById(R.id.isWritting);
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
        tokenSocket=getIntent().getStringExtra("tokenSocket");
        String limit = "20";
        String offset = "0";
        String head = "";





        MyAdapter adapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(adapter);
        callReceiveService(receiveMessagesService, limit, offset, head, token);

        socket.on("inbound_msg",onNewMessage);
        socket.on("user_typing_inbound_msg",onWriteMessage);

/*

        messageForm.setOnKeyListener(new View.OnKeyListener() {


            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("onKeyUp","I am writting");
                JSONObject jsonWriting =new JSONObject();
                try {
                    jsonWriting.put("login",username);
                    jsonWriting.put("token",token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                    socket.emit("user_typing_outbound_msg",jsonWriting);
                    return true;

            }

    });*/
        messageForm.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int after){
                Log.i("onKeyUp","I am writting");
                JSONObject jsonWriting =new JSONObject();
                try {
                    jsonWriting.put("login",username);
                    jsonWriting.put("token",tokenSocket);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("user_typing_outbound_msg",jsonWriting);

            }



            @Override
            public void afterTextChanged(Editable s){
                // do nothing
            }
        });


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
                            JSONObject jsonMsg=new JSONObject();
                            try {
                                jsonMsg.put("login",json.get("login").getAsString());
                                jsonMsg.put("uuid", json.get("uuid").getAsString());
                                jsonMsg.put("token",tokenSocket);
                                jsonMsg.put("message",json.get("message").getAsString());
                                //jsonMsg.put("attachments","");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i("onResponse","Emit Socket" + jsonMsg.toString());
                            socket.emit("outbound_msg",jsonMsg);
                            socket.on("post_success_msg",onPostSuccess);
                            socket.on("bad_request_msg",onPostFail);

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
                socket.disconnect();

            }
        });

        searchProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, SearchProfileActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("username", username);
                startActivity(intent);
                socket.disconnect();

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

               finish();
              startActivity(getIntent());

            } catch (JSONException e) {
                Log.e("onNewMessage","ERROR !");

            }

        }
    };



    Emitter.Listener onPostSuccess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("onPostSuccess","Message sent successfully");

        }
    };

    Emitter.Listener onPostFail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String errorMessage = (String)args[0];
           Log.i("onPostFail",errorMessage);
        }
    };

    Emitter.Listener onWriteMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.i("onWriteMessage","Is writting");
            JSONObject json = (JSONObject) args[0];
            String login;
            try {
                login = json.getString("login");
                Log.i("onWriteMessage",login+"is writing");
                notifText.setText(login+"is writing");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    };

}
