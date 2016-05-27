/*
 * CS 193, Winter 2015, Marty Stepp
 * This activity is a short demonstration of the provided OnSwipeListener library.
 * You can swipe a TextView left and right to trigger an event.
 */

package com.example.stepp.swipedemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class SwipeDemoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_demo);

        TextView textView = (TextView) findViewById(R.id.textview1);
        textView.setOnTouchListener(
                new OnSwipeListener(this) {
                    {
                        setDragHorizontal(true);
                        setExitScreenOnSwipe(true);
                        setAnimationDelay(1000);
                    }

                    @Override
                    public void onSwipeLeft(float distance) {
                        Toast.makeText(SwipeDemoActivity.this, "swiped left!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSwipeRight(float distance) {
                        Toast.makeText(SwipeDemoActivity.this, "swiped right!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void onClickButton1(View view) {
        Toast.makeText(this, "button 1 clicked", Toast.LENGTH_SHORT).show();
    }
}
