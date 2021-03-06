package com.example.pc.bluedating.Object;


import com.example.pc.bluedating.Utils.BitmapUtils;
import com.google.zxing.common.StringUtils;

/**
 * Created by PC on 10/23/2017.
 */

public class User {
    String name,gender,birthday,email,avatar64;
    byte[] avatar;


    public User() {
        super();
    }

    public User(String name, String gender, String birthday, String email, byte[] avatar) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;

        this.email = email;
        this.avatar = avatar;
    }


    public User(String email, String name, String avatar64) {
        this.email = email;
        this.name = name;
        this.avatar64 = avatar64;
    }

    public String getAvatar64() {
        if(avatar64==null)
            avatar64 = BitmapUtils.getStringFromArray(avatar);

        return avatar64;
    }

    public void setAvatar64(String avatar64) {
        this.avatar64 = avatar64;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
