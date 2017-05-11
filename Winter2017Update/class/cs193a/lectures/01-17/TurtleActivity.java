/*
 * CS 193A, Winter 2017, Marty Stepp
 * This program displays pictures of the Ninja Turtles and lets the user choose
 * which turtle to show by clicking radio buttons.
 */

package edu.stanford.cs193a.turtleapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import stanford.androidlib.SimpleActivity;

public class TurtleActivity extends SimpleActivity {

    /*
     * Called when the app is first loading up.
     * This is default IDE-generated code.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turtle);
    }

    /*
     * Called when the various radio buttons are clicked.
     * Changes which turtle image is showing in the ImageView on screen.
     */
    public void radioClick(View view) {
        ImageView img = $(R.id.turtle_image);

        // choose image to show based on ID of radio button clicked
        int id = view.getId();
        if (id == R.id.leo_button) {
            img.setImageResource(R.drawable.tmntleo);
        } else if (id == R.id.mike_button) {
            img.setImageResource(R.drawable.tmntmike);
        } else if (id == R.id.don_button) {
            img.setImageResource(R.drawable.tmntdon);
        } else if (id == R.id.raph_button) {
            img.setImageResource(R.drawable.tmntraph);
        }
    }
}






