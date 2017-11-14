package com.example.chted.chatprojet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
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
    String password;
    LoginService loginService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginService = ((MyApplication) getApplicationContext()).getLoginService();
        final Socket socket = ((MyApplication) getApplicationContext()).getSocket();


        registerBtn = (Button) findViewById(R.id.registerbtn);
        submitBtn = (Button) findViewById(R.id.submitbtn);
        clearBtn = (Button) findViewById(R.id.clearbtn);
        progressBar = (ProgressBar) findViewById(R.id.progbar);
        usernameForm = (EditText) findViewById(R.id.edit_username);
        passwordForm = (EditText) findViewById(R.id.edit_password);


        socket.connect();


        if(socket.connected()){
            Log.i("onCreate","Connected");
        }
        else {
            Log.i("onCreate","Not Connected ");
         }

        socket.on("auth_required",onConnexion);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameForm.getText().toString();
                password = passwordForm.getText().toString();

                JSONObject jsonAuth =new JSONObject();
                try {
                    jsonAuth.put("login",username);
                    jsonAuth.put("password",password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("auth_attempt",jsonAuth);

                socket.on("auth_success",onSuccessAuth);


                socket.on("auth_failed",onFail);

                //il va falloir établir la connexion depuis la socket et non par le bouton.
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

    Emitter.Listener onConnexion = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //Toast.makeText(LoginActivity.this, "Listener OK !", Toast.LENGTH_LONG).show();
            Log.i("onConnexion","OK Connexion !");

        }
    };
    Emitter.Listener onSuccessAuth = new Emitter.Listener() {
        // {"login":"foo","token":"session-token1234","message": "Authentication successful"}
        @Override
        public void call(final Object... args) {

            Log.i("onSuccess","OK AUTHENTICATION");

            try {
                JSONObject json = (JSONObject) args[0];
                String login;
                String token;
                String message;

                    login = json.getString("login");
                    token = json.getString("token");
                    message = json.getString("message");

                Log.i("onSuccess",json.getString("message"));
                Log.i("onSuccess",json.toString());

                sendRequest(token,login);


                } catch (JSONException e) {
                    Log.e("onSuccess","ERROR !");

                }




        }
    };

    private void sendRequest(final String token, final String login) {

    }


    Emitter.Listener onFail = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            args.toString();
            //Toast.makeText(LoginActivity.this, "Listener OK ! - FAiled", Toast.LENGTH_LONG).show();
            String Err_message = "Failed Authentication";
            //progressBar.setVisibility(View.INVISIBLE );
            Toast.makeText(LoginActivity.this, Err_message, Toast.LENGTH_LONG).show();

            Log.i("onFail","FAIL AUTHENTICATION");

        }
    };


}