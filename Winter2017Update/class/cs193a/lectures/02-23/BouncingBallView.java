/*
 * CS 193A, Marty Stepp
 * This class is a graphical view of a simple animated app
 * with a ball that moves around and bounces off the edges of the screen.
 * This file DOES use the Stanford Android library, unlike SmileyView.
 */

package cs193a.stanford.edu.bouncingball;

import android.content.Context;
import android.util.AttributeSet;
import stanford.androidlib.graphics.*;

public class BouncingBallView extends GCanvas {
    private static final float BALL_SIZE = 100;
    private static final float BALL_MAX_VELOCITY = 50;

    private GOval oval;

    /** Empty required constructor. */
    public BouncingBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * Sets up the initial state of the view and sprites.
     */
    public void init() {
        oval = new GOval(50, 80, 200, 300);
        oval.setFillColor(GColor.PURPLE);
        add(oval);
        animate(30);
    }

    /*
     * This method is called by the animation resources of the GCanvas 30 times per second.
     * In this method you can update the positions of various shapes
     * so that they will seem to have moved the next time the screen is redrawn.
     */
    @Override
    public void onAnimateTick() {
        super.onAnimateTick();

        oval.moveBy(5, 0);
    }
}
