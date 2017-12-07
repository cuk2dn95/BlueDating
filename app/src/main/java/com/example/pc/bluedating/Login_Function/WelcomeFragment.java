package com.example.pc.bluedating.Login_Function;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.pc.bluedating.Utils.BitmapUtils;
import com.example.pc.bluedating.R;

/**
 * Created by PC on 9/23/2017.
 */

public class WelcomeFragment extends Fragment {
    int mResource_Img;

    public void setResource_img(int picture)
    {
        mResource_Img = picture;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_welcome);


//        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(getActivity().getResources(),mResource_Img,imageView.getMaxWidth(),imageView.getMaxHeight());
//        imageView.setImageBitmap(bitmap);
        Glide.with(this).load(mResource_Img).fitCenter().override(200,200).into(imageView);


        return view;

    }
}
