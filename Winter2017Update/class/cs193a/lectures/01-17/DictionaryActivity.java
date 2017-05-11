/*
 * CS 193A, Winter 2017, Marty Stepp
 * This program shows a ListView of words and lets the user click them to see
 * each word's definition pop up on screen as a Toast.
 */

package edu.stanford.cs193a.dictionaryawesome;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;
import stanford.androidlib.SimpleActivity;

public class DictionaryActivity extends SimpleActivity {
    // A list of words and definitions to show in our app.
    // In a later lecture, we will read the words from a file instead.
    private static final String[] WORDS = {
            "mediocre",       "UC Berkeley",
            "underachiever",  "Stanford A- student",
            "jerk",           "Marty Stepp",
            "defenestrate",   "to throw out of a window"
    };

    // a dictionary of {word -> definition} pairs for lookup
    private Map<String, String> dictionary;

    /*
     * This method runs when the app is first loading up.
     * It sets up the dictionary of words and definitions.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        // convert the array into a map
        dictionary = new HashMap<>();
        for (int i = 0; i < WORDS.length; i += 2) {
            dictionary.put(WORDS[i], WORDS[i + 1]);
        }

        // put the dictionary words into an Adapter so they can be seen in ListView
        ListView list = (ListView) findViewById(R.id.word_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,                                        // activity
                android.R.layout.simple_list_item_1,         // layout,
                new ArrayList<String>(dictionary.keySet())   // array
        );
        list.setAdapter(adapter);

        // this is the code that should run when the user taps items in the list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // look up definition of word and display as a Toast
                String word = parent.getItemAtPosition(position).toString();
                String defn = dictionary.get(word);
                toast(defn);
            }
        });
    }
}



