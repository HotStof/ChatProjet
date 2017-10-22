package com.example.chted.chatprojet;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.StatusLine;


public class LoginActivity extends AppCompatActivity {
    private Button submitBtn;
    private Button clearBtn ;
    private ProgressBar progressBar ;
    private EditText usernameForm ;
    private EditText passwordForm ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        submitBtn = (Button) findViewById(R.id.submitbtn);
        clearBtn = (Button) findViewById(R.id.clearbtn);
        progressBar = (ProgressBar) findViewById(R.id.progbar);
        usernameForm = (EditText) findViewById(R.id.edit_username);
        passwordForm = (EditText) findViewById(R.id.edit_password);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameForm.getText().toString();
                String password = passwordForm.getText().toString();
                new LoginTask().execute("https://training.loicortola.com/chat-rest/1.0/connect/"+username+"/"+password);
            }

        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameForm.setText("");
                passwordForm.setText("");


            }
        });


    }

    private class LoginTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            OkHttpClient httpclient = new OkHttpClient();
            Response response;
            String responseString = null;
            try {
                Request request = new Request.Builder().url(uri[0]).build();
                response = httpclient.newCall(request).execute();
                if(response.code() == 200){
                    responseString = response.toString();
                    Intent intent = new Intent(LoginActivity.this,ChatActivity.class);
                    startActivity(intent);
                } else{
                    //Closes the connection.
                    String Err_message = response.message();
                    response.body().close();
                    throw new IOException(Err_message);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE );
        }

        @Override
        protected void onPostExecute(String result) {
            //HTTP GET
            progressBar.setVisibility(View.INVISIBLE );


        }
    }
}