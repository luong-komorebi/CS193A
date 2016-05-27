/*
 * CS 193A, Marty Stepp
 * This service plays songs in the background.
 * Even if the downloader app is exited, the music keeps playing.
 */

package com.jukebox;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class JukeboxService extends Service {
    // we declare constants for "actions" that are packed into each intent
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_STOP = "stop";

    // the actual media player to play the music
    private MediaPlayer player;

    /*
     * This method is called each time a request comes in from the app
     * via an intent.  It processes the request by playing or stopping
     * the song as appropriate.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_PLAY)) {
            // convert the song title into a resource ID
            // e.g. "ehonda" -> R.raw.ehonda
            String title = intent.getStringExtra("title");
            int id = getResources().getIdentifier(title, "raw", getPackageName());

            // stop any previous playing song
            if (player != null) {
                player.stop();
                player.release();
            }

            // start the new song
            player = MediaPlayer.create(this, id);
            player.setLooping(true);
            player.start();
        } else if (action.equals(ACTION_STOP)) {
            // stop any currently playing song
            if (player != null) {
                player.stop();
                player.release();
            }
        }

        return START_STICKY;   // START_STICKY means service will keep running
    }

    /*
     * This method specifies how our service will deal with binding.
     * We don't choose to support binding, which is indicated by returning null.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
