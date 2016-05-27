/*
 * CS 193A, Winter 2015, Marty Stepp
 * This class is a helper to wrap up some of the icky code needed to
 * initiate an animation thread that repaints a view at regular intervals.
 */

package com.example.stepp.bouncingball;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * This class is a helper to wrap up some of the icky code needed to
 * initiate an animation thread that repaints a view at regular intervals.
 */
public class DrawingThread {
    private View view = null;
    private int fps;
    private Thread thread = null;
    private Handler handler = null;
    private volatile boolean isRunning = false;

    /**
     * Constructs a new DrawingThread to update the given view
     * the given number of times per second.
     * Does NOT start the thread running; call start() to do so.
     */
    public DrawingThread(View view, int fps) {
        if (view == null || fps <= 0) {
            throw new IllegalArgumentException();
        }
        this.view = view;
        this.fps = fps;
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Returns true if the drawing thread is currently started and running.
     */
    public boolean isRunning() {
        return thread != null;
    }

    /**
     * Starts the thread running so that it will repaint the view repeatedly.
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(new MainRunner());
            thread.start();
        }
    }

    /**
     * Stops the thread so that it will not repaint the view any more.
     */
    public void stop() {
        if (thread != null) {
            isRunning = false;
            try {
                thread.join();
            } catch (InterruptedException ie) {
                // empty
            }
            thread = null;
        }
    }

    /*
     * Small runnable helper class that contains the thread's main loop
     * to repeatedly sleep-and-redraw the view.
     */
    private class MainRunner implements Runnable {
        public void run() {
            isRunning = true;
            while (isRunning) {
                // sleep for a short time between frames of animation
                try {
                    Thread.sleep(1000 / fps);
                } catch (InterruptedException ie) {
                    isRunning = false;
                }

                // post a message that will cause the view to redraw
                handler.post(new Updater());
            }
        }
    }

    /*
     * Small runnable helper class needed by Android to redraw a view.
     */
    private class Updater implements Runnable {
        public void run() {
            view.invalidate();
        }
    }
}
