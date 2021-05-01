package com.onesilicondiode.dropme;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

public class Prefrences extends AppCompatActivity {
    private View background;
    RoundedImageView photo;
    TextView email;
    TextView email_full;
    ParallaxImageView userBack;
    Button signOut,about;
    int counter = 0;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefrences);
        userBack = findViewById(R.id.userMotion);
        about = findViewById(R.id.aboutDropMe);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrateDevice();
                Intent i = new Intent(Prefrences.this, AboutDropMe.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String> (about,"imageTransition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Prefrences.this,pairs);
                startActivity(i,options.toBundle());
                int splash_screen_time_out = 1000;
                new Handler().postDelayed(() -> {
                    finish();
                }, splash_screen_time_out);
            }
        });
        signOut = findViewById(R.id.signOut);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signOut.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                vibrateDevice();
                Toast.makeText(Prefrences.this,"Tap twice to Logout",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDoubleClick(View view) {
                FirebaseAuth.getInstance().signOut();
                vibrateDevice();
                fullyLogout();
            }
        }));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        photo = findViewById(R.id.accPhoto);
        email = findViewById(R.id.accName);
        email.setOnClickListener(v -> {
            if(counter == 0){
                Toast.makeText(this,"Now showing userID",Toast.LENGTH_SHORT).show();
                email.setText(user.getUid());
                counter++;
            }
            else {
                email.setText(user.getDisplayName());
                counter = 0;
            }
        });
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

    private void fullyLogout() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> {
                    Toast.makeText(Prefrences.this,"Sign out successful",Toast.LENGTH_SHORT).show();
                    Intent toBack = new Intent(Prefrences.this,LoginActivity.class);
                    startActivity(toBack);
                    finishAffinity();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
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
