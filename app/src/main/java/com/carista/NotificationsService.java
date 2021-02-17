package com.carista;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.carista.photoeditor.EditImageActivity;
import com.carista.ui.main.UserProfileActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationsService extends FirebaseMessagingService {

    public static final String USER_CHANNEL_ID = "USER";
    public static final String BROADCAST_CHANNEL_ID = "BROADCAST";
    public static final String NEW_STICKERS_PACK_CHANNEL_ID = "STICKERS";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String channel = BROADCAST_CHANNEL_ID;
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getChannelId() != null)
            channel = remoteMessage.getNotification().getChannelId();

        PendingIntent pendingIntent = null;

        switch (channel) {
            case BROADCAST_CHANNEL_ID:
                break;
            case NEW_STICKERS_PACK_CHANNEL_ID:
                Intent intent = new Intent(this, EditImageActivity.class);
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                break;

            case USER_CHANNEL_ID:
                intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("userId", remoteMessage.getData().get("userId"));
                intent.putExtra("nickname", remoteMessage.getData().get("nickname"));
                pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                break;
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(this.getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(-1, builder.build());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    public static void createNotificationChannel(Context context, String channelName, String channelId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
