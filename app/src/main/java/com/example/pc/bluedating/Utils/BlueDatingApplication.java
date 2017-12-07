package com.example.pc.bluedating.Utils;

import android.app.Application;
import android.widget.Toast;

import com.example.pc.bluedating.DataObject.DataUserResolver;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by PC on 10/24/2017.
 */

public class BlueDatingApplication extends Application {

    static  private Socket mSocket;
    static BlueDatingApplication mInstance;






    static {

        try {
         // mSocket = IO.socket("http://192.168.56.1:3000");
            mSocket = IO.socket("http://bluedating.herokuapp.com");


        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    static public BlueDatingApplication getInstance()
    {
        return mInstance;
    }

   static public Socket getSocket() {
        return mSocket;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    static  public void disconnect()
    {
        mSocket.disconnect();
        mSocket.off("registered");
        mSocket.off("existed");
    }

}
