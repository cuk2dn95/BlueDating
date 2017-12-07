package com.example.pc.bluedating.MainScreen_Function.Chatting_Function;

import com.example.pc.bluedating.Object.User;
import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by KA on 11/22/2017.
 */

public class Author implements IUser {

    User user;

    public Author(User user) {
        super();
        this.user = user;
    }

    @Override
    public String getId() {
        return user.getEmail();
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getAvatar() {
        return user.getAvatar64();
    }
}
