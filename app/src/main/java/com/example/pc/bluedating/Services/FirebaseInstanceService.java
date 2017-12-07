package com.example.pc.bluedating.Services;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.pc.bluedating.DataObject.DataUserResolver;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.BlueDatingApplication;
import com.example.pc.bluedating.Utils.FirebaseToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by KA on 11/22/2017.
 */

public class FirebaseInstanceService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseToken.saveToken(refreshedToken);

    }


}
