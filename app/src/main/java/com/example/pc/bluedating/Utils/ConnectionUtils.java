package com.example.pc.bluedating.Utils;

import android.net.Uri;

import com.example.pc.bluedating.Object.User;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.pc.bluedating.Utils.BitmapUtils.getBytes;

/**
 * Created by PC on 11/2/2017.
 */

public class ConnectionUtils {

    public static  void uploadUserAvatarFromUri(final Uri uri,final User User)
    {
        Thread uploadImage = new Thread(new Runnable(){
            @Override
            public void run() {
                InputStream iStream = null;
                Socket mSocket = BlueDatingApplication.getSocket();
                try {
                    iStream = BlueDatingApplication.getInstance().getContentResolver().openInputStream(uri);
                    byte[] inputData = getBytes(iStream);
                    String avatar64 = BitmapUtils.getStringFromArray(inputData);
                    String email = User.getEmail();
                    JSONObject object = new JSONObject();
                    object.put("email",email);
                    object.put("avatar",avatar64);
                    mSocket.emit("saveImage",object);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        uploadImage.start();
    }


  public static   void uploadUserAvatarFromPath(final String path,final User user)
    {
        Thread uploadImage = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    File file = new File(path);
                    Socket mSocket = BlueDatingApplication.getSocket();
                    byte[] bytesArray = new byte[(int) file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(bytesArray); //read file into bytes[]
                    fis.close();

                    String avatar64 = BitmapUtils.getStringFromArray(bytesArray);
                    String email = user.getEmail();
                    JSONObject object = new JSONObject();
                    object.put("email",email);
                    object.put("avatar",avatar64);
                    mSocket.emit("saveImage",object);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        uploadImage.start();
    }
}
