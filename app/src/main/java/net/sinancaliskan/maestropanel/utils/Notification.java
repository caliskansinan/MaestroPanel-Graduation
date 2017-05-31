package net.sinancaliskan.maestropanel.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by SinanCaliskan on 16.04.2017.
 */

public class Notification {
    public static void Show(Context context,int smallIcon,int contentTitle,String message){       //get activity context ;icon,content title resource file id and message
        final NotificationCompat.Builder mBuilder =                                               //creating new notification builder
                new NotificationCompat.Builder(context)                                           //creating notification
                        .setSmallIcon(smallIcon)                                                  //set showing image
                        .setContentTitle(context.getString(contentTitle))                         //set notification title
                        .setContentText(message);                                                 //set notification message
        NotificationManager mNotifyMgr =                                                          //get notification service
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(0, mBuilder.build());                                                   //push to service created notification
    }
}
