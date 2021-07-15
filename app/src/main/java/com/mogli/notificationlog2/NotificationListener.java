package com.mogli.notificationlog2;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {

    private final String TAG = NotificationListener.class.getSimpleName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "ID:" + sbn.getId());
        Log.i(TAG, "Posted by:" + sbn.getPackageName());
        try {
            NotificationHandler notificationHandler = new NotificationHandler(this);
            notificationHandler.handlePosted(sbn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        Log.v(TAG, "ID:" + sbn.getId());
//        Log.v(TAG, "Removed ," + "Posted by:" + sbn.getPackageName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected() {
        Log.i(TAG, "Connected");
    }

    @Override
    public void onListenerDisconnected() {
        Log.v(TAG, "Disconnected");
    }
}
