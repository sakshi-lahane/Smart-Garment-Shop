package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Register_Page extends AppCompatActivity {

    TextView tvLogin;
    MaterialButton btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_page);

        tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register_Page.this,Login_Page.class);
            startActivity(intent);
        });

        btnCreateAccount=findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(Register_Page.this,MainActivity.class);
            startActivity(intent);
        });

    }
}