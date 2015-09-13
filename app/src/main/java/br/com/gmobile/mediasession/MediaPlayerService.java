package br.com.gmobile.mediasession;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import br.com.gmobile.mediasession.helpers.LogHelper;
import br.com.gmobile.mediasession.notification.MediaNotificationManager;

public class MediaPlayerService extends Service implements Playback.Callback {

    private static final String TAG = LogHelper.makeLogTag(MediaPlayerService.class);

    private MediaSessionCompat mMediaSession;
    private ComponentName mediaButtonReceiver;
    private MediaNotificationManager mNotificationManager;
    private Playback mPlayback;

    private MediaMetadataCompat currentTrack;
    private String currentTrackSource;

    public MediaPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaButtonReceiver = new ComponentName(this, MediaButtonReceiver.class);

        mMediaSession = new MediaSessionCompat(this, "MediaPlayerService", mediaButtonReceiver, getMediaPendingIntent());

        mMediaSession.setCallback(new MediaSessionCallback());
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackToLocal(AudioManager.STREAM_MUSIC);

        mNotificationManager = new MediaNotificationManager(this);

        createPlayback();


        updatePlaybackState(PlaybackStateCompat.STATE_NONE);
    }

    private void createPlayback() {
        mPlayback = new LocalPlayback(this);
        mPlayback.setState(PlaybackStateCompat.STATE_NONE);
        mPlayback.setCallback(this);
        mPlayback.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if( intent == null || intent.getAction() == null )
            return START_STICKY;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( Constants.ACTION_PLAY ) ) {
            currentTrack = intent.getParcelableExtra(Constants.PLAY_MEDIA_METADATA);
            currentTrackSource = intent.getStringExtra(Constants.CUSTOM_METADATA_TRACK_SOURCE);
            mNotificationManager.getTransportControls().play();
        } else if( action.equalsIgnoreCase( Constants.ACTION_PAUSE ) ) {
            mNotificationManager.getTransportControls().pause();
        } else if( action.equalsIgnoreCase( Constants.ACTION_FAST_FORWARD ) ) {
            mNotificationManager.getTransportControls().fastForward();
        } else if( action.equalsIgnoreCase( Constants.ACTION_REWIND ) ) {
            mNotificationManager.getTransportControls().rewind();
        } else if( action.equalsIgnoreCase( Constants.ACTION_PREVIOUS ) ) {
            mNotificationManager.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase(Constants.ACTION_NEXT ) ) {
            mNotificationManager.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( Constants.ACTION_STOP ) ) {
            mNotificationManager.getTransportControls().stop();
        }
        else if (action.equalsIgnoreCase(Constants.ACTION_PLAY_PAUSE)){
            if (mPlayback.isPlaying()) {
                mNotificationManager.getTransportControls().pause();
            }
            else {
                mNotificationManager.getTransportControls().play();
            }
        }

        return START_STICKY;
    }

    // Build the PendingIntent for the remote control client
    private PendingIntent getMediaPendingIntent(){

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(mediaButtonReceiver);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);

        return mediaPendingIntent;
    }

    @Override
    public void onCompletion() {
        updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(state);
    }

    @Override
    public void onError(String error) {
        updatePlaybackState(PlaybackStateCompat.STATE_ERROR);
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat item) {
        currentTrack = item;
        updateMetadata();
    }

    public final class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            return MediaPlayerService.this.onMediaButtonEvent(mediaButtonEvent);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            LogHelper.d(TAG, "onPlay");

            updatePlaybackState(PlaybackStateCompat.STATE_PLAYING);
            updateMetadata();

            if (!mMediaSession.isActive())
                mMediaSession.setActive(true);


            mPlayback.play(currentTrack, currentTrackSource);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
        }

        @Override
        public void onPause() {
            super.onPause();
            updatePlaybackState(PlaybackStateCompat.STATE_PAUSED);
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            super.onStop();
            updatePlaybackState(PlaybackStateCompat.STATE_STOPPED);
            mPlayback.stop(false);
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
        }
    }


    private void updatePlaybackState(int state) {

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_STOP);

        stateBuilder.setState(state, mPlayback.getCurrentStreamPosition(), 0, SystemClock.elapsedRealtime());

        mMediaSession.setPlaybackState(stateBuilder.build());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (mMediaSession.getRemoteControlClient() != null && mMediaSession.getRemoteControlClient() instanceof RemoteControlClient) {
                RemoteControlClient remoteControlClient = (RemoteControlClient) mMediaSession.getRemoteControlClient();

                int flags = RemoteControlClient.FLAG_KEY_MEDIA_PLAY
                        | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                        | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE;

                remoteControlClient.setTransportControlFlags(flags);
            }
        }

        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
            mNotificationManager.startNotification();
        }
    }

    public MediaSessionCompat.Token getSessionToken() {
        return  mMediaSession.getSessionToken();
    }

    public MediaControllerCompat getMediaController() {
        return mMediaSession.getController();
    }

    private void updateMetadata() {

        mMediaSession.setMetadata(currentTrack);

        // Set the proper album artwork on the media session, so it can be shown in the
        // locked screen and in other places.
        if (currentTrack.getDescription().getIconBitmap() == null &&
                currentTrack.getDescription().getIconUri() != null) {
            String albumUri = currentTrack.getDescription().getIconUri().toString();


            Picasso.with(this).load(albumUri).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    MediaMetadataCompat track = new MediaMetadataCompat.Builder(currentTrack)

                            // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
                            // example, on the lockscreen background when the media session is active.
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)

                                    // set small version of the album art in the DISPLAY_ICON. This is used on
                                    // the MediaDescription and thus it should be small to be serialized if
                                    // necessary..
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)

                            .build();

                    mMediaSession.setMetadata(track);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        if (mMediaSession != null) {
            mMediaSession.release();
        }

        super.onDestroy();
    }

    private boolean onMediaButtonEvent(Intent mediaButtonEvent) {

        KeyEvent event = (KeyEvent) mediaButtonEvent.getExtras().get(Intent.EXTRA_KEY_EVENT);
        if (KeyEvent.ACTION_DOWN == event.getAction()) {

            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

                    if (mPlayback.isPlaying()) {
                        mPlayback.pause();
                    }
                    else {
                        mPlayback.play(currentTrack, currentTrackSource);
                    }
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    mPlayback.play(currentTrack, currentTrackSource);
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    mPlayback.pause();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    mPlayback.stop(true);
                    return true;
            }
        }

        return false;
    }
}
