package com.example.pc.bluedating.MainScreen_Function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.pc.bluedating.DataObject.DataUserResolver;
import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.Author;
import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.ChattingFragment;
import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.Dialog.Dialog;
import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.ImageLoad;
import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.Message;
import com.example.pc.bluedating.Object.User;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Services.MyMessageReceiverService;
import com.example.pc.bluedating.Utils.BitmapUtils;
import com.example.pc.bluedating.Utils.BlueDatingApplication;
import com.example.pc.bluedating.Utils.CircleTransform;
import com.example.pc.bluedating.Utils.ConnectionUtils;
import com.example.pc.bluedating.Utils.FileUtils;
import com.example.pc.bluedating.Utils.FirebaseToken;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;




public class MainContentActivity extends AppCompatActivity implements ProfileFragment.IProfileListener,ChattingFragment.openMessageList {

    final public static int REQUEST_LOAD_IMAGE = 1;
    final public static int REQUEST_TAKE_IMAGE = 2;
    final public static String ACTION_RECEIVE_MESSAGE = "RECEIVE_MESSAGE";
    @BindView(R.id.navigation_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawLayout;

    ImageView mProfileAvatar,mProfileBackground;
    TextView mProfileName;
    ActionBar mActionBar;
    FrameLayout mContainer;
    String mCurrentPhotoPath;
    Socket mSocket;
    User mUser;
    ActionBarDrawerToggle mDrawerToggle;
    ProfileFragment mProfileFragment;
    ChattingFragment mChattingFragment;
    MessagesListAdapter<Message> mMessageAdapter;
    Emitter.Listener onReceiveOnlineUser,onReceiveUser;
    BroadcastReceiver messageBroadcast;
    Dialog mCurrentDialog;
    DialogsListAdapter<Dialog> mDialogsListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);
        mSocket = BlueDatingApplication.getSocket();
        ButterKnife.bind(this);
        setUpNavigationView();
        mUser = DataUserResolver.getInstance().getUser();
        exposeUserDataIntoDrawer();
        setUpListener();
        initChatting();

        messageBroadcast = new ReceiveMessageBroadcast ();




        if(FirebaseInstanceId.getInstance() != null)
        {
            String token = FirebaseInstanceId.getInstance().getToken();
            FirebaseToken.saveToken(token);

        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       if(mDrawerToggle.onOptionsItemSelected(item)){
           return true;
       }

        return super.onOptionsItemSelected(item);
    }

