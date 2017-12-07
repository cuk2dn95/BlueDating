package com.example.pc.bluedating.Services;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.pc.bluedating.MainScreen_Function.MainContentActivity;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.BlueDatingApplication;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by KA on 11/22/2017.
 */

public class MyMessageReceiverService extends FirebaseMessagingService {

    LocalBroadcastManager broadcast;
    @Override
    public void onCreate() {
        super.onCreate();
        broadcast = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String,String> data = remoteMessage.getData();
        Intent intent = new Intent();
        intent.setAction(MainContentActivity.ACTION_RECEIVE_MESSAGE);
        intent.putExtra("body",data.get("body"));
        intent.putExtra("date",data.get("date"));
        intent.putExtra("sender",data.get("sender"));
        intent.putExtra("receiver",data.get("receiver"));
       broadcast.sendBroadcast(intent);

    }


}
