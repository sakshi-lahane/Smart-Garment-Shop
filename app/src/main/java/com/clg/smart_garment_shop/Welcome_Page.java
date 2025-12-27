package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Welcome_Page extends AppCompatActivity {

    MaterialButton btnGetStarted;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);

        btnGetStarted = findViewById(R.id.btnGetStarted);
        txtLogin = findViewById(R.id.txtLogin);

            btnGetStarted.setOnClickListener(v -> {
                Intent intent = new Intent(Welcome_Page.this,Register_Page.class);
                startActivity(intent);
            });

            txtLogin.setOnClickListener(v -> {
                Intent intent = new Intent(Welcome_Page.this,Login_Page.class);
                startActivity(intent);
            });

    }
}