    class ReceiveMessageBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
            try {
                Date date = dateFormatter.parse(intent.getStringExtra("date"));
                String body = intent.getStringExtra("body");
                String sender = intent.getStringExtra("sender");

                User user = mChattingFragment.getUser(sender);
                Message message = new Message(new Author(user),body,date);




                if(mDialogsListAdapter.updateDialogWithMessage(sender,message))
                {  if(!mChattingFragment.isOpen()) {
                    mChattingFragment.getDialog(sender).addUnReadMessage();
                    mChattingFragment.saveMessage(sender, message);
                }
                else {
                    mChattingFragment.getDialog(sender).addUnReadMessage();
                    mMessageAdapter.addToStart(message,false);
                    mChattingFragment.saveMessage(sender,message);

                }

                } else {
                    Toast.makeText(MainContentActivity.this," khong co dialog",Toast.LENGTH_SHORT).show();
                    JSONObject object = new JSONObject();
                    object.put("sender",DataUserResolver.getInstance().getUser().getEmail());
                    object.put("receiver",sender);
                    BlueDatingApplication.getSocket().emit("getUser",object);


                }






            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageBroadcast,new IntentFilter(ACTION_RECEIVE_MESSAGE));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageBroadcast);
        super.onStop();

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

            ConnectionUtils.uploadUserAvatarFromUri(uri,DataUserResolver.getInstance().getUser());
            try {
                DataUserResolver.getInstance().updateUserAvatarFromUri(uri);
            }catch ( IOException e)
            {
                e.printStackTrace();
            }

            return;
        }

        if(requestCode == REQUEST_TAKE_IMAGE && resultCode == RESULT_OK)
        {
            Glide.with(this).load(mCurrentPhotoPath)
                    .asBitmap()
                    .transform(new CircleTransform(this))
                    .error(R.drawable.ic_place_holder)
                    .into(mProfileAvatar);

            ConnectionUtils.uploadUserAvatarFromPath(mCurrentPhotoPath,DataUserResolver.getInstance().getUser()); // path = mCurrentPhotoPath
            try {
                DataUserResolver.getInstance().updateUserAvatarFromPath(mCurrentPhotoPath);
            }catch ( IOException e)
            {
                e.printStackTrace();
            }
            return;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        if (mDrawLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawLayout.closeDrawers();
            return;
        }

        if(mChattingFragment.isOpen())
        {
            mChattingFragment.getSwipeLayout().toggle(true);
            mChattingFragment.toggle();
            getSupportActionBar().setTitle(getString(R.string.app_name));
            return;
        }

        FragmentManager manager = getSupportFragmentManager();
       if(manager.getBackStackEntryCount() >=2)
       {
           manager.popBackStack();
           return;
       }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BlueDatingApplication.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void saveProfile(User user) {

        Gson gson = new Gson();
        String json = gson.toJson(user);
        mSocket.emit("updateUser",json);
        updateProfileHeader(user);
        DataUserResolver.getInstance().updateUser(user);


    }

    @Override
    public void cancel() {


        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack();

       // this.onBackPressed();

    }

    @Override
    public void openMessageList(String name, Dialog dialog) {
        mActionBar.setTitle(name);
       // mMessageAdapter.addToStart(new Message(new Author(DataUserResolver.getInstance().getUser()),"helo",new Date()),true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCurrentDialog = dialog;


    }





    void setUpNavigationView()
    {
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        View headerView = mNavigationView.getHeaderView(0);
        mProfileAvatar  = (ImageView)headerView.findViewById(R.id.image_nav_avatar);
        mProfileBackground = (ImageView)headerView.findViewById(R.id.image_nav_header_background);
        mProfileName = (TextView)headerView.findViewById(R.id.text_nav_header_name);
        setUpAvatar();
        mDrawerToggle= new ActionBarDrawerToggle(this,mDrawLayout,R.string.open_drawer,R.string.close_drawer){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        mDrawLayout.addDrawerListener(mDrawerToggle);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId())
                {
                    case R.id.menu_nav_profile:
                        User user = DataUserResolver.getInstance().getUser();
                        mProfileFragment = new ProfileFragment();
                        mProfileFragment.setUser(user);
                        mProfileFragment.setListener(MainContentActivity.this);
                        replaceFragment(mProfileFragment);
                        closeDrawer();
                        break;
                }

                return true;
            }
        });

    }


    void exposeUserDataIntoDrawer()
    {
        Glide.with(this).load(mUser.getAvatar())
                .asBitmap()
                .transform(new CircleTransform(this))
                .error(R.drawable.ic_place_holder)
                .into(mProfileAvatar);
        mProfileName.setText(mUser.getName());
    }

    void setUpAvatar()
    {
        mProfileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainContentActivity.this,mProfileAvatar);
                popupMenu.getMenuInflater().inflate(R.menu.header_profile_avatar,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        switch (id)
                        {
                            case R.id.menu_upload_header_profile_avatar:
                                loadAvatar();
                                break;
                            case R.id.menu_take_header_profile_avatar:

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


    void replaceFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commit();
    }


// take picture for avatar
    void takeAvatar()
    {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                // create file for storing image file
                photoFile = FileUtils.createImageFile();
                mCurrentPhotoPath = photoFile.getAbsolutePath();

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
    }

    void loadAvatar()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
       startActivityForResult(intent,REQUEST_LOAD_IMAGE);
    }

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











    void updateProfileHeader(User user)
    {
        mProfileName.setText(user.getName());
    }
    public void updateProfileAvatar(Uri uri)
    {
        Glide.with(this).load(uri).asBitmap().transform(new CircleTransform(this))
                .error(R.drawable.ic_place_holder).into(mProfileAvatar);
    }
    public void updateProfileAvatar(String  path)
    {
        Glide.with(this).load(path).asBitmap().transform(new CircleTransform(this))
                .error(R.drawable.ic_place_holder).into(mProfileAvatar);
    }


    void closeDrawer()
    {
        if (mDrawLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawLayout.closeDrawers();
        }
    }



    void initChatting()
    {

        mChattingFragment = new ChattingFragment();
        mMessageAdapter =  new MessagesListAdapter<Message>(DataUserResolver.getInstance().getUser().getEmail(),null);

        mDialogsListAdapter = new DialogsListAdapter<Dialog>(R.layout.item_dialog,new ImageLoad(this));
        mChattingFragment.setmDialogsListAdapter(mDialogsListAdapter);
        mChattingFragment.setMessageAdapter(mMessageAdapter);
        mChattingFragment.setInstance(this);
        mChattingFragment.setContext(this);
        mChattingFragment.setUsers(new ArrayList<User>());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_container,mChattingFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
        mSocket.emit("getOnlineUser");


    }

    void setUpListener()
    {
        onReceiveOnlineUser = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<User> users = new ArrayList<User>();
                        try {

                            JSONArray JsonUsers = (JSONArray)args[0];
                            for(int i =0; i<JsonUsers.length();++i)
                            {
                                JSONObject JsonUser = JsonUsers.getJSONObject(i);
                                User user = new User(JsonUser.getString("email"),
                                            JsonUser.getString("name"),
                                            JsonUser.getString("avatar"));
                                user.setAvatar(BitmapUtils.getArrayFromString(user.getAvatar64()));
                                users.add(user);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    mChattingFragment.loadOnlineUser(users);

                    }
                });
                 }
        };

        mSocket.on("sendOnlineUser",onReceiveOnlineUser);

        onReceiveUser = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject object = (JSONObject)args[0];
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
                        ArrayList<Message> messagesList = new ArrayList<Message>();
                        User sendTo=null;
                        try{
                            JSONArray messages = object.getJSONArray("messages");
                          sendTo  = new User(object.getString("email"),
                                    object.getString("name"),
                                    object.getString("avatar"));
                            sendTo.setAvatar(BitmapUtils.getArrayFromString(sendTo.getAvatar64()));


                            User user = DataUserResolver.getInstance().getUser();
                            Author sender = new Author(user);
                            Author receiver = new Author(sendTo);
                            for(int i =messages.length()-1; i>=0 ;--i)
                            {
                                JSONObject message = messages.getJSONObject(i);
                                String emailSender = message.getString("sender");
                                Date date = dateFormatter.parse(message.getString("date"));
                                Message mess=null;
                                if(emailSender.equals(user.getEmail()))
                                {
                                    mess = new Message(sender,message.getString("body"),date);
                                }else if(emailSender.equals(sendTo.getEmail())){
                                    mess = new Message(receiver,message.getString("body"),date);
                                }

                                messagesList.add(mess);
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mChattingFragment.addDialog(new Dialog(sendTo),messagesList);
                    }
                });
            }
        };
        mSocket.on("sendUser",onReceiveUser);


    }




}
