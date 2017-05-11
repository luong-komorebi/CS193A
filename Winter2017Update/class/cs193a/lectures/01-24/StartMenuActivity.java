package edu.stanford.cs193a.dictionaryawesome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import stanford.androidlib.SimpleActivity;

public class StartMenuActivity extends SimpleActivity {
    private static final int REQ_CODE_ADD_WORD = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);
    }

    /*
     * Called when the Play the Game button is clicked.
     * Launches the DictionaryActivity to play the game.
     */
    public void playTheGameClick(View view) {
        // go to the DictionaryActivity
        Intent intent = new Intent(this, DictionaryActivity.class);
        startActivity(intent);
    }

    /*
     * Called when the Add a New Word button is clicked.
     * Launches the AddWordActivity to add a new word to the game.
     */
    public void addANewWordClick(View view) {
        // go to the AddWordActivity
        // (pass a parameter named "initialtext")
        Intent intent = new Intent(this, AddWordActivity.class);
        intent.putExtra("initialtext", "FooBar");
        startActivityForResult(intent, REQ_CODE_ADD_WORD);

        // with the Stanford library, you can just write:
        // startActivityForResult(AddWordActivity.class, REQ_CODE_ADD_WORD,
        //          "initialtext", "FooBar");
    }

    /*
     * Called when AddWordActivity finish()es and comes back to me.
     * Allows this activity to examine any data that was "returned" by the AddWordActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQ_CODE_ADD_WORD) {
            // we are coming back from AddWordActivity
            // intent
            // -> "newword", "newdefn"
            String newWord = intent.getStringExtra("newword");
            String newDefn = intent.getStringExtra("newdefn");

            toast("You added the word: " + newWord);
        }
    }
}
