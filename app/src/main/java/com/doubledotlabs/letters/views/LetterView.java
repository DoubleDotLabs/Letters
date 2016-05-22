package com.doubledotlabs.letters.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.doubledotlabs.letters.data.Letter;
import com.doubledotlabs.letters.R;

import java.util.ArrayList;

public class LetterView extends AppCompatImageView {

    private double x, y;
    private int canvasWidth, canvasHeight;
    private ArrayList<Letter> letters;
    private Paint paint;

    public LetterView(Context context) {
        super(context);

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.textColorSecondaryInverse));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
    }

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.textColorSecondaryInverse));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
    }

    public LetterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.textColorSecondaryInverse));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(80);
    }

    public void setLetters(ArrayList<Letter> letters) {
        this.letters = letters;
    }

    public double getCanvasWidth() {
        return canvasWidth;
    }

    public double getCanvasHeight() {
        return canvasHeight;
    }

    public void update(float x, float y) {
        this.x = x;
        this.y = y;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (letters != null) drawLetters(canvas);
    }

    private void drawLetters(Canvas canvas) {
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

        for (Letter letter : letters) {
            if (letter.found) continue;

            double offsetX = (1 - (letter.x + x)) * canvasWidth * Math.PI;
            double offsetY = (letter.y + y) * canvasHeight * Math.PI;

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
    }
}
