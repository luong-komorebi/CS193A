/*
   CS 193A, Marty Stepp
   This class makes it easier to detect mouse/finger swipe motions on a view.
   An OnSwipeListener has several configuration 'set' methods that all return
   the listener itself so that they can be chained together.

   Example usage (in your activity class, perhaps in onCreate method):

       protected void onCreate(Bundle savedInstanceState) {
           ...
           TextView view = (TextView) findViewById(R.id.someID);
           view.setOnTouchListener(
               new OnSwipeListener(this) {
                    {
                        setDragHorizontal(true);
                        setExitScreenOnSwipe(true);
                        setAnimationDelay(1000);
                    }

                    public void onSwipeLeft(float distance) {
                        // ...
                    }

                    public void onSwipeRight(float distance) {
                        // ...
                    }
               }
           );
       }

   This code is based on an example found at the following URL:
   http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
*/

package com.example.stepp.swipedemo;

import android.content.*;
import android.content.res.*;
import android.view.*;

public class OnSwipeListener implements View.OnTouchListener {
    private boolean dragHorizontal = false;
    private boolean dragVertical = false;
    private boolean dragSnapBack = false;
    private boolean animated = true;
    private boolean exitScreenOnSwipe = false;
    private long animationDelay = 500;
    private float dragSnapThreshold = 50;
    private float swipeDistanceThreshold = 100;
    private float swipeVelocityThreshold = 100;
    private float dragPrevX;
    private float dragPrevY;
    private GestureDetector gestureDetector = null;
    private Impl swiper = null;
    private View draggedView = null;

