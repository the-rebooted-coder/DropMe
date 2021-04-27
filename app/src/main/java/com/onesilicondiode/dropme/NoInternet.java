package com.onesilicondiode.dropme;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NoInternet extends AppCompatActivity {
    Button retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        retry = findViewById(R.id.retryConnection);
        retry.setOnClickListener(v -> {
            if (haveNetwork()){
                Toast.makeText(NoInternet.this,"Wohoooooooo!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NoInternet.this, Magic.class));
                finish();
            }
            else {
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
}