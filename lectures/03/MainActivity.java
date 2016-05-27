/*
 * CS 193A, Marty Stepp
 * This app is a continuation of our TMNT app from last week.
 * Today's version adds a second activity and launches that activity using an Intent.
 * This file represents the Java code for the main activity.
 * This activity class represents the event handling and state
 * of our ninja turtle app.
 */

package com.example.stepp.layoutfun;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity {
    private MediaPlayer player;

    /*
     * Called when the activity first gets created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = MediaPlayer.create(this, R.raw.tmnt_theme);
        Log.d("testing", "onCreate got called");
    }

    public void onPause() {
        super.onPause();
        player.stop();
        Log.d("testing", "onPause got called");
    }

    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setLooping(true);
            player.start();
        }
        Log.d("testing", "onResume got called");
    }

    public void onStart() {
        super.onStart();
        Log.d("testing", "onStart got called");
    }

    public void onStop() {
        super.onStop();
        Log.d("testing", "onStop got called");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d("testing", "onDestroy got called");
    }

    /*
     * Called when the Details activity finishes running and comes back to here.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /*
     * Called when the user clicks on the large TMNT image button.
     * Loads the DetailsActivity for more information about that turtle.
     */
    public void onClickTurtleImage(View view) {
        Intent intent = new Intent(this, DetailsActivity.class);

        RadioGroup group = (RadioGroup) findViewById(R.id.turtle_group);
        int id = group.getCheckedRadioButtonId();
        intent.putExtra("turtle_id", id);
        startActivity(intent);
    }

    /*
     * This method is called when the user chooses one of the turtle radio buttons.
     * In this code we set which turtle image is visible on the screen in the ImageView.
     */
    public void pickTurtle(View view) {
        ImageButton img = (ImageButton) findViewById(R.id.turtle);
        if (view.getId() == R.id.leo) {
            img.setImageResource(R.drawable.tmntleo);
        } else if (view.getId() == R.id.mike) {
            img.setImageResource(R.drawable.tmntmike);
        } else if (view.getId() == R.id.don) {
            img.setImageResource(R.drawable.tmntdon);
        } else if (view.getId() == R.id.raph) {
            img.setImageResource(R.drawable.tmntraph);
        }
    }
}
