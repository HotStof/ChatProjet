package com.example.chted.chatprojet;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
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
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imgPicture;
    private String pictureMimeType = "image/jpeg";
    private String pictureUri = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final ProfileService profileService = ((MyApplication) getApplicationContext()).getProfileService();
        imgPicture = (ImageButton) findViewById(R.id.edit_image);
        saveBtn = (Button) findViewById(R.id.savebtn);
        passwordForm = (EditText) findViewById(R.id.edit_password);
        emailForm = (EditText) findViewById(R.id.edit_email);


        imgPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // invoke the image gallery using an implict intent.
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                // where do we want to find the data?
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();
                // finally, get a URI representation
                Uri data = Uri.parse(pictureDirectoryPath);

                // set the data and type.  Get all image types.
                photoPickerIntent.setDataAndType(data, "image/*");

                // we will invoke this activity, and get something back from it.
                startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = passwordForm.getText().toString();
                email = emailForm.getText().toString();
                token = getIntent().getStringExtra("token");
                json = new JsonObject();
                json.addProperty("password", password);
                json.addProperty("email", email);
                picture = new JsonObject();
                picture.addProperty("mimeType",pictureMimeType);
                picture.addProperty("data",pictureUri);
                json.add("picture", picture);
                Call<ResponseBody> connect = profileService.edit(token, json);
                connect.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200){
                            String username = getIntent().getStringExtra("username");
                            if (!Objects.equals(password, "")) {
                                token = Credentials.basic(username, password);
                            }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // if we are here, everything processed successfully.
            if (requestCode == IMAGE_GALLERY_REQUEST) {
                // if we are here, we are hearing back from the image gallery.

                // the address of the image on the SD Card.
                Uri imageUri = data.getData();
                pictureUri = imageUri.toString();
                // declare a stream to read the image data from the SD Card.
                InputStream inputStream;

                // we are getting an input stream, based on the URI of the image.
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    // show the image to the user
                    imgPicture.setImageBitmap(image);
                    ContentResolver cR = ProfileActivity.this.getContentResolver();
                    pictureMimeType = cR.getType(imageUri);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // show a message to the user indictating that the image is unavailable.
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }

            }
        }

    };

}