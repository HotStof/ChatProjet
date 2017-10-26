package com.example.chted.chatprojet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class RegisterActivity extends AppCompatActivity {

    private Button submitBtn;
    private Button registerBtn;
    private Button clearBtn ;
    private ProgressBar progressBar ;
    private EditText usernameForm ;
    private EditText passwordForm ;
    String username;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final RegisterService registerService = ((MyApplication) getApplicationContext()).getRegisterService();
        setContentView(R.layout.activity_register);
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
                password = passwordForm.getText().toString();
                progressBar.setVisibility(View.VISIBLE );
                JsonObject userRegister = new JsonObject();
                userRegister.addProperty("login",username);
                userRegister.addProperty("password",password);
                Call<ResponseBody> register = registerService.register(userRegister);
                register.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if(response.code() == 200){
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.INVISIBLE );
                        } else{
                            //Closes the connection.
                            String Err_message = response.message();
                            progressBar.setVisibility(View.INVISIBLE );
                            Toast.makeText(RegisterActivity.this, Err_message, Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }

        });


    }
}
