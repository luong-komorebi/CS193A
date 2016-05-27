/*
 * CS 193A, Winter 2015, Marty Stepp
 * This class is a graphical view of a drawing of a red/white target figure.
 */

package com.example.stepp.targets;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class TargetsView extends View {
    public TargetsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * This method draws the target oval shapes on the view.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint red = new Paint();
        red.setARGB(255, 255, 0, 0);

        Paint white = new Paint();
        white.setARGB(255, 255, 255, 255);

        int w = getWidth();
        int h = getHeight();

        for (int i = 0; i < 5; i++) {
            canvas.drawOval(new RectF(w*i/10, h*i/10, w*(10-i)/10, h*(10-i)/10), (i % 2 == 0 ? red : white));
        }
    }
}
