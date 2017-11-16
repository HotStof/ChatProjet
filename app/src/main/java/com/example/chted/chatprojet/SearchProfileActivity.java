package com.example.chted.chatprojet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import java.util.ArrayList;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchProfileActivity extends AppCompatActivity {

    private EditText searchUserForm ;
    private TextView email;
    private TextView login;
    private ImageView userImage;
    private String username;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);
        final SearchProfileService searchProfileService = ((MyApplication) getApplicationContext()).getSearchProfileService();
        ImageButton searchBtn = (ImageButton) findViewById(R.id.search_user_btn);
        Button BackBtn = (Button) findViewById(R.id.backbtn);
        searchUserForm = (EditText) findViewById(R.id.search_user_form);
        login = (TextView) findViewById(R.id.user_login);
        email = (TextView) findViewById(R.id.user_email);
        userImage = (ImageView) findViewById(R.id.user_image);
        token = getIntent().getStringExtra("token");
        username = getIntent().getStringExtra("username");

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchProfileActivity.this, ChatActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("username", username);
                startActivity(intent);

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String login_searched = searchUserForm.getText().toString();
                Call<ResponseBody> search = searchProfileService.search(token, login_searched);
                search.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            JSONObject json = null;
                            try {
                                if(userImage != null) {
                                    Glide.with(getApplicationContext()).clear(userImage);
                                }
                                json = new JSONObject(response.body().string());
                                login.setText(String.format("login : %s", json.getString("login")));
                                email.setText(String.format("email : %s", json.getString("email")));
                                String image = json.getString("picture");
                                if(image != null) {
                                    GlideUrl glideUrl = new GlideUrl(image, new LazyHeaders.Builder().addHeader("Authorization", token).build());
                                    GlideApp.with(getApplicationContext()).load(glideUrl).into(userImage);
                                }



                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }


                        } else {
                            //Closes the connection.
                            String Err_message = response.message();
                            Toast.makeText(SearchProfileActivity.this, Err_message, Toast.LENGTH_LONG).show();
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