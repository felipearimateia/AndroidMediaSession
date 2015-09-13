package br.com.gmobile.mediasession.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.com.gmobile.mediasession.Constants;
import br.com.gmobile.mediasession.MainActivity;
import br.com.gmobile.mediasession.MediaPlayerService;
import br.com.gmobile.mediasession.R;
import br.com.gmobile.mediasession.helpers.LogHelper;

/**
 * Created by felipe.arimateia on 3/18/15.
 */
public class MediaNotificationManager extends BroadcastReceiver {

    private static final String TAG = LogHelper.makeLogTag(MediaNotificationManager.class);

    private static final int REQUEST_CODE = 100;
    private static final int NOTIFICATION_ID = 512;

    private final NotificationManager mNotificationManager;
    private final PendingIntent mPauseIntent;
    private final PendingIntent mPlayIntent;
    private final PendingIntent mStopIntent;

    private MediaPlayerService mService;
    private MediaControllerCompat mMediaController;
    private MediaControllerCompat.TransportControls mTransportControls;
    private MediaSessionCompat.Token mSessionToken;

    private MediaMetadataCompat mMetadata;
    private PlaybackStateCompat mPlaybackState;

    public MediaNotificationManager(MediaPlayerService service) {
        mService = service;

        updateSessionToken();

        mNotificationManager = (NotificationManager) mService
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String pkg = mService.getPackageName();
        mPauseIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(Constants.ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(Constants.ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);
        mStopIntent = PendingIntent.getBroadcast(mService, REQUEST_CODE,
                new Intent(Constants.ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationManager.cancelAll();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        LogHelper.d(TAG, "Received intent with action " + action);
        switch (action) {
            case Constants.ACTION_PAUSE:
                mTransportControls.pause();
                break;
            case Constants.ACTION_PLAY:
                mTransportControls.play();
                break;
            case Constants.ACTION_STOP:
                mTransportControls.stop();
                break;
            default:
                LogHelper.w(TAG, "Unknown intent ignored. Action=", action);
        }
    }

    public void startNotification() {

        mMetadata = mMediaController.getMetadata();
        mPlaybackState = mMediaController.getPlaybackState();

        // The notification must be updated after setting started to true
        Notification notification = createNotification(mService);

        if (notification != null) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_PAUSE);
            filter.addAction(Constants.ACTION_PLAY);
            filter.addAction(Constants.ACTION_STOP);

            mService.registerReceiver(this, filter);
            mService.startForeground(NOTIFICATION_ID, notification);
        }
    }


    private void updateSessionToken() {

        MediaSessionCompat.Token freshToken = mService.getSessionToken();

        if (mSessionToken == null || !mSessionToken.equals(freshToken)) {
            mSessionToken = freshToken;

            try {
                mMediaController = mService.getMediaController();
                mTransportControls = mMediaController.getTransportControls();
                mMediaController.registerCallback(mCb);

            } catch (Exception e) {
                LogHelper.e(TAG, e, e.getMessage());
            }

        }
    }


    private final MediaControllerCompat.Callback mCb = new MediaControllerCompat.Callback() {

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {

            mPlaybackState = state;

            LogHelper.d(TAG, "Received new playback state", state);
            if (state != null && (state.getState() == PlaybackStateCompat.STATE_STOPPED || state.getState() == PlaybackStateCompat.STATE_NONE)) {
                stopNotification();
            } else {
                Notification notification = createNotification(mService);
                if (notification != null) {
                    mNotificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMetadata = metadata;
            LogHelper.d(TAG, "Received new metadata ", metadata);
            Notification notification = createNotification(mService);
            if (notification != null) {
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            LogHelper.d(TAG, "Session was destroyed, resetting to the new session token");
            updateSessionToken();
        }
    };

    private Notification createNotification(Context context) {

        LogHelper.d(TAG, "updateNotificationMetadata. mMetadata=" + mMetadata);
        if (mMetadata == null || mPlaybackState == null) {
            return null;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);


        addPlayPauseAction(builder);
        addStopAction(builder);

        MediaDescriptionCompat description = mMetadata.getDescription();


        String fetchArtUrl = null;
        Bitmap art = BitmapFactory.decodeResource(mService.getResources(), R.drawable.ic_default_art);

        if (description.getIconUri() != null) {
            fetchArtUrl = description.getIconUri().toString();
        }

        builder
                .setColor(mService.getResources().getColor(android.R.color.black))
                .setSmallIcon(R.drawable.ic_notification)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(createContentIntent(description))
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setLargeIcon(art);

        builder.setStyle(new android.support.v7.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(new int[]{0})  // show only play/pause in compact view
                .setMediaSession(MediaSessionCompat.Token.fromToken(mSessionToken.getToken())));

        builder.setOngoing(mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING);

        if (fetchArtUrl != null) {
            fetchBitmapFromURLAsync(fetchArtUrl, builder);
        }

        Notification notification = builder.build();

        return notification;
    }

    private void stopNotification() {
        mMediaController.unregisterCallback(mCb);
        try {
            mNotificationManager.cancel(NOTIFICATION_ID);
            mService.unregisterReceiver(this);

        } catch (IllegalArgumentException ex) {
            // ignore if the receiver is not registered.
        }

        mService.stopForeground(true);
    }

    private void addPlayPauseAction(NotificationCompat.Builder builder) {
        LogHelper.d(TAG, "addPlayPauseAction");
        String label;
        int icon;
        PendingIntent intent;
        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            label = mService.getString(R.string.label_pause);
            icon = android.R.drawable.ic_media_pause;
            intent = mPauseIntent;
        } else {
            label = mService.getString(R.string.label_play);
            icon = android.R.drawable.ic_media_play;
            intent = mPlayIntent;
        }

        builder.addAction(new NotificationCompat.Action.Builder(icon, label, intent).build());
    }

    private void addStopAction(NotificationCompat.Builder builder) {
        LogHelper.d(TAG, "addStopAction");

        String label = mService.getString(R.string.label_stop);
        int icon = R.drawable.ic_close_black_24dp;

        builder.addAction(new NotificationCompat.Action.Builder(icon, label, mStopIntent).build());
    }

    private PendingIntent createContentIntent(MediaDescriptionCompat description) {

        Intent openUI = new Intent(mService, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        if (description != null) {
//            openUI.putExtra(Constants.EXTRA_CURRENT_MEDIA_DESCRIPTION, description);
//        }

        return PendingIntent.getActivity(mService, REQUEST_CODE, openUI,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private void fetchBitmapFromURLAsync(final String bitmapUrl, final NotificationCompat.Builder builder) {


        Picasso.with(mService).load(bitmapUrl).memoryPolicy(MemoryPolicy.NO_CACHE).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    builder.setLargeIcon(bitmap);
                    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        return mTransportControls;
    }
}


