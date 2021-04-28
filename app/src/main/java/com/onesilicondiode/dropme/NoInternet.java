package com.onesilicondiode.dropme;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nvanbenschoten.motion.ParallaxImageView;

public class NoInternet extends AppCompatActivity {
    Button retry;
    ParallaxImageView noInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        noInternet = findViewById(R.id.noInternetMotion);
        retry = findViewById(R.id.retryConnection);
        retry.setOnClickListener(v -> {
            if (haveNetwork()){
                vibrateDevice();
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> vibrateDeviceTwice(), 100);
                Toast.makeText(NoInternet.this,"Wohoooooooo!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NoInternet.this, Magic.class));
                finish();
            }
            else {
                vibrateDevice();
                Toast.makeText(NoInternet.this,"Nope, Still no Luck!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Network Checking Boolean
    private boolean haveNetwork() {
        boolean have_WIFI = false;
        boolean have_MobileData = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info : networkInfos) {
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected())
                    have_WIFI = true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if (info.isConnected())
                    have_MobileData = true;
        }
        return have_MobileData||have_WIFI;
    }
    private void vibrateDevice() {
        Vibrator v3 = (Vibrator) getSystemService(NoInternet.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v3.vibrate(25);
        }
    }
    private void vibrateDeviceTwice() {
        Vibrator v3 = (Vibrator) getSystemService(NoInternet.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createOneShot(32, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v3.vibrate(28);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        noInternet.registerSensorManager();
    }
    @Override
    public void onPause() {
        noInternet.unregisterSensorManager();
        super.onPause();
    }
}