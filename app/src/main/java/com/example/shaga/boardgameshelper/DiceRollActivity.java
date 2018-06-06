package com.example.shaga.boardgameshelper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DiceRollActivity extends AppCompatActivity {

    private final Random random = new Random();

    private int nrOfDices = 1;
    private int nrOfSizes = 6;

    private float accel = 0f;
    private boolean cooldown = false;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice_roll);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        SeekBar seekBarDices = (SeekBar) findViewById(R.id.seekBarDice);
        SeekBar seekBarSizes = (SeekBar) findViewById(R.id.seekBarSize);

        seekBarDices.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView textViewDices = findViewById(R.id.textViewDice);
                nrOfDices = ++i;
                textViewDices.setText(String.format("%d", new Object[]{nrOfDices}));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarSizes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TextView textViewSizes = findViewById(R.id.textViewSize);
                nrOfSizes = ++i;
                textViewSizes.setText(String.format("%d", new Object[]{nrOfSizes}));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accel = (float) Math.sqrt((double) (x*x + y*y + z*z));
            accel -= 9.81f;



            if(accel > 3f && !cooldown) {
                cooldown = true;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        cooldown = false;
                    }
                }, 1000);
                TextView tV = findViewById(R.id.textViewSensor);
                int roll = diceRoll();
                tV.setText(String.format("%d", roll));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorEventListener);
        super.onPause();
    }

    int diceRoll() {
        int ret = 0;
        for(int i=1;i<=nrOfDices;++i)
        {
            ret += random.nextInt(nrOfSizes) + 1;
        }
        return ret;
    }
}
