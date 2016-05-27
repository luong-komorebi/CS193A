/*
 * CS 193A, Winter 2015, Marty Stepp
 * This class is a helper that can be used to represent sprites,
 * which are in-game objects that can move around.
 * This class is fairly simple and is just meant to gather together
 * several variables that would be associated with in-game sprite objects
 * such as x/y location, width/height size, dx/dy velocity, and Paint color.
 */

package com.example.stepp.bouncingball;

import android.graphics.*;

public class Sprite {
    // state (fields)
    public RectF rect = new RectF();
    public float dx = 0;
    public float dy = 0;
    public Paint paint = new Paint();

    /* Constructs a default empty sprite. */
    public Sprite() {
        // empty
    }

    /* Constructs a sprite of the given location and size. */
    public Sprite(float x, float y, float width, float height) {
        setLocation(x, y);
        setSize(width, height);
    }

    /* Tells the sprite to move itself by its current velocity dx,dy. */
    public void move() {
        rect.offset(dx, dy);
    }

    /* Stops the sprite from moving by setting its velocity to 0,0. */
    public void stopMoving() {
        setVelocity(0, 0);
    }

    /* Sets the sprite's x,y location on screen to be the given values. */
    public void setLocation(float x, float y) {
        rect.offsetTo(x, y);
    }

    /* Sets the sprite's size to be the given values. */
    public void setSize(float width, float height) {
        rect.right = rect.left + width;
        rect.bottom = rect.top + height;
    }

    /* Sets the sprites dx,dy velocity to be the given values. */
    public void setVelocity(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
