package com.example.pc.bluedating.MainScreen_Function.Chatting_Function;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pc.bluedating.Object.User;
import com.example.pc.bluedating.R;
import com.example.pc.bluedating.Utils.CircleTransform;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by PC on 11/4/2017.
 */

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {

    ArrayList<User> users;
    Context context;

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ChattingAdapter(ArrayList<User> users, Context context) {
        super();
        this.users = users;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_chatting_user,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getName());
        Glide.with(context).load(user.getAvatar())
                .transform(new CircleTransform(context))
                .error(R.drawable.ic_place_holder)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder  extends  RecyclerView.ViewHolder{
        ImageView avatar;
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView)itemView.findViewById(R.id.image_avatar_chatting_item);
            name = (TextView)itemView.findViewById(R.id.text_name_chatting_item);
        }
    }
}
