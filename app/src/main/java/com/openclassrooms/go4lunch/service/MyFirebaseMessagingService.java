package com.openclassrooms.go4lunch.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.ui.MySettings;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    MySettings mySettings;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.i("MyFirebaseMsgService", "onMessageReceived");
        String title = null;
        String body = null;
        if (remoteMessage.getNotification() == null) {
            Log.i("MyFirebaseMsgService", "onMessageReceived unknown message");
            super.onMessageReceived(remoteMessage);
            return;
        }
//        title = remoteMessage.getNotification().getTitle();
//        body = remoteMessage.getNotification().getBody();

        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
 //       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Heads_Up_Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setAutoCancel(true);

            mySettings = MySettings.getMySettings();
            Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
            bigTextStyle.setBigContentTitle(mySettings.getTitle());
            bigTextStyle.bigText(mySettings.getBody());

            notification.setStyle(bigTextStyle);

            NotificationManagerCompat.from(this).notify(1, notification.build());
 //       }
        super.onMessageReceived(remoteMessage);
    }

    @Override
    public void onNewToken (@NonNull String token) {}
}
