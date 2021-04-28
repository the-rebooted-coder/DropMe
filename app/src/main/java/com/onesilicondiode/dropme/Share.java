package com.onesilicondiode.dropme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Share extends AppCompatActivity  {
ImageView sharedBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        sharedBitmap = findViewById(R.id.sharedBitmap);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (bmp!=null){
            Toast.makeText(this,"Yay, Bitmap Received",Toast.LENGTH_SHORT).show();
            sharedBitmap.setImageBitmap(bmp);
        }
        else {
            Toast.makeText(this,"We encountered an error, Please Retry",Toast.LENGTH_SHORT).show();
        }
    }
}