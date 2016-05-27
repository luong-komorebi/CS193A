/*
 * CS 193A, Marty Stepp
 * This activity is the main GUI for the jukebox app.
 * Click on a song from the list, and it will play in the background
 * using a service.
 * Even if the app is exited, the song will continue playing to completion
 * because it is running in a service.
 */

package com.jukebox;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;


public class JukeboxActivity extends Activity
        implements AdapterView.OnItemClickListener {

    /*
     * This method runs when the activity is started up.
     * Sets up initial state of the GUI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jukebox);

        // listen for clicks on list items
        ListView list = (ListView) findViewById(R.id.song_list);
        list.setOnItemClickListener(this);
    }

    /*
     * This method is called when items in the ListView are clicked.
     * It initiates a request to the JukeboxService to play the given song.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
        String[] songTitles = getResources().getStringArray(R.array.song_titles);
        String title = songTitles[index].toLowerCase().replace(" ", "");

        Intent intent = new Intent(this, JukeboxService.class);
        intent.putExtra("title", title);
        intent.setAction(JukeboxService.ACTION_PLAY);
        startService(intent);
    }

    /*
     * This method is called when the Play button is clicked.
     * We didn't do anything here, so the button does nothing.
     */
    public void onClickPlay(View view) {
        // ListView list = (ListView) findViewById(R.id.song_list);
        // int index = list.getSelectedItemPosition();
        // ...
    }

    /*
     * This method is called when the Play button is clicked.
     * It initiates a request to the JukeboxService to stop the current playback.
     */
    public void onClickStop(View view) {
        Intent intent = new Intent(this, JukeboxService.class);
        intent.setAction(JukeboxService.ACTION_STOP);
        startService(intent);
    }
}
