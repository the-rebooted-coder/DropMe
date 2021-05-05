package com.onesilicondiode.dropme;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nvanbenschoten.motion.ParallaxImageView;

import java.io.ByteArrayOutputStream;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class Share extends AppCompatActivity  {
    RoundedImageView sharedBitmap;
    ParallaxImageView parallaxImageView;
    String myUID;
    private Swipe swipe;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference(user.getUid());
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    DropMe dropMe;
    Vibrator v3;
    Uri imageUri;
    LottieAnimationView shootRocket;
    TextView howtoUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_share);
        v3 = (Vibrator) getSystemService(Share.VIBRATOR_SERVICE);
        parallaxImageView = findViewById(R.id.motion_back);
        shootRocket = findViewById(R.id.shootRocket);
        dropMe = new DropMe();
        howtoUse = findViewById(R.id.howToUse);
        howtoUse.setOnClickListener(v -> {
            vibrateDevice();
            createNotifier();
        });
        sharedBitmap = findViewById(R.id.sharedBitmap);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (bmp!=null){
            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bmp, null,null));
            sharedBitmap.setImageURI(imageUri);
        }
        else {
            //Pure Exception Handling
            Toast.makeText(this,"We encountered an error, Please Retry",Toast.LENGTH_SHORT).show();
            Intent toHome = new Intent(Share.this,Magic.class);
            startActivity(toHome);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
        swipe = new Swipe(350,400);
        swipe.setListener(new SwipeListener() {
            @Override public void onSwipingLeft(final MotionEvent event) {
            }
            @Override public boolean onSwipedLeft(final MotionEvent event) {
                return false;
            }

            @Override public void onSwipingRight(final MotionEvent event) {
            }

            @Override public boolean onSwipedRight(final MotionEvent event) {
                return false;
            }

            @Override public void onSwipingUp(final MotionEvent event) {
            }

            @Override public boolean onSwipedUp(final MotionEvent event) {
                long[] pattern = {0, 25, 100, 35, 100};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    VibrationEffect effect = VibrationEffect.createWaveform(pattern, -1);
                    v3.vibrate(effect);
                } else {
                    //deprecated in API 26
                    v3.vibrate(pattern, -1);
                }
                Toast.makeText(Share.this, "Transmitting Over Air", Toast.LENGTH_SHORT).show();
                   myUID = user.getUid();
                   addDataFirebase(myUID,imageUri);
                return false;
            }

            @Override public void onSwipingDown(final MotionEvent event) {
            }

            @Override public boolean onSwipedDown(final MotionEvent event) {
                vibrateDevice();
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(Share.this)
                        .setTitle("Delete Transmitted Images?")
                        .setMessage("Tapping 'Delete' will remove all the images from DropMe Web.")
                        .setCancelable(false)
                        .setPositiveButton("Delete", R.drawable.ic_delete, (dialogInterface, which) -> {
                            vibrateDeviceAfterDelete();
                            root.removeValue();
                            Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("Cancel", R.drawable.ic_close, (dialogInterface, which) -> {
                            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        })
                        .build();

                // Show Dialog
                mBottomSheetDialog.show();
                return false;
            }
        });
    }

    private void createNotifier() {
        BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(this)
                .setTitle(getString(R.string.get_images_tip_title))
                .setMessage(getString(R.string.get_images_tip))
                .setAnimation("linked.json")
                .setCancelable(true)
                .build();
        mDialog.show();
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        swipe.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }
    private void addDataFirebase(String myUID, Uri uri) {
        dropMe.setMyUID(myUID);
        StorageReference fileRef = reference.child(System.currentTimeMillis()+"."+ getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DropMe dropMe = new DropMe(uri.toString());
                        String modelID = root.push().getKey();
                        root.child(modelID).setValue(dropMe);
                        shootRocket.playAnimation();
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                howtoUse.setVisibility(View.VISIBLE);
                                shootRocket.cancelAnimation();
                                shootRocket.setVisibility(View.INVISIBLE);
                                sharedBitmap.setVisibility(View.VISIBLE);
                                Toast.makeText(Share.this,"Success \uD83C\uDF89",Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                sharedBitmap.setVisibility(View.INVISIBLE);
                howtoUse.setVisibility(View.INVISIBLE);
                shootRocket.setVisibility(View.VISIBLE);
                shootRocket.playAnimation();
                final Handler handler2 = new Handler(Looper.getMainLooper());
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shootRocket.cancelAnimation();
                    }
                }, 2500);
            }
        });
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // after adding this data we are showing toast message.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Share.this, "Transmission Cancelled"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent toHome = new Intent(Share.this,Magic.class);
        startActivity(toHome);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    private void vibrateDevice() {
        Vibrator v3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,25,100,35,100,45,100};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createWaveform(pattern,-1));
        } else {
            v3.vibrate(pattern,-1);
        }
    }
    private void vibrateDeviceAfterDelete() {
        Vibrator v3 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,25,100,35,100,45,100,55,100};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v3.vibrate(VibrationEffect.createWaveform(pattern,-1));
        } else {
            v3.vibrate(pattern,-1);
        }
    }
}