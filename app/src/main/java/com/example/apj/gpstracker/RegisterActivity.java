package com.example.apj.gpstracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

public class RegisterActivity extends AppCompatActivity {

    EditText email;
    Button button;

    FirebaseAuth auth;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email= (EditText) findViewById(R.id.signup_email);
        button= (Button) findViewById(R.id.signup_next);

        auth= FirebaseAuth.getInstance();

        dialog= new ProgressDialog(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPasswordActivity(v);
            }
        });
    }

    public void goToPasswordActivity(View view)
    {
        dialog.setMessage("Checking email address");
        dialog.show();

        auth.fetchProvidersForEmail(email.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if(task.isSuccessful())
                        {
                            dialog.dismiss();
                            boolean check= !task.getResult().getProviders().isEmpty();
                            if(!check)
                            {
                                Intent intent=new Intent(RegisterActivity.this,PasswordActivity.class);
                                intent.putExtra("email",email.getText().toString().trim());
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"This Email has already registered",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}
