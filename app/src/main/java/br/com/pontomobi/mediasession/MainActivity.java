package br.com.pontomobi.mediasession;

import android.content.Intent;
import android.media.MediaMetadata;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        Intent intent = new Intent(this, MediaPlayerService.class);
//        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.action_play)
    public void onClickPlay(View view) {

        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(Constants.ACTION_PLAY);
        intent.putExtra(Constants.CUSTOM_METADATA_TRACK_SOURCE,
                "http://jovemnerd.com.br/podpress_trac/feed/108752/0/nerdcast_456_diabo.mp3 ");
        intent.putExtra(Constants.PLAY_MEDIA_METADATA, createMetadata());

        startService(intent);
    }

    @OnClick(R.id.action_pause)
    public void onClickPause(View view) {

        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(Constants.ACTION_PAUSE);

        startService(intent);
    }

    @OnClick(R.id.action_stop)
    public void onClickStop(View view) {

        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(Constants.ACTION_STOP);

        startService(intent);
    }

    private MediaMetadataCompat createMetadata() {

        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "55043d8a3e499b9da36b3274")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Nerdcast")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Nerdcast")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "http://jumphawk.jovemnerd.com.br/wp-content/uploads/destaque_DIABO_01-e1426210345352.jpg")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Nerdcast 456 – O Cramunhão")
                .build();
    }
}
