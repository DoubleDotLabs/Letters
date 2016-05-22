package com.doubledotlabs.letters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    AppCompatDialog dialog;

    ImagePreview preview;
    Camera camera;
    LetterView letterView;
    FrameLayout fl;

    RecyclerView recycler;
    AppCompatImageView circle;
    LettersAdapter adapter;

    SensorManager sensorManager;
    Sensor magnetometer;
    Sensor accelerometer;

    private float[] magnetometerValue = new float[3];
    private float[] accelerometerValue = new float[3];

    private float[] sensorValues = new float[9];
    private float[] orientation = new float[3];

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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            accelerometerValue = event.values;
        } else if (event.sensor == magnetometer) {
            magnetometerValue = event.values;
        }

        if (!SensorManager.getRotationMatrix(sensorValues, null, accelerometerValue, magnetometerValue)) {
            Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show();
        }

        orientation = SensorManager.getOrientation(sensorValues, orientation);

        float x = orientation[0] / 360;
        float y = orientation[2] / 360;

        letterView.update((this.x + x) / 2, (this.y + y) / 2);

        this.x = x;
        this.y = y;
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

        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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

        super.onPause();

        sensorManager.unregisterListener(this);
    }
}
