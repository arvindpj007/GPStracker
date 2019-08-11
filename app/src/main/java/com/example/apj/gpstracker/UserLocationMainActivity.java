package com.example.apj.gpstracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class UserLocationMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mMap;
    FirebaseAuth auth;
    FirebaseUser user;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLng;
    DatabaseReference databaseReference;

    String current_username,current_email,current_imageurl,current_code;

    TextView title_text,email_text,code_text;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user= auth.getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");

        View header= navigationView.getHeaderView(0);
        title_text= header.findViewById(R.id.title_text);
        email_text= header.findViewById(R.id.email_text);
        imageView= header.findViewById(R.id.imageView);
        code_text=header.findViewById(R.id.title_code);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                current_username= dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                current_email= dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                current_code= dataSnapshot.child(user.getUid()).child("code").getValue(String.class);
                current_imageurl= dataSnapshot.child(user.getUid()).child("imageUri").getValue(String.class);

                //Toast.makeText(getApplicationContext(),"x",Toast.LENGTH_LONG).show();

                title_text.setText(current_username);
                email_text.setText(current_email);
                code_text.setText("circle code: "+current_code);
                Picasso.get().load(current_imageurl).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_location_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final MenuItem share=item;
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_shareloc) {

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(user.getUid()).child("issharing").getValue().equals("false"))
                    {
                        Toast.makeText(getApplicationContext(),"LOCATION SHARING: ON",Toast.LENGTH_SHORT).show();
                        share.setTitle("STOP SHARING");
                        databaseReference.child(user.getUid()).child("issharing").setValue("true");
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"LOCATION SHARING: OFF",Toast.LENGTH_SHORT).show();
                        share.setTitle("START SHARING");
                        databaseReference.child(user.getUid()).child("lat").setValue(String.valueOf("na"));
                        databaseReference.child(user.getUid()).child("lng").setValue(String.valueOf("na"));
                        databaseReference.child(user.getUid()).child("issharing").setValue("false");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //Toast.makeText(getApplicationContext(),"SHARING LOCATION IS SWITCHED ON",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_joincircle)
        {
            Intent intent= new Intent(UserLocationMainActivity.this,JoinCircleActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_mycircle) {

            //Intent intent= new Intent(UserLocationMainActivity.this,MyCircleActivity.class);
            //startActivity(intent);

        } else if (id == R.id.nav_joinedcircle) {

        } else if (id == R.id.nav_sharelocvia) {

            Intent intent= new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,"My Location: "+"https://www.google.com/maps/@"+latLng.latitude+","+latLng.longitude+",17z");
            startActivity(intent.createChooser(intent,"Share using:"));

        } else if (id == R.id.nav_invite) {

        } else if (id == R.id.nav_signout)
        {

            user= auth.getCurrentUser();
            if(user!=null)
            {
                auth.signOut();
                Intent intent= new Intent(UserLocationMainActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {

        if(location==null)
        {

            Toast.makeText(getApplicationContext(),"Could Not Get Location",Toast.LENGTH_SHORT).show();

        }
        else
        {
            latLng= new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            options.title("Current Location");
            mMap.clear();
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            databaseReference.child(user.getUid()).child("lat").setValue(String.valueOf(latLng.latitude));
            databaseReference.child(user.getUid()).child("lng").setValue(String.valueOf(latLng.longitude));

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(user.getUid()).hasChild("circleMembers"))
                    {
                        for(DataSnapshot circlemembers: dataSnapshot.child(user.getUid()).child("circleMembers").getChildren())
                        {

                            String circlemember=circlemembers.child("circlememberid").getValue(String.class);
                            if(dataSnapshot.child(circlemember).child("issharing").getValue().toString().equals("true"))
                            {
                                LatLng latLng1= new LatLng(Double.valueOf(dataSnapshot.child(circlemember).child("lat").getValue().toString()),Double.valueOf(dataSnapshot.child(circlemember).child("lng").getValue().toString()));
                                MarkerOptions options1 = new MarkerOptions();
                                options1.position(latLng1);
                                options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                options1.title(dataSnapshot.child(circlemember).child("name").getValue().toString()+" Location");
                                mMap.addMarker(options1);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(3000);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(request,
                new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                }, Looper.myLooper());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
