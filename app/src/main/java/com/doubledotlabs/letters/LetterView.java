package com.doubledotlabs.letters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.ArrayList;

public class LetterView extends AppCompatImageView {

    private double x, y, z;
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
    public void onDraw(Canvas canvas) {
        if (letters != null) drawLetters(canvas);
    }

    private void drawLetters(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(40);

        for (Letter letter : letters) {
            double offsetX = (1 - (letter.x + x)) * canvas.getWidth();
            double offsetY = (letter.z + z) * canvas.getHeight();

            canvas.drawText(letter.letter, (int) offsetX, (int) offsetY, paint);
        }

        for (Letter letter : letters) {
            double offsetX = (1 - (letter.x + x)) * canvas.getWidth();
            double offsetY = (letter.z + z) * canvas.getHeight();

            offsetX -= canvas.getWidth();
            offsetY -= canvas.getHeight();


            canvas.drawText(letter.letter, (int) offsetX, (int) offsetY, paint);
        }

        for (Letter letter : letters) {
            double offsetX = (1 - (letter.x + x)) * canvas.getWidth();
            double offsetY = (letter.z + z) * canvas.getHeight();

            offsetX += canvas.getWidth();
            offsetX += canvas.getHeight();

            canvas.drawText(letter.letter, (int) offsetX, (int) offsetY, paint);
        }

        canvas.rotate((float) y * 3600, canvas.getWidth() / 2, canvas.getHeight() / 2);
    }
}
