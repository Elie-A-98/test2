package com.carista.data.realtimedb.models;

import com.google.gson.annotations.Expose;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.PropertyName;


public class StickerModel {

    @NonNull
    @Expose
    @PropertyName("image")
    public String image;

    @PropertyName("name")
    public String name;

    public StickerModel(String image, String name){
        this.image=image;
        this.name=name;
    }

}
