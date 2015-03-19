package br.com.pontomobi.mediasession;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by felipe.arimateia on 3/3/15.
 */
public class MediaButtonReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        // Only react if this actually is a media button event
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            try {
                // Find out if the event was a button press
                KeyEvent event = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
                if (KeyEvent.ACTION_UP == event.getAction()) {

                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                            sendCommand(context, Constants.ACTION_PLAY_PAUSE);
                            break;
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            sendCommand(context, Constants.ACTION_PLAY);
                            break;
                        case KeyEvent.KEYCODE_HEADSETHOOK:
                        case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            sendCommand(context, Constants.ACTION_PAUSE);
                            break;
//                        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
//                            actionIntent.setAction(PlayerService.ACTION_PREVIOUS);
//                            break;
//                        case KeyEvent.KEYCODE_MEDIA_NEXT:
//                            actionIntent.setAction(PlayerService.ACTION_SKIP);
//                            break;
//                        case KeyEvent.KEYCODE_MEDIA_REWIND:
//                            actionIntent.setAction(ACTION_REWIND);
//                            break;
//                        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
//                            actionIntent.setAction(ACTION_FORWARD);
//                            break;
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                        case KeyEvent.KEYCODE_MEDIA_EJECT:
                            sendCommand(context, Constants.ACTION_STOP);
                            break;
                    }
                }
            } catch (SecurityException se) {
                // This might happen if called from the outside since our
                // service is not exported, just do nothing.
                Log.d("MediaButtonReceiver", se.getMessage(), se);
            }
        }
    }

    private void sendCommand(Context context, String action) {
        String pkg = context.getPackageName();

        final Intent actionIntent = new Intent(context, MediaPlayerService.class);
//        actionIntent.setPackage(pkg);
        actionIntent.setAction(action);

        context.startService(actionIntent);
    }
}
