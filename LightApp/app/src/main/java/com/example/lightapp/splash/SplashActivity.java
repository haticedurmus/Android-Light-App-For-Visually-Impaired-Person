package com.example.lightapp.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lightapp.MainActivity;
import com.example.lightapp.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 10000;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkPermission();
        lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.animate().translationY(2000).setDuration(1000).setStartDelay(4000);

    }

    public boolean isPermissionAvailable(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO}, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent nIntent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(nIntent);
                    }
                }).start();
            } else {
                Toast.makeText(this, "İzin vermediğiniz için erişim yok.", Toast.LENGTH_SHORT).show();
                checkPermission();
            }
        }
    }

}