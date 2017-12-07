package com.example.pc.bluedating.Broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionChange extends BroadcastReceiver {
    public ConnectionChange() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Toast.makeText(context.getApplicationContext(), " Đã kết nối mạng ", Toast.LENGTH_SHORT).show();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Toast.makeText(context.getApplicationContext(), " Vui lòng kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
