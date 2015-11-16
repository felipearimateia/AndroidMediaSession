package br.com.gmobile.mediasession.events;

import android.support.v4.media.MediaMetadataCompat;

/**
 * Created by cit on 06/10/15.
 */
public class MetadataEvent {

    public MediaMetadataCompat metadataCompat;

    public MetadataEvent(MediaMetadataCompat metadataCompat) {
        this.metadataCompat = metadataCompat;
    }
}
