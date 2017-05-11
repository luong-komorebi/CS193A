/*
 * CS 193A, Marty Stepp
 * This view draws the main graphics of the Lunar Lander game.
 * Today's version adds some new features (asteroids, etc.)
 * as well as Localization features to work in multiple languages.
 */

package cs193a.stanford.edu.lunarlander;

import android.content.Context;
import android.graphics.*;
import android.util.*;
import android.view.*;
import java.util.*;
import stanford.androidlib.*;
import stanford.androidlib.graphics.*;
import stanford.androidlib.util.*;

public class LanderCanvas extends GCanvas {
    // frames per second of animation
    private static final int FRAMES_PER_SECOND = 30;

    // maximum velocity at which the rocket can hit the ground without crashing
    private static final float MAX_SAFE_LANDING_VELOCITY = 7.0f;

    private static final float ASTEROID_VELOCITY = -12.0f;

    // downward acceleration due to gravity
    private static final float GRAVITY_ACCELERATION = .5f;

    // upward acceleration due to thrusters
    private static final float THRUST_ACCELERATION = -.3f;

    // private fields
    private GSprite rocket;
    private GSprite moonSurface;
    private Bitmap rocketNonThrust;
    private ArrayList<Bitmap> rocketThrust;
    private boolean isThrusting = false;

    /*
     * Required auto-generated constructor.
     */
    public LanderCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * Called when the GCanvas is being created.
     * Sets up the sprite objects and adds them to the screen.
     */
    @Override
    public void init() {
        setBackgroundColor(GColor.BLACK);

        GSprite.setDebug(true);

        moonSurface = new GSprite(SimpleBitmap.with(this)
                .scaleToFit(R.drawable.moonsurface, this));
        moonSurface.setBottomY(getHeight());
        moonSurface.setCollisionMarginTop(moonSurface.getHeight()/3);
        add(moonSurface);

        rocketThrust = new ArrayList<>();
        rocketThrust.add(SimpleBitmap.with(this)
                .scaleToHeight(R.drawable.rocketshipthrust1, getHeight()/6));
        rocketThrust.add(SimpleBitmap.with(this)
                .scaleToHeight(R.drawable.rocketshipthrust2, getHeight()/6));
        rocketThrust.add(SimpleBitmap.with(this)
                .scaleToHeight(R.drawable.rocketshipthrust3, getHeight()/6));
        rocketThrust.add(SimpleBitmap.with(this)
                .scaleToHeight(R.drawable.rocketshipthrust4, getHeight()/6));

        rocketNonThrust = SimpleBitmap.with(this)
                .scaleToHeight(R.drawable.rocketship1, getHeight()/6);
        rocket = new GSprite(rocketNonThrust);
        rocket.setCollisionMargin(rocket.getWidth() / 4);
        rocket.setVelocityY(2);
        rocket.setAccelerationY(GRAVITY_ACCELERATION);
        add(rocket);
    }

    /*
     * Called by the GCanvas internal animation loop each time the animation ticks,
     * 30 times per second.
     * Updates the sprites and checks for collisions.
     */
    @Override
    public void onAnimateTick() {
        super.onAnimateTick();

        if (rocket.collidesWith(moonSurface)) {
            rocket.stop();
        }
    }

    /*
     * Called when the user presses or releases their finger from the screen.
     * Causes the rocket to start or stop thrusting.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // code to run when finger is pressed; begin thrusting
            isThrusting = true;
            rocket.setAccelerationY(THRUST_ACCELERATION);
            rocket.setBitmaps(rocketThrust);
            rocket.setFramesPerBitmap(5);
            rocket.setLoopBitmaps(true);
        } else {
            // lifted finger up; stop thrusting
            isThrusting = false;
            rocket.setAccelerationY(GRAVITY_ACCELERATION);
            rocket.setBitmap(rocketNonThrust);
        }

        return super.onTouch(v, event);
    }
    /*
     * Called when user clicks Play Game button.
     * Starts a new game.
     */
    public void startGame() {
        animate(FRAMES_PER_SECOND);
    }

    /*
     * Called when user clicks Stop Game button.
     * Ends the game.
     */
    public void stopGame() {
        animationStop();
    }
}
