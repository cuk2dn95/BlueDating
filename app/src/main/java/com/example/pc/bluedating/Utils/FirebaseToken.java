package com.example.pc.bluedating.Utils;

import com.example.pc.bluedating.DataObject.DataUserResolver;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KA on 11/27/2017.
 */

public class FirebaseToken {

    static public void saveToken(String token)
    {
        String email = DataUserResolver.getInstance().getUser().getEmail();
        Socket socket = BlueDatingApplication.getSocket();
        JSONObject object = new JSONObject();
        try {
            object.put("email",email);
            object.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("updateToken",object);
    }
}
