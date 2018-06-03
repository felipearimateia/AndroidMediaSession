package br.com.gmobile.mediasession;

import android.content.Intent;
import android.support.v4.media.MediaMetadataCompat;
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
        setContentView(br.com.gmobile.mediasession.R.layout.activity_main);

        ButterKnife.bind(this);

//        Intent intent = new Intent(this, MediaPlayerService.class);
//        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(br.com.gmobile.mediasession.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == br.com.gmobile.mediasession.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(br.com.gmobile.mediasession.R.id.action_play)
    public void onClickPlay(View view) {

        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(Constants.ACTION_PLAY);
        intent.putExtra(Constants.CUSTOM_METADATA_TRACK_SOURCE,
                "https://nerdcast-cdn.jovemnerd.com.br/nerdcast_530_esquadrao_suicida.mp3");
        intent.putExtra(Constants.PLAY_MEDIA_METADATA, createMetadata());

        startService(intent);
    }

    @OnClick(br.com.gmobile.mediasession.R.id.action_pause)
    public void onClickPause(View view) {

        Intent intent = new Intent(this, MediaPlayerService.class);
        intent.setAction(Constants.ACTION_PAUSE);

        startService(intent);
    }

    @OnClick(br.com.gmobile.mediasession.R.id.action_stop)
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
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "https://jovemnerd.com.br/wp-content/uploads/2016/08/NOVO_GIGA_SUICIDA_02.jpg")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Nerdcast 530 – Esquadrão Suicida, ou não…")
                .build();
    }
}
