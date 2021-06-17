package com.example.lightapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lightapp.lightLevelApp.LightLevelActivity;
import com.example.lightapp.openLigthApp.OpenLightActivity;
import com.example.lightapp.splash.SplashActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;
    Button openLightApp, lightLevelApp;
    private TextToSpeech mTTS;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static int SPLASH_TIME_OUT = 9000;
    private static int SPLASH_TIME_OUT2 = 12000;
    private static int SPLASH_TIME_OUT3 = 13000;


    private SpeechRecognizer speechRecognizer;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.getDefault());
                    mTTS.speak("Işık açık uygulamasına gitmek için bir , " +
                                    "ışık seviyesi uygulamasına gitmek için iki deyiniz.",
                            TextToSpeech.QUEUE_FLUSH, null);
                    /*if(isConnecionAvaliable()) {
                        userVoice();
                        mTTS.speak("internet bağlantınız fena kardeşim",TextToSpeech.QUEUE_FLUSH, null) ;
                    }else{
                        mTTS.speak("lütfen internet bağlantınızı kontrol ediniz ",TextToSpeech.QUEUE_FLUSH, null) ;
                        speechRecognizer.stopListening();
                    }*/
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
        init();
        userVoice(SPLASH_TIME_OUT);
        onClick();
    }

    private void userVoice(int SPLASH_TIME_OUT) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                }
                catch (Exception e) {
                    Toast
                            .makeText(MainActivity.this, " " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                        Log.e("Mesaj", Objects.requireNonNull(result).get(0));
                if(Objects.requireNonNull(result).get(0).contains("bir") || Objects.requireNonNull(result).get(0).contains("ışık açık")
                        || Objects.requireNonNull(result).get(0).contains("açık")|| Objects.requireNonNull(result).get(0).contains("1")){
                    isikAcikUygulamasıCheck();
                }else if(Objects.requireNonNull(result).get(0).contains("iki") || Objects.requireNonNull(result).get(0).contains("ışık seviyesi")
                        || Objects.requireNonNull(result).get(0).contains("seviye") || Objects.requireNonNull(result).get(0).contains("2")){
                    Intent intent = new Intent(getApplicationContext(), LightLevelActivity.class);
                    startActivity(intent);
                }
                else{
                    speech("Herhangi bir uygulamaya geçiş yapılamadı.Lütfen tekrar deneyiniz. Işık açık uygulamasına gitmek için bir , " + "ışık seviyesi uygulamasına gitmek için iki deyiniz.");
                    userVoice(SPLASH_TIME_OUT3);
                }
            }
        }
    }

    private void init() {
        lottieAnimationView = findViewById(R.id.animationView);
        openLightApp = findViewById(R.id.leftArrow);
        lightLevelApp = findViewById(R.id.rightArrow);
    }

    private void speech(String text) {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void onClick() {
        openLightApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               isikAcikUygulamasıCheck();
            }
        });
        lightLevelApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LightLevelActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
    void isikAcikUygulamasıCheck(){
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        if (hour < 7 || hour > 19) {
            Intent intent = new Intent(getApplicationContext(), OpenLightActivity.class);
            startActivity(intent);
        } else {
            speech("Uygun saat aralığında olmadığınız için bu özellik kullanılamıyor.Işık açık uygulamasına gitmek için bir , " + "ışık seviyesi uygulamasına gitmek için iki deyiniz.");
            userVoice(SPLASH_TIME_OUT2);
        }
    }
    private boolean isConnecionAvaliable(){
        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo net =connectivityManager.getActiveNetworkInfo();
        if(net!=null && net.isAvailable() && net.isConnected()){
            return true;
        }else{
            return false;
        }
    }

}