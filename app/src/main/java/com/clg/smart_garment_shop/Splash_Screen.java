package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView logo = findViewById(R.id.logoImage);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        logo.startAnimation(animation);

        // Move to Welcome Screen after animation
        new android.os.Handler().postDelayed(() -> {
            startActivity(new Intent(Splash_Screen.this, Welcome_Page.class));
            finish();
        }, 2000);
    }
}
