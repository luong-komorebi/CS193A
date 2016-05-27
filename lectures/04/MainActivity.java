/*
 * CS 193A, Marty Stepp
 * This app is a continuation of our TMNT app from previous weeks.
 * This file represents the Java code for the main activity.
 * Today's version adds onSaveInstanceState to make sure that the turtle is
 * not forgotten when the user leaves and returns to the activity.
 */

package com.example.stepp.layoutfun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends Activity {
    // these "request codes" are used to identify sub-activities that return results
    private static final int REQUEST_CODE_DETAILS_ACTIVITY = 1234;
    private static final int REQUEST_CODE_TAKE_PHOTO = 4321;

    private MediaPlayer player;   // media player for playing TMNT music

    /*
     * Called when the activity first gets created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = MediaPlayer.create(this, R.raw.tmnt_theme);
        Log.d("testing", "onCreate got called; Bundle=" + savedInstanceState);
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
     * Called when the activity is stopped and wants to save its state.
     * We save which turtle is selected and showing so that the app stays on that turtle
     * when the user comes back later.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("testing", "onSaveInstanceState got called");
        RadioGroup group = (RadioGroup) findViewById(R.id.turtle_group);
        int id = group.getCheckedRadioButtonId();
        outState.putInt("id", id);
    }

    /*
     * Called when the activity returns to an active running state and wants to restore its state.
     * We restore which turtle was previously selected.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("testing", "onRestoreInstanceState got called");
        int id = savedInstanceState.getInt("id");
        RadioGroup group = (RadioGroup) findViewById(R.id.turtle_group);
        group.check(id);
        updateTurtleImage();
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
        startActivityForResult(intent, REQUEST_CODE_DETAILS_ACTIVITY);
    }

    /*
     * Event handler that is called when a sub-activity returns.
     * We can extract the data that came back from the other activity
     * (packed into the Intent that it sends back) and use it here.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_DETAILS_ACTIVITY &&
                resultCode == RESULT_OK) {
            // returned from DetailsActivity; user sent us a dictionary word, so show it as a Toast
            String word = intent.getStringExtra("the_word");
            Toast.makeText(this, "You typed: " + word, Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO
                && resultCode == RESULT_OK) {
            // returned from taking a photo with the camera; grab the photo and show it
            Bitmap bmp = (Bitmap) intent.getExtras().get("data");
            ImageButton img = (ImageButton) findViewById(R.id.turtle);
            img.setImageBitmap(bmp);
        }
    }

    /*
     * This method is called when the user chooses one of the turtle radio buttons.
     * In this code we set which turtle image is visible on the screen in the ImageView.
     */
    public void pickTurtle(View view) {
        updateTurtleImage();
    }

    /*
     * Called when the "Take Photo" button is clicked.
     * Launches a new activity to take a photo using the camera.
     */
    public void onClickTakePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    /*
     * Called by various event handlers to update which turtle image is showing
     * based on which radio button is currently checked.
     */
    private void updateTurtleImage() {
        ImageButton img = (ImageButton) findViewById(R.id.turtle);
        RadioGroup group = (RadioGroup) findViewById(R.id.turtle_group);
        int checkedID = group.getCheckedRadioButtonId();
        if (checkedID == R.id.leo) {
            img.setImageResource(R.drawable.tmntleo);
        } else if (checkedID == R.id.mike) {
            img.setImageResource(R.drawable.tmntmike);
        } else if (checkedID == R.id.don) {
            img.setImageResource(R.drawable.tmntdon);
        } else if (checkedID == R.id.raph) {
            img.setImageResource(R.drawable.tmntraph);
        }
    }
}








