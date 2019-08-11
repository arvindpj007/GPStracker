package com.example.apj.gpstracker;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class NameActivity extends AppCompatActivity {


    String email,password;
    EditText name;
    Button button;

    CircleImageView circleImageView;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        circleImageView= (CircleImageView) findViewById(R.id.circleImageView);

        Intent intent=getIntent();
        if(intent!=null)
        {
            email=intent.getStringExtra("email");
            password=intent.getStringExtra("password");
        }

        name= (EditText) findViewById(R.id.signup_name);
        button= (Button) findViewById(R.id.signup_namebtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode(v);
            }
        });
    }

    public void generateCode(View view)
    {

        Date myDate= new Date();
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-mm-dd hh:mm:ss a",Locale.getDefault());
        String date= simpleDateFormat.format(myDate);

        Random random= new Random();

        int n= 100000 + random.nextInt(900000);
        String code = String.valueOf(n);

        if(uri!=null)
        {
            Intent intent= new Intent(NameActivity.this,InviteCodeActivity.class);
            intent.putExtra("Name",name.getText().toString().trim());
            intent.putExtra("Email",email);
            intent.putExtra("Password",password);
            intent.putExtra("Date",date);
            intent.putExtra("isSharing","false");
            intent.putExtra("Code",code);
            intent.putExtra("imageUri",uri);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please choose an Image",Toast.LENGTH_SHORT).show();
        }

    }

    public void selectImage(View view)
    {

        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,12);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==12 && resultCode==RESULT_OK && data!=null)
        {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                circleImageView.setImageURI(uri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
