package us.gpop.aid.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import us.gpop.aid.Activities.DFPedometerActivityWear;
import us.gpop.aid.R;

/**
 * Created by Michael Yoon Huh on 10/10/2014.
 */

public class DFNotificationsWear {

    /**
     * CLASS VARIABLES ________________________________________________________________________ *
     */

    private Context df_context; // Context for the instance in which this class is used.

    /**
     * INITIALIZATION FUNCTIONALITY ___________________________________________________________ *
     */

    // DFNotifications(): Constructor for the DFNotifications class.
    public final static DFNotificationsWear dfNotify = new DFNotificationsWear();

    // DFNotifications(): Deconstructor for the DFNotifications class.
    public DFNotificationsWear() {
    }

    // getInstance(): Returns the dfNotify instance.
    public static DFNotificationsWear getInstance() {
        return dfNotify;
    }

    // initializeDF(): Initializes the DFNotifications class variables.
    public void initializeDF(Context con) {
        df_context = con; // Context for the instance in which this class is used.
    }

    /**
     * NOTIFICATION FUNCTIONALITY _____________________________________________________________ *
     */

    // Creates the notification.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification(Context con, String notiText) {

        // Notification ID tag.
        int notificationId = 001;

        // Intent to launch the splash.
        Intent df_intent = new Intent(con, DFPedometerActivityWear.class);
        PendingIntent pIntent = PendingIntent.getActivity(con, 0, df_intent, 0);

        // ANDROID-WEAR
        // Specify the 'big view' content to display the long
        // event description that may not fit the normal content text.
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(notiText);

        // ANDROID WEAR-SPECIFIC ACTION
        // ONLY APPEARS ON WEAR
        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_launcher, "VIEW", pIntent).build();

        // ANDROID WEAR NOTIFICATION FOR ANDROID WEAR ONLY ACTIONS.
        // Build the notification and add the action via WearableExtender
        Notification noti =
                new NotificationCompat.Builder(con)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("AID")
                        .setContentText(notiText).setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(bigStyle)
                        .extend(new NotificationCompat.WearableExtender().addAction(action))
                        .build();

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(con);

        // ANDROID WEAR SPECIFIC MANAGER NOTIFY
        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, noti);
    }
}

