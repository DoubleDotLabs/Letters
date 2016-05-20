package com.doubledotlabs.letters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class LetterView extends AppCompatImageView {

    private double x, y, z;
    private int canvasWidth, canvasHeight;
    private ArrayList<Letter> letters;

    public LetterView(Context context) {
        super(context);
    }

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLetters(ArrayList<Letter> letters) {
        this.letters = letters;
    }

    public void update(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX(), y = event.getRawY();

        for (Letter letter : letters) {
            double thisX = (1 - (letter.x + x)) * canvasWidth * 2, thisY = (letter.z + z) * canvasHeight;

            if (Math.abs(x - thisX) < 5) {
                if (Math.abs(y - thisY) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                } else if (Math.abs(y - thisY + canvasHeight) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                } else if (Math.abs(y - thisY - canvasHeight) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                }
            } else if (Math.abs(x - thisX + canvasWidth) < 5) {
                if (Math.abs(y - thisY) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                } else if (Math.abs(y - thisY + canvasHeight) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                } else if (Math.abs(y - thisY - canvasHeight) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                }
            } else if (Math.abs(x - thisX - canvasWidth) < 5) {
                if (Math.abs(y - thisY) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                } else if (Math.abs(y - thisY + canvasHeight) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                } else if (Math.abs(y - thisY - canvasHeight) < 5) {
                    letter.found = true;
                    Toast.makeText(getContext(), letter.letter + " clicked", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (letters != null) drawLetters(canvas);
    }

    private void drawLetters(Canvas canvas) {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(40);

        for (Letter letter : letters) {
            double offsetX = (1 - (letter.x + x)) * canvasWidth * 2;
            double offsetY = (letter.z + z) * canvasHeight * 2;

            canvas.drawText(letter.letter, (int) offsetX, (int) offsetY, paint);
            canvas.drawText(letter.letter, (int) offsetX - canvasWidth, (int) offsetY, paint);
            canvas.drawText(letter.letter, (int) offsetX + canvasWidth, (int) offsetY, paint);
            canvas.drawText(letter.letter, (int) offsetX, (int) offsetY - canvasHeight, paint);
            canvas.drawText(letter.letter, (int) offsetX, (int) offsetY + canvasHeight, paint);
            canvas.drawText(letter.letter, (int) offsetX - canvasWidth, (int) offsetY - canvasHeight, paint);
            canvas.drawText(letter.letter, (int) offsetX + canvasWidth, (int) offsetY - canvasHeight, paint);
            canvas.drawText(letter.letter, (int) offsetX - canvasWidth, (int) offsetY + canvasHeight, paint);
            canvas.drawText(letter.letter, (int) offsetX + canvasWidth, (int) offsetY + canvasHeight, paint);
        }

        canvas.rotate((float) y * 3600, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }
}
