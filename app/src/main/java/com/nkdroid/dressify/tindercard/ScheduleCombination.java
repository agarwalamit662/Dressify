package com.nkdroid.dressify.tindercard;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.nkdroid.dressify.R;

public class ScheduleCombination extends IntentService {

    public ScheduleCombination() {
        super("ScheduleCombination");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e("In Handle Intent","In handle intent");
        Log.e("In Handle Intent","In handle intent");
        Log.e("In Handle Intent","In handle intent");

        if (intent != null) {

            Notification notification;

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_heart_red_36dp);
            mBuilder.setContentTitle("New Combination Available");
            mBuilder.setContentText("Hi, New Combination of shirt and pant is available");
            mBuilder.setAutoCancel(true);

            notification = mBuilder.build();
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(1, notification);


        }
    }

}
