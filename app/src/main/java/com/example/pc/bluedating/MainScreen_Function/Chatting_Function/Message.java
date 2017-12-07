package com.example.pc.bluedating.MainScreen_Function.Chatting_Function;

import com.example.pc.bluedating.Object.User;
import com.google.gson.GsonBuilder;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

/**
 * Created by KA on 11/22/2017.
 */

public class Message implements IMessage {


    IUser user;
    String text;
    Date   date;
    public Message(IUser user,String text,Date date) {
        this.user = user;
        this.text = text;
        this.date = date;
    }



    public void setText(String text) {
        this.text = text;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public String getId() {
        return user.getId();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return date;
    }
}
