package com.labs.yatsenkolex.lab4;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    TextView aX;
    TextView aY;
    TextView aZ;
    TextView mX;
    TextView mY;
    TextView mZ;
    TextView proximity;
    TextView light;
    TextView CompOrient;

    SensorManager sensorManager;
    Sensor aSensor;
    Sensor pSensor;
    Sensor mSensor;
    Sensor lSensor;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;

    private ImageView compassImage;
    private float RotateDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aX = findViewById(R.id.textView2);
        aY = findViewById(R.id.textView3);
        aZ = findViewById(R.id.textView4);
        mX = findViewById(R.id.textView6);
        mY = findViewById(R.id.textView7);
        mZ = findViewById(R.id.textView8);
        proximity = findViewById(R.id.textView10);
        light = findViewById(R.id.textView12);
        CompOrient = findViewById(R.id.Header);
        compassImage = findViewById(R.id.CompassView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        lSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];
        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];
    }

    @Override
    public void onStart() {
        super.onStart();
        sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, lSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, aSensor);
        sensorManager.unregisterListener(this, mSensor);
        sensorManager.unregisterListener(this, pSensor);
        sensorManager.unregisterListener(this, lSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            aX.setText(Float.toString(event.values[0]));
            aY.setText(Float.toString(event.values[1]));
            aZ.setText(Float.toString(event.values[2]));
            System.arraycopy(event.values, 0, valuesAccelerometer, 0, 3);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mX.setText(Float.toString(event.values[0]));
            mY.setText(Float.toString(event.values[1]));
            mZ.setText(Float.toString(event.values[2]));
            System.arraycopy(event.values, 0, valuesMagneticField, 0, 3);
        }
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity.setText(Float.toString(event.values[0]));
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light.setText(Float.toString(event.values[0]));
            /** Изменение яркости на основе показаний датчика освещенности */
            float brightness;
            if (event.values[0] < 40) {
                brightness = 0.1f;
            } else {
                if (event.values[0] < 300) {
                    brightness = 0.5f;
                } else {
                    brightness = 0.9f;
                }
            }
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = brightness;
            getWindow().setAttributes(lp);
        }
        /** Рассчитывание показателя отклонения от севера на основе датчиков магнитного поля и акселерометра */
        boolean success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                valuesAccelerometer,
                valuesMagneticField);
        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues);
            double azimuth = Math.toDegrees(matrixValues[0]);
            CompOrient.setText("Отклонение от севера: " + Math.round(azimuth) + " градусов");
            RotateAnimation rotateAnimation = new RotateAnimation(
                    RotateDegree,
                    (float) -azimuth,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            compassImage.startAnimation(rotateAnimation);
            RotateDegree = (float) -azimuth;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
