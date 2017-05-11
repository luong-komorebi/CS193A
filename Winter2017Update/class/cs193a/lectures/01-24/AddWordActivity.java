/**
 * CS 193A, Winter 2017, Marty Stepp
 * This activity helps the user add a word to the app.
 * After it is finished, it returns to the StartWordActivity.
 */

package edu.stanford.cs193a.dictionaryawesome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.PrintStream;

import stanford.androidlib.SimpleActivity;

public class AddWordActivity extends SimpleActivity {

    /**
     * Called when the activity is first loading.
     * Unpacks the "initialtext" parameter sent by the StartWordActivity and displays it.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        Intent intent = getIntent();
        String text = intent.getStringExtra("initialtext");   // "FooBar"
        $TV(R.id.new_word).setText(text);
    }

    /**
     * Called when the Add This Word button is clicked.
     * Adds the given word and definition to a line at the end of the file added_words.txt.
     */
    public void addThisWordClick(View view) {
        // add the given word/defn to dictionary
        String newWord = $ET(R.id.new_word).getText().toString();
        String newDefn = $ET(R.id.new_defn).getText().toString();

        // write the new line to the file, e.g. "word \t defn"
        PrintStream output = new PrintStream(openFileOutput("added_words.txt", MODE_APPEND));
        output.println(newWord + "\t" + newDefn);
        output.close();

        // go back to start menu activity
        // ("return" new word/defn back to StartMenuActivity)
        Intent goBack = new Intent();
        goBack.putExtra("newword", newWord);
        goBack.putExtra("newdefn", newDefn);
        setResult(RESULT_OK, goBack);
        finish();

        // with the Stanford library, you can just write:
        // finish("newword", newWord, "newdefn", newDefn);
    }
}
