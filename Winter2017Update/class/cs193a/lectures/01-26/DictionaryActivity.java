/*
 * CS 193A, Winter 2017, Marty Stepp
 * This program performs a kind of "vocab quiz" with the user.
 * It shows a word and 5 possible definitions of that word.
 * The user must click the one they think is correct.
 * The app will give them feedback about whether they picked the right answer each time.
 * It also reads words from a separate file, added_words.txt.
 *
 * Today's version has new code to deal with the activity lifecycle, specifically methods like
 * onPause, onResume, onSaveInstanceState, and onRestoreInstanceState.
 */

package edu.stanford.cs193a.dictionaryawesome;

import android.content.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.*;
import stanford.androidlib.*;

public class DictionaryActivity extends SimpleActivity {
    private Map<String, String> dictionary;   // a dictionary of {word -> definition} pairs for lookup
    private MediaPlayer mp;                   // for playing music
    private int points;                       // current # of points earned
    private int highScore;                    // most points ever earned

    /*
     * This method runs when the app is first loading up.
     * It sets up the dictionary of words and definitions.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        setTraceLifecycle(true);
        points = 0;

        // read file data and choose initial words/definitions
        dictionary = new HashMap<>();
        readFileData();
        chooseWords();

        // set up event listener to run when the user taps items in the list
        $LV(R.id.word_list).setOnItemClickListener(this);

        // load the high score
        SharedPreferences prefs = getSharedPreferences("myprefs", MODE_PRIVATE);
        highScore = prefs.getInt("highScore", /* default */ 0);

        mp = MediaPlayer.create(this, R.raw.jeopardy);
        mp.start();
    }

    /*
     * Called when the activity is stopped or the app jumps to another activity.
     * We pause the media player here to stop the music from playing.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            mp.pause();
        }
    }

    /*
     * Called when the app returns from jumping to another activity.
     * We resume the media player here to start the music playing again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null) {
            mp.start();
        }
    }

    /*
     * Called on actions like rotation that might lose the activity's instance state.
     * We save the total points earned so that they are not forgotten.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("points", points);
        outState.putString("the_word", $TV(R.id.the_word).getText().toString());
    }

    /*
     * Called on actions like rotation that might lose the activity's instance state.
     * We restore the total points earned so that they are not forgotten.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        points = savedInstanceState.getInt("points", /* default */ 0);
    }

    /*
     * This method is called when items in the ListView are clicked.
     * We check whether the user clicked on the right definition and then pick a new random word
     * and 5 new random definitions.
     * Note: This version of onItemClick (with just two parameters) is provided by the SimpleActivity
     * from the Stanford library.  It wouldn't work if we didn't extend SimpleActivity up above.
     */
    @Override
    public void onItemClick(ListView list, int index) {
        String defnClicked = list.getItemAtPosition(index).toString();
        String theWord = $TV(R.id.the_word).getText().toString();
        String correctDefn = dictionary.get(theWord);
        if (defnClicked.equals(correctDefn)) {
            points++;
            if (points > highScore) {
                highScore = points;

                SharedPreferences prefs = getSharedPreferences(
                                  "myprefs", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putInt("highScore", highScore);
                prefsEditor.apply();

            }
            toast("COOL! Score = " + points + ", hi=" + highScore);
        } else {
            points--;
            toast(":-( LOL. Score = " + points + ", hi=" + highScore);
        }
        chooseWords();
    }

    /*
     * Reads data from two files: grewords.txt (built into the app),
     * and added_words.txt (extra words added by the user).
     */
    private void readFileData() {
        // read from grewords.txt
        Scanner scan = new Scanner(
                getResources().openRawResource(R.raw.grewords));
        readFileHelper(scan);

        // read from added_words.txt (try/catch in case file is not found)
        try {
            Scanner scan2 = new Scanner(openFileInput("added_words.txt"));
            readFileHelper(scan2);
        } catch (Exception e) {
            // do nothing
        }
    }

    /*
     * Reads all words from the given file scanner.
     * Each word is on a line of the form, "word \t definition."
     */
    private void readFileHelper(Scanner scan) {
        while (scan.hasNextLine()) {
            // "abate	to lessen to subside"
            String line = scan.nextLine();
            String[] parts = line.split("\t");
            if (parts.length < 2) continue;
            dictionary.put(parts[0], parts[1]);
        }
    }

    /*
     * pick the word,
     * pick ~5 random defns for that word (1 of which is correct)
     * show everything on screen
     */
    private void chooseWords() {
        // choose the correct word
        List<String> words = new ArrayList<>(dictionary.keySet());
        Random randy = new Random();
        int randomIndex = randy.nextInt(words.size());
        String theWord = words.get(randomIndex);
        String theDefn = dictionary.get(theWord);

        // pick 4 other (wrong) definitions at random
        List<String> defns = new ArrayList<>(dictionary.values());
        defns.remove(theDefn);
        Collections.shuffle(defns);
        defns = defns.subList(0, 4);
        defns.add(theDefn);
        Collections.shuffle(defns);

        // display everything on screen
        $TV(R.id.the_word).setText(theWord);
        SimpleList.with(this).setItems(R.id.word_list, defns);
    }

    /*
     * Called when the Add A Word button is clicked.
     * Jumps to the AddWordActivity to add a new word.
     */
    public void addAWordClick(View view) {
        // go to the add word activity
        Intent intent = new Intent(this, AddWordActivity.class);
        startActivity(intent);
    }
}
