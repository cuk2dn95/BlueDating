package com.example.pc.bluedating.Login_Function;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pc.bluedating.R;
import com.facebook.login.widget.LoginButton;

/**
 * Created by PC on 9/23/2017.
 *
 * configure login function here
 */

public class LogInFragment extends Fragment {


    int mResource_Img;
    LogInFragmentListener listener;
    interface LogInFragmentListener{
        void setUpLoginButton(LoginButton loginButton);
    }

    void setListener(Context context)
    {
        listener = (LogInFragmentListener) context;
    }
    public void clearListener(){
        listener = null;
    };

    public void setResource_img(int resource_img) {
        this.mResource_Img = resource_img;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome_login,container,false);

        ImageView imageView = (ImageView)view.findViewById(R.id.image_login);
        Glide.with(this).load(mResource_Img).fitCenter().into(imageView);
        LoginButton loginButton = (LoginButton)view.findViewById(R.id.button_login);
        listener.setUpLoginButton(loginButton);
        return view;

    }

}
