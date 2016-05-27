/*
 * CS 193A, Winter 2015, Marty Stepp
 * This file implements the main activity for the insult list app.
 * It shows a list of insult phrases and speaks them aloud when
 * the user clicks on each one.
 */

package com.example.stepp.insultlist;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Scanner;

public class InsultListActivity extends Activity {
    private ArrayList<String> lines;       // lines of file of insults
    private TextToSpeech tts;              // TTS engine
    private boolean speechReady = false;   // true when TTS engine is loaded

    /*
     * Initializes the state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insult_list);

        // read input file
        lines = readEntireFile(R.raw.phrases);

        // set up the ListView to use the lines from the file
        ListView myList = (ListView) findViewById(R.id.list_of_insults);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, lines);
        myList.setAdapter(adapter);

        // set up event listening for clicks on the list
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
                handleClick(index);
            }
        });

        // set up text-to-speech engine
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                speechReady = true;
            }
        });
    }

    /*
     * Handles a click on the list item at the given 0-based index.
     * Speaks the given insult aloud using text-to-speech.
     */
    private void handleClick(int index) {
        String text = lines.get(index);
        if (speechReady) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    /*
     * Reads the lines of the file with the given resource ID,
     * returning them as an array list of strings.
     * Assumes that the file with the given ID exists in res/raw folder.
     */
    private ArrayList<String> readEntireFile(int id) {
        ArrayList<String> list = new ArrayList<String>();
        Scanner scan = new Scanner(getResources().openRawResource(id));
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            list.add(line);
        }
        return list;
    }
}
