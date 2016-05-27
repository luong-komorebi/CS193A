/*
 * CS 193A, Winter 2015, Marty Stepp
 * This app is a continuation of our TMNT app from previous weeks.
 * This file represents the Java code for the second activity.
 * Today's version adds a dictionary for looking up words.
 */

package com.example.stepp.layoutfun;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class DetailsActivity extends ActionBarActivity {
    private Set<String> dictionary;   // dictionary of words to look up

    /*
     * Constant array of data about each of the four turtles.
     * (This is not the most idiomatic way to store such information,
     * but we'll come back to it later.)
     */
    private static final String[] TURTLE_DETAILS = {
            "Leonardo, or Leo, is one of the four protagonists of the Teenage Mutant Ninja Turtles comics and all related media. In the Mirage/Image Comics, all four turtles wear red bandanas, but in other versions, he wears a blue bandana. His signature weapons are two ninjato. Throughout the various media, he is often depicted as the eldest and leader of the four turtles, as well as the most disciplined. He is named after Leonardo da Vinci. In the 2012 series, he is the only turtle who harbors strong romantic affections for Karai, considering her his love interest.",
            "Michelangelo, Mike or Mikey (as he is usually called), is a fictional character and one of the four protagonists of the Teenage Mutant Ninja Turtles comics and all related media. His mask is typically portrayed as orange outside of the Mirage/Image Comics and his weapons are dual nunchucks, though he has also been portrayed using other weapons, such as a grappling hook, manriki-gusari, tonfa, and a three section staff (in some action figures).",
            "Donatello, often shortened to Don, Donny or Donnie, is a fictional character and one of the four protagonists of the Teenage Mutant Ninja Turtles comics and all related media. He is co-creator Peter Laird's favorite Turtle. In the Mirage/Image Comics, all four turtles wear red bandanas, but in other versions he wears a purple bandana. His primary signature weapon is his effective bō staff. In all media, he is depicted as the smartest and second-in-command of the four turtles. Donnie often speaks in technobabble with a natural aptitude for science and technology. He is named after the Italian sculptor Donatello.",
            "Raphael, or Raph, is a fictional character and one of the four protagonists of the Teenage Mutant Ninja Turtles comics and all related media. In the Mirage/Image Comics, all four turtles wear red bandanas over their eyes, but unlike his brothers in other versions, he is the only one who keeps the red bandana. Raphael wields twin sai as his primary weapon. (In the Next Mutation series, his sai stick together to make a staff-like weapon.) He is generally the most likely to experience extremes of emotion, and is usually depicted as being aggressive, sullen, maddened, and rebellious. The origin of Raphael's anger is not always fully explored, but in some incarnations appears to stem partly from the realization that they are the only creatures of their kind and ultimately alone. He also has a somewhat turbulent relationship with his older brother Leonardo because Leonardo is seen as the group's leader. Raphael is named after the 16th-century Italian painter Raphael. In 2011 Raphael placed 23rd on IGN's Top 100 Comic Book Heroes, a list that did not feature any of his brothers."
    };

    /*
     * Called when the activity first gets created.
     * Shows detail text about the selected ninja turtle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // pull the turtle's ID out of the intent that the MainActivity used to load me
        Intent intent = getIntent();
        int id = intent.getIntExtra("turtle_id", R.id.leo);
        String text = "";
        if (id == R.id.leo) {
            text = TURTLE_DETAILS[0];
        } else if (id == R.id.mike) {
            text = TURTLE_DETAILS[1];
        } else if (id == R.id.don) {
            text = TURTLE_DETAILS[2];
        } else { // if (id == R.id.raph)
            text = TURTLE_DETAILS[3];
        }
        TextView tv = (TextView) findViewById(R.id.turtle_info);
        tv.setText(text);

        if (dictionary == null) {
            loadDictionary();
        }
    }

    /*
     * Called when the user presses the Submit button.
     * Checks whether the user has typed a legal dictionary word in the text box,
     * and if so, closes this activity and returns to the MainActivity.
     */
    public void onclickSubmit(View view) {
        // if user has typed a valid dictionary word, accept it and send it back to main activity
        EditText edit = (EditText) findViewById(R.id.the_word);
        String text = edit.getText().toString().trim().toLowerCase();
        if (dictionary.contains(text)) {
            Intent intent = new Intent();
            intent.putExtra("the_word", text);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Not in dictionary, brah", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Loads the dictionary words from the provided text file, dict.txt.
     */
    private void loadDictionary() {
        dictionary = new HashSet<String>();
        Scanner scan = new Scanner(getResources().openRawResource(R.raw.dict));
        while (scan.hasNextLine()) {
            String word = scan.nextLine();
            dictionary.add(word);
        }
    }

    // AUTO-GENERATED CODE

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
}
