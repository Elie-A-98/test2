package com.carista.data.realtimedb.models;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.firebase.firestore.PropertyName;

public class PackModel {

    @NonNull
    @Expose
    public String id;

    @PropertyName("title")
    public String title;

    @PropertyName("icon")
    public String icon;

    public PackModel(String id,String icon, String title){
        this.id=id;
        this.title=title;
        this.icon=icon;
    }
}