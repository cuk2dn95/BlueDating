package com.example.pc.bluedating.MainScreen_Function.Chatting_Function.Dialog;

import com.example.pc.bluedating.MainScreen_Function.Chatting_Function.Author;
import com.example.pc.bluedating.Object.User;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KA on 11/22/2017.
 */

public class Dialog  implements IDialog {

    User user;
    IMessage lastMessage;
    int unReadMessage = 0;

    public Dialog(User user) {
        super();
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void  clearUnReadMessage()
    {
        this.unReadMessage = 0;
    }

    public void addUnReadMessage()
    {
        this.unReadMessage++;
    }


    @Override
    public String getId() {
        return user.getEmail();
    }

    @Override
    public String getDialogPhoto() {
        return user.getAvatar64();
    }

    @Override
    public String getDialogName() {
        return user.getName();
    }

    @Override
    public ArrayList<IUser> getUsers() {
        ArrayList<IUser> arrayList =  new ArrayList<IUser>();
        arrayList.add(new Author(user));
        return arrayList;
    }

    @Override
    public IMessage getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(IMessage message) {
        this.lastMessage = message;
    }

    @Override
    public int getUnreadCount() {
        return unReadMessage;
    }


}
