package br.com.pontomobi.mediasession.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

/**
 * Created by felipe.arimateia on 3/13/15.
 */
public class NotificationBuilderAPI20 implements NotificationBuilder {

    private NotificationCompat.Builder builder;

    public NotificationBuilderAPI20(Context context) {
        builder = new NotificationCompat.Builder(context);
    }

    @Override
    public NotificationBuilder setWhen(long when) {
        builder.setWhen(when);
        return this;
    }

    @Override
    public NotificationBuilder setUsesChronometer(boolean b) {
        builder.setUsesChronometer(b);
        return this;
    }

    @Override
    public NotificationBuilder setSmallIcon(int icon) {
        builder.setSmallIcon(icon);
        return this;
    }

    // ...

    @Override
    public NotificationBuilder setMediaStyleActionsInCompactView(int... actions) {
        // Noop for Android API V20-.
        return this;
    }

    @Override
    public Notification build() {
        return builder.build();
    }

    @Override
    public NotificationBuilder addAction(int icon, CharSequence title, PendingIntent intent) {
        builder.addAction(icon, title, intent);
        return this;
    }

    @Override
    public NotificationBuilder setSubText(CharSequence text) {
        builder.setSubText(text);
        return this;
    }

    @Override
    @TargetApi(21)
    public NotificationBuilder setStyle(Notification.Style style) {
        return this;
    }

    @Override
    public NotificationBuilder setStyle(NotificationCompat.Style style) {
        builder.setStyle(style);
        return this;
    }

    @Override
    public NotificationBuilder setColor(int color) {
        builder.setColor(color);
        return this;
    }

    @Override
    public NotificationBuilder setVisibility(int visibility) {
        builder.setVisibility(visibility);
        return this;
    }

    @Override
    public NotificationBuilder setShowWhen(boolean show) {
        builder.setShowWhen(show);
        return this;
    }

    @Override
    public NotificationBuilder setOngoing(boolean ongoing) {
        builder.setOngoing(ongoing);
        return this;
    }

    @Override
    public NotificationBuilder setContentIntent(PendingIntent intent) {
        builder.setContentIntent(intent);
        return this;
    }

    @Override
    public NotificationBuilder setContentTitle(CharSequence title) {
        builder.setContentTitle(title);
        return this;
    }

    @Override
    public NotificationBuilder setContentText(CharSequence text) {
        builder.setContentText(text);
        return this;
    }

    @Override
    public NotificationBuilder setLargeIcon(Bitmap icon) {
        builder.setLargeIcon(icon);
        return this;
    }

    @Override
    public NotificationBuilder addAction(NotificationCompat.Action action) {
        builder.addAction(action);
        return this;
    }

    @Override
    @TargetApi(21)
    public NotificationBuilder addAction(Notification.Action action) {
        return this;
    }


}
