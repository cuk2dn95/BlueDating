package com.example.pc.bluedating.MainScreen_Function.Chatting_Function;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pc.bluedating.Utils.BitmapUtils;
import com.stfalcon.chatkit.commons.ImageLoader;

/**
 * Created by KA on 11/22/2017.
 */

public class ImageLoad implements ImageLoader {

    Context context;
    public ImageLoad(Context context) {
        super();
        this.context = context;
    }
    @Override

    public void loadImage(ImageView imageView, String url) {
        byte[] avatar = BitmapUtils.getArrayFromString(url);
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB) {
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        Glide.with(context).load(avatar).fitCenter().into(imageView);
    }
}
