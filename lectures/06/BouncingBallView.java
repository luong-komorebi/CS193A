/*
 * CS 193A, Winter 2015, Marty Stepp
 * This class is a graphical view of a simple animated app
 * with a ball that moves around and bounces off the edges of the screen,
 * as well as a yellow "pac-man" sprite that can move around in response
 * to the user touching the screen at its various edges.
 */

package com.example.stepp.bouncingball;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BouncingBallView extends View {
    private static final float BALL_SIZE = 100;
    private static final float BALL_MAX_VELOCITY = 80;

    private Sprite ball;
    private Sprite pacman;
    private DrawingThread dthread;

    /*
     * This constructor sets up the initial state of the view and sprites.
     */
    public BouncingBallView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // set up initial state of ball
        ball = new Sprite();
        ball.setLocation(200, 200);
        ball.setSize(BALL_SIZE, BALL_SIZE);
        ball.setVelocity(
            (float) ((Math.random() - .5) * 2 * BALL_MAX_VELOCITY),
            (float) ((Math.random() - .5) * 2 * BALL_MAX_VELOCITY)
        );
        ball.paint.setARGB(255, 255, 0, 0);

        // set up initial state of pac-man
        pacman = new Sprite();
        pacman.setLocation(0, 0);
        pacman.setSize(80, 80);
        pacman.paint.setARGB(255, 200, 200, 0);

        // start a drawing thread to animate screen at 50 frames/sec
        dthread = new DrawingThread(this, 50);
        dthread.start();
    }

    /*
     * Called when the user touches the screen with their finger.
     * Used to control Pac-Man's movement.
     * If the user touches the edges of the screen, moves Pac-Man toward that edge.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int w = getWidth();
        int h = getHeight();

        if (x < w/5) {
            pacman.dx = -10;              // left edge of screen
        } else if (x >= w*4/5) {
            pacman.dx = 10;               // right edge
        } else {
            pacman.dx = 0;                // center
        }
        if (y < h/5) {
            pacman.dy = -10;              // top edge
        } else if (y >= h*4/5) {
            pacman.dy = 10;               // bottom edge
        } else {
            pacman.dy = 0;                // center
        }

        return super.onTouchEvent(event);
    }

    /*
     * This method draws the bouncing ball and Pac-Man on the screen,
     * and also updates their positions for the next time the screen
     * is redrawn.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(ball.rect, ball.paint);
        canvas.drawOval(pacman.rect, pacman.paint);
        updateSprites();
    }

    // updates sprites' positions between frames of animation
    private void updateSprites() {
        pacman.move();
        ball.move();

        // handle ball bouncing off edges
        if (ball.rect.left < 0 || ball.rect.right >= getWidth()) {
            ball.dx = -ball.dx;
        }
        if (ball.rect.top < 0 || ball.rect.bottom >= getHeight()) {
            ball.dy = -ball.dy;
        }
    }
}
