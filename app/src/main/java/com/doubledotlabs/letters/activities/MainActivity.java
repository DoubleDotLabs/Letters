package com.doubledotlabs.letters.activities;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.doubledotlabs.letters.data.Letter;
import com.doubledotlabs.letters.adapters.LettersAdapter;
import com.doubledotlabs.letters.R;
import com.doubledotlabs.letters.dialogs.FinishedDialog;
import com.doubledotlabs.letters.dialogs.TutorialDialog;
import com.doubledotlabs.letters.utils.Orientation;
import com.doubledotlabs.letters.views.ImagePreview;
import com.doubledotlabs.letters.views.LetterView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    AppCompatDialog dialog;

    ImagePreview preview;
    Camera camera;
    LetterView letterView;
    FrameLayout fl;

    RecyclerView recycler;
    AppCompatImageView circle;
    LettersAdapter adapter;

    Orientation orientation;

    ArrayList<Letter> letters;
    ArrayList<Letter> foundLetters;
    String word;

    float x, y;

    final int TOUCH_RANGE = 80;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = new ImagePreview(this, (SurfaceView) findViewById(R.id.surfaceView));
        letterView = (LetterView) findViewById(R.id.letterView);
        fl = (FrameLayout) findViewById(R.id.fl);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        circle = (AppCompatImageView) findViewById(R.id.circle);

        fl.addView(preview, 0);
        preview.setKeepScreenOn(true);

        orientation = new Orientation(this, new Orientation.OrientationListener() {
            @Override
            public void onOrientation(float x, float y) {
                letterView.update(x, y);
            }

            @Override
            public void onAccuracyChanged(int accuracy) {
            }
        });

        Random rand = new Random();
        word = getString(R.string.word);
        letters = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            letters.add(new Letter(String.valueOf(word.charAt(i)), rand.nextDouble(), rand.nextDouble()));
        }

        letterView.setLetters(letters);

        foundLetters = new ArrayList<>();

        letterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                circle.animate().x(event.getX()).y(event.getY()).setInterpolator(new DecelerateInterpolator()).setDuration(150).start();

                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    float x = event.getX(), y = event.getY();

                    for (Letter letter : letters) {
                        if (letter.found) continue;

                        double thisX = (1 - (letter.x + MainActivity.this.x)) * letterView.getCanvasWidth() * Math.PI;
                        double thisY = (letter.y + MainActivity.this.y) * letterView.getCanvasHeight() * Math.PI;

                        if (Math.abs(x - thisX) < TOUCH_RANGE || Math.abs(x - thisX + letterView.getCanvasWidth()) < TOUCH_RANGE || Math.abs(x - thisX - letterView.getCanvasWidth()) < TOUCH_RANGE) {
                            if (Math.abs(y - thisY) < TOUCH_RANGE || Math.abs(y - thisY + letterView.getCanvasHeight()) < TOUCH_RANGE || Math.abs(y - thisY - letterView.getCanvasHeight()) < TOUCH_RANGE) {
                                letter.found = true;
                                foundLetters.add(letter);
                                adapter.notifyItemInserted(foundLetters.size() - 1);
                                onLetterChanged();
                            }
                        }
                    }
                }
                return true;
            }
        });

        recycler.setLayoutManager(new GridLayoutManager(this, 1));

        adapter = new LettersAdapter(this, foundLetters);
        recycler.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(foundLetters, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                onLetterChanged();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN);
            }
        }).attachToRecyclerView(recycler);

        new TutorialDialog().show(getSupportFragmentManager(), null);
    }

    public void onLetterChanged() {
        String word = "";
        for (Letter letter : foundLetters) {
            word += letter.letter;
        }

        if (word.matches(this.word)) {
            new FinishedDialog().show(getSupportFragmentManager(), null);
        }
    }

    public void setCameraSize() {
        List<Camera.Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        boolean error = true;
        while (error) {
            error = false;
            Camera.Size bestSize = null;
            int bestInt = -1;

            for(int i = 0; i < previewSizes.size(); i++) {
                if(bestSize == null || (previewSizes.get(i).width * previewSizes.get(i).height) > (bestSize.width * bestSize.height)) {
                    bestSize = previewSizes.get(i);
                    bestInt = i;
                }
            }

            if (bestSize == null) {
                finish();
                return;
            }

            try {
                Camera.Parameters p = camera.getParameters();
                p.setPreviewSize(bestSize.width, bestSize.height);
                camera.setParameters(p);
            } catch (Exception e) {
                error = true;
                e.printStackTrace();
                previewSizes.remove(bestInt);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(0);
                camera.startPreview();
                preview.setCamera(camera);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            setCameraSize();
        }

        orientation.onResume();
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }

        if (dialog != null && dialog.isShowing()) dialog.dismiss();

        orientation.onPause();

        super.onPause();
    }
}
