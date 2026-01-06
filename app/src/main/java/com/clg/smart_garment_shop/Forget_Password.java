package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_Password extends AppCompatActivity {

    private TextInputEditText etEmail;
    private MaterialButton btnResetPassword;
    private TextView tvBackToLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Reset Password
        btnResetPassword.setOnClickListener(v -> resetPassword());

        // Back to Login
        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, Login_Page.class));
            finish();
        });
    }

    // ================= RESET PASSWORD =================

    private void resetPassword() {

        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Password reset email sent. Check your inbox.",
                                Toast.LENGTH_LONG).show();

                        startActivity(new Intent(this, Login_Page.class));
                        finish();

                    } else {
                        Toast.makeText(this,
                                "Email not registered or error occurred",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
