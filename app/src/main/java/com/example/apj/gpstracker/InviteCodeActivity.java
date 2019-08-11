package com.example.apj.gpstracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InviteCodeActivity extends AppCompatActivity {

    String name,email,password,code,date,isSharing;
    Uri imageUri;
    String userId;

    ProgressDialog progressDialog;

    TextView tv_code;
    Button button;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference= FirebaseStorage.getInstance().getReference().child("user_images");

        progressDialog=new ProgressDialog(this);

        Intent intent=getIntent();
        if(intent!=null)
        {
            name=intent.getStringExtra("Name");
            email=intent.getStringExtra("Email");
            password=intent.getStringExtra("Password");
            code=intent.getStringExtra("Code");
            date=intent.getStringExtra("Date");
            isSharing=intent.getStringExtra("isSharing");
            imageUri= intent.getParcelableExtra("imageUri");

        }

        tv_code=(TextView) findViewById(R.id.ic_code);
        tv_code.setText(code);

        button=(Button) findViewById(R.id.ic_register);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(v);
            }
        });
    }

    public void registerUser(View view)
    {

        progressDialog.setMessage("Please wait while creating an account");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            firebaseUser = auth.getCurrentUser();
                            userId= firebaseUser.getUid();
                            CreateUser createUser=new CreateUser(name,email,password,code,"false","na","na","na",firebaseUser.getUid());

                            databaseReference.child(userId).setValue(createUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        final StorageReference sr = storageReference.child(firebaseUser.getUid() + ".jpg");
                                        sr.putFile(imageUri)
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {

                                                                String image_path= uri.toString();
                                                                databaseReference.child(firebaseUser.getUid()).child("imageUri").setValue(image_path)
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    progressDialog.dismiss();
                                                                                    Toast.makeText(getApplicationContext(), "Registering User", Toast.LENGTH_SHORT);
                                                                                    sendVerificatoinMail();
                                                                                    Intent intent = new Intent(InviteCodeActivity.this,  MainActivity.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                }
                                                                                else
                                                                                {
                                                                                    progressDialog.dismiss();
                                                                                    Toast.makeText(getApplicationContext()," Registeration Failed",Toast.LENGTH_SHORT);
                                                                                }
                                                                                        }
                                                                        });

                                                            }
                                                        });
                                                    }
                                                });
//                                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                                                if (task.isSuccessful()) {
//
//
//
//                                                    String image_path = task.getResult().getStorage().getDownloadUrl().toString();
//                                                    databaseReference.child(firebaseUser.getUid()).child("imageUri").setValue(image_path)
//                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                    if (task.isSuccessful()) {
//                                                                        progressDialog.dismiss();
//                                                                        Toast.makeText(getApplicationContext(), "Registering User", Toast.LENGTH_SHORT);
//                                                                        sendVerificatoinMail();
//                                                                        Intent intent = new Intent(InviteCodeActivity.this,  MainActivity.class);
//                                                                        startActivity(intent);
//                                                                        finish();
//                                                                    }
//                                                                    else
//                                                                    {
//                                                                        progressDialog.dismiss();
//                                                                        Toast.makeText(getApplicationContext()," Registeration Failed",Toast.LENGTH_SHORT);
//                                                                    }
//                                                                }
//                                                            });
//
//                                                }
//                                            }
//                                        });

                                    }

                                }
                            });
                        }

                    }
                });

    }

    public void sendVerificatoinMail()
    {
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(),"Email sent for verification",Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            return;
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Could not send email",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
