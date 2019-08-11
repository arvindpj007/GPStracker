package com.example.apj.gpstracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    String email;
    EditText password;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Intent intent = getIntent();
        if(intent!=null)
        {
            email= intent.getStringExtra("email");
        }

        password= (EditText) findViewById(R.id.signup_password);
        button=(Button) findViewById(R.id.signup_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNamePickActivity(v);
            }
        });
    }

    public void goToNamePickActivity(View view)
    {

        if(password.getText().toString().length()>6)
        {
            Intent intent= new Intent(PasswordActivity.this,NameActivity.class);
            intent.putExtra("email",email);
            intent.putExtra("password",password.getText().toString().trim());
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Password should be more than 6 characters",Toast.LENGTH_SHORT).show();
        }
    }
}
