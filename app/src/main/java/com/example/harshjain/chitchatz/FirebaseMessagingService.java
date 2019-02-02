package com.example.harshjain.chitchatz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Harsh Jain on 11-01-2019.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String title = remoteMessage.getNotification().getTitle();
        String msg = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();

        String fromUSerid = remoteMessage.getData().get("from_User_id");

        Log.i(TAG, "onMessageReceived: "+fromUSerid);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        Intent intent = new Intent(click_action);
        intent.putExtra("uid",fromUSerid);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        0);
        mBuilder.setContentIntent(pendingIntent);

        int nid = (int)System.currentTimeMillis();
        NotificationManager mNotify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotify.notify(nid,mBuilder.build());

    }
}
