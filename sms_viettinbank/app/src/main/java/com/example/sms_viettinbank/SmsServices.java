package com.example.sms_viettinbank;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SmsServices extends Service {
    private DatabaseReference database;
    private Sqlite sql;
    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.package.MyService";
    public static final String NOTIFICATION_CHANNEL_ID_INFO = "com.package.download_info";

    private void showNotification(String body) {
        String title = "Vietinbank";
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    private ChildEventListener childListener;

    private void runAsForeground(){
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
                notificationIntent, Intent.FILL_IN_ACTION);

        Notification notification=new NotificationCompat.Builder(this,"channel-01")
                .setSmallIcon(R.drawable.icon_notification)
                .setContentText("Vietinbank")
                .setContentIntent(pendingIntent).build();

        startForeground(123, notification);

    }

    @Override
    public void onCreate() {
        super.onCreate();
//        runAsForeground();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO, "Download Info", NotificationManager.IMPORTANCE_DEFAULT));
        }
        //All notifications should go through NotificationChannel on Android 26 & above
        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_01",
                    "vietinbank",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);

        }
        runAsForeground();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);



        database = FirebaseDatabase.getInstance().getReference();
        sql = new Sqlite(SmsServices.this,"sms", null, 1);
        sql.query("CREATE TABLE IF NOT EXISTS vietinbank ( content text );");
        try{
            database.child("sms").removeEventListener(childListener);
        }
        catch (Exception e){

        }
        childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String sms = dataSnapshot.getValue(String.class);
                Cursor c = sql.select("SELECT content FROM vietinbank");
                while(c.moveToNext()){
                    String lastSms = c.getString(0);
                    if(lastSms.equals(sms)){
                        return;
                    }
                }
                sql.query("INSERT INTO vietinbank VALUES ('" + sms + "') ");
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(500); // for 500 ms
                }
//                Toast.makeText(getApplicationContext(), "add child", Toast.LENGTH_SHORT).show();
                showNotification(sms);
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
        };


        database.child("sms").addChildEventListener(childListener);
//        Toast.makeText(this, "on create", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        database = FirebaseDatabase.getInstance().getReference();
        sql = new Sqlite(SmsServices.this,"sms", null, 1);
        sql.query("CREATE TABLE IF NOT EXISTS vietinbank ( content text );");
        try{
            database.child("sms").removeEventListener(childListener);
        }
        catch (Exception e){

        }
        childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String sms = dataSnapshot.getValue(String.class);
                Cursor c = sql.select("SELECT content FROM vietinbank");
                while(c.moveToNext()){
                    String lastSms = c.getString(0);
                    if(lastSms.equals(sms)){
                        return;
                    }
                }
                sql.query("INSERT INTO vietinbank VALUES ('" + sms + "') ");
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(500); // for 500 ms
                }
//                Toast.makeText(getApplicationContext(), "add child", Toast.LENGTH_SHORT).show();
                showNotification(sms);
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
        };


        database.child("sms").addChildEventListener(childListener);
//        Toast.makeText(this, "on create", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        database.child("sms").removeEventListener(childListener);
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
