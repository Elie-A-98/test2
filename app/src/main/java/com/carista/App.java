package com.carista;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.carista.api.RetrofitManager;

import static com.carista.NotificationsService.BROADCAST_CHANNEL_ID;
import static com.carista.NotificationsService.NEW_STICKERS_PACK_CHANNEL_ID;
import static com.carista.NotificationsService.USER_CHANNEL_ID;

public class App extends Application {

    public static final String PREF_DARK_THEME = "dark_theme";

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREF_DARK_THEME, Context.MODE_PRIVATE);

        boolean isDarkTheme = sharedPreferences.getBoolean(PREF_DARK_THEME, false);

        if (isDarkTheme)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        RetrofitManager.getInstance(getApplicationContext());

        NotificationsService.createNotificationChannel(this, "Users", USER_CHANNEL_ID);
        NotificationsService.createNotificationChannel(this, "Broadcast", BROADCAST_CHANNEL_ID);
        NotificationsService.createNotificationChannel(this, "Stickers", NEW_STICKERS_PACK_CHANNEL_ID);
    }
}
