package com.carista.data.realtimedb.models;


import com.google.firebase.firestore.PropertyName;

public class UserModel {

    public int id;

    @PropertyName("nickname")
    public String nickname;

    @PropertyName("avatar")
    public String avatar;

    public UserModel(int id, String nickname, String avatar) {
        this.id = id;
        this.nickname = nickname;
        this.avatar = avatar;
    }
}
