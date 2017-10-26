package com.example.chted.chatprojet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import okhttp3.Credentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private Button submitBtn;
    private Button registerBtn;
    private Button clearBtn ;
    private ProgressBar progressBar ;
    private EditText usernameForm ;
    private EditText passwordForm ;
    String token;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final LoginService loginService = ((MyApplication) getApplicationContext()).getLoginService();

        registerBtn = (Button) findViewById(R.id.registerbtn);
        submitBtn = (Button) findViewById(R.id.submitbtn);
        clearBtn = (Button) findViewById(R.id.clearbtn);
        progressBar = (ProgressBar) findViewById(R.id.progbar);
        usernameForm = (EditText) findViewById(R.id.edit_username);
        passwordForm = (EditText) findViewById(R.id.edit_password);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameForm.getText().toString();
                String password = passwordForm.getText().toString();
                token = Credentials.basic(username,password);
                progressBar.setVisibility(View.VISIBLE );
                Call<ResponseBody> connect = loginService.connect(token);
                connect.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code() == 200){
                                Intent intent = new Intent(LoginActivity.this,ChatActivity.class);
                                intent.putExtra("token", token);
                                intent.putExtra("username", username);

                                startActivity(intent);
                                progressBar.setVisibility(View.INVISIBLE );
                            } else{
                                //Closes the connection.
                                String Err_message = response.message();
                                progressBar.setVisibility(View.INVISIBLE );
                                Toast.makeText(LoginActivity.this, Err_message, Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {


                        }
                });

            }

        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameForm.setText("");
                passwordForm.setText("");


            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }

        });


    }

}