package br.com.pontomobi.mediasession.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

/**
 * Created by felipe.arimateia on 3/13/15.
 */
public interface NotificationBuilder {

    public NotificationBuilder setWhen(long when);
    public NotificationBuilder setUsesChronometer(boolean b);
    public NotificationBuilder setSmallIcon(int icon);
    // ...

    /** Sets MediaStyle with setShowActionsInCompactView(). */
    public NotificationBuilder setMediaStyleActionsInCompactView(int... actions);

    public Notification build();

    public NotificationBuilder addAction(int icon, CharSequence title, PendingIntent intent);

    public NotificationBuilder setSubText(CharSequence text);

    public NotificationBuilder setStyle(Notification.Style style);

    public NotificationBuilder setStyle(NotificationCompat.Style style);

    public NotificationBuilder setColor(int color);

    public NotificationBuilder setVisibility(int visibility);

    public NotificationBuilder setShowWhen(boolean show);

    public NotificationBuilder setOngoing(boolean ongoing);

    public NotificationBuilder setContentIntent(PendingIntent intent);

    public NotificationBuilder setContentTitle(CharSequence title);

    public NotificationBuilder setContentText(CharSequence text);

    public NotificationBuilder setLargeIcon(Bitmap icon);

    public NotificationBuilder addAction(NotificationCompat.Action action);

    public NotificationBuilder addAction(Notification.Action action);


}
