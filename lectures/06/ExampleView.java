/*
 * CS 193A, Winter 2015, Marty Stepp
 * This class is a graphical view of a basic example of 2D graphics.
 */

package com.example.stepp.targets;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

public class ExampleView extends View {
    public ExampleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * This method draws some shapes and text on the view.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawARGB(255, 255, 90, 90);

        Paint aqua = new Paint();
        aqua.setARGB(255, 0, 80, 220);

        canvas.drawRect(new RectF(10, 30, 300, 700), aqua);
        canvas.drawOval(new RectF(400, 50, getWidth(), getHeight()), aqua);

        Paint font = new Paint();
        font.setARGB(255, 0, 0, 0);
        font.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        font.setTextSize(40);

        canvas.drawText("CS 193A is great", 80, 200, font);
    }
}
