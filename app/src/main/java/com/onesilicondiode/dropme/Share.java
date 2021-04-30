package com.onesilicondiode.dropme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nvanbenschoten.motion.ParallaxImageView;

import java.io.ByteArrayOutputStream;

public class Share extends AppCompatActivity  {
    RoundedImageView sharedBitmap;
    ParallaxImageView parallaxImageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference
    String myUID;
    DropMe dropMe;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        parallaxImageView = findViewById(R.id.motion_back);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dropMe = new DropMe();
        sharedBitmap = findViewById(R.id.sharedBitmap);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (bmp!=null){
            sharedBitmap.setImageBitmap(bmp);
            imageUri = getImageUri(this,bmp);
        }
        else {
            //Pure Exception Handling
            Toast.makeText(this,"We encountered an error, Please Retry",Toast.LENGTH_SHORT).show();
            Intent toHome = new Intent(Share.this,Magic.class);
            startActivity(toHome);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
        sharedBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myUID = user.getUid();
                    addDatatoFirebase(myUID);
            }
        });
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "TransmissionData", null);
        return Uri.parse(path);
    }
    private void addDatatoFirebase(String myUID) {
        // below 3 lines of code is used to set
        // data in our object class.
        dropMe.setMyUID(myUID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(myUID);
                // after adding this data we are showing toast message.
                Toast.makeText(Share.this, "Transmitting Over Air", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Share.this, "Transmission Cancelled"+error, Toast.LENGTH_SHORT).show();
            }
        });
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