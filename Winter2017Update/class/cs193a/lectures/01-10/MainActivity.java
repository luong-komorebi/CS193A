/*
 * CS 193A, Winter 2017, Marty Stepp
 *
 * This app is a very simple first demo of Android programming.
 * It shows two numbers and asks the user to click on the larger one.
 * If the user clicks the correct number, he/she earns a point.
 * This is a demonstration of basic widgets and events.
 */

package edu.stanford.cs193a.berkeleysucksapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // fields - live throughout the lifetime of the activity
    private int points;

    // Called when the app loads up.
    // Sets up initial points and random numbers on the buttons.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        points = 0;
        pickRandomNumbers();
    }

    // Chooses two random numbers from 0-9 and places them on the left/right buttons.
    private void pickRandomNumbers() {
        // pick two unique random numbers
        Random randy = new Random();
        int rand1 = randy.nextInt(10);
        int rand2 = 0;
        while (true) {
            rand2 = randy.nextInt(10);
            if (rand2 != rand1) break;
        }

        Button lbutt = (Button) findViewById(R.id.left_button);
        lbutt.setText(String.valueOf(rand1));
        Button rbutt = (Button) findViewById(R.id.right_button);
        rbutt.setText(String.valueOf(rand2));
    }

    // Called when the left number button is clicked.
    public void leftButtonClick(View view) {
        buttonClickHelper(/* isLeft */ true);
    }

    // Called when the right number button is clicked.
    public void rightButtonClick(View view) {
        buttonClickHelper(/* isLeft */ false);
    }

    // A helper to handle a click on the left or right button, since their behavior is similar.
    // Checks whether the user clicked the larger number and awards points accordingly.
    private void buttonClickHelper(boolean isLeft) {
        Button lbutt = (Button) findViewById(R.id.left_button);
        Button rbutt = (Button) findViewById(R.id.right_button);
        int rand1 = Integer.parseInt(lbutt.getText().toString());  // "3" -> 3
        int rand2 = Integer.parseInt(rbutt.getText().toString());

        if ((isLeft && rand1 >= rand2) || (!isLeft && rand2 >= rand1)) {
            // correct
            points++;
            Toast.makeText(this, "Great job!", Toast.LENGTH_SHORT).show();
        } else {
            // incorrect
            points--;
            Toast.makeText(this, "You SUCK.", Toast.LENGTH_SHORT).show();
        }

        // update display of points
        TextView tv = (TextView) findViewById(R.id.points_field);
        tv.setText("Points: " + points);

        // choose two new random numbers for next round
        pickRandomNumbers();
    }
}
