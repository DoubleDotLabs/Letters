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

    public double getCanvasWidth() {
        return canvasWidth;
    }

    public double getCanvasHeight() {
        return canvasHeight;
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
        paint.setTextSize(80);

        for (Letter letter : letters) {
            if (letter.found) continue;

            double offsetX = (1 - (letter.x + x)) * canvasWidth * Math.PI;
            double offsetY = (letter.y + z) * canvasHeight * Math.PI;

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
