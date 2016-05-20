package com.doubledotlabs.letters;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ImagePreview preview;
    Camera camera;
    LetterView letterView;
    FrameLayout fl;

    SensorManager sensorManager;
    Sensor sensor;

    ArrayList<Letter> letters;

    float x, y, z;

    final int TOUCH_RANGE = 80;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = new ImagePreview(this, (SurfaceView) findViewById(R.id.surfaceView));
        letterView = (LetterView) findViewById(R.id.letterView);
        fl = (FrameLayout) findViewById(R.id.fl);

        fl.addView(preview);
        preview.setKeepScreenOn(true);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        Random rand = new Random();
        String word = "DoubleDotLabs";
        letters = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            letters.add(new Letter(String.valueOf(word.charAt(i)), rand.nextDouble(), rand.nextDouble()));
        }

        letterView.setLetters(letters);

        letterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    float x = event.getX(), y = event.getY();

                    Log.e("MainActivity", "x:" + x + " y:" + y);

                    for (Letter letter : letters) {
                        double thisX = (1 - (letter.x + MainActivity.this.x)) * letterView.getCanvasWidth() * Math.PI;
                        double thisY = (letter.y + MainActivity.this.z) * letterView.getCanvasHeight() * Math.PI;

                        if (Math.abs(x - thisX) < TOUCH_RANGE) {
                            if (Math.abs(y - thisY) < TOUCH_RANGE) {
                                letter.found = true;
                            } else if (Math.abs(y - thisY + letterView.getCanvasHeight()) < TOUCH_RANGE) {
                                letter.found = true;
                            } else if (Math.abs(y - thisY - letterView.getCanvasHeight()) < TOUCH_RANGE) {
                                letter.found = true;
                            }
                        } else if (Math.abs(x - thisX + letterView.getCanvasWidth()) < TOUCH_RANGE) {
                            if (Math.abs(y - thisY) < TOUCH_RANGE) {
                                letter.found = true;
                            } else if (Math.abs(y - thisY + letterView.getCanvasHeight()) < TOUCH_RANGE) {
                                letter.found = true;
                            } else if (Math.abs(y - thisY - letterView.getCanvasHeight()) < TOUCH_RANGE) {
                                letter.found = true;
                            }
                        } else if (Math.abs(x - thisX - letterView.getCanvasWidth()) < TOUCH_RANGE) {
                            if (Math.abs(y - thisY) < TOUCH_RANGE) {
                                letter.found = true;
                            } else if (Math.abs(y - thisY + letterView.getCanvasHeight()) < TOUCH_RANGE) {
                                letter.found = true;
                            } else if (Math.abs(y - thisY - letterView.getCanvasHeight()) < TOUCH_RANGE) {
                                letter.found = true;
                            }
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }

        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0] / 360;
        float y = event.values[1] / 360;
        float z = event.values[2] / 360;

        letterView.update((this.x + x) / 2, (this.y + y) / 2, (this.z + z) / 2);

        this.x = x;
        this.y = y;
        this.z = z;
    }
}
