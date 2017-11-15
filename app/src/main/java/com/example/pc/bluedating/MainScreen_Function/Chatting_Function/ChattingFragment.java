package com.example.pc.bluedating.MainScreen_Function.Chatting_Function;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.pc.bluedating.Object.User;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.BlueDatingApplication;

import java.util.ArrayList;

/**
 * Created by PC on 11/4/2017.
 */

public class ChattingFragment extends Fragment {

    ArrayList<User> users;
    ChattingAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting,container,false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getOnlineUser();
            }
        });
        recyclerView= (RecyclerView)view.findViewById(R.id.chatting_recyclerView);
        adapter = new ChattingAdapter(users,getContext());
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }



    void getOnlineUser()
    {
        BlueDatingApplication.getSocket().emit("getOnlineUser");
    }

    public void loadOnlineUser(ArrayList<User> users)
    {
        swipeRefreshLayout.setRefreshing(true);
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }


}
