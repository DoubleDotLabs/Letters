package com.doubledotlabs.letters.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Orientation implements SensorEventListener {

    public interface OrientationListener {
        void onOrientation(float x, float y);
        void onAccuracyChanged(int accuracy);
    }

    private OrientationListener listener;
    private Activity activity;

    private SensorManager sensorManager;

    private float[] accelerometerVals, magneticVals;

    private float[] sensorValues = new float[9];
    private float[] orientation = new float[3];

    public Orientation(Activity activity, OrientationListener listener) {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    }

    public void onResume() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerVals = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticVals = event.values.clone();
                break;
        }

        if (accelerometerVals != null && magneticVals != null && SensorManager.getRotationMatrix(sensorValues, new float[16], accelerometerVals, magneticVals)) {
            SensorManager.getOrientation(sensorValues, orientation);
            listener.onOrientation(orientation[0] / 360, orientation[2] / 360);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        listener.onAccuracyChanged(accuracy);
    }
}
