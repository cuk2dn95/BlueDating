package com.example.pc.bluedating.MainScreen_Function;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pc.bluedating.DataObject.DataUserResolver;
import com.example.pc.bluedating.Object.User;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.CircleTransform;
import com.example.pc.bluedating.Utils.ConnectionUtils;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.path;
import static android.app.Activity.RESULT_OK;
import static com.example.pc.bluedating.MainScreen_Function.MainContentActivity.REQUEST_LOAD_IMAGE;
import static com.example.pc.bluedating.MainScreen_Function.MainContentActivity.REQUEST_TAKE_IMAGE;
import static com.example.pc.bluedating.Utils.FileUtils.createImageFile;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public static final int FRAGMENT_REQUEST_LOAD_IMAGE =3 ;
    public static final int FRAGMENT_REQUEST_TAKE_IMAGE =4 ;

    @BindView(R.id.image_profile_avatar)
    ImageView avatar;
    @BindView(R.id.editText_name)
    EditText name;
    @BindView(R.id.editText_birthday)
    EditText birthday;
    @BindView(R.id.editText_email)
    EditText email;
    @BindView(R.id.editText_gender)
    EditText gender;
    @BindView(R.id.btn_save)
    Button btnSave;
    @BindView(R.id.btn_cancel)
    Button btnCancel;

    String mCurrentPath;
    User user;

    IProfileListener listener;

   public interface IProfileListener{
        void saveProfile(User user);
        void cancel();
    }


    void setListener(IProfileListener listener)
    {
        this.listener = listener;
    }

    void setUser(User user)
    {
        this.user = user;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,view);

        Glide.with(this).load(user.getAvatar())
                .asBitmap()
                .transform(new CircleTransform(getContext()))
                .error(R.drawable.ic_place_holder)
                .into(avatar);
        name.setText(user.getName());
        birthday.setText(user.getBirthday());
        email.setText(user.getEmail());
        gender.setText(user.getGender());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = DataUserResolver.getInstance().getUser();
                user.setName(name.getText().toString());
                user.setGender(gender.getText().toString());
                user.setBirthday(birthday.getText().toString());
                listener.saveProfile(user);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(),avatar);
                popupMenu.getMenuInflater().inflate(R.menu.profile_avatar,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.menu_take_profile_avatar:
                                takeAvatar();
                                break;
                            case R.id.menu_upload_profile_avatar:
                                loadAvatar();
                                break;

                        }

                        return true;
                    }
                });

                popupMenu.show();
            }
        });




        return view;
    }

    public void updateAvatarFromUri(Uri uri)
    {
        Glide.with(this).load(uri)
                .asBitmap()
                .transform(new CircleTransform(getContext()))
                .error(R.drawable.ic_place_holder)
                .into(avatar);
    }

   public void updateAvatarFromPath()
    {
        Glide.with(this).load(mCurrentPath)
                .asBitmap()
                .transform(new CircleTransform(getContext()))
                .error(R.drawable.ic_place_holder)
                .into(avatar);


    }


    void loadAvatar()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,FRAGMENT_REQUEST_LOAD_IMAGE);
    }

    void takeAvatar()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            File photoFile = null;
            try{
                // create file for storing image file
                photoFile = createImageFile();
                mCurrentPath = photoFile.getAbsolutePath();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            if(photoFile != null)
            {
                // set file as output for image captured
                Uri uri = FileProvider.getUriForFile(getContext(),"com.example.pc.bluedating",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent, FRAGMENT_REQUEST_TAKE_IMAGE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FRAGMENT_REQUEST_LOAD_IMAGE && resultCode == RESULT_OK)
        {
             Uri uri = data.getData();
            updateAvatarFromUri(uri);
            MainContentActivity activity = (MainContentActivity)listener;
            ConnectionUtils.uploadUserAvatarFromUri(uri,DataUserResolver.getInstance().getUser());
             activity.updateProfileAvatar(uri);
            try {
                DataUserResolver.getInstance().updateUserAvatarFromUri(uri);
            }catch ( IOException e)
            {
                e.printStackTrace();
            }

            return;
        }


        if(requestCode == FRAGMENT_REQUEST_TAKE_IMAGE && resultCode == RESULT_OK)
        {

            updateAvatarFromPath();
            MainContentActivity activity = (MainContentActivity)listener;
            ConnectionUtils.uploadUserAvatarFromPath(mCurrentPath,DataUserResolver.getInstance().getUser());
            activity.updateProfileAvatar(mCurrentPath);
            try {
                DataUserResolver.getInstance().updateUserAvatarFromPath(mCurrentPath);
            }catch ( IOException e)
            {
                e.printStackTrace();
            }

            return ;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
