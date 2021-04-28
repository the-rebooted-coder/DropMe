package com.onesilicondiode.dropme;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

public class Share extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (bmp!=null){
            Toast.makeText(this,"Yay, Bitmap Received",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this,"Oops, Problem",Toast.LENGTH_SHORT).show();
        }
    }
}