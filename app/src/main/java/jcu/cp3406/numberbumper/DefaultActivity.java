package jcu.cp3406.numberbumper;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

public abstract class DefaultActivity extends AppCompatActivity implements SensorEventListener {

    Locale locale = Locale.getDefault();

    Vibrator vibrator;

    private static final long[] TREMBLE = {500, 100};

    SensorManager sensorManager;

    Sensor accelerometer;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.drawable.bump);
            actionBar.setDisplayUseLogoEnabled(true);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float valueX = event.values[SensorManager.DATA_X];
            float valueY = event.values[SensorManager.DATA_Y];
            float valueZ = event.values[SensorManager.DATA_Z];
            if (shakeCache == null) {
                shakeCache = new ShakeCache(System.currentTimeMillis(), valueX, valueY, valueZ);
                return;
            }
            if (shakeCache.checkThreshold(valueX, valueY, valueZ)) {
                onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private ShakeCache shakeCache;

    class ShakeCache {

        private static final int THRESHOLD = 1000;

        private long time;

        private float lastX, lastY, lastZ;

        ShakeCache(long time, float lastX, float lastY, float lastZ) {
            this.time = time;
            this.lastX = lastX;
            this.lastY = lastY;
            this.lastZ = lastZ;
        }

        boolean checkThreshold(float currentX, float currentY, float currentZ) {
            long currentTime = System.currentTimeMillis();
            long differedTime = (currentTime - time);
            if (differedTime > 100) {
                time = currentTime;
                float differedPosition = currentX + currentY + currentZ - lastX - lastY - lastZ;
                double speed = Math.abs(differedPosition) / differedTime * 10000;
                if (speed > THRESHOLD) {
                    return true;
                }
                lastX = currentX;
                lastY = currentY;
                lastZ = currentZ;
            }
            return false;
        }
    }

    abstract void onShake();

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    void doVibrate() {
        if (vibrator != null) {
            vibrator.vibrate(TREMBLE, 1);
        }
    }
}
