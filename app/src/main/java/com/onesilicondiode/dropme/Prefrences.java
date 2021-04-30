package com.onesilicondiode.dropme;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nvanbenschoten.motion.ParallaxImageView;

public class Prefrences extends AppCompatActivity {
    private View background;
    RoundedImageView photo;
    TextView email;
    TextView email_full;
    ParallaxImageView userBack;
    Button signOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefrences);
        userBack = findViewById(R.id.userMotion);
        signOut = findViewById(R.id.signOut);
        signOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            vibrateDevice();
            Intent toBack = new Intent(Prefrences.this,LoginActivity.class);
            startActivity(toBack);
            finishAffinity();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        photo = findViewById(R.id.accPhoto);
        email = findViewById(R.id.accName);
        email_full = findViewById(R.id.accEmail);
        Uri photoUrl = user.getPhotoUrl(); Glide.with(this).load(photoUrl).into(photo);
        String personEmail = user.getDisplayName();
        email.setText(personEmail);
        String personRealEmail = user.getEmail();
        email_full.setText(personRealEmail);
        background = findViewById(R.id.background);
        if (savedInstanceState == null) {
            background.setVisibility(View.INVISIBLE);
            final ViewTreeObserver viewTreeObserver = background.getViewTreeObserver();

            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        background.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                });
            }

        }

    }

    private void circularRevealActivity() {
        int cx = background.getRight() - getDips(20);
        int cy = background.getBottom() - getDips(60);

        float finalRadius = Math.max(background.getWidth(), background.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                background,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(500);
        background.setVisibility(View.VISIBLE);
        circularReveal.start();

    }
    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }
    @Override
    public void onBackPressed() {
        int cx = background.getWidth() - getDips(30);
        int cy = background.getBottom() - getDips(30);

        float finalRadius = Math.max(background.getWidth(), background.getHeight());
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(background, cx, cy, finalRadius, 0);

        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                background.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        circularReveal.setDuration(500);
        circularReveal.start();
    }
    @Override
    protected void onResume() {
        super.onResume();
        userBack.registerSensorManager();
    }
    @Override
    public void onPause() {
        userBack.unregisterSensorManager();
        super.onPause();
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
}
