package com.example.pc.bluedating.Model;


import com.facebook.login.widget.LoginButton;

/**
 * Created by PC on 10/23/2017.
 */

public class User {
    String name,gender,birthday,email,avatar64;
    byte[] avatar;

    public User(String name, String gender, String birthday, String email, byte[] avatar) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;

        this.email = email;
        this.avatar = avatar;
    }

    public String getAvatar64() {
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
