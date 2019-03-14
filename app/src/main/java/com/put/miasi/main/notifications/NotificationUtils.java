package com.put.miasi.main.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.put.miasi.R;
import com.put.miasi.main.MainActivity;

public class NotificationUtils {
    private static final String TAG = "NotificationUtils";

    public static String NOTIFICATION_INTENT_EXTRA = "open_notifications";

    private static final int MIASI_NOTIFICATION_ID = 1302;
    private static final String MIASI_NOTIFICATION_CHANNEL_ID = "miasi_notification_channel_01";

    private static NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mNotificationBuilder;


    public static void createNotification(Context context) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MIASI_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationBuilder =
                new NotificationCompat.Builder(context, MIASI_NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setLargeIcon(largeIcon(context))
                    // .setContentTitle("title")
                    // .setContentText("text")
                    // .setColor(ContextCompat.getColor(context, R.color.secondaryLightColor))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(contentIntent(context))
                    .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mNotificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // return mNotificationBuilder.build();
    }


    public static void updateNotification(String message) {
        mNotificationBuilder.setContentTitle(message);
        notifyManager();
    }

    public static void notifyManager() {
        mNotificationManager.notify(MIASI_NOTIFICATION_ID, mNotificationBuilder.build());
    }


    public static void clearAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private static PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_INTENT_EXTRA, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();

        Bitmap largeIcon = BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_notifications_black_24dp);

        return largeIcon;
    }
}
