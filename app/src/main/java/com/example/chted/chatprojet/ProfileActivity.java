package com.example.chted.chatprojet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.UUID;

import okhttp3.Credentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText passwordForm ;
    private EditText emailForm ;
    private ImageButton profileImage ;
    Button saveBtn;
    String email;
    String password;
    String token;
    JsonObject json;
    JsonObject picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final ProfileService profileService = ((MyApplication) getApplicationContext()).getProfileService();

        saveBtn = (Button) findViewById(R.id.savebtn);
        passwordForm = (EditText) findViewById(R.id.edit_password);
        emailForm = (EditText) findViewById(R.id.edit_email);
        profileImage = (ImageButton) findViewById(R.id.edit_image);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = passwordForm.getText().toString();
                email = emailForm.getText().toString();
                //image = profileImage.getResources().toString();
                token = getIntent().getStringExtra("token");
                json = new JsonObject();
                json.addProperty("password", password);
                json.addProperty("email", email);
                picture = new JsonObject();
                picture.addProperty("mimeType","image/png");
                picture.addProperty("data","https://www.google.fr/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwj4wMeekLTXAhWFxxQKHciNDaYQjRwIBw&url=https%3A%2F%2Ftplugin.com%2F&psig=AOvVaw3IUKneYiFua0Xx8ximJl_T&ust=1510407406146244");
                json.add("picture", picture);
                Call<ResponseBody> connect = profileService.edit(token, json);
                connect.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            String username = getIntent().getStringExtra("username");
                            token = Credentials.basic(username,password);
                            Intent intent = new Intent(ProfileActivity.this,ChatActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        } else{
                            //Closes the connection.
                            String Err_message = response.message();
                            Toast.makeText(ProfileActivity.this, Err_message, Toast.LENGTH_LONG).show();
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