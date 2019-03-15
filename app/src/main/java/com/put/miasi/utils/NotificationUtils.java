package com.put.miasi.utils;

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
import android.support.v4.app.NotificationManagerCompat;

import com.put.miasi.R;

public class NotificationUtils {
    public static final int MOON_RUNNER_WORKOUT_NOTIFICATION_ID = 1138;

    private static final int MOON_RUNNER_PENDING_INTENT_ID = 3417;
    private static final String MOON_RUNNER_CHANNEL_ID = "moon_runner_notification_channel_01";

    private static final int ACTION_RESUME_PENDING_INTENT_ID = 3418;
    public static final String ACTION_RESUME_WORKOUT = "resume_sport_activity";

    private static final int ACTION_PAUSE_PENDING_INTENT_ID = 3419;
    public static final String ACTION_PAUSE_WORKOUT = "pause_sport_activity";

    private static NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mNotificationBuilder;

    private static NotificationCompat.Action mResumeAction;
    private static NotificationCompat.Action mPauseAction;

    public static Notification createNotification(Context context, Class<?> cls) {
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, cls);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );
        intent.putExtra("menuFragment", "favoritesMenuItem");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    MOON_RUNNER_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        mNotificationBuilder =
                new NotificationCompat.Builder(context, MOON_RUNNER_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_edit_white_24dp)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mNotificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        return mNotificationBuilder.build();
    }


    public static void updateNotification(String message) {
        mNotificationBuilder.setContentTitle(message);
        notifyManager();
    }

    public static void notifyManager() {
        mNotificationManager.notify(MOON_RUNNER_WORKOUT_NOTIFICATION_ID, mNotificationBuilder.build());
    }

    public static void clearAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }



    // Create a helper method called largeIcon which takes in a Context as a parameter and
    // returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
    private static Bitmap largeIcon(Context context) {
        // Get a Resources object from the context.
        Resources resources = context.getResources();

        // Create and return a bitmap using BitmapFactory.decodeResource, passing in the
        // resources object and R.drawable.ic_local_drink_black_24px
        Bitmap largeIcon = BitmapFactory.decodeResource(
                resources,
                R.drawable.ic_edit_white_24dp);

        return largeIcon;
    }
}
