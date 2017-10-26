package com.example.pc.bluedating.Login_Function;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by PC on 9/23/2017.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> mData;


    public  void setData(ArrayList<Fragment> arrayList )
    {
        mData = arrayList;
    }

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }


}
