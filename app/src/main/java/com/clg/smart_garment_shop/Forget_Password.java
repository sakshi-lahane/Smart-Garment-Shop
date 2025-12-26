package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Forget_Password extends AppCompatActivity {

    TextView tvBackTologin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        tvBackTologin=findViewById(R.id.tvBackToLogin);

        tvBackTologin.setOnClickListener(v -> {
            Intent intent = new Intent(Forget_Password.this,Login_Page.class);
            startActivity(intent);
        });

    }
}