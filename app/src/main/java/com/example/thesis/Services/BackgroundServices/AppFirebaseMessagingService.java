package com.example.thesis.Services.BackgroundServices;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.thesis.MainActivity;
import com.example.thesis.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AppFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        // If you want to fire a local notification (that notification on the top of the phone screen)
        // you should fire it from here
        sendLocalNotification(notificationTitle, notificationBody);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    private void sendLocalNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                    "Channel",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setAutoCancel(true)   //Automatically delete the notification
                    .setSmallIcon(R.drawable.ic_chat) //Notification icon
                    .setContentIntent(pendingIntent)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationBody)
                    .setSound(defaultSoundUri);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(1234, notificationBuilder.build());
        }

    }
}
