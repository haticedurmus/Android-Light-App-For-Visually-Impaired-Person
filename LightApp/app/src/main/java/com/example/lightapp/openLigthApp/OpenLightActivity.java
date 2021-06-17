package com.example.lightapp.openLigthApp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.lightapp.MainActivity;
import com.example.lightapp.R;

import java.util.Calendar;
import java.util.Locale;

public class OpenLightActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor;
    private TextToSpeech mTTS;
    TextView textView,lightAmount;
    Calendar calendar;
    int hour, minute;
    String state = "";
    int ilkDeger = 0, ikinciDeger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_light);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textView = findViewById(R.id.clock);
        lightAmount = findViewById(R.id.lightAmount);
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        if (minute < 10 && hour < 10) {
            textView.setText("0" + hour + " : 0" + minute);
        } else if (minute > 10 && hour > 10) {
            textView.setText(hour + " : " + minute);
        } else if (minute > 10 && hour < 10) {
            textView.setText("0" + hour + " : " + minute);
        } else {
            textView.setText(hour + " : 0" + minute);
        }


        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.getDefault()); //telefonun kendi dili

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });


        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    private void speech(String text) {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        state = " ";
        ilkDeger = 0;
        ikinciDeger = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, 50);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        lightAmount.setText((int) event.values[0]+ " lx");
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (ilkDeger == 0) {
                ilkDeger = (int) event.values[0];
            } else {
                ikinciDeger = ilkDeger;
                ilkDeger = (int) event.values[0];
            }
            getValueFromSensor(ilkDeger, ikinciDeger);

        }
    }

    private void getValueFromSensor(int ilkDeger, int ikinciDeger) {
        if (ilkDeger < 10 && ikinciDeger == 0) {
            speech("IŞIK KAPALI");
            state = "IŞIK KAPALI";
        } else if (ilkDeger > 10 && ikinciDeger == 0) {
            state = "IŞIK AÇIK";
            speech("IŞIK AÇIK");
        } else if (ilkDeger < 10 && ikinciDeger > 10 && state == "IŞIK AÇIK") {
            speech("IŞIK KAPALI");
            state = "IŞIK KAPALI";
        } else if (ilkDeger > 10 && ikinciDeger < 10 && state == "IŞIK KAPALI") {
            state = "IŞIK AÇIK";
            speech("IŞIK AÇIK");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }
}