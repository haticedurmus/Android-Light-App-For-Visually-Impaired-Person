package com.example.lightapp.lightLevelApp;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.lightapp.MainActivity;
import com.example.lightapp.R;

import java.util.Calendar;
import java.util.Locale;

public class LightLevelActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager sensorManager;
    Sensor sensor;
    private TextToSpeech mTTS;
    TextView textView_lüx;
    TextView textView_soa;
    ProgressBar progressBar;
    ImageView imageView;
    String state = "";
    int ilkDeger = 0, ikinciDeger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_level);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textView_lüx = findViewById(R.id.textView_lüx);
        progressBar = findViewById(R.id.progress_circular);
        textView_soa = findViewById(R.id.textView_soa);
        imageView = findViewById(R.id.imageView);


        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.getDefault()); //telefonun kendi dili

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                    message();
                    //mTTS.speak("Haticee canım", TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        message();
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

    }

    private void message() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        if (hour <= 7 || hour > 19) {
            mTTS.speak("İYİ GECELER", TextToSpeech.QUEUE_FLUSH, null);
           // speech("İYİ GECELER");
        } else if (hour > 7 && hour <= 11) {
            mTTS.speak("GÜNAYDIN", TextToSpeech.QUEUE_FLUSH, null);
            //speech("GÜNAYDIN");
        } else if (hour > 11 && hour <= 15) {
            mTTS.speak("TÜNAYDIN", TextToSpeech.QUEUE_FLUSH, null);
            //speech("İYİ ÖĞLENLER");
        } else if (hour > 15 && hour <= 19) {
            mTTS.speak("İYİ AKŞAMLAR", TextToSpeech.QUEUE_FLUSH, null);
            //speech("İYİ AKŞAMLAR");
        }
    }

    private void speech(String text) {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        textView_lüx.setText(event.values[0] + " lüx");
        progressBar.setProgress((int) event.values[0]);
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
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);

        if (hour <= 7 || hour > 19) {
            textView_soa.setText("İYİ GECELER");
            imageView.setImageResource(R.mipmap.aksam);
            progressBar.setMax(2300);
            if (ilkDeger < 2300 && ilkDeger > 100 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if (ilkDeger < 100 && ilkDeger > 10 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            } else if (ilkDeger < 10 && ikinciDeger == 0) {
                speech("Işık kapalı.");
                state = "Işık kapalı.";
            } else if (ilkDeger > 2300 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            } else if ((ilkDeger < 2300 && ilkDeger > 100) && !(ikinciDeger < 2300 && ikinciDeger > 100) && !state.equals("Ortamdaki ışık seviyesi yeterli.")) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if ((ilkDeger < 100 && ilkDeger > 10) && !(ikinciDeger < 100 && ikinciDeger > 10) && !state.equals("Ortamdaki ışık seviyesi yetersiz.")) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            } else if (ilkDeger < 10 && !(ikinciDeger < 10) && !state.equals("Işık kapalı.")) {
                speech("Işık kapalı.");
                state = "Işık kapalı.";
            } else if (ilkDeger > 2300 && !(ikinciDeger > 2300) && !state.equals("Ortamdaki ışık seviyesi yüksek.")) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            }
        } else if (hour > 7 && hour <= 11) {
            textView_soa.setText("GÜNAYDIN");
            imageView.setImageResource(R.mipmap.sabah);
            progressBar.setMax(5000);
            if ((ilkDeger > 2300 && ilkDeger < 5000) && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if (ilkDeger > 5000 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            } else if ((ilkDeger < 2300 && ilkDeger > 10) && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            } else if (ilkDeger < 10 && ikinciDeger == 0) {
                speech("Işık kapalı.");
                state = "Işık kapalı.";
            } else if ((ilkDeger > 2300 && ilkDeger < 5000) && !(ikinciDeger > 2300 && ikinciDeger < 5000) && !state.equals("Ortamdaki ışık seviyesi yeterli.")) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if ((ilkDeger > 5000) && !(ikinciDeger > 5000) && !state.equals("Ortamdaki ışık seviyesi yüksek.")) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            } else if ((ilkDeger < 2300 && ilkDeger > 10) && !(ikinciDeger < 2300 && ikinciDeger > 10) && !state.equals("Ortamdaki ışık seviyesi yetersiz.")) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            } else if (ilkDeger < 10 && !(ikinciDeger < 10) && !state.equals("Işık kapalı.")) {
                speech("Işık kapalı.");
                state = "Işık kapalı.";
            }
        } else if (hour > 11 && hour <= 15) {
            imageView.setImageResource(R.mipmap.ogle);
            textView_soa.setText("TÜNAYDIN");
            progressBar.setMax(30000);
            if ((ilkDeger < 30000 && ilkDeger > 5000) && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if ((ilkDeger < 5000 && ilkDeger > 500) && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi beklenenin altında.");
                state = "Ortamdaki ışık seviyesi beklenenin altında.";
            } else if (ilkDeger > 30000 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            } else if (ilkDeger < 500 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            } else if ((ilkDeger < 30000 && ilkDeger > 5000) && !(ikinciDeger < 30000 && ikinciDeger > 5000) && !state.equals("Ortamdaki ışık seviyesi yeterli.")) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if ((ilkDeger < 5000 && ilkDeger > 500) && !(ikinciDeger < 5000 && ikinciDeger > 500) && !state.equals("Ortamdaki ışık seviyesi beklenenin altında.")) {
                speech("Ortamdaki ışık seviyesi beklenenin altında.");
                state = "Ortamdaki ışık seviyesi beklenenin altında.";
            } else if (ilkDeger > 30000 && !(ikinciDeger > 30000) && !state.equals("Ortamdaki ışık seviyesi yüksek.")) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            } else if (ilkDeger < 500 && !(ikinciDeger < 500) && !state.equals("Ortamdaki ışık seviyesi yetersiz.")) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            }
        } else if (hour > 15 && hour <=19) {
            textView_soa.setText("İYİ AKŞAMLAR");
            imageView.setImageResource(R.mipmap.aksam);
            progressBar.setMax(20000);
            if ((ilkDeger < 20000 && ilkDeger > 2000) && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if ((ilkDeger < 2000 && ilkDeger > 200) && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi beklenenin altında.");
                state = "Ortamdaki ışık seviyesi beklenenin altında.";
            } else if (ilkDeger > 20000 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            } else if (ilkDeger < 200 && ikinciDeger == 0) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            } else if ((ilkDeger < 20000 && ilkDeger > 2000) && !(ikinciDeger < 20000 && ikinciDeger > 2000) && !state.equals("Ortamdaki ışık seviyesi yeterli.")) {
                speech("Ortamdaki ışık seviyesi yeterli.");
                state = "Ortamdaki ışık seviyesi yeterli.";
            } else if ((ilkDeger < 2000 && ilkDeger > 200) && !(ikinciDeger < 2000 && ikinciDeger > 200) && !state.equals("Ortamdaki ışık seviyesi beklenenin altında.")) {
                speech("Ortamdaki ışık seviyesi beklenenin altında.");
                state = "Ortamdaki ışık seviyesi beklenenin altında.";
            } else if (ilkDeger > 20000 && !(ikinciDeger > 20000) && !state.equals("Ortamdaki ışık seviyesi yüksek.")) {
                speech("Ortamdaki ışık seviyesi yüksek.");
                state = "Ortamdaki ışık seviyesi yüksek.";
            } else if (ilkDeger < 200 && !(ikinciDeger < 200) && !state.equals("Ortamdaki ışık seviyesi yetersiz.")) {
                speech("Ortamdaki ışık seviyesi yetersiz.");
                state = "Ortamdaki ışık seviyesi yetersiz.";
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        state = "";
        ilkDeger = 0;
        ikinciDeger = 0;
        if(mTTS !=null){
            mTTS.stop();
            mTTS.shutdown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        state =" ";
        ilkDeger = 0;
        ikinciDeger = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, 50);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }
}