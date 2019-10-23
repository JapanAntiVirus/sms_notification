package com.example.sms_viettinbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference database;
    private ListView lvSms;
    private ArrayList<String> sms;


    private String getTime(){
        return new Date().getTime() + "";
    }
    private void Toasts(String s){
        Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS},2);
        }
        if(!isNetworkConnected()){
            Toasts("Chưa kết nối internet");
        }

        database = FirebaseDatabase.getInstance().getReference();
        Intent intent = new Intent(getApplicationContext(), SmsServices.class);
        try{
            stopService(intent);
        }
        catch (Exception e){

        }

        startService(intent);


        lvSms = (ListView) findViewById(R.id.lvSms);
        sms = new ArrayList<String>();

        database.child("sms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                database.child("sms").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sms.clear();
                        for(DataSnapshot dt : dataSnapshot.getChildren()){
                            sms.add(dt.getValue(String.class));
                        }
                        for(int i=0; i<sms.size()/2; i++){
                            String tg = sms.get(i);
                            sms.set(i,sms.get(sms.size()-i-1));
                            sms.set(sms.size()-i-1,tg);
                        }
                        ArrayAdapter smsAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,sms);
                        lvSms.setAdapter(smsAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
