package com.example.apj.gpstracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinCircleActivity extends AppCompatActivity {

    Button button;
    Pinview pinview;

    DatabaseReference databaseReference,currentReference,circleReference;
    FirebaseAuth auth;
    FirebaseUser user;

    String current_user,joinuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_circle);


        pinview=(Pinview) findViewById(R.id.pinView);
        button= (Button) findViewById(R.id.jc_btn);

        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        currentReference= databaseReference.child(user.getUid());

        current_user=user.getUid();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButton(v);
            }
        });

    }


    public void submitButton(View view)
    {

        Query query= databaseReference.orderByChild("code").equalTo(pinview.getValue());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    CreateUser createUser=null;
                    for(DataSnapshot childDss: dataSnapshot.getChildren())
                    {
                        createUser=childDss.getValue(CreateUser.class);
                        joinuserid= createUser.getUserid();

                        circleReference= FirebaseDatabase.getInstance().getReference().child("Users").child(joinuserid).child("circleMembers");

                        CircleJoin circleJoin= new CircleJoin(current_user);
                        CircleJoin circleJoin1= new CircleJoin(joinuserid);

                        circleReference.child(user.getUid()).setValue(circleJoin)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(),"User Joined Circle Successfully",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Invalid Circle Code",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
