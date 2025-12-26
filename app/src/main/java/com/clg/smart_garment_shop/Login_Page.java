package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login_Page extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        textView=findViewById(R.id.tvForgotPassword);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(Login_Page.this,Forget_Password.class);
            startActivity(intent);
        });
    }
}