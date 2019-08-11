package com.example.apj.gpstracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;

import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    Button signup,login;

    FirebaseAuth auth;
    FirebaseUser user;

    PermissionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth= FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        if(user == null)
        {
            manager = new PermissionManager() { };
            manager.checkAndRequestPermissions(this);
        }
        else
        {
            Intent intent= new Intent(MainActivity.this,UserLocationMainActivity.class);
            startActivity(intent);
            finish();
        }

        login=(Button) findViewById(R.id.main_login);
        signup=(Button) findViewById(R.id.main_signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginActivity(v);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignupActivity(v);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        manager.checkResult(requestCode,permissions, grantResults);

        ArrayList<String> denied_permissions= manager.getStatus().get(0).denied;

        if(denied_permissions.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Permissions Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoLoginActivity(View v)
    {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void gotoSignupActivity(View v)
    {
        Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
