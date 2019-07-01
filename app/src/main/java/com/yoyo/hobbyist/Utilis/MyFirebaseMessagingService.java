package com.yoyo.hobbyist.Utilis;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yoyo.hobbyist.MainActivity;
//todo: לסדר פה
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (!MainActivity.isIsVisible()) {

            Log.d(TAG, "From: " + remoteMessage.getFrom());

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                Intent intent = new Intent("message_received");
//            intent.putExtra("message",remoteMessage.getData().get("message"));
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                //if the application is not in forground post notification



            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
//                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                Notification.Builder builder = new Notification.Builder(this);
//
//                if (Build.VERSION.SDK_INT >= 26) {
//                    NotificationChannel channel = new NotificationChannel("id_1", "name_1", NotificationManager.IMPORTANCE_HIGH);
//                    manager.createNotificationChannel(channel);
//                    builder.setChannelId("id_1");
//                }
//                builder.setContentTitle("!New Activity!").setContentText(remoteMessage.getData().get("message")).setSmallIcon(android.R.drawable.star_on);
//                manager.notify(1, builder.build());
            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.

    }
}
