package com.example.pc.bluedating.DataObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.example.pc.bluedating.Object.User;
import com.example.pc.bluedating.Utils.BitmapUtils;
import com.example.pc.bluedating.Utils.BlueDatingApplication;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by PC on 11/1/2017.
 */

public class DataUserResolver {

    private static final String PREF_NAME="user";
    private static SharedPreferences mSharedPreferences;
    private static DataUserResolver mUserResolver;


    public static DataUserResolver getInstance()
    {
        if (mUserResolver == null)
            mUserResolver = new DataUserResolver();
        return mUserResolver;
    }

    private DataUserResolver()
    {
        mSharedPreferences = BlueDatingApplication.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    }


    public void clear (){
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.clear();
        e.commit();
    }



    public  void saveUser(User user)
    {
        Gson gson = new Gson();
        String myUser = gson.toJson(user);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREF_NAME,myUser);
        editor.commit();
    }

    public void updateUser(User user)
    {
        Gson gson = new Gson();
        String myUser = gson.toJson(user);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREF_NAME,myUser);
        editor.apply();
    }

    public  User getUser()
    {
        User resultUser = null;
        String savedUser = mSharedPreferences.getString("user", "");
        if(!savedUser.equals("")){
            Gson gson = new Gson();
            resultUser = gson.fromJson(savedUser,User.class);
        }
        return resultUser;
    }

    public  void updateUserAvatarFromUri(Uri uri) throws IOException
    {
        User user = getUser();
        InputStream iStream =   BlueDatingApplication.getInstance().getContentResolver().openInputStream(uri);
        byte[] avatar = BitmapUtils.getBytes(iStream);
        user.setAvatar(avatar);
        updateUser(user);
    }

    public void updateUserAvatarFromPath(String path) throws IOException
    {
        File file = new File(path);
        byte[] avatar = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(avatar); //read file into bytes[]
        fis.close();
        User user = getUser();
        user.setAvatar(avatar);
        updateUser(user);
    }
}
