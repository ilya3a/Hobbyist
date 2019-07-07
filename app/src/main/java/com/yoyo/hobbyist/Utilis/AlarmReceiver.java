package com.yoyo.hobbyist.Utilis;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.yoyo.hobbyist.MainActivity;
import com.yoyo.hobbyist.R;

import static android.content.Context.NOTIFICATION_SERVICE;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final int NOTIF_ID = 1;
//        Toast.makeText(context, "Check new Updates", Toast.LENGTH_SHORT).show();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long myInterval = intent.getLongExtra("interval", 0);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + myInterval, nextPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        String channelId = null;
        if (Build.VERSION.SDK_INT >= 26) {
            channelId = "some_channel_id";
            CharSequence channelName = "Some Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200});
            notificationManager.createNotificationChannel(notificationChannel);
        }


        Intent notifIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setSmallIcon(R.mipmap.ic_logo_foreground)
                .setContentTitle(context.getString(R.string.we_miss_you ))
                .setContentText(context.getString(R.string.checkout) +
                        context.getString(R.string.click_here));
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        // add sound light....
        notification.defaults = Notification.DEFAULT_ALL;
        //
        //notif not cleareble from bar and makes noise till got attention
//                notification.flags |=Notification.FLAG_NO_CLEAR|Notification.FLAG_INSISTENT;
        notificationManager.notify(NOTIF_ID, notification);

//        Notification.Builder builder = new Notification.Builder(context);
//        Notification notification = builder.setContentTitle("Notification test app")
//                .setContentText("new notification")
//                .setSmallIcon(android.R.drawable.star_on)
//                .setContentIntent(pendingIntent).build();
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);


    }

}
