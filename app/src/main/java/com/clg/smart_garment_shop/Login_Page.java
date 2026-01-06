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

public class Login_Page extends AppCompatActivity {

    // UI
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgetPassword, tvSignUp;

    // Firebase
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Firebase
        auth = FirebaseAuth.getInstance();

        // UI init
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgetPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignup);

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> loginUser());

        // SIGN UP
        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, Register_Page.class)));

        // FORGOT PASSWORD
        tvForgetPassword.setOnClickListener(v ->
                startActivity(new Intent(this, Forget_Password.class)));
    }

    // ================= LOGIN =================

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (!isValidLogin(email, password)) return;

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    } else {

                        Toast.makeText(this,
                                "Invalid email or password",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ================= VALIDATION =================

    private boolean isValidLogin(String email, String password) {

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Minimum 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }
}
