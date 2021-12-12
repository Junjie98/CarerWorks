package com.example.carertrackingapplication.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.carertrackingapplication.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class firebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().isEmpty()){
            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());

        }else{
            showNotification(remoteMessage.getData());
        }
    }

    private void showNotification(Map<String,String> data) {
        String title = data.get("title").toString();
        String body = data.get("body").toString();

        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "example.carertrackingapplication.services.test";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notiChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notiChannel.setDescription("Updates Info");
            notiChannel.enableLights(true);
            notiChannel.setLightColor(Color.BLUE);
            notiChannel.enableLights(true);
            notiManager.createNotificationChannel(notiChannel);

        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentText(title)
                .setContentText(body)
                .setContentInfo("Info");

        notiManager.notify(new Random().nextInt(), notificationBuilder.build());

    }
    private void showNotification(String title, String body){

        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "example.carertrackingapplication.services.test";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notiChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notiChannel.setDescription("Updates Info");
            notiChannel.enableLights(true);
            notiChannel.setLightColor(Color.BLUE);
            notiChannel.enableLights(true);
            notiManager.createNotificationChannel(notiChannel);

        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentText(title)
                .setContentText(body)
                .setContentInfo("Info");

        notiManager.notify(new Random().nextInt(), notificationBuilder.build());
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("TOKENFIREBASE",s);

    }


}
