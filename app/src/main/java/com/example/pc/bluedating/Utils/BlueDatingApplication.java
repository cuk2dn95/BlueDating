package com.example.pc.bluedating.Utils;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by PC on 10/24/2017.
 */

public class BlueDatingApplication extends Application {

    static  private Socket mSocket;

    static {

        try {
            mSocket = IO.socket("http://192.168.182.1:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

   static public Socket getSocket() {
        return mSocket;
    }


}
