package com.example.pc.bluedating.Login_Function;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pc.bluedating.DataObject.DataUserResolver;
import com.example.pc.bluedating.MainScreen_Function.MainContentActivity;
import com.example.pc.bluedating.Object.User;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.BitmapUtils;
import com.example.pc.bluedating.Utils.BlueDatingApplication;
import com.example.pc.bluedating.Utils.FirebaseToken;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LogInActivity extends AppCompatActivity implements LogInFragment.LogInFragmentListener{
    ArrayList<Fragment> mData = new ArrayList<>();
   @BindView(R.id.view_pager_introduction) ViewPager mViewPager;
    ViewPagerAdapter mAdapterViewPager;
    Socket mSocket;
    int num_count;
    ImageView mIndicators[];
    //login
    CallbackManager mCallbackManager;
    Emitter.Listener onRegistered,onExisted;
    LogInFragment loginFragment;
    User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_log_in);

        mSocket = BlueDatingApplication.getSocket();
        mSocket.connect();
        mCallbackManager = CallbackManager.Factory.create();
        //get views
        ButterKnife.bind(this);

        if (!isNetworkAvailable())
            Toast.makeText(this,"Vui Lòng kết nối mạng",Toast.LENGTH_SHORT).show();
        //load fragments
        loadFragments();
        //set up view pager
        configureViewPager();
        createIndicator();
        mViewPager.setOnPageChangeListener(new ViewPagerListener());
        mViewPager.setAdapter(mAdapterViewPager);

 //       set up login button

        setUpListener();
        DataUserResolver.getInstance().clear();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);

    }

    // set up button in login fragment
    @Override
    public void setUpLoginButton(LoginButton loginButton)
    {
        this.setUpLogInButton(loginButton);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
        BlueDatingApplication.getSocket().off("registered",onRegistered);
        BlueDatingApplication.getSocket().off("existed",onExisted);
        loginFragment.clearListener();
        for(ImageView imageView : mIndicators)
            imageView = null;
        LogInActivity_ViewBinding a = new LogInActivity_ViewBinding(this);
        a.unbind();




    }

    // create circular indicator
    void createIndicator() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.circular_indicator);
        IndicatorListener listener = new IndicatorListener();
        int count = mAdapterViewPager.getCount();
        mIndicators = new ImageView[count];
        for (int i = 0; i < count; ++i) {
            mIndicators[i] = new ImageView(this);
            mIndicators[i].setImageResource(R.drawable.nonselecteditem_dot);
            mIndicators[i].setOnClickListener(listener);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(4, 0, 4, 0);
            linearLayout.addView(mIndicators[i], params);
        }

        mIndicators[0].setImageResource(R.drawable.selecteditem_dot);

    }

    //load fragment
    void loadFragments() {



        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setResource_img(R.drawable.notification_bg_2);
        mData.add(fragment);

        WelcomeFragment fragment1 = new WelcomeFragment();
        fragment1.setResource_img(R.drawable.notification_bg_1);
        mData.add(fragment1);

        loginFragment = new LogInFragment();
        loginFragment.setResource_img(R.drawable.notification_bg_login);
        loginFragment.setListener(this);
        mData.add(loginFragment);




    }

    //initialize views


    // create adapter for viewpager
    void configureViewPager() {
        mAdapterViewPager = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapterViewPager.setData(mData);
        num_count = mAdapterViewPager.getCount();
    }


    void changeState(int position) {
        for (int i = 0; i < num_count; ++i) {

            mIndicators[i].setImageResource(R.drawable.nonselecteditem_dot);
        }
        mIndicators[position].setImageResource(R.drawable.selecteditem_dot);
    }

    // click to change fragment
    class IndicatorListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int click_position = 0;
            for (int i = 0; i < mAdapterViewPager.getCount(); ++i) {
                if (v == mIndicators[i]) {
                    click_position = i;
                    break;
                }
            }
            changeState(click_position);
            mViewPager.setCurrentItem(click_position);
        }
    }

    class ViewPagerListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageSelected(int position) {

            changeState(position);


        }

    }


        /*
            Login function
         */

    // set up login button
    void setUpLogInButton(LoginButton logInButton)
    {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.INTERNET},
                    0
            );
        }


        logInButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday"));
        logInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                getResultFromFaceBook(loginResult);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(LogInActivity.this,"đăng nhập thất bại: "+error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    void getResultFromFaceBook(LoginResult loginResult)
    {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

            GetUserTask getUserTask = new GetUserTask();
                getUserTask.execute(object);
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields","id,name,email,gender,birthday");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

// login here
class GetUserTask extends AsyncTask<JSONObject,Void,User>
{
    @Override
    protected User doInBackground(JSONObject... params) {
        User user = null;
        try {
            JSONObject object = params[0];

            String id = object.getString("id");
            String name = object.getString("name");
            String email = object.getString("email");
            String gender = object.getString("gender");
            String birthday = object.getString("birthday");
            URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] avatar = stream.toByteArray();
            user = new User(name, gender, birthday, email, avatar);
            user.setAvatar64(BitmapUtils.getStringFromArray(avatar));
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }



        return user;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(User user) {
        super.onPostExecute(user);

        mUser = user;
        if(user != null) {
            Toast.makeText(getApplicationContext(), " đăng nhập  ", Toast.LENGTH_SHORT).show();

        }
        Gson gson = new Gson();
        String myUser = gson.toJson(user);

        mSocket.emit("login",myUser);


    }
}

    void moveToMainScreen()
    {
        Toast.makeText(getApplicationContext(),"đăng nhập thành công",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LogInActivity.this, MainContentActivity.class);

        startActivity(intent);
        finish();
    }



    // listener for event from server
    void setUpListener()
    {
        // listener when register an new account
        onRegistered = new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataUserResolver.getInstance().saveUser(mUser);
                        moveToMainScreen();
                    }
                });

            }
        };
        // listener when an account has existed
        onExisted = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject object = (JSONObject) args[0];

                        try {
                            String avatar64 = object.getString("avatar");
                           byte[] bytes =  BitmapUtils.getArrayFromString(avatar64);
                            mUser = new User(object.getString("name"),
                                    object.getString("gender"),
                                    object.getString("birthday"),
                                    object.getString("email"),bytes);

                            DataUserResolver.getInstance().saveUser(mUser);
                            moveToMainScreen();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });



            }
        };

        mSocket.once("registered",onRegistered);
        mSocket.once("existed",onExisted);
    }

    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