    /**
     * Constructs a new listener for the given context (activity or fragment).
     */
    public OnSwipeListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
        if (context instanceof Impl) {
            swiper = (Impl) context;
        }
    }

    /**
     * You can override this method if you want to subclass OnSwipeListener.
     * Called when the user swipes the view to the left.
     */
    public void onSwipeLeft(float distance) {
        if (swiper != null) {
            swiper.onSwipeLeft(distance);
        }
    }

    /**
     * You can override this method if you want to subclass OnSwipeListener.
     * Called when the user swipes the view to the right.
     */
    public void onSwipeRight(float distance) {
        if (swiper != null) {
            swiper.onSwipeRight(distance);
        }
    }

    /**
     * You can override this method if you want to subclass OnSwipeListener.
     * Called when the user swipes the view upward.
     */
    public void onSwipeUp(float distance) {
        if (swiper != null) {
            swiper.onSwipeUp(distance);
        }
    }

    /**
     * You can override this method if you want to subclass OnSwipeListener.
     * Called when the user swipes the view downward.
     */
    public void onSwipeDown(float distance) {
        if (swiper != null) {
            swiper.onSwipeDown(distance);
        }
    }

    /**
     * Internal method used to implement mouse touch events.
     * Not to be called directly by clients.
     */
    @Override
    public final boolean onTouch(View view, MotionEvent event) {
        if (view != null) {
            draggedView = view;
        }

        boolean gesture = gestureDetector.onTouchEvent(event);

        int action = event.getAction();
        if (dragHorizontal || dragVertical) {
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                // initialViewX = view.getX();
                // initialViewY = view.getY();
            } else if (action == MotionEvent.ACTION_MOVE) {
                float dragCurrX = event.getRawX();
                float dragCurrY = event.getRawY();
                if (dragHorizontal) {
                    view.setTranslationX(view.getTranslationX() + dragCurrX - dragPrevX);
                }
                if (dragVertical) {
                    view.setTranslationY(view.getTranslationY() + dragCurrY - dragPrevY);
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_POINTER_UP) {
                if (dragSnapBack) {
                    float dx = event.getRawX() - dragPrevX;
                    float dy = event.getRawY() - dragPrevY;

                    boolean shouldDoX = Math.abs(dx) <= dragSnapThreshold || dragSnapThreshold <= 0;
                    boolean shouldDoY = Math.abs(dy) <= dragSnapThreshold || dragSnapThreshold <= 0;

                    if (animated) {
                        ViewPropertyAnimator anim = view.animate();
                        if (shouldDoX) {
                            anim.translationX(0);
                        }
                        if (shouldDoY) {
                            anim.translationY(0);
                        }
                        anim.setDuration(animationDelay);
                        anim.start();
                    } else {
                        if (shouldDoX) {
                            view.setTranslationX(0);
                        }
                        if (shouldDoY) {
                            view.setTranslationY(0);
                        }
                    }
                }
            }
            dragPrevX = event.getRawX();
            dragPrevY = event.getRawY();
        }

        return gesture;
    }

    /**
     * Sets the number of pixels before the listener considers the user to have swiped.
     */
    public OnSwipeListener setDistanceThreshold(float px) {
        swipeDistanceThreshold = px;
        return this;
    }

    /**
     * Sets the rate of finger speed before the listener considers the user to have swiped.
     */
    public OnSwipeListener setVelocityThreshold(float px) {
        swipeVelocityThreshold = px;
        return this;
    }

    /**
     * Sets the number of pixels in which the view will "snap back" if dragged.
     */
    public OnSwipeListener setDragSnapThreshold(float px) {
        dragSnapThreshold = px;
        if (dragSnapThreshold > 0) {
            setDragSnapBack(true);
        }
        return this;
    }

    /**
     * Sets the number of milliseconds long that each drag/snap animation will take.
     */
    public OnSwipeListener setAnimationDelay(long ms) {
        animationDelay = ms;
        setAnimated(animationDelay > 0);
        return this;
    }

    /**
     * Sets whether the view should slide itself off the screen once it has been swiped.
     */
    public OnSwipeListener setExitScreenOnSwipe(boolean exit) {
        exitScreenOnSwipe = exit;
        return this;
    }

    /**
     * Sets whether the view should track the user's finger as it is dragged horizontally.
     */
    public OnSwipeListener setDragHorizontal(boolean drag) {
        dragHorizontal = drag;
        return this;
    }

    /**
     * Sets whether the view should track the user's finger as it is dragged vertically.
     */
    public OnSwipeListener setDragVertical(boolean drag) {
        dragVertical = drag;
        return this;
    }

    /**
     * Sets whether the view should snap back into position when the user stops dragging it.
     */
    public OnSwipeListener setDragSnapBack(boolean snap) {
        dragSnapBack = snap;
        return this;
    }

    /**
     * Sets whether the view should animate itself when it snaps back or slides off screen.
     */
    public OnSwipeListener setAnimated(boolean anim) {
        animated = anim;
        return this;
    }

    /*
     * Internal class to implement finger gesture tracking.
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
            float dx = e2.getRawX() - e1.getRawX();
            float dy = e2.getRawY() - e1.getRawY();

            Configuration config = draggedView.getContext().getApplicationContext().getResources().getConfiguration();
            int screenWidth = config.screenWidthDp;
            int screenHeight = config.screenHeightDp;

            if (Math.abs(dx) > Math.abs(dy)
                    && Math.abs(dx) > swipeDistanceThreshold
                    && Math.abs(vx) > swipeVelocityThreshold) {
                if (dx > 0) {
                    onSwipeRight(dx);
                    dragEdgeHelper(screenWidth * 2, true, 0, false);
                } else {
                    onSwipeLeft(-dx);
                    dragEdgeHelper(-screenWidth, true, 0, false);
                }
                return true;
            } else if (Math.abs(dy) > Math.abs(dx)
                    && Math.abs(dy) > swipeDistanceThreshold
                    && Math.abs(vy) > swipeVelocityThreshold) {
                if (dy > 0) {
                    onSwipeDown(dy);
                    dragEdgeHelper(0, false, screenHeight * 2, true);
                } else {
                    onSwipeUp(-dy);
                    dragEdgeHelper(0, false, -screenHeight, true);
                }
                return true;
            }

            return false;
        }

        private void dragEdgeHelper(float tx, boolean useTX, float ty, boolean useTY) {
            if (exitScreenOnSwipe && draggedView != null) {
                if (animated) {
                    ViewPropertyAnimator anim = draggedView.animate()
                            .setDuration(animationDelay);
                    if (useTX) {
                        anim.translationX(tx);
                    }
                    if (useTY) {
                        anim.translationY(ty);
                    }
                    anim.start();
                } else {
                    draggedView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * Interface that can be implemented by your activity/fragment to make it
     * possible to put the onSwipe___ methods directly in your activity class.
     */
    public static interface Impl {
        void onSwipeLeft(float distance);
        void onSwipeRight(float distance);
        void onSwipeUp(float distance);
        void onSwipeDown(float distance);
    }
}
