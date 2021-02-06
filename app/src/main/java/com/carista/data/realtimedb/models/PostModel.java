package com.carista.data.realtimedb.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Entity
public class PostModel {

    @NonNull
    @Expose
    public String id;

    @Expose
    @PropertyName("title")
    public String title;

    @Expose
    @PropertyName("image")
    public String image;

    @Expose
    @PropertyName("timestamp")
    public long timestamp;

    @Expose
    @PropertyName("userId")
    public String userId;

    @Exclude
    @ColumnInfo(name = "likes")
    public int likes;

    @Exclude
    public String username;

    @Exclude
    public boolean likedByUser;

    public PostModel(String title, String image, String userId) {
        this.title = title;
        this.image = image;
        this.timestamp = new Date().getTime();
        this.userId = userId;
    }

    public PostModel(String id, Object data) {
        HashMap<String, Object> _data = (HashMap<String, Object>) data;
        this.id = id;
        this.title = (String) _data.get("title");
        this.image = (String) _data.get("image");
        this.timestamp = (long) _data.get("timestamp");
        this.userId = (String) _data.get("userId");
        if (_data.get("likes") != null) {
            this.likes = ((ArrayList) _data.get("likes")).size();
            for (Object like : ((ArrayList) _data.get("likes"))) {
                if (like.toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    this.likedByUser = true;
                    break;
                }
            }
        }
    }
}