package com.example.pc.bluedating.MainScreen_Function;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bumptech.glide.Glide;
import com.example.pc.bluedating.Model.User;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.BitmapUtils;
import com.example.pc.bluedating.Utils.BlueDatingApplication;
import com.example.pc.bluedating.Utils.CircleTransform;
import com.facebook.CallbackManager;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.input;
import static com.example.pc.bluedating.Utils.BitmapUtils.getBytes;


public class MainContentActivity extends AppCompatActivity {

    final public static int REQUEST_LOAD_IMAGE = 1;
    final public static int REQUEST_TAKE_IMAGE = 2;
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawLayout;
    ImageView mProfileAvatar,mProfileBackground;
    TextView mProfileName;
    String mCurrentPhotoPath;
    Socket mSocket;
    User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);
        mSocket = BlueDatingApplication.getSocket();
        ButterKnife.bind(this);
        setUpNavigationView();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        if(!json.equals(""))
        {
            Gson gson = new Gson();
            mUser = gson.fromJson(json,User.class);

            Glide.with(this).load(mUser.getAvatar())
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .error(R.drawable.ic_place_holder)
                    .into(mProfileAvatar);
            mProfileName.setText(mUser.getName());
        }
        else {
            Toast.makeText(this,"deo co gi het",Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK)
        {
           final Uri uri = data.getData();
            Glide.with(this).load(uri)
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .error(R.drawable.ic_place_holder)
                    .into(mProfileAvatar);


            Thread uploadImage = new Thread(new Runnable(){
                @Override
                public void run() {
                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
                        byte[] inputData = getBytes(iStream);
                        String avatar64 = BitmapUtils.getStringFromArray(inputData);
                        String email = mUser.getEmail();
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




        return;
        }

        if(requestCode == REQUEST_TAKE_IMAGE && resultCode == RESULT_OK)
        {
            Glide.with(this).load(mCurrentPhotoPath)
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .error(R.drawable.ic_place_holder)
                    .into(mProfileAvatar);
            Thread uploadImage = new Thread(new Runnable(){
                @Override
                public void run() {

                    try {
                        File file = new File(mCurrentPhotoPath);
                        byte[] bytesArray = new byte[(int) file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(bytesArray); //read file into bytes[]
                        fis.close();

                        String avatar64 = BitmapUtils.getStringFromArray(bytesArray);
                        String email = mUser.getEmail();
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

            return;

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    void setUpNavigationView()
    {
        View headerView = mNavigationView.getHeaderView(0);
        mProfileAvatar  = (ImageView)headerView.findViewById(R.id.image_nav_avatar);
        mProfileBackground = (ImageView)headerView.findViewById(R.id.image_nav_header_background);
        mProfileName = (TextView)headerView.findViewById(R.id.text_nav_header_name);
        setUpAvatar();
    }


    void setUpAvatar()
    {
        mProfileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainContentActivity.this,mProfileAvatar);
                popupMenu.getMenuInflater().inflate(R.menu.profile_avatar,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id)
                        {
                            case R.id.menu_view_profile_avatar:

                                break;
                            case R.id.menu_view_profile_upload:
                                loadAvatar();
                                break;
                            case R.id.menu_view_profile_take:

                                 takeAvatar();
                                break;
                            default:
                                return false;
                        }

                        return true;
                    }
                });

                popupMenu.show();



            }
        });
    }


// take picture for avatar
    void takeAvatar()
    {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                // create file for storing image file
                photoFile = createImageFile();

            }catch (IOException e)
            {
                e.printStackTrace();
            }
            if(photoFile != null)
            {
                // set file as output for image captured
                    Uri uri = FileProvider.getUriForFile(this,"com.example.pc.bluedating",photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent, REQUEST_TAKE_IMAGE);
            }

        }
    };

    void loadAvatar()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
       startActivityForResult(intent,REQUEST_LOAD_IMAGE);
    };

    //open image from uri
    Bitmap getImageFromUri(Uri uri) throws IOException
    {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    // create image file
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
