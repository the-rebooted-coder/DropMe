package com.onesilicondiode.dropme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nvanbenschoten.motion.ParallaxImageView;

public class Share extends AppCompatActivity  {
    RoundedImageView sharedBitmap;
    ParallaxImageView parallaxImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        parallaxImageView = findViewById(R.id.motion_back);
        sharedBitmap = findViewById(R.id.sharedBitmap);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (bmp!=null){
            sharedBitmap.setImageBitmap(bmp);
        }
        else {
            //Pure Exception Handling
            Toast.makeText(this,"We encountered an error, Please Retry",Toast.LENGTH_SHORT).show();
            Intent toHome = new Intent(Share.this,Magic.class);
            startActivity(toHome);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        parallaxImageView.registerSensorManager();
    }
    @Override
    public void onPause() {
        parallaxImageView.unregisterSensorManager();
        super.onPause();
    }
